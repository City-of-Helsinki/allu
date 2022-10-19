package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Decision search result")
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

  @Schema(description = "Decision maker name")
  public String getDecisionMakerName() {
    return decisionMakerName;
  }

  public void setDecisionMakerName(String decisionMakerName) {
    this.decisionMakerName = decisionMakerName;
  }

  @Schema(description = "Decision date")
  public ZonedDateTime getDecisionDate() {
    return decisionDate;
  }

  public void setDecisionDate(ZonedDateTime decisionDate) {
    this.decisionDate = decisionDate;
  }
}
