package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.CommentType;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Comment for an application
 */
public class Comment {
  private Integer id;
  @NotNull
  private CommentType type;
  @NotBlank
  private String text;
  private ZonedDateTime createTime;
  private ZonedDateTime updateTime;
  private Integer userId;

  /**
   * Get the database ID of the comment. Can be null for new comments.
   *
   * @return database ID or null
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Get the type of the comment
   *
   * @return comment type
   */
  public CommentType getType() {
    return type;
  }

  public void setType(CommentType type) {
    this.type = type;
  }

  /**
   * Get the comment text.
   *
   * @return comment text
   */
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  /**
   * Get comment's creation time. Can be null for new comments.
   *
   * @return creation time or null
   */
  public ZonedDateTime getCreateTime() {
    return createTime;
  }

  public void setCreateTime(ZonedDateTime createTime) {
    this.createTime = createTime;
  }

  /**
   * Get comment's last update time. Can be null for new comments.
   *
   * @return update time or null
   */
  public ZonedDateTime getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(ZonedDateTime updateTime) {
    this.updateTime = updateTime;
  }

  /**
   * Get the ID of the user who last updated the comment. Can be null for new
   * comments.
   *
   * @return user ID or null
   */
  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }
}
