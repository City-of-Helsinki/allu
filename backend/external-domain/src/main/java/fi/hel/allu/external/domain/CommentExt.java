package fi.hel.allu.external.domain;

import org.hibernate.validator.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Application comments")
public class CommentExt {

  private String commentator;
  @NotBlank(message = "{comment.content.empty}")
  private String commentContent;

  @ApiModelProperty(value = "Commentator name")
  public String getCommentator() {
    return commentator;
  }

  public void setCommentator(String commentator) {
    this.commentator = commentator;
  }

  @ApiModelProperty(value = "Comment content")
  public String getCommentContent() {
    return commentContent;
  }

  public void setCommentContent(String commentContent) {
    this.commentContent = commentContent;
  }

}
