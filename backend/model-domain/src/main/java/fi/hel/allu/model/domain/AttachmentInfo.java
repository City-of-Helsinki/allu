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
  private String mimeType;
  private String name;
  private String description;
  private Integer attachmentDataId;
  private ZonedDateTime creationTime;
  private boolean decisionAttachment;

  public AttachmentInfo() {
  }

  public AttachmentInfo(Integer id,
                        Integer userId,
                        AttachmentType type,
                        String mimeType,
                        String name,
                        String description,
                        Integer attachmentDataId,
                        ZonedDateTime creationTime,
                        boolean decisionAttachment) {
    this.id = id;
    this.userId = userId;
    this.type = type;
    this.mimeType = mimeType;
    this.name = name;
    this.description = description;
    this.attachmentDataId = attachmentDataId;
    this.creationTime = creationTime;
    this.decisionAttachment = decisionAttachment;
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
   * Attachments mime type
   */
  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
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

  /**
   * Indication if attachment should be included in decision distribution
   */
  public boolean isDecisionAttachment() {
    return decisionAttachment;
  }

  public void setDecisionAttachment(boolean decisionAttachment) {
    this.decisionAttachment = decisionAttachment;
  }
}
