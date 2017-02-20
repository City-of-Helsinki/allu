package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;
import fi.hel.allu.QCityDistrict;
import fi.hel.allu.QLocationGeometry;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.CityDistrict;
import fi.hel.allu.model.domain.FixedLocation;
import fi.hel.allu.model.domain.Location;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QCityDistrict.cityDistrict;
import static fi.hel.allu.QFixedLocation.fixedLocation;
import static fi.hel.allu.QLocation.location;
import static fi.hel.allu.QLocationFlids.locationFlids;
import static fi.hel.allu.QLocationGeometry.locationGeometry;

@Repository
public class LocationDao {
  private static final Logger logger = LoggerFactory.getLogger(LocationDao.class);

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<Location> locationBean = bean(Location.class, location.all());
  final QBean<FixedLocation> fixedLocationBean = bean(FixedLocation.class, fixedLocation.all());

  @Transactional(readOnly = true)
  public Optional<Location> findById(int id) {
    Location cont = queryFactory.select(locationBean).from(location).where(location.id.eq(id)).fetchOne();
    if (cont != null) {
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

  @Transactional
  public List<Location> findByApplication(int applicationId) {
    List<Integer> locationIds = queryFactory.select(location.id).from(location).where(location.applicationId.eq(applicationId)).fetch();
    return locationIds.stream().map(id -> findById(id).get()).collect(Collectors.toList());
  }

  @Transactional
  public Location insert(Location locationData) {
    Integer id = queryFactory.insert(location).populate(locationData).executeWithKey(location.id);
    if (id == null) {
      throw new QueryException("Failed to insert record");
    }
    setGeometry(id, locationData.getGeometry());
    setFixedLocationIds(id, locationData.getFixedLocationIds());
    return findById(id).get();
  }

  @Transactional
  public Location update(int id, Location locationData) {
    locationData.setId(id);
    long changed = queryFactory.update(location).populate(locationData, DefaultMapper.WITH_NULL_BINDINGS)
        .where(location.id.eq(id)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update the record", Integer.toString(id));
    }
    queryFactory.delete(locationGeometry).where(locationGeometry.locationId.eq(id)).execute();
    setGeometry(id, locationData.getGeometry());
    queryFactory.delete(locationFlids).where(locationFlids.locationId.eq(id)).execute();
    setFixedLocationIds(id, locationData.getFixedLocationIds());
    return findById(id).get();
  }

  @Transactional
  public void deleteByApplication(int applicationId) {
    List<Integer> locationIds = queryFactory.select(location.id).from(location).where(location.applicationId.eq(applicationId)).fetch();
    locationIds.forEach(locationId -> {
      queryFactory.delete(locationGeometry).where(locationGeometry.locationId.eq(locationId)).execute();
      queryFactory.delete(locationFlids).where(locationFlids.locationId.eq(locationId)).execute();
      queryFactory.delete(location).where(location.id.eq(locationId)).execute();
    });
  }

  @Transactional(readOnly = true)
  public List<FixedLocation> getFixedLocationList() {
    List<FixedLocation> fxs = queryFactory
        .select(fixedLocationBean)
        .from(fixedLocation)
        .where(fixedLocation.isActive.eq(true))
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
        gc.forEach(
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

}
