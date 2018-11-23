package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import fi.hel.allu.common.domain.types.StatusType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * External API application output data
 *
 */
@ApiModel(value = "Application data")
public class ApplicationExt {

  private Integer id;
  private String name;
  private String applicationId;
  private StatusType status;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private UserExt owner;

  @ApiModelProperty(value = "Id of the application")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value = "Name of the application")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @ApiModelProperty(value = "Application identifier (hakemustunniste)")
  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  @ApiModelProperty(value = "Status of the application")
  public StatusType getStatus() {
    return status;
  }

  public void setStatus(StatusType status) {
    this.status = status;
  }

  @ApiModelProperty(value = "End time of the application" )
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  @ApiModelProperty(value = "Owner of the application")
  public UserExt getOwner() {
    return owner;
  }

  public void setOwner(UserExt owner) {
    this.owner = owner;
  }

  @ApiModelProperty(value = "Start time of the application" )
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

}
