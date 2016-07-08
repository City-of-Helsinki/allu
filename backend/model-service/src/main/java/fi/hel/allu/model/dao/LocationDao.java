package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QGeometry.geometry1;
import static fi.hel.allu.QLocation.location;

import java.util.List;
import java.util.Optional;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Location;

@Repository
public class LocationDao {
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
    queryFactory.delete(geometry1).where(geometry1.locationId.eq(id));
    setGeometry(id, locationData.getGeometry());
    return findById(id).get();
  }

  private void setGeometry(int locationId, Geometry geometry) {
    if (geometry != null) {
      if (geometry instanceof GeometryCollection) {
        GeometryCollection gc = (GeometryCollection) geometry;
        gc.forEach(geo -> queryFactory.insert(geometry1).columns(geometry1.locationId, geometry1.geometry)
            .values(locationId, geo).execute());
      } else {
        queryFactory.insert(geometry1).columns(geometry1.locationId, geometry1.geometry).values(locationId, geometry)
            .execute();
      }
    }
  }
}
