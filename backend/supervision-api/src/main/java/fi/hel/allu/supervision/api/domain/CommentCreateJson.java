package fi.hel.allu.supervision.api.domain;

import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotBlank;

import fi.hel.allu.common.types.CommentType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Model for creating new comments")
public class CommentCreateJson {
  @NotNull(message = "{comment.type}")
  private CommentType type;
  @NotBlank(message = "{comment.content.empty}")
  private String text;

  @Schema(description = "Comment type", required = true)
  public CommentType getType() {
    return type;
  }

  public void setType(CommentType type) {
    this.type = type;
  }

  @Schema(description = "Comment content", required = true)
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

}
