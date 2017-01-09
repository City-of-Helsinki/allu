package fi.hel.allu.ui.domain;

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
  @NotNull(message = "{attachment.name}")
  private String name;
  private String description;
  private Long size;
  private ZonedDateTime creationTime;

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

}
