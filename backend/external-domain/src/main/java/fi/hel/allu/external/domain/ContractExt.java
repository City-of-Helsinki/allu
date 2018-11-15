package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Contract metadata")
public class ContractExt {

  private HandlerExt handler;
  private ZonedDateTime creationTime;

  public ContractExt() {
  }

  public ContractExt(HandlerExt handler, ZonedDateTime creationTime) {
    this.handler = handler;
    this.creationTime = creationTime;
  }

  @ApiModelProperty(value = "User that created contract proposal")
  public HandlerExt getHandler() {
    return handler;
  }

  public void setHandler(HandlerExt handler) {
    this.handler = handler;
  }

  @ApiModelProperty(value = "Gets creation time of the contract proposal")
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

}
