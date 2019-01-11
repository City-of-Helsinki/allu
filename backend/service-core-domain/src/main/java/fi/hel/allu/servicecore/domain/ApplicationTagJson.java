package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Application tag")
public class ApplicationTagJson {
  private Integer addedBy;
  @NotNull
  private ApplicationTagType type;
  @NotNull
  private ZonedDateTime creationTime;

  /**
   * Add a fake "id" field during serialization so that comparison of tag lists
   * in allu-ui-service's ObjectComparer compares by ID.
   */
  @ApiModelProperty(hidden = true)
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

  @ApiModelProperty(value = "Id of the user who added the tag")
  public Integer getAddedBy() {
    return addedBy;
  }

  public void setAddedBy(Integer addedBy) {
    this.addedBy = addedBy;
  }

  @ApiModelProperty(value = "Type of the tag")
  public ApplicationTagType getType() {
    return type;
  }

  public void setType(ApplicationTagType type) {
    this.type = type;
  }

  @ApiModelProperty(value = "Time the tag was added.")
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }
}
