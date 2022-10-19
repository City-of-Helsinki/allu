package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import fi.hel.allu.common.types.AttachmentType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Attachment information")
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

  public AttachmentInfoJson() {
  }

  public AttachmentInfoJson(Integer id, String handlerName, AttachmentType type, String mimeType, String name,
      String description, Long size, ZonedDateTime creationTime, boolean decisionAttachment) {
    this.id = id;
    this.handlerName = handlerName;
    this.type = type;
    this.mimeType = mimeType;
    this.name = name;
    this.description = description;
    this.size = size;
    this.creationTime = creationTime;
    this.decisionAttachment = decisionAttachment;
  }

  private ZonedDateTime creationTime;
  private boolean decisionAttachment;

  @Schema(description = "The attachment ID", accessMode = Schema.AccessMode.READ_ONLY)
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(description = "Id of the user who has made the latest change to the attachment.", accessMode = Schema.AccessMode.READ_ONLY)
  public String getHandlerName() {
    return handlerName;
  }

  public void setHandlerName(String handlerName) {
    this.handlerName = handlerName;
  }

  @Schema(description = "Type of the attachment.", required = true)
  public AttachmentType getType() {
    return type;
  }

  public void setType(AttachmentType type) {
    this.type = type;
  }

  @Schema(description = "Attachments mime type")
  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  @Schema(description = "Attachment name (file name)", required = true)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Schema(description = "Attachment description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Schema(description = "Attachment size", accessMode = Schema.AccessMode.READ_ONLY)
  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  @Schema(description = "Attachment creation time", accessMode = Schema.AccessMode.READ_ONLY)
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  @Schema(description = "Indication if attachment should be included in decision distribution. Only PDF attachments are allowed as decision attachments.")
  public boolean isDecisionAttachment() {
    return decisionAttachment;
  }

  public void setDecisionAttachment(Boolean decisionAttachment) {
    this.decisionAttachment = decisionAttachment;
  }
}
