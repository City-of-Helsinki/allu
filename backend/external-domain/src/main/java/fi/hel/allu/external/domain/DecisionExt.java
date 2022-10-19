package fi.hel.allu.external.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Decision metadata")
public class DecisionExt {
  private UserExt handler;
  private UserExt decisionMaker;

  public DecisionExt() {
  }

  public DecisionExt(UserExt handler, UserExt decisionMaker) {
    this.handler = handler;
    this.decisionMaker = decisionMaker;
  }

  @Schema(description = "User that created proposal")
  public UserExt getHandler() {
    return handler;
  }

  public void setHandler(UserExt handler) {
    this.handler = handler;
  }

  @Schema(description = "Decision maker (päättäjä).")
  public UserExt getDecisionMaker() {
    return decisionMaker;
  }

  public void setDecisionMaker(UserExt decisionMaker) {
    this.decisionMaker = decisionMaker;
  }


}
