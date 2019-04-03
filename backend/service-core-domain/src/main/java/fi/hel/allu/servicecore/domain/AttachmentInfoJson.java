package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import fi.hel.allu.common.types.AttachmentType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Attachment information")
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

  @ApiModelProperty(value = "The attachment ID", readOnly = true)
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value = "Id of the user who has made the latest change to the attachment.", readOnly = true)
  public String getHandlerName() {
    return handlerName;
  }

  public void setHandlerName(String handlerName) {
    this.handlerName = handlerName;
  }

  @ApiModelProperty(value = "Type of the attachment.", required = true)
  public AttachmentType getType() {
    return type;
  }

  public void setType(AttachmentType type) {
    this.type = type;
  }

  @ApiModelProperty(value = "Attachments mime type")
  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  @ApiModelProperty(value = "Attachment name (file name)", required = true)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @ApiModelProperty(value = "Attachment description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @ApiModelProperty(value = "Attachment size", readOnly = true)
  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  @ApiModelProperty(value = "Attachment creation time", readOnly = true)
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  @ApiModelProperty(value = "Indication if attachment should be included in decision distribution. Only PDF attachments are allowed as decision attachments.")
  public boolean isDecisionAttachment() {
    return decisionAttachment;
  }

  public void setDecisionAttachment(Boolean decisionAttachment) {
    this.decisionAttachment = decisionAttachment;
  }
}
