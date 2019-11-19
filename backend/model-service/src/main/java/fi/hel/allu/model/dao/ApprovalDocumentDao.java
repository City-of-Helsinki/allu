package fi.hel.allu.model.dao;

import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.sql.SQLQueryFactory;
import static fi.hel.allu.QApprovalDocument.approvalDocument;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;

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

  private Optional<byte[]> getDocumentData(Integer applicationId, ApprovalDocumentType type,
      SimplePath<byte[]> documentField) {
    byte[] data = queryFactory.select(documentField).from(approvalDocument)
        . where(approvalDocument.applicationId.eq(applicationId).and(approvalDocument.type.eq(type)))
          .fetchOne();
      return Optional.ofNullable(data);
  }

}
