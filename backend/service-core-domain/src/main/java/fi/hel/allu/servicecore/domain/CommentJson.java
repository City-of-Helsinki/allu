package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import fi.hel.allu.common.types.CommentType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Comment for an application")
public class CommentJson {
  private Integer id;
  @NotNull
  private CommentType type;
  @NotBlank
  private String text;
  private ZonedDateTime createTime;
  private ZonedDateTime updateTime;
  private UserJson user;
  private String commentator;

  @ApiModelProperty(value = "Id of the comment. Should be null when creating new comment")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  @ApiModelProperty(value = "Type of the comment")
  public CommentType getType() {
    return type;
  }

  public void setType(CommentType type) {
    this.type = type;
  }

  @ApiModelProperty(value = "Comment text")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @ApiModelProperty(value = "Comment creation time")
  public ZonedDateTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(ZonedDateTime createTime) {
    this.createTime = createTime;
  }

  @ApiModelProperty(value = "Comment last update time")
  public ZonedDateTime getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(ZonedDateTime updateTime) {
    this.updateTime = updateTime;
  }


  @ApiModelProperty(value = "User who last updated comment")
  public UserJson getUser() {
    return user;
  }

  public void setUser(UserJson user) {
    this.user = user;
  }

  @ApiModelProperty(value = "Name of the commentator")
  public String getCommentator() {
    return commentator;
  }

  public void setCommentator(String commentator) {
    this.commentator = commentator;
  }
}
