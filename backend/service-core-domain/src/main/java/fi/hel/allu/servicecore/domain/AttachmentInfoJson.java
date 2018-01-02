package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.types.AttachmentType;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Attachment information
 */
public class AttachmentInfoJson {

  private Integer id;
  private String handlerName;
  @NotNull(message = "{attachment.type}")
  private AttachmentType type;
  private String mimeType;
  @NotNull(message = "{attachment.name}")
  private String name;
  private String description;
  private Long size;
  private Integer attachmentDataId;
  private ZonedDateTime creationTime;
  private boolean decisionAttachment;

  /**
   * The attachment ID -- created by database
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Id of the user who has made the latest change to the attachment.
   *
   * @return  Name of the handler who has made the latest change to the attachment.
   */
  public String getHandlerName() {
    return handlerName;
  }

  public void setHandlerName(String handlerName) {
    this.handlerName = handlerName;
  }

  /**
   * Type of the attachment.
   *
   * @return  Type of the attachment.
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
   * Attachment name (file name) -- supplied by UI
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Attachment description -- Supplied by UI
   */
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Attachment size -- supplied by model
   */
  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  /**
   * Attachment creation time -- supplied by model
   */
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  /**
   * Database ID of the attachment data.
   */
  public Integer getAttachmentDataId() {
    return attachmentDataId;
  }

  public void setAttachmentDataId(Integer attachmentDataId) {
    this.attachmentDataId = attachmentDataId;
  }

  /**
   * Indication if attachment should be included in decision distribution
   */
  public boolean isDecisionAttachment() {
    return decisionAttachment;
  }

  public void setDecisionAttachment(Boolean decisionAttachment) {
    this.decisionAttachment = decisionAttachment;
  }
}
