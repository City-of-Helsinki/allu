package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Decision search result")
public class DecisionSearchResult extends AbstractDocumentSearchResult {

  private String decisionMakerName;
  private ZonedDateTime decisionDate;

  public DecisionSearchResult() {
  }

  public DecisionSearchResult(Integer id, String applicationId, String address, String decisionMakerName, ZonedDateTime decisionDate) {
    super(id, applicationId, address);
    this.decisionMakerName = decisionMakerName;
    this.decisionDate = decisionDate;
  }

  @ApiModelProperty(value = "Decision maker name")
  public String getDecisionMakerName() {
    return decisionMakerName;
  }

  public void setDecisionMakerName(String decisionMakerName) {
    this.decisionMakerName = decisionMakerName;
  }

  @ApiModelProperty(value = "Decision date")
  public ZonedDateTime getDecisionDate() {
    return decisionDate;
  }

  public void setDecisionDate(ZonedDateTime decisionDate) {
    this.decisionDate = decisionDate;
  }
}
