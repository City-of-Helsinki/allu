package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.model.domain.CommentInterface;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Schema(description = "Comment for an application")
public class CommentJson implements CommentInterface {
  private Integer id;
  @NotNull
  private CommentType type;
  @NotBlank
  private String text;
  private ZonedDateTime createTime;
  private ZonedDateTime updateTime;
  private UserJson user;
  private String commentator;

  public CommentJson() {
  }

  public CommentJson(CommentType type, String text) {
    this.type = type;
    this.text = text;
  }

  @Schema(description = "Id of the comment. Should be null when creating new comment", accessMode = Schema.AccessMode.READ_ONLY)
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  @Schema(description = "Type of the comment", required = true)
  public CommentType getType() {
    return type;
  }

  public void setType(CommentType type) {
    this.type = type;
  }

  @Schema(description = "Comment text", required = true)
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Schema(description = "Comment creation time", accessMode = Schema.AccessMode.READ_ONLY)
  public ZonedDateTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(ZonedDateTime createTime) {
    this.createTime = createTime;
  }

  @Schema(description = "Comment last update time", accessMode = Schema.AccessMode.READ_ONLY)
  public ZonedDateTime getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(ZonedDateTime updateTime) {
    this.updateTime = updateTime;
  }


  @Schema(description = "User who last updated comment", accessMode = Schema.AccessMode.READ_ONLY)
  public UserJson getUser() {
    return user;
  }

  public void setUser(UserJson user) {
    this.user = user;
  }

  @Schema(description = "Name of the commentator")
  public String getCommentator() {
    return commentator;
  }

  public void setCommentator(String commentator) {
    this.commentator = commentator;
  }
}