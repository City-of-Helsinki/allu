package fi.hel.allu.model.dao;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.model.pricing.OutdoorPricingConfiguration;
import fi.hel.allu.model.domain.PricingKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QCityDistrict.cityDistrict;
import static fi.hel.allu.QOutdoorPricing.outdoorPricing;
import static fi.hel.allu.QPricing.pricing;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.NoSuchEntityException;

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

  @Transactional(readOnly = true)
  public int findValue(ApplicationType type, PricingKey key, String paymentClass) {
    final Integer value = queryFactory
        .select(pricing.value)
        .from(pricing)
        .where(pricing.applicationType.eq(type)
            .and(pricing.key.eq(key.name()))
            .and(pricing.paymentClass.eq(paymentClass)))
        .fetchFirst();
    if (value == null) {
      throw new NoSuchEntityException("pricing.notFound");
    }
    return value;
  }

  @Transactional(readOnly = true)
  public int findValue(ApplicationType type, PricingKey key) {
    final Integer value = queryFactory
        .select(pricing.value)
        .from(pricing)
        .where(pricing.applicationType.eq(type)
            .and(pricing.key.eq(key.name())))
        .fetchFirst();
    if (value == null) {
      throw new NoSuchEntityException("pricing.notFound");
    }
    return value;
  }
}
