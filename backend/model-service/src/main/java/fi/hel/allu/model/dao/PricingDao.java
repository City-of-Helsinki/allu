package fi.hel.allu.model.dao;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.model.pricing.PricingConfiguration;

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

  final QBean<PricingConfiguration> pricingBean = bean(PricingConfiguration.class, outdoorPricing.all());

  @Transactional(readOnly = true)
  public Optional<PricingConfiguration> findByFixedLocationAndNature(int fixedLocationId, EventNature nature) {
    PricingConfiguration pc = queryFactory.select(pricingBean).from(outdoorPricing)
        .where(outdoorPricing.fixedLocationId.eq(fixedLocationId).and(outdoorPricing.nature.eq(nature.toString())))
        .fetchFirst();
    return Optional.ofNullable(pc);
  }

  @Transactional(readOnly = true)
  public Optional<PricingConfiguration> findByDisctrictAndNature(int districtId, EventNature nature) {
    PricingConfiguration pc = queryFactory.select(pricingBean).from(outdoorPricing).innerJoin(cityDistrict)
        .on(outdoorPricing.zoneId.eq(cityDistrict.zoneId))
        .where(cityDistrict.districtId.eq(districtId).and(outdoorPricing.nature.eq(nature.toString()))).fetchFirst();
    return Optional.ofNullable(pc);
  }
}
