package fi.hel.allu.model.dao;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;

import static fi.hel.allu.QDecision.decision;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.sql.SQLQueryFactory;

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
}
