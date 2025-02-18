package fi.hel.allu.model.dao;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.domain.DocumentSearchCriteria;
import fi.hel.allu.common.domain.DocumentSearchResult;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.PublicityType;

import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QDecision.decision;

@Repository
public class DecisionDao {
  @Autowired
  private SQLQueryFactory queryFactory;

  /**
   * Store a decision PDF for an application
   *
   * @param applicationId
   *          the application ID
   * @param data
   *          PDF data
   */
  @Transactional
  public void storeDecision(int applicationId, byte[] data) {
    // Insert a database row if needed:
    if (queryFactory.select(decision.id).from(decision).where(decision.applicationId.eq(applicationId))
        .fetchCount() == 0) {
      queryFactory.insert(decision).columns(decision.applicationId).values(applicationId).execute();
    }
    // Then update the row
    queryFactory.update(decision).where(decision.applicationId.eq(applicationId))
        .set(decision.data, data)
        .set(decision.creationTime, ZonedDateTime.now()).execute();
  }

  @Transactional
  public void storeAnonymizedDecision(int applicationId, byte[] anonymizedData) {
    queryFactory.update(decision).where(decision.applicationId.eq(applicationId))
    .set(decision.anonymizedData, anonymizedData).execute();
 }

  /**
   * Retrieve the decision PDF for an application
   *
   * @param applicationId
   *          application's ID
   * @return PDF data
   */
  @Transactional(readOnly = true)
  public Optional<byte[]> getDecision(int applicationId) {
    byte[] data = queryFactory.select(decision.data).from(decision).where(decision.applicationId.eq(applicationId))
        .fetchOne();
    return Optional.ofNullable(data);
  }


  @Transactional
  public int getPlacementContractSectionNumber() {
    return queryFactory.select(Expressions.simpleTemplate(Integer.class, "allu.get_placement_contract_section_number()")).fetchOne();
  }

  @Transactional(readOnly = true)
  public Optional<byte[]> getAnonymizedDecision(int applicationId) {
    byte[] data = queryFactory.select(decision.anonymizedData)
    .from(decision)
    .join(application).on(decision.applicationId.eq(application.id))
    .where(decision.applicationId.eq(applicationId).and(getAnonymizedDecisionSearchFilter()))
    .fetchOne();
    return Optional.ofNullable(data);
  }

  @Transactional(readOnly = true)
  public List<DocumentSearchResult> searchDecisions(DocumentSearchCriteria searchCriteria) {
    BooleanExpression filter = getAnonymizedDecisionSearchFilter();
    Optional<BooleanExpression> searchConditions = conditions(searchCriteria);
    return queryFactory
      .select(Projections.constructor(DocumentSearchResult.class, decision.applicationId, application.decisionTime, application.decisionMaker))
      .from(decision)
      .join(application).on(decision.applicationId.eq(application.id))
      .where(searchConditions.map(sc -> filter.and(sc)).orElse(filter))
      .fetch();
  }

  private BooleanExpression getAnonymizedDecisionSearchFilter() {
    return decision.anonymizedData.isNotNull()
        .and(application.type.ne(ApplicationType.CABLE_REPORT))
        .and(application.decisionPublicityType.eq(PublicityType.PUBLIC));
  }

  private static Optional<BooleanExpression> conditions(DocumentSearchCriteria searchCriteria) {
    return Stream.of(
        Optional.ofNullable(searchCriteria.getAfter()).map(application.decisionTime::after),
        Optional.ofNullable(searchCriteria.getBefore()).map(application.decisionTime::before),
        Optional.ofNullable(searchCriteria.getApplicationType()).map(application.type::eq)
      )
      .filter(p -> p.isPresent())
      .map(p -> p.get())
      .reduce((left, right) -> left.and(right));
  }

  @Transactional
  public void removeDecisions(List<Integer> applicationIds) {
    queryFactory.delete(decision).where(decision.applicationId.in(applicationIds)).execute();
  }
}
