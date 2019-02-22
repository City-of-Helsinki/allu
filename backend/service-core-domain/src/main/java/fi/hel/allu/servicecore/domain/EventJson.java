package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.SurfaceHardness;
import fi.hel.allu.common.types.EventNature;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Event specific fields")
public class EventJson extends ApplicationExtensionJson {
  private EventNature nature;
  private String description;
  private String url;
  @NotNull(message = "{event.starttime}")
  private ZonedDateTime eventStartTime;
  @NotNull(message = "{event.endtime}")
  private ZonedDateTime eventEndTime;
  private int attendees;
  private int entryFee;
  private boolean ecoCompass;
  private boolean foodSales;
  private String foodProviders;
  private String marketingProviders;
  private float structureArea;
  private String structureDescription;
  private String timeExceptions;
  @NotNull(message = "{event.surfaceHardness}")
  private SurfaceHardness surfaceHardness;

  @ApiModelProperty(value = "Application type (always EVENT).", allowableValues="EVENT", required = true)
  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.EVENT;
  }


  @ApiModelProperty(value = "Event nature")
  public EventNature getNature() {
    return nature;
  }

  public void setNature(EventNature nature) {
    this.nature = nature;
  }

  @ApiModelProperty(value = "Event description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * in Finnish: Tapahtuman WWW-sivu
   */
  @ApiModelProperty(value = "Event's WWW page")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @ApiModelProperty(value = "Event start time", required = true)
  public ZonedDateTime getEventStartTime() {
    return eventStartTime;
  }

  public void setEventStartTime(ZonedDateTime eventStartTime) {
    this.eventStartTime = eventStartTime;
  }

  @ApiModelProperty(value = "Event end time", required = true)
  public ZonedDateTime getEventEndTime() {
    return eventEndTime;
  }

  public void setEventEndTime(ZonedDateTime eventEndTime) {
    this.eventEndTime = eventEndTime;
  }

  @ApiModelProperty(value = "Estimated number of attendees")
  public int getAttendees() {
    return attendees;
  }

  public void setAttendees(int attendees) {
    this.attendees = attendees;
  }

  @ApiModelProperty(value = "Entry fee")
  public int getEntryFee() {
    return entryFee;
  }

  public void setEntryFee(int entryFee) {
    this.entryFee = entryFee;
  }

  @ApiModelProperty(value = "True if applicant has EcoCompass")
  public boolean isEcoCompass() {
    return ecoCompass;
  }

  public void setEcoCompass(boolean ecoCompass) {
    this.ecoCompass = ecoCompass;
  }

  @ApiModelProperty(value = "True if there's food sales in event")
  public boolean isFoodSales() {
    return foodSales;
  }

  public void setFoodSales(boolean foodSales) {
    this.foodSales = foodSales;
  }

  @ApiModelProperty(value = "Food providers")
  public String getFoodProviders() {
    return foodProviders;
  }

  public void setFoodProviders(String foodProviders) {
    this.foodProviders = foodProviders;
  }

  @ApiModelProperty(value = "Marketing providers")
  public String getMarketingProviders() {
    return marketingProviders;
  }

  public void setMarketingProviders(String marketingProviders) {
    this.marketingProviders = marketingProviders;
  }

  @ApiModelProperty(value = "Total area of structures in sq. meters")
  public float getStructureArea() {
    return structureArea;
  }

  public void setStructureArea(float structureArea) {
    this.structureArea = structureArea;
  }

  @ApiModelProperty(value = "Description of the structures")
  public String getStructureDescription() {
    return structureDescription;
  }

  public void setStructureDescription(String structureDescription) {
    this.structureDescription = structureDescription;
  }


  @ApiModelProperty(value = "Event time exceptions")
  public String getTimeExceptions() {
    return timeExceptions;
  }

  public void setTimeExceptions(String timeExceptions) {
    this.timeExceptions = timeExceptions;
  }

  @ApiModelProperty(value = "Surface type")
  public SurfaceHardness getSurfaceHardness() {
    return surfaceHardness;
  }

  public void setSurfaceHardness(SurfaceHardness surfaceHardness) {
    this.surfaceHardness = surfaceHardness;
  }
}
