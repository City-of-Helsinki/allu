package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QGeometry.geometry1;
import static fi.hel.allu.QLocation.location;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Location;

@Repository
public class LocationDao {
  private static final Logger logger = LoggerFactory.getLogger(LocationDao.class);

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<Location> locationBean = bean(Location.class, location.all());

  @Transactional(readOnly = true)
  public Optional<Location> findById(int id) {
    Location cont = queryFactory.select(locationBean).from(location).where(location.id.eq(id)).fetchOne();
    if (cont != null) {
      List<Geometry> geometries = queryFactory.select(geometry1.geometry).from(geometry1)
          .where(geometry1.locationId.eq(cont.getId())).fetch();
      Geometry[] geoArray = geometries.toArray(new Geometry[geometries.size()]);
      GeometryCollection collection = new GeometryCollection(geoArray);
      cont.setGeometry(collection);
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
    queryFactory.delete(geometry1).where(geometry1.locationId.eq(id)).execute();
    setGeometry(id, locationData.getGeometry());
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
      queryFactory.delete(geometry1).where(geometry1.locationId.eq(locationId)).execute();
      queryFactory.delete(location).where(location.id.eq(locationId)).execute();
    }
  }

  private void setGeometry(int locationId, Geometry geometry) {
    double area = 0.0;
    if (geometry != null) {
      if (geometry instanceof GeometryCollection) {
        GeometryCollection gc = removeOverlaps((GeometryCollection) geometry);
        gc.forEach(geo -> queryFactory.insert(geometry1).columns(geometry1.locationId, geometry1.geometry)
            .values(locationId, geo).execute());
      } else {
        queryFactory.insert(geometry1).columns(geometry1.locationId, geometry1.geometry).values(locationId, geometry)
            .execute();
      }
      area = getArea(locationId);
    }
    // store geometry's area to the location
    queryFactory.update(location).set(location.area, area).where(location.id.eq(locationId)).execute();
  }

  private double getArea(int locationId) {
    SQLQuery<Double> query = queryFactory.select(geometry1.geometry.asPolygon().area().sum()).from(geometry1)
        .where(geometry1.locationId.eq(locationId));
    logger.debug("Executing query {}", query.getSQL().getSQL());
    Optional<Double> area = Optional.of(query.fetchOne());
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
