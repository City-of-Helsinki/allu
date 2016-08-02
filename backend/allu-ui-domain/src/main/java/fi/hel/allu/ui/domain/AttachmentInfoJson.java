package fi.hel.allu.ui.domain;

import java.time.ZonedDateTime;

/**
 * Attachment information
 */
public class AttachmentInfoJson {

  private Integer id;
  private String name;
  private String description;
  private String type;
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
   * Attachment type (MIME type, e.g. "application/pdf") -- supplied by UI
   */
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  /**
   * Attachment size -- supplied by UI
   */
  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  /**
   * Attachment creation time -- supplied by UI
   */
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

}
