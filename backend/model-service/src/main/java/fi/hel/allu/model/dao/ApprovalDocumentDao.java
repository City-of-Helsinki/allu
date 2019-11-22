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
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.domain.DocumentSearchCriteria;
import fi.hel.allu.common.domain.DocumentSearchResult;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;

import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QApprovalDocument.approvalDocument;

@Repository
public class ApprovalDocumentDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  @Transactional
  public void storeApprovalDocument(Integer applicationId, ApprovalDocumentType type, byte[] data) {
    if (queryFactory.select(approvalDocument.id).from(approvalDocument)
          .where(approvalDocument.applicationId.eq(applicationId).and(approvalDocument.type.eq(type))).fetchCount() == 0) {
      queryFactory.insert(approvalDocument)
          .columns(approvalDocument.applicationId, approvalDocument.creationTime, approvalDocument.document, approvalDocument.type)
          .values(applicationId, ZonedDateTime.now(), data, type)
          .execute();
    } else {
      queryFactory.update(approvalDocument)
          .where(approvalDocument.applicationId.eq(applicationId).and(approvalDocument.type.eq(type)))
          .set(approvalDocument.document, data)
          .set(approvalDocument.creationTime, ZonedDateTime.now())
          .execute();
    }
  }

  @Transactional
  public void storeAnonymizedDocument(Integer applicationId, ApprovalDocumentType type, byte[] anonymizedData) {
    queryFactory.update(approvalDocument)
      .where(approvalDocument.applicationId.eq(applicationId).and(approvalDocument.type.eq(type)))
      .set(approvalDocument.anonymizedDocument, anonymizedData)
      .execute();
  }

  @Transactional(readOnly = true)
  public Optional<byte[]> getApprovalDocument(Integer applicationId, ApprovalDocumentType type) {
    return getDocumentData(applicationId, type, approvalDocument.document);
  }

  @Transactional(readOnly = true)
  public Optional<byte[]> getAnonymizedDocument(Integer applicationId, ApprovalDocumentType type) {
    return getDocumentData(applicationId, type, approvalDocument.anonymizedDocument);
  }

  @Transactional(readOnly = true)
  public List<DocumentSearchResult> searchApprovalDocuments(DocumentSearchCriteria searchCriteria, ApprovalDocumentType type) {
    BooleanExpression typeCondition = approvalDocument.type.eq(type);
    Optional<BooleanExpression> searchConditions = conditions(searchCriteria);
    return queryFactory.select(Projections.constructor(DocumentSearchResult.class, approvalDocument.applicationId, approvalDocument.creationTime))
      .from(approvalDocument)
      .join(application).on(approvalDocument.applicationId.eq(application.id))
      .where(searchConditions.map(sc -> typeCondition.and(sc)).orElse(typeCondition))
      .fetch();
  }

  private static Optional<BooleanExpression> conditions(DocumentSearchCriteria searchCriteria) {
    return Stream.of(
        Optional.ofNullable(searchCriteria.getAfter()).map(approvalDocument.creationTime::after),
        Optional.ofNullable(searchCriteria.getBefore()).map(approvalDocument.creationTime::before),
        Optional.ofNullable(searchCriteria.getApplicationType()).map(application.type::eq)
      )
      .filter(p -> p.isPresent())
      .map(p -> p.get())
      .reduce((left, right) -> left.and(right));
  }

  private Optional<byte[]> getDocumentData(Integer applicationId, ApprovalDocumentType type,
      SimplePath<byte[]> documentField) {
    byte[] data = queryFactory.select(documentField).from(approvalDocument)
        . where(approvalDocument.applicationId.eq(applicationId).and(approvalDocument.type.eq(type)))
          .fetchOne();
      return Optional.ofNullable(data);
  }

}
