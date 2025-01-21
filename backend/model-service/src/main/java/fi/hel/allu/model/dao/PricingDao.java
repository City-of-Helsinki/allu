package fi.hel.allu.model.dao;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.SurfaceHardness;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.model.domain.PricingKey;
import fi.hel.allu.model.pricing.OutdoorPricingConfiguration;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QCityDistrict.cityDistrict;
import static fi.hel.allu.QOutdoorPricing.outdoorPricing;
import static fi.hel.allu.QPricing.pricing;

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
  public Optional<OutdoorPricingConfiguration> findByDisctrictAndNature(int cityDistrictId, EventNature nature, SurfaceHardness surfaceHardness) {
    OutdoorPricingConfiguration pc = queryFactory.select(outdoorPricingBean).from(outdoorPricing).innerJoin(cityDistrict)
        .on(outdoorPricing.zoneId.eq(cityDistrict.zoneId))
        .where(cityDistrict.id.eq(cityDistrictId)
            .and(outdoorPricing.nature.eq(nature.toString()))
            .and(outdoorPricing.surfaceHardness.eq(surfaceHardness))).fetchFirst();
    return Optional.ofNullable(pc);
  }

  @Transactional(readOnly = true)
  public int findValue(ApplicationType type, PricingKey key, String paymentClass) {
    final Integer value = queryFactory
        .select(pricing.value)
        .from(pricing)
        .where(pricing.applicationType.eq(type)
          .and(pricing.key.eq(key.name()))
          .and(pricing.paymentClass.eq(paymentClass))
          .and(pricing.validity.eq(Date.valueOf(LocalDate.of(2025, 3, 1))).or(pricing.validity.isNull())))
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
          .and(pricing.key.eq(key.name()))
          .and(pricing.validity.eq(Date.valueOf(LocalDate.of(2025, 3, 1))).or(pricing.validity.isNull())))
        .fetchFirst();
    if (value == null) {
      throw new NoSuchEntityException("pricing.notFound");
    }
    return value;
  }

  @Transactional(readOnly = true)
  public List<String> getPaymentClasses(ApplicationType type, ApplicationKind kind) {
    BooleanExpression condition = pricing.applicationType.eq(type)
      .and(pricing.paymentClass.isNotNull())
      .and(pricing.validity.eq(Date.valueOf(LocalDate.of(2025, 3, 1))).or(pricing.validity.isNull()));
    if (type == ApplicationType.SHORT_TERM_RENTAL) {
      // Short term rentals have kind specific payment classes
      condition = condition.and(pricing.key.eq(kind.name()));
    }
    return queryFactory
        .selectDistinct(pricing.paymentClass)
        .from(pricing)
        .where(condition)
        .orderBy(pricing.paymentClass.asc())
        .fetch();
  }
}
