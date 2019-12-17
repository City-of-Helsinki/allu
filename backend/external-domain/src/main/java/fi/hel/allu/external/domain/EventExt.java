package fi.hel.allu.external.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.common.validator.NotFalse;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@NotFalse(rules = {
  "eventStartTime, notBeforeStartTime, {event.eventStartBeforeStart}",
  "eventEndTime, notAfterEndTime, {event.eventEndAfterEnd}",
})
public abstract class EventExt extends BaseApplicationExt {

  private Integer structureArea;
  private String structureDescription;
  private String description;
  @NotNull(message = "{event.starttime}")
  private ZonedDateTime eventStartTime;
  @NotNull(message = "{event.endtime}")
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

  @ApiModelProperty(value = "Start time of the event. If event's start time is after application's start time " +
    "it means that period between those days is reserved for building structures.", required = true)
  public ZonedDateTime getEventStartTime() {
    return eventStartTime;
  }

  public void setEventStartTime(ZonedDateTime eventStartTime) {
    this.eventStartTime = eventStartTime;
  }

  @ApiModelProperty(value = "End time of the event. If event's end time is before application's end time " +
    "it means that period between those days is reserved for disassembling structures.", required = true)
  public ZonedDateTime getEventEndTime() {
    return eventEndTime;
  }

  public void setEventEndTime(ZonedDateTime eventEndTime) {
    this.eventEndTime = eventEndTime;
  }

  @JsonIgnore
  public boolean getNotBeforeStartTime() {
    return !TimeUtil.isDateBefore(eventStartTime, getStartTime());
  }

  @JsonIgnore
  public boolean getNotAfterEndTime() {
    return !TimeUtil.isDateAfter(eventEndTime, getEndTime());
  }
}
