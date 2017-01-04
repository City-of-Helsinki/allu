package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.AttachmentType;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public class AttachmentInfo {
  private Integer id;
  private Integer applicationId;
  private Integer userId;
  @NotNull
  private AttachmentType type;
  private String name;
  private String description;
  private Long size;
  private ZonedDateTime creationTime;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * @return the attachmentId
   */
  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  /**
   * Returns the id of the user who added the attachment.
   *
   * @return  the id of the user who added the attachment or <code>null</code> in case attachment was added by customer.
   */
  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  /**
   * Returns the type of the attachment.
   *
   * @return  the type of the attachment.
   */
  public AttachmentType getType() {
    return type;
  }

  public void setType(AttachmentType type) {
    this.type = type;
  }

  /**
   * The attachment name (file name)
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * The attachment description
   */
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the size
   */
  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  /**
   * @return the creationTime
   */
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

}
