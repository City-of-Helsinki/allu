package fi.hel.allu.model.dao;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.types.OutdoorEventNature;
import fi.hel.allu.model.pricing.PricingConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QOutdoorPricing.outdoorPricing;
import static fi.hel.allu.QSquareSection.squareSection;

@Repository
public class PricingDao {
  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<PricingConfiguration> pricingBean = bean(PricingConfiguration.class, outdoorPricing.all());

  @Transactional(readOnly = true)
  public Optional<PricingConfiguration> findByLocationAndNature(String square, String section,
      OutdoorEventNature nature) {
    Predicate sectionMatch;
    // Different equality for non-null and null:
    if (section != null) {
      sectionMatch = squareSection.section.eq(section);
    } else {
      sectionMatch = squareSection.section.isNull();
    }
    PricingConfiguration pc = queryFactory.select(pricingBean).from(outdoorPricing).join(squareSection)
        .on(outdoorPricing.squareSectionId.eq(squareSection.id))
        .where(squareSection.square.eq(square).and(sectionMatch).and(outdoorPricing.nature.eq(nature.toString())))
        .fetchFirst();
    return Optional.ofNullable(pc);
  }
}
