package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;

import fi.hel.allu.common.exception.NoSuchEntityException;
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
import static fi.hel.allu.QApplication.application;
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
    Integer locationId = queryFactory.select(application.locationId).from(application)
        .where(application.id.eq(applicationId)).fetchOne();
    if (locationId == null) {
      throw new NoSuchEntityException("Can't find location for application", Integer.toString(applicationId));
    } else {
      queryFactory.update(application).setNull(application.locationId).where(application.id.eq(applicationId))
          .execute();
      queryFactory.delete(locationGeometry).where(locationGeometry.locationId.eq(locationId)).execute();
      queryFactory.delete(locationFlids).where(locationFlids.locationId.eq(locationId)).execute();
      queryFactory.delete(location).where(location.id.eq(locationId)).execute();
    }
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
    // store geometry's area to the location
    queryFactory.update(location).set(location.area, area).where(location.id.eq(locationId)).execute();
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
}
