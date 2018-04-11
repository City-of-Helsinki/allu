package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.QCityDistrict;
import fi.hel.allu.QLocationGeometry;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.common.PostalAddressUtil;
import fi.hel.allu.model.coordinates.CoordinateTransformation;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.querydsl.ExcludingMapper;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QCityDistrict.cityDistrict;
import static fi.hel.allu.QFixedLocation.fixedLocation;
import static fi.hel.allu.QLocation.location;
import static fi.hel.allu.QLocationArea.locationArea;
import static fi.hel.allu.QLocationFlids.locationFlids;
import static fi.hel.allu.QLocationGeometry.locationGeometry;
import static fi.hel.allu.QPaymentClass.paymentClass1;
import static fi.hel.allu.QPostalAddress.postalAddress;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

@Repository
public class LocationDao {
  private static final Logger logger = LoggerFactory.getLogger(LocationDao.class);

  public static final Integer ALLU_SRID = Integer.valueOf(3879);

  @Autowired
  private SQLQueryFactory queryFactory;
  @Autowired
  PostalAddressDao postalAddressDao;
  @Autowired
  private CoordinateTransformation coordinateTransformation;

  final QBean<Location> locationBean = bean(Location.class, location.all());
  final QBean<PostalAddress> postalAddressBean = bean(PostalAddress.class, postalAddress.all());

  @Transactional(readOnly = true)
  public Optional<Location> findById(int id) {
    Tuple locationPostalAddress = queryFactory
        .select(locationBean, postalAddressBean)
        .from(location)
        .leftJoin(postalAddress).on(location.postalAddressId.eq(postalAddress.id))
        .where(location.id.eq(id)).fetchOne();
    Location cont = null;
    if (locationPostalAddress != null) {
      cont = PostalAddressUtil.mapPostalAddress(locationPostalAddress).get(0, Location.class);
      List<Geometry> geometries = queryFactory.select(locationGeometry.geometry).from(locationGeometry)
          .where(locationGeometry.locationId.eq(cont.getId())).fetch();
      GeometryCollection collection = toGeometryCollection(geometries);
      cont.setGeometry(collection);
      List<Integer> fixedLocationIds = queryFactory.select(locationFlids.fixedLocationId).from(locationFlids)
          .where(locationFlids.locationId.eq(cont.getId())).fetch();
      cont.setFixedLocationIds(fixedLocationIds);
    }
    return Optional.ofNullable(cont);
  }

  @Transactional(readOnly = true)
  public List<Location> findByApplication(int applicationId) {
    return findByApplication(applicationId, ALLU_SRID);
  }

  @Transactional(readOnly = true)
  public List<Location> findByApplication(int applicationId, Integer srid) {
    List<Integer> locationIds = queryFactory.select(location.id).from(location).where(location.applicationId.eq(applicationId)).fetch();
    List<Location> locations = locationIds.stream()
        .map(id -> findById(id).get())
        .collect(Collectors.toList());
    locations.forEach(l -> transformCoordinates(l, srid));
    return locations;
  }

  @Transactional(readOnly = true)
  public int findApplicationId(List<Integer> locationIds) {
    return queryFactory.selectDistinct(location.applicationId).from(location).where(location.id.in(locationIds)).fetchOne();
  }

  @Transactional
  public Location insert(Location locationData) {
    transformCoordinates(locationData, ALLU_SRID);
    locationData.setId(null);
    Integer maxLocationKey = queryFactory.select(SQLExpressions.max(location.locationKey))
        .from(location).where(location.applicationId.eq(locationData.getApplicationId())).fetchOne();
    locationData.setLocationKey(Optional.ofNullable(maxLocationKey).orElse(0) + 1);
    locationData.setLocationVersion(1);
    Integer id = queryFactory.insert(location)
        .populate(locationData).set(location.postalAddressId, postalAddressDao.insertIfNotNull(locationData)).executeWithKey(location.id);
    if (id == null) {
      throw new QueryException("Failed to insert record");
    }
    setGeometry(id, locationData.getGeometry());
    setFixedLocationIds(id, locationData.getFixedLocationIds());
    updateApplicationDate(locationData.getApplicationId());
    return findById(id).get();
  }

  private Location update(Location locationData) {
    transformCoordinates(locationData, ALLU_SRID);
    int id = locationData.getId();
    Optional<Location> currentLocationOpt = findById(id);
    if (!currentLocationOpt.isPresent()) {
      throw new NoSuchEntityException("Attempted to update non-existent location", Integer.toString(id));
    }
    Location currentLocation = currentLocationOpt.get();
    Integer deletedPostalAddressId = postalAddressDao.mapAndUpdatePostalAddress(currentLocation, locationData);
    Integer postalAddressId = Optional.ofNullable(currentLocation.getPostalAddress()).map(pAddress -> pAddress.getId()).orElse(null);

    queryFactory
        .update(location)
        .populate(locationData, new ExcludingMapper(WITH_NULL_BINDINGS, Arrays.asList(location.locationKey, location.locationVersion)))
        .set(location.postalAddressId, postalAddressId)
        .where(location.id.eq(id).and(location.applicationId.eq(locationData.getApplicationId()))).execute();

    if (deletedPostalAddressId != null) {
      postalAddressDao.delete(Collections.singletonList(deletedPostalAddressId));
    }
    queryFactory.delete(locationGeometry).where(locationGeometry.locationId.eq(id)).execute();
    setGeometry(id, locationData.getGeometry());
    queryFactory.delete(locationFlids).where(locationFlids.locationId.eq(id)).execute();
    setFixedLocationIds(id, locationData.getFixedLocationIds());
    return findById(id).get();
  }

  @Transactional
  public void deleteById(int locationId) {
    Location deletedLocation = queryFactory.query().select(locationBean).from(location).where(location.id.eq(locationId)).fetchOne();
    if (deletedLocation == null) {
      throw new NoSuchEntityException("Attempted to delete non-existent location", Integer.toString(locationId));
    }
    queryFactory.delete(locationGeometry).where(locationGeometry.locationId.eq(locationId)).execute();
    queryFactory.delete(locationFlids).where(locationFlids.locationId.eq(locationId)).execute();
    queryFactory.delete(location).where(location.id.eq(locationId)).execute();
    if (deletedLocation.getPostalAddress() != null) {
      postalAddressDao.delete(Collections.singletonList(deletedLocation.getPostalAddress().getId()));
    }
    updateApplicationDate(deletedLocation.getApplicationId());
  }

  @Transactional
  public void deleteByApplication(int applicationId) {
    List<Integer> locationIds = queryFactory.select(location.id).from(location).where(location.applicationId.eq(applicationId)).fetch();
    locationIds.forEach(locationId -> {
      deleteById(locationId);
    });
  }

  /**
   * Update application's locations. Goes through the given locations and
   * detects which of them already belong to the application. Those are updated.
   * Locations that don't belong to application are inserted. Locations that
   * were in the application but are not provided are removed.
   *
   * @param applicationId the application's ID
   * @param locations new list of locations for the application.
   * @return the application's locations after the update.
   */
  @Transactional
  public List<Location> updateApplicationLocations(int applicationId, List<Location> locations) {
    // Make sure all new locations have an id (map null to -1, since it's not a
    // valid Database ID).
    locations.stream().filter(l -> l.getId() == null).forEach(l -> l.setId(-1));
    locations.sort((l1, l2) -> l1.getId().compareTo(l2.getId()));
    // Read existing location IDs
    List<Integer> oldLocationIds = queryFactory.select(location.id).from(location)
        .where(location.applicationId.eq(applicationId)).orderBy(location.id.asc()).fetch();

    // walk through the sorted lists and compare IDs.
    Iterator<Integer> oldIter = oldLocationIds.iterator();
    Iterator<Location> newIter = locations.iterator();
    while (oldIter.hasNext() && newIter.hasNext()) {
      Integer oldId = oldIter.next();
      Location newLocation = newIter.next();

      while (!oldId.equals(newLocation.getId())) {
        if (oldId < newLocation.getId()) {
          // Not in new list, remove
          deleteById(oldId);
          if (!oldIter.hasNext())
            break;
          oldId = oldIter.next();
        } else {
          // ID not in old list, add
          insert(newLocation);
          if (!newIter.hasNext())
            break;
          newLocation = newIter.next();
        }
      }
      if (oldId.equals(newLocation.getId())) {
        // ID in both lists, update
        update(newLocation);
      }
    }
    // Handle possible tails
    while (oldIter.hasNext()) {
      // remove
      Integer oldId = oldIter.next();
      deleteById(oldId);
    }
    while (newIter.hasNext()) {
      // add
      Location newLocation = newIter.next();
      insert(newLocation);
    }
    updateApplicationDate(applicationId);
    return findByApplication(applicationId);
  }

  /**
   * Get the list of all active fixed locations
   */
  @Transactional(readOnly = true)
  public List<FixedLocation> getFixedLocationList() {
    return findFixedLocations(Collections.emptyList());
  }

  /**
   * Find a fixed location by ID
   * @param id
   */
  @Transactional(readOnly = true)
  public Optional<FixedLocation> findFixedLocation(int id) {
    return findFixedLocations(Collections.singletonList(id)).stream().findFirst();
  }

  /*
   *  Find fixed locations: either by ids or all active
   */
  private List<FixedLocation> findFixedLocations(List<Integer> ids)
  {
    final Predicate locationPredicate =
        (ids.isEmpty()) ? fixedLocation.isActive.eq(true)
            : fixedLocation.id.in(ids);

    List<FixedLocation> fxs = queryFactory
        .select(bean(FixedLocation.class, fixedLocation.id, locationArea.name.as("area"), fixedLocation.section,
            fixedLocation.applicationKind, fixedLocation.geometry))
        .from(fixedLocation).innerJoin(locationArea).on(fixedLocation.areaId.eq(locationArea.id))
        .where(locationPredicate)
        .fetch();

    return fxs.stream()
            .map(fx -> {
              GeometryCollection gc = Optional.ofNullable(fx.getGeometry())
                .map(geometry -> toGeometryCollectionIfNeeded(geometry))
                .orElse(GeometryCollection.createEmpty());

              fx.setGeometry(gc);
              return fx;
            }).collect(Collectors.toList());
  }

  /**
   * Get all defined fixed location areas as a list
   *
   * @return list of FixedLocationAreas
   */
  @Transactional(readOnly = true)
  public List<FixedLocationArea> getFixedLocationAreas(Integer srid) {

    List<FixedLocationArea> areas = queryFactory.from(locationArea, fixedLocation)
        .where(fixedLocation.areaId.eq(locationArea.id).and(fixedLocation.isActive.eq(true)))
        .transform(groupBy(locationArea.id).as(
            Projections.constructor(FixedLocationArea.class, locationArea.id, locationArea.name,
                list(bean(FixedLocationSection.class, fixedLocation.all())))))
        .entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());

    // Force all section geometries to be geometry collections:
    areas.forEach(fla -> fla.getSections().forEach(fls -> fls.setGeometry(Optional.ofNullable(fls.getGeometry())
                .map(geometry -> toGeometryCollectionIfNeeded(geometry))
        .orElse(GeometryCollection.createEmpty()))));
    areas.forEach(a -> transformCoordinates(a, srid));

    return areas;
  }

  @Transactional(readOnly = true)
  public List<CityDistrict> getCityDistrictList() {
    return queryFactory.select(bean(CityDistrict.class, QCityDistrict.cityDistrict.all()))
        .from(QCityDistrict.cityDistrict).fetch();
  }

  private void setGeometry(int locationId, Geometry geometry) {
    double area = 0.0;
    if (geometry != null) {
      if (geometry instanceof GeometryCollection) {
        GeometryCollection gc = removeOverlaps((GeometryCollection) geometry);
        List<Geometry> flat = new ArrayList<>();
        flatten(gc, flat);
        flat.forEach(
            geo -> queryFactory.insert(locationGeometry).columns(locationGeometry.locationId, locationGeometry.geometry)
            .values(locationId, geo).execute());
      } else {
        queryFactory.insert(locationGeometry).columns(locationGeometry.locationId, locationGeometry.geometry)
            .values(locationId, geometry)
            .execute();
      }
      area = getArea(locationId);
    }
    // store geometry's area and district id to the location
    queryFactory.update(location).set(location.area, area)
        .set(location.cityDistrictId, findDistrict(locationId).orElse(null))
        .where(location.id.eq(locationId)).execute();
  }

  private GeometryCollection toGeometryCollectionIfNeeded(Geometry geometry) {
    if (geometry instanceof GeometryCollection)
      return (GeometryCollection) geometry;
    return toGeometryCollection(Arrays.asList(geometry));
  }

  private GeometryCollection toGeometryCollection(List<Geometry> geometries) {
    Geometry[] geoArray = geometries.toArray(new Geometry[geometries.size()]);
    return new GeometryCollection(geoArray);
  }

  /*
   * flatten the geometries into destination
   */
  private void flatten(Geometry geometry, Collection<Geometry> destination) {
    if (geometry instanceof GeometryCollection) {
      ((GeometryCollection) geometry).forEach(g -> flatten(g, destination));
    } else {
      destination.add(geometry);
    }
  }

  private void setFixedLocationIds(int locationId, List<Integer> fixedLocationIds) {
    if (fixedLocationIds != null) {
      fixedLocationIds.forEach(flid -> queryFactory.insert(locationFlids)
        .columns(locationFlids.locationId, locationFlids.fixedLocationId).values(locationId, flid).execute());
    }
  }

  private double getArea(int locationId) {
    SQLQuery<Double> query = queryFactory.select(locationGeometry.geometry.asPolygon().area().sum())
        .from(locationGeometry).where(locationGeometry.locationId.eq(locationId));
    logger.debug("Executing query {}", query.getSQL().getSQL());
    Optional<Double> area = Optional.ofNullable(query.fetchOne());
    logger.debug("Area for location ID {} is {} m2", locationId, area.orElse(0.0));
    return area.orElse(0.0);
  }

  private GeometryCollection removeOverlaps(GeometryCollection coll) {
    logger.debug("Removing overlaps from {}", coll.asText());
    LinkedList<Geometry> geomIn = new LinkedList<>();
    coll.forEach(geom -> geomIn.add(geom));
    Vector<Geometry> geomOut = new Vector<>();
    while (geomIn.size() > 0) {
      // Pop the first element
      Geometry candidate = geomIn.removeFirst();
      // Iterate through all the rest and remove all overlapping ones:
      Iterator<Geometry> iter = geomIn.iterator();
      while (iter.hasNext()) {
        Geometry otherOne = iter.next();
        if (candidate.intersects(otherOne)) {
          logger.debug("Combining overlapping geometries {} and {}", candidate.asText(), otherOne.asText());
          candidate = candidate.union(otherOne);
          logger.debug("Result of combination is {}", candidate);
          iter.remove();
        }
      }
      geomOut.addElement(candidate);
    }
    GeometryCollection collOut = new GeometryCollection(geomOut.toArray(new Geometry[geomOut.size()]));
    logger.debug("Resulting geometries: {}", collOut.asText());
    return collOut;
  }

  /*
   * Find location's district and return its ID
   */
  private Optional<Integer> findDistrict(int locationId) {

    // The following SQL finds the district that has the largest overlap area
    // with the application when application's location_id is known:

    // with appGeom as
    // (select st_union(g.geometry) as geom
    // from allu.location_geometry as g where g.location_id = {0})
    // select st_area(ST_Intersection(d.geometry, (select geom from appGeom)))
    // as area,
    // d.id from allu.city_district as d
    // where ST_Intersects(d.geometry, (select geom from appGeom)) order by area
    // desc
    // limit 1

    // This code creates the query using querydsl:

    // alias handle for the "WITH" clause:
    PathBuilder<Tuple> appGeom = new PathBuilder<>(Tuple.class, "appGeom");
    // reference to the table allu.location_geometry as "lg":
    QLocationGeometry geo = new QLocationGeometry("lg", "allu", "location_geometry");

    // The SQL query for the "with" clause:
    Optional<Tuple> result = Optional
        .ofNullable(
            queryFactory.query()
                .with(appGeom,
                    SQLExpressions
                        .select(Expressions.simpleTemplate(Geometry.class, "st_union(lg.geometry)").as("geom")).from(
                            geo)
                        .where(
                            geo.locationId.eq(locationId)))
                .select(
                    Expressions
                        .simpleTemplate(Float.class, "st_area({0})",
                            cityDistrict.geometry.intersection(
                                SQLExpressions.select(Expressions.path(Geometry.class, "geom")).from(appGeom)))
                        .as("area"),
                    cityDistrict.id)
                .from(cityDistrict)
                .where(cityDistrict.geometry
                    .intersects(SQLExpressions.select(Expressions.path(Geometry.class, "geom")).from(appGeom)))
                .orderBy(new OrderSpecifier<>(Order.DESC, Expressions.stringPath("area"))).fetchFirst());

    return result.map(r -> r.get(1, Integer.class));
  }

  private void updateApplicationDate(int applicationId) {
    Tuple startEndDate =
        queryFactory.query().select(SQLExpressions.min(location.startTime), SQLExpressions.max(location.endTime))
            .from(location).where(location.applicationId.eq(applicationId)).fetchOne();
    if (startEndDate != null) {
      // update the application start and end time only if there are locations available for application. Otherwise just leave them be
      ZonedDateTime startTime = startEndDate.get(SQLExpressions.min(location.startTime));
      ZonedDateTime endTime = startEndDate.get(SQLExpressions.max(location.endTime));
      queryFactory
          .update(application)
          .set(application.startTime, startTime)
          .set(application.endTime, endTime)
          .where(application.id.eq(applicationId))
          .execute();
    }
  }

  /**
   * Get the payment class for a location
   *
   * @param id location's DB id
   * @return the payment class (1, 2, or 3)
   */
  public int getPaymentClass(Integer id) {
    Integer paymentClass = queryFactory.select(paymentClass1.paymentClass).from(paymentClass1)
        .where(paymentClass1.geometry.intersects(
            SQLExpressions.select(locationGeometry.geometry).from(locationGeometry)
                .where(locationGeometry.locationId.eq(id))))
        .orderBy(paymentClass1.paymentClass.asc()).fetchFirst();
    return Optional.ofNullable(paymentClass).orElse(3); // Payment class undefined -> default to lowest
  }

  private void transformCoordinates(Location locationData, Integer targetSrId) {
    locationData.setGeometry(coordinateTransformation.transformCoordinates(locationData.getGeometry(), targetSrId));
  }

  private void transformCoordinates(FixedLocationArea area, Integer targetSrId) {
    area.getSections().forEach(s -> s.setGeometry(coordinateTransformation.transformCoordinates(s.getGeometry(), targetSrId)));
  }
}
