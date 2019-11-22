package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Approval document search result")
public class ApprovalDocumentSearchResult extends AbstractDocumentSearchResult {

  private ApprovalDocumentType type;
  private ZonedDateTime approvalDate;

  public ApprovalDocumentSearchResult() {
  }

  public ApprovalDocumentSearchResult(Integer id, String applicationId, String address, ApprovalDocumentType type,
      ZonedDateTime approvalDate) {
    super(id, applicationId, address);
    this.approvalDate = approvalDate;
    this.type = type;
  }

  @ApiModelProperty(value = "Approval document type")
  public ApprovalDocumentType getType() {
    return type;
  }

  public void setType(ApprovalDocumentType type) {
    this.type = type;
  }

  @ApiModelProperty(value = "Approval date")
  public ZonedDateTime getApprovalDate() {
    return approvalDate;
  }

  public void setApprovalDate(ZonedDateTime approvalDate) {
    this.approvalDate = approvalDate;
  }

}
