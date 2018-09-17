package fi.hel.allu.model.querydsl;

import com.querydsl.sql.types.EnumAsObjectType;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;

public class StringToApprovalDocumentType extends EnumAsObjectType<ApprovalDocumentType> {
  public StringToApprovalDocumentType() {
    super(ApprovalDocumentType.class);
  }
}
