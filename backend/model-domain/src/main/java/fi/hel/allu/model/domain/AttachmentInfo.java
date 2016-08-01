package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

public class AttachmentInfo {
  private Integer id;
  private Integer applicationId;
  private String name;
  private String type;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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
