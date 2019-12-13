package fi.hel.allu.external.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;

@ApiModel("Promotion event (promootio) input model.")
public class PromotionExt extends BaseApplicationExt {

  private List<Integer> fixedLocationIds;
  private Integer structureArea;
  private String structureDescription;
  private String description;
  @NotNull(message = "{event.starttime}")
  private ZonedDateTime eventStartTime;
  @NotNull(message = "{event.endtime}")
  private ZonedDateTime eventEndTime;

  @ApiModelProperty(value = "IDs of the fixed locations. Should be set if geometry of the application is selected from fixed locations.")
  public List<Integer> getFixedLocationIds() {
    return fixedLocationIds;
  }

  public void setFixedLocationIds(List<Integer> fixedLocationIds) {
    this.fixedLocationIds = fixedLocationIds;
  }


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
