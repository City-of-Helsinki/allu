package fi.hel.allu.model.dao;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.model.pricing.OutdoorPricingConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QCityDistrict.cityDistrict;
import static fi.hel.allu.QOutdoorPricing.outdoorPricing;

@Repository
public class PricingDao {
  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<OutdoorPricingConfiguration> outdoorPricingBean = bean(OutdoorPricingConfiguration.class, outdoorPricing.all());

  @Transactional(readOnly = true)
  public Optional<OutdoorPricingConfiguration> findByFixedLocationAndNature(int fixedLocationId, EventNature nature) {
    OutdoorPricingConfiguration pc = queryFactory.select(outdoorPricingBean).from(outdoorPricing)
        .where(outdoorPricing.fixedLocationId.eq(fixedLocationId).and(outdoorPricing.nature.eq(nature.toString())))
        .fetchFirst();
    return Optional.ofNullable(pc);
  }

  @Transactional(readOnly = true)
  public Optional<OutdoorPricingConfiguration> findByDisctrictAndNature(int cityDistrictId, EventNature nature) {
    OutdoorPricingConfiguration pc = queryFactory.select(outdoorPricingBean).from(outdoorPricing).innerJoin(cityDistrict)
        .on(outdoorPricing.zoneId.eq(cityDistrict.zoneId))
        .where(cityDistrict.id.eq(cityDistrictId).and(outdoorPricing.nature.eq(nature.toString()))).fetchFirst();
    return Optional.ofNullable(pc);
  }
}
