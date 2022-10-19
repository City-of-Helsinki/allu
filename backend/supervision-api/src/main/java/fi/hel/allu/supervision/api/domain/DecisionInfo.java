package fi.hel.allu.supervision.api.domain;

import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Decision making information")
public class DecisionInfo {

  @NotNull(message = "{decision.maker.required}")
  private Integer decisionMakerId;
  @NotBlank(message = "{decision.note}")
  private String decisionNote;

  public DecisionInfo() {
  }

  @Schema(description = "Decision maker user ID. User must have ROLE_DECISION -role.")
  public Integer getDecisionMakerId() {
    return decisionMakerId;
  }

  public void setDecisionMakerId(Integer decisionMakerId) {
    this.decisionMakerId = decisionMakerId;
  }

  @Schema(description = "Note for decision maker.")
  public String getDecisionNote() {
    return decisionNote;
  }

  public void setDecisionNote(String decisionNote) {
    this.decisionNote = decisionNote;
  }

}
