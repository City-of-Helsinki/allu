package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.hel.allu.common.domain.types.ApplicationKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Event (tapahtuma)")
public class EventExt extends ApplicationExt {

  private Integer structureArea;
  private String structureDescription;
  private String description;
  @NotNull(message = "event.starttime")
  private ZonedDateTime eventStartTime;
  @NotNull(message = "event.endtime")
  private ZonedDateTime eventEndTime;

  @ApiModelProperty(value = "Structure area in square meters")
  public Integer getStructureArea() {
    return structureArea;
  }

  public void setStructureArea(Integer structureArea) {
    this.structureArea = structureArea;
  }

  @ApiModelProperty(value = "Description of structures")
  public String getStructureDescription() {
    return structureDescription;
  }

  public void setStructureDescription(String structureDescription) {
    this.structureDescription = structureDescription;
  }

  @ApiModelProperty(value = "Event description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @ApiModelProperty(value = "Start time of the event", required = true)
  public ZonedDateTime getEventStartTime() {
    return eventStartTime;
  }

  public void setEventStartTime(ZonedDateTime eventStartTime) {
    this.eventStartTime = eventStartTime;
  }

  @ApiModelProperty(value = "End time of the event", required = true)
  public ZonedDateTime getEventEndTime() {
    return eventEndTime;
  }

  public void setEventEndTime(ZonedDateTime eventEndTime) {
    this.eventEndTime = eventEndTime;
  }

}
