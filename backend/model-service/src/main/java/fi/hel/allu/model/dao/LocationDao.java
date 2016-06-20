package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QLocation.location;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Location;

public class LocationDao {
  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<Location> locationBean = bean(Location.class, location.all());

  @Transactional(readOnly = true)
  public Optional<Location> findById(int id) {
    Location cont = queryFactory.select(locationBean).from(location).where(location.id.eq(id)).fetchOne();
    return Optional.ofNullable(cont);
  }

  @Transactional
  public Location insert(Location locationData) {
    Integer id = queryFactory.insert(location).populate(locationData).executeWithKey(location.id);
    if (id == null) {
      throw new QueryException("Failed to insert record");
    }
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
    return findById(id).get();
  }
}
