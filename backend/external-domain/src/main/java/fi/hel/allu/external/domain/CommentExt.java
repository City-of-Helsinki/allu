package fi.hel.allu.external.domain;

import javax.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Application comments")
public class CommentExt {

  private String commentator;
  @NotBlank(message = "{comment.content.empty}")
  private String commentContent;

  public CommentExt() {
  }

  public CommentExt(String commentator, String commentContent) {
    this.commentator = commentator;
    this.commentContent = commentContent;
  }

  @Schema(description = "Commentator name")
  public String getCommentator() {
    return commentator;
  }

  public void setCommentator(String commentator) {
    this.commentator = commentator;
  }

  @Schema(description = "Comment content")
  public String getCommentContent() {
    return commentContent;
  }

  public void setCommentContent(String commentContent) {
    this.commentContent = commentContent;
  }

}
