package fi.hel.allu.ui.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import fi.hel.allu.common.types.ApplicationTagType;

import javax.validation.constraints.NotNull;

import java.time.ZonedDateTime;

/**
 * in Finnish: hakemuksen tagi.
 */
public class ApplicationTagJson {
  @NotNull
  private Integer addedBy;
  @NotNull
  private ApplicationTagType type;
  @NotNull
  private ZonedDateTime creationTime;

  /**
   * Add a fake "id" field during serialization so that comparison of tag lists
   * in allu-ui-service's ObjectComparer compares by ID.
   */
  @JsonProperty(access = Access.READ_ONLY)
  public int getId() {
    return type.ordinal();
  }

  public ApplicationTagJson() {
    // JSON serialization
  }

  public ApplicationTagJson(Integer addedBy, ApplicationTagType type, ZonedDateTime creationTime) {
    this.addedBy = addedBy;
    this.type = type;
    this.creationTime = creationTime;
  }

  /**
   * User (handler) who added the tag.
   *
   * @return  User (handler) who added the tag.
   */
  public Integer getAddedBy() {
    return addedBy;
  }

  public void setAddedBy(Integer addedBy) {
    this.addedBy = addedBy;
  }

  /**
   * Type of the tag.
   *
   * @return  Type of the tag.
   */
  public ApplicationTagType getType() {
    return type;
  }

  public void setType(ApplicationTagType type) {
    this.type = type;
  }

  /**
   * Time the tag was added.
   *
   * @return  Time the tag was added.
   */
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }
}
