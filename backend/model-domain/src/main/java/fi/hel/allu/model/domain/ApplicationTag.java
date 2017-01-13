package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.ApplicationTagType;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * In Finnish: hakemustagi.
 * Tags add information to applications without affecting application state. For example, an application being processed could have a
 * waiting for extra data tag, which would tell that application is waiting for additional information before it can be processed further.
 */
public class ApplicationTag {

  private Integer id;
  private Integer applicationId;
  @NotNull
  private Integer addedBy;
  @NotNull
  private ApplicationTagType type;
  @NotNull
  private ZonedDateTime creationTime;

  public ApplicationTag() {
    // JSON serialization
  }

  public ApplicationTag(Integer addedBy, ApplicationTagType type, ZonedDateTime creationTime) {
    this.addedBy = addedBy;
    this.type = type;
    this.creationTime = creationTime;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Returns the id of application the tag is related to.
   *
   * @return  the id of application the tag is related to.
   */
  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  /**
   * Returns the id of the user who added the tag.
   *
   * @return  the id of the user who added the tag.
   */
  public Integer getAddedBy() {
    return addedBy;
  }

  public void setAddedBy(Integer addedBy) {
    this.addedBy = addedBy;
  }

  /**
   * Returns the type of the tag.
   *
   * @return  the type of the tag.
   */
  public ApplicationTagType getType() {
    return type;
  }

  public void setType(ApplicationTagType type) {
    this.type = type;
  }

  /**
   * Returns the time tag was created.
   *
   * @return  the time tag was created.
   */
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }
}
