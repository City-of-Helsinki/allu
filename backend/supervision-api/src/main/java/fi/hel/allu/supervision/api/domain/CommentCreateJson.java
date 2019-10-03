package fi.hel.allu.supervision.api.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import fi.hel.allu.common.types.CommentType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Model for creating new comments")
public class CommentCreateJson {
  @NotNull(message = "{comment.type}")
  private CommentType type;
  @NotBlank(message = "{comment.content.empty}")
  private String text;

  @ApiModelProperty(value = "Comment type", required = true)
  public CommentType getType() {
    return type;
  }

  public void setType(CommentType type) {
    this.type = type;
  }

  @ApiModelProperty(value = "Comment content", required = true)
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

}
