package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import fi.hel.allu.common.domain.types.ContractStatusType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Contract metadata")
public class ContractExt extends DecisionExt {

  private ZonedDateTime creationTime;
  private ContractStatusType status;

  public ContractExt() {
  }

  public ContractExt(UserExt handler, UserExt decisionMaker, ContractStatusType status, ZonedDateTime creationTime) {
    super(handler, decisionMaker);
    this.status = status;
    this.creationTime = creationTime;
  }

  @Schema(description = "Gets creation time of the contract proposal")
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  @Schema(description = "Contract status")
  public ContractStatusType getStatus() {
    return status;
  }

  public void setStatus(ContractStatusType status) {
    this.status = status;
  }
}
