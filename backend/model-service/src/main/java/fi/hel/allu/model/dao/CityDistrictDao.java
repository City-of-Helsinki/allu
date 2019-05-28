package fi.hel.allu.model.dao;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import fi.hel.allu.model.domain.CityDistrict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QCityDistrict.cityDistrict;

@Repository
public class CityDistrictDao {

  private static final Logger logger = LoggerFactory.getLogger(CityDistrictDao.class);

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<CityDistrict> cityDistrictQBean = bean(CityDistrict.class, cityDistrict.all());

  @Transactional
  public void upsert(List<CityDistrict> cityDistricts) {
    Set<Integer> existing = getCityDistricts().stream()
        .map(district -> district.getDistrictId())
        .collect(Collectors.toSet());

    List<CityDistrict> newDistricts = new ArrayList<>();
    List<CityDistrict> existingDistricts  = new ArrayList<>();

    cityDistricts.forEach(cd -> {
      if (existing.contains(cd.getDistrictId())) {
        existingDistricts.add(cd);
      } else {
        newDistricts.add(cd);
      }
    });

    Long inserted = insert(newDistricts);
    logger.info(String.format("Inserted %d city districts", inserted));

    Long updated = update(existingDistricts);
    logger.info(String.format("Updated %d city districts", updated));
  }

  @Transactional(readOnly = true)
  public List<CityDistrict> getCityDistricts() {
    return queryFactory.select(cityDistrictQBean).from(cityDistrict).fetch();
  }

  private long update(List<CityDistrict> cityDistricts) {
    if (cityDistricts.isEmpty()) {
      return 0;
    }
    SQLUpdateClause update = queryFactory.update(cityDistrict);
    cityDistricts.forEach(cd -> update.populate(cd).where(cityDistrict.districtId.eq(cd.getDistrictId())).addBatch());
    return update.execute();
  }

  private long insert(List<CityDistrict> cityDistricts) {
    if (cityDistricts.isEmpty()) {
      return 0;
    }
    SQLInsertClause insert = queryFactory.insert(cityDistrict);
    cityDistricts.forEach(cd -> insert.populate(cd).addBatch());
    return insert.execute();
  }
}
