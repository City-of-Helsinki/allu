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
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.domain.DocumentSearchCriteria;
import fi.hel.allu.common.domain.DocumentSearchResult;

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
    return getDecisionData(applicationId, decision.data);
  }

  @Transactional(readOnly = true)
  public Optional<byte[]> getAnonymizedDecision(int applicationId) {
    return getDecisionData(applicationId, decision.anonymizedData);
  }

  private Optional<byte[]> getDecisionData(int applicationId, SimplePath<byte[]> documentField) {
    byte[] data = queryFactory.select(documentField).from(decision).where(decision.applicationId.eq(applicationId))
        .fetchOne();
    return Optional.ofNullable(data);
  }


  @Transactional
  public int getPlacementContractSectionNumber() {
    return queryFactory.select(Expressions.simpleTemplate(Integer.class, "allu.get_placement_contract_section_number()")).fetchOne();
  }

  @Transactional(readOnly = true)
  public List<DocumentSearchResult> searchDecisions(DocumentSearchCriteria searchCriteria) {
    Optional<BooleanExpression> conditions = conditions(searchCriteria);
    SQLQuery<DocumentSearchResult> query = queryFactory
      .select(Projections.constructor(DocumentSearchResult.class,decision.applicationId, application.decisionTime, application.decisionMaker))
      .from(decision)
      .join(application).on(decision.applicationId.eq(application.id));
    conditions.ifPresent(c -> query.where(c));
    return query.fetch();
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
}
