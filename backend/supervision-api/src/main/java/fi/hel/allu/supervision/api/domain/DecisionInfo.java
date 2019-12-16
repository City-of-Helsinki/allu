package fi.hel.allu.supervision.api.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Decision making information")
public class DecisionInfo {

  @NotNull(message = "{decision.maker.required}")
  private Integer decisionMakerId;
  @NotBlank(message = "{decision.note}")
  private String decisionNote;

  public DecisionInfo() {
  }

  @ApiModelProperty(value = "Decision maker user ID. User must have ROLE_DECISION -role.")
  public Integer getDecisionMakerId() {
    return decisionMakerId;
  }

  public void setDecisionMakerId(Integer decisionMakerId) {
    this.decisionMakerId = decisionMakerId;
  }

  @ApiModelProperty(value = "Note for decision maker.")
  public String getDecisionNote() {
    return decisionNote;
  }

  public void setDecisionNote(String decisionNote) {
    this.decisionNote = decisionNote;
  }

}
