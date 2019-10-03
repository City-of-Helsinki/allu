package fi.hel.allu.external.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Decision metadata")
public class DecisionExt {
  private UserExt handler;
  private UserExt decisionMaker;

  public DecisionExt() {
  }

  public DecisionExt(UserExt handler, UserExt decisionMaker) {
    this.handler = handler;
    this.decisionMaker = decisionMaker;
  }

  @ApiModelProperty(value = "User that created proposal")
  public UserExt getHandler() {
    return handler;
  }

  public void setHandler(UserExt handler) {
    this.handler = handler;
  }

  @ApiModelProperty(value = "Decision maker (päättäjä).")
  public UserExt getDecisionMaker() {
    return decisionMaker;
  }

  public void setDecisionMaker(UserExt decisionMaker) {
    this.decisionMaker = decisionMaker;
  }


}
