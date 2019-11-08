package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Comment ouput model")
public class CommentOutExt extends CommentExt {

  private ZonedDateTime creationTime;

  public CommentOutExt() {
  }

  public CommentOutExt(String commentator, String commentContent, ZonedDateTime creationTime) {
    super(commentator, commentContent);
    this.creationTime = creationTime;
  }

  @ApiModelProperty(value = "Comment creation time", readOnly = true)
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

}
