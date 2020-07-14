package fi.hel.allu.model.dao;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import fi.hel.allu.model.coordinates.GeometrySimplification;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryCollection;
import org.geolatte.geom.GeometryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryException;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.SimpleTemplate;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.QCityDistrict;
import fi.hel.allu.QLocationGeometry;
import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.geometry.Constants;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.common.PostalAddressUtil;
import fi.hel.allu.model.coordinates.CoordinateTransformation;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.querydsl.ExcludingMapper;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QCityDistrict.cityDistrict;
import static fi.hel.allu.QCustomerLocationValidity.customerLocationValidity;
import static fi.hel.allu.QFixedLocation.fixedLocation;
import static fi.hel.allu.QLocation.location;
import static fi.hel.allu.QLocationArea.locationArea;
import static fi.hel.allu.QLocationFlids.locationFlids;
import static fi.hel.allu.QLocationGeometry.locationGeometry;
import static fi.hel.allu.QPostalAddress.postalAddress;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

@Repository
public class LocationDao {

  private static final Logger logger = LoggerFactory.getLogger(LocationDao.class);
  public static final List<Path<?>> UPDATE_READ_ONLY_FIELDS = Arrays.asList(location.paymentTariff);
  private static final int TUPLE_LOCATION = 0;
  private static final int TUPLE_CUSTOMER_LOCATION_VALIDITY = 2;
  private static final double LINE_BUFFER = 0.5;

  @Autowired
  private SQLQueryFactory queryFactory;
  @Autowired
  private PostalAddressDao postalAddressDao;
  @Autowired
  private CoordinateTransformation coordinateTransformation;
  @Autowired
  private GeometrySimplification geometrySimplification;

  final QBean<Location> locationBean = bean(Location.class, location.all());
  final QBean<PostalAddress> postalAddressBean = bean(PostalAddress.class, postalAddress.all());
  final QBean<CustomerLocationValidity> customerLocationValidityBean = bean(CustomerLocationValidity.class, customerLocationValidity.all());
  final QBean<FixedLocationArea> locationAreaBean = bean(FixedLocationArea.class, locationArea.all());

  @Transactional(readOnly = true)
  public Optional<Location> findById(int id) {
    final Tuple locationPostalAddressCustomerLocationValidity = queryFactory
        .select(locationBean, postalAddressBean, customerLocationValidityBean)
        .from(location)
        .leftJoin(postalAddress).on(location.postalAddressId.eq(postalAddress.id))
        .leftJoin(customerLocationValidity).on(customerLocationValidity.locationId.eq(location.id))
        .where(location.id.eq(id)).fetchOne();
    Location cont = null;
    if (locationPostalAddressCustomerLocationValidity != null) {
      cont = mapCustomerLocationValidity(
              PostalAddressUtil.mapPostalAddress(locationPostalAddressCustomerLocationValidity))
            .get(TUPLE_LOCATION, Location.class);
      List<Geometry> geometries = queryFactory.select(locationGeometry.geometry).from(locationGeometry)
          .where(locationGeometry.locationId.eq(cont.getId())).fetch();
      GeometryCollection collection = GeometryUtil.toGeometryCollection(geometries);
      cont.setGeometry(collection);
      List<Integer> fixedLocationIds = queryFactory.select(locationFlids.fixedLocationId).from(locationFlids)
          .where(locationFlids.locationId.eq(cont.getId())).fetch();
      cont.setFixedLocationIds(fixedLocationIds);
    }
    return Optional.ofNullable(cont);
  }

  @Transactional(readOnly = true)
  public List<Location> findByIds(List<Integer> ids) {
    return ids.stream()
      .map(id -> findById(id).get())
      .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<Location> findByApplication(int applicationId) {
    return findByApplication(applicationId, Constants.ALLU_DEFAULT_SRID);
  }

  @Transactional(readOnly = true)
  public List<Location> findByApplication(int applicationId, Integer srId) {
    List<Integer> locationIds = queryFactory.select(location.id).from(location).where(location.applicationId.eq(applicationId)).fetch();
    List<Location> locations = locationIds.stream()
        .map(id -> findById(id).get())
        .collect(Collectors.toList());
    locations.forEach(l -> transformAndCleanupCoordinates(l, srId));
    return locations;
  }

  @Transactional(readOnly = true)
  public int findApplicationId(List<Integer> locationIds) {
    return queryFactory.selectDistinct(location.applicationId).from(location).where(location.id.in(locationIds)).fetchOne();
  }

  @Transactional
  public Location insert(Location locationData) {
    transformCoordinates(locationData, Constants.ALLU_DEFAULT_SRID);
    locationData.setId(null);
    Integer maxLocationKey = queryFactory.select(SQLExpressions.max(location.locationKey))
        .from(location).where(location.applicationId.eq(locationData.getApplicationId())).fetchOne();
    locationData.setLocationKey(Optional.ofNullable(maxLocationKey).orElse(0) + 1);
    locationData.setLocationVersion(1);
    Integer id = queryFactory.insert(location)
        .populate(locationData).set(location.postalAddressId, postalAddressDao.insertIfNotNull(locationData)).executeWithKey(location.id);
    if (id == null) {
      throw new QueryException("location.insert.failed");
    }
    setGeometry(id, locationData.getGeometry());
    setFixedLocationIds(id, locationData.getFixedLocationIds());
    updateApplicationDate(locationData.getApplicationId());
    return findById(id).get();
  }

  @Transactional(readOnly = true)
  public boolean isValidGeometry(Geometry geometry) {
    return queryFactory
        .select(Expressions.simpleTemplate(Boolean.class, "st_isvalid({0})", geometry))
        .fetchFirst();
  }

  @Transactional
  public void setCustomerLocationValidity(Integer locationId, ApplicationDateReport dateReport) {
    if (queryFactory.select(customerLocationValidityBean).from(customerLocationValidity)
        .where(customerLocationValidity.locationId.eq(locationId)).fetchCount() > 0) {
      queryFactory
          .update(customerLocationValidity)
          .set(customerLocationValidity.startTime, dateReport.getReportedDate())
          .set(customerLocationValidity.endTime, dateReport.getReportedEndDate())
          .set(customerLocationValidity.reportingTime, dateReport.getReportingDate())
          .where(customerLocationValidity.locationId.eq(locationId))
          .execute();
    } else {
      queryFactory
          .insert(customerLocationValidity)
          .columns(customerLocationValidity.locationId, customerLocationValidity.startTime,
              customerLocationValidity.endTime, customerLocationValidity.reportingTime)
          .values(locationId, dateReport.getReportedDate(), dateReport.getReportedEndDate(), dateReport.getReportingDate())
          .execute();
    }
  }

  public Location update(Location locationData) {
    transformCoordinates(locationData, Constants.ALLU_DEFAULT_SRID);
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
    // Read existing location IDs
    List<Integer> oldLocationIds = queryFactory.select(location.id).from(location)
        .where(location.applicationId.eq(applicationId)).fetch();

    Set<Location> locationsToUpdate = locations.stream().filter(l -> isExistingLocation(l, oldLocationIds)).collect(Collectors.toSet());
    Set<Integer> locationIdsToUpdate = locationsToUpdate.stream().map(Location::getId).collect(Collectors.toSet());
    Set<Location> locationsToAdd = locations.stream().filter(l -> !locationIdsToUpdate.contains(l.getId())).collect(Collectors.toSet());
    Set<Integer> locationIdsToDelete = oldLocationIds.stream().filter(i -> !locationIdsToUpdate.contains(i)).collect(Collectors.toSet());

    locationIdsToDelete.forEach(i -> deleteById(i));
    locationsToUpdate.forEach(l -> update(l));
    locationsToAdd.forEach(l -> insert(l));

    updateApplicationDate(applicationId);
    return findByApplication(applicationId);
  }

  public boolean isExistingLocation(Location l, List<Integer> oldLocationIds) {
    return l.getId() != null && oldLocationIds.contains(l.getId());
  }

  /**
   * Get the list of all active fixed locations
   */
  @Transactional(readOnly = true)
  public List<FixedLocation> getActiveFixedLocations(ApplicationKind kind, Integer srId) {
    List<FixedLocation> fixedLocations = findFixedLocations(Collections.emptyList(), kind);
    transformCoordinates(fixedLocations, srId);
    return fixedLocations;
  }

  @Transactional(readOnly = true)
  public List<FixedLocation> getAllFixedLocation(ApplicationKind kind, Integer srId) {
    Predicate hasKind = Optional.ofNullable(kind).map(k -> fixedLocation.applicationKind.eq(kind)).orElse(null);
    List<FixedLocation> fixedLocations = findFixedLocationsWhere(hasKind);
    transformCoordinates(fixedLocations, srId);
    return fixedLocations;
  }

  @Transactional(readOnly = true)
  public FixedLocation findFixedLocation(Integer id, Integer srId) {
    FixedLocation fixedLocation = findFixedLocation(id).orElseThrow(() -> new NoSuchEntityException("fixedlocation.notfound"));
    transformCoordinates(Collections.singletonList(fixedLocation), srId);
    return fixedLocation;
  }

  /**
   * Find a fixed location by ID
   * @param id
   */
  @Transactional(readOnly = true)
  public Optional<FixedLocation> findFixedLocation(int id) {
    return findFixedLocations(Collections.singletonList(id), null).stream().findFirst();
  }

  /*
   *  Find fixed locations: either by ids or all active
   */
  private List<FixedLocation> findFixedLocations(List<Integer> ids, ApplicationKind kind)
  {
    final Predicate locationPredicate =
        (ids.isEmpty()) ?
            (kind != null ? activeFixedLocationWithKind(kind) : activeFixedLocation())
            : fixedLocation.id.in(ids);

    return findFixedLocationsWhere(locationPredicate);
  }

  private List<FixedLocation> findFixedLocationsWhere(Predicate predicate) {
    List<FixedLocation> fxs = queryFactory
        .select(bean(FixedLocation.class, fixedLocation.id, locationArea.name.as("area"), fixedLocation.section,
            fixedLocation.applicationKind, fixedLocation.geometry, fixedLocation.isActive.as("active")))
        .from(fixedLocation).innerJoin(locationArea).on(fixedLocation.areaId.eq(locationArea.id))
        .where(predicate)
        .fetch();

    return fxs.stream().map(this::mapGeometryIfNeeded).collect(Collectors.toList());
  }

  private FixedLocation mapGeometryIfNeeded(FixedLocation fixedLocation) {
    GeometryCollection gc = Optional.ofNullable(fixedLocation.getGeometry())
        .map(geometry -> toGeometryCollectionIfNeeded(geometry))
        .orElse(GeometryCollection.createEmpty());

    fixedLocation.setGeometry(gc);
    return fixedLocation;
  }

  private BooleanExpression activeFixedLocationWithKind(ApplicationKind kind) {
    return activeFixedLocation().and(fixedLocation.applicationKind.eq(kind));
  }

  private BooleanExpression activeFixedLocation() {
    return fixedLocation.isActive.eq(true);
  }

  @Transactional(readOnly = true)
  public List<CityDistrict> getCityDistrictList() {
    return queryFactory.select(bean(CityDistrict.class, QCityDistrict.cityDistrict.all()))
        .from(QCityDistrict.cityDistrict).fetch();
  }

  @Transactional(readOnly = true)
  public Optional<CityDistrict> getCityDistrictById(Integer id) {
    return Optional.ofNullable(
        queryFactory.select(bean(CityDistrict.class, QCityDistrict.cityDistrict.all()))
        .from(QCityDistrict.cityDistrict).where(cityDistrict.id.eq(id)).fetchOne()
    );
  }

  @Transactional(readOnly = true)
  public List<FixedLocationArea> getFixedLocationAreas() {
    return queryFactory
            .select(locationAreaBean)
            .from(locationArea)
            .fetch();
  }

  private void setGeometry(int locationId, Geometry geometry) {
    double area = 0.0;
    if (geometry != null) {
      if (geometry instanceof GeometryCollection) {
        GeometryCollection gc = bufferLineStrings((GeometryCollection) geometry);
        gc = removeOverlaps(gc);
        List<Geometry> flat = new ArrayList<>();
        GeometryUtil.flatten(gc, flat);
        flat.stream()
          .map(g -> removeRepeatedPoints(g))
          .forEach(
            geo -> queryFactory.insert(locationGeometry).columns(locationGeometry.locationId, locationGeometry.geometry)
            .values(locationId, geo).execute());
      } else {
        geometry = bufferLineString(geometry);
        geometry = removeRepeatedPoints(geometry);
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
    if (geometry instanceof GeometryCollection) {
      return (GeometryCollection) geometry;
    }
    return GeometryUtil.toGeometryCollection(Arrays.asList(geometry));
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

  private GeometryCollection bufferLineStrings(GeometryCollection geometryCollection) {
    List<Geometry> result = new ArrayList<>();
    geometryCollection.forEach(g -> {
        result.add(bufferLineString(g));
    });
    return new GeometryCollection(result.toArray(new Geometry[result.size()]));
  }

  private Geometry bufferLineString(Geometry geometry) {
    Geometry resultGeometry = geometry;
    if (geometry.getGeometryType() == GeometryType.LINE_STRING || geometry.getGeometryType() == GeometryType.MULTI_LINE_STRING) {
      resultGeometry = queryFactory
          .select(Expressions.simpleTemplate(Geometry.class, "ST_Buffer({0}, {1}, 'endcap=flat')", geometry, LINE_BUFFER))
          .fetchFirst();
    }
    return resultGeometry;
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
                        .select(geometryUnion().as("geom")).from(
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

  /**
   * Buffers line strings and points to polygons before union (if there's more than
   * one type of geometries for location, union returns geometry collection
   * which cannot be used with ST_Intersects)
   */
  protected SimpleTemplate<Geometry> geometryUnion() {
    return Expressions.simpleTemplate(Geometry.class,
          "ST_Union("
        + "case "
        + " when st_geometrytype(lg.geometry) in ('ST_LineString', 'ST_Point') then st_buffer(lg.geometry, 0.5, 4)"
        + " else lg.geometry "
        + "end"
        + ")");
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

  private void transformAndCleanupCoordinates(Location locationData, Integer targetSrId) {
    transformCoordinates(locationData, targetSrId);
    removeRepeatedPoints(locationData);
  }

  private void removeRepeatedPoints(Location locationData) {
    if (locationData.getGeometry() != null) {
      Geometry geometry = removeRepeatedPoints(locationData.getGeometry());
      locationData.setGeometry(geometry);
    }
  }

  private Geometry removeRepeatedPoints(Geometry geometry) {
    return queryFactory
        .select(Expressions.simpleTemplate(Geometry.class, "ST_MakeValid(ST_RemoveRepeatedPoints({0}, 0.2))", geometry))
        .fetchFirst();
  }

  private void transformCoordinates(Location locationData, Integer targetSrId) {
    locationData.setGeometry(coordinateTransformation.transformCoordinates(locationData.getGeometry(), targetSrId));
  }

  private void transformCoordinates(List<FixedLocation> fixedLocations, Integer targetSrId) {
    fixedLocations.forEach(f -> f.setGeometry(coordinateTransformation.transformCoordinates(f.getGeometry(), targetSrId)));
  }

  @Transactional(readOnly = true)
  public Geometry transformCoordinates(Geometry geometry, Integer targetSrid) {
     return coordinateTransformation.transformCoordinates(geometry, targetSrid);
  }

  @Transactional(readOnly = true)
  public Geometry simplifyGeometry(Geometry geometry, Integer tolerance) {
     return geometrySimplification.simplifyGeometry(geometry, tolerance);
  }

  private Tuple mapCustomerLocationValidity(Tuple item) {
    final CustomerLocationValidity validity = item.get(TUPLE_CUSTOMER_LOCATION_VALIDITY, CustomerLocationValidity.class);
    if (validity != null && validity.getId() != null) {
      item.get(TUPLE_LOCATION, Location.class).setCustomerStartTime(validity.getStartTime());
      item.get(TUPLE_LOCATION, Location.class).setCustomerEndTime(validity.getEndTime());
      item.get(TUPLE_LOCATION, Location.class).setCustomerReportingTime(validity.getReportingTime());
    }
    return item;
  }

  @Transactional(readOnly = true)
  public String getCityDistrictNameById(Integer id) {
    return queryFactory.select(cityDistrict.name)
        .from(cityDistrict).where(cityDistrict.id.eq(id)).fetchFirst();
  }
}
