package fi.hel.allu.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.hel.allu.common.types.AttachmentType;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true) // without this DefaultAttachmentInfo fields would mess JSON deserialization
public class AttachmentInfo {
  private Integer id;
  private Integer userId;
  @NotNull
  private AttachmentType type;
  private String name;
  private String description;
  private Integer attachmentDataId;
  private ZonedDateTime creationTime;

  public AttachmentInfo() {
  }

  public AttachmentInfo(Integer id,
                        Integer userId,
                        AttachmentType type,
                        String name,
                        String description,
                        Integer attachmentDataId,
                        ZonedDateTime creationTime) {
    this.id = id;
    this.userId = userId;
    this.type = type;
    this.name = name;
    this.description = description;
    this.attachmentDataId = attachmentDataId;
    this.creationTime = creationTime;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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
   * @return id of the attachment data
   */
  public Integer getAttachmentDataId() {
    return attachmentDataId;
  }

  public void setAttachmentDataId(Integer attachmentDataId) {
    this.attachmentDataId = attachmentDataId;
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
