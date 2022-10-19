package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Comment ouput model")
public class CommentOutExt extends CommentExt {

  private ZonedDateTime creationTime;

  public CommentOutExt() {
  }

  public CommentOutExt(String commentator, String commentContent, ZonedDateTime creationTime) {
    super(commentator, commentContent);
    this.creationTime = creationTime;
  }

  @Schema(description = "Comment creation time", accessMode = Schema.AccessMode.READ_ONLY)
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

}
