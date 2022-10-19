package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.SurfaceHardness;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.servicecore.domain.mapper.UpdatableProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Event specific fields")
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

  @Schema(description = "Application type (always EVENT).", allowableValues="EVENT", required = true)
  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.EVENT;
  }


  @Schema(description = "Event nature. Can be changed only for outdoor events (allowed natures for outdoor events: PUBLIC_FREE, PUBLIC_NONFREE, CLOSED)")
  public EventNature getNature() {
    return nature;
  }

  @UpdatableProperty
  public void setNature(EventNature nature) {
    this.nature = nature;
  }

  @Schema(description = "Event description")
  public String getDescription() {
    return description;
  }

  @UpdatableProperty
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * in Finnish: Tapahtuman WWW-sivu
   */
  @Schema(description = "Event's WWW page")
  public String getUrl() {
    return url;
  }

  @UpdatableProperty
  public void setUrl(String url) {
    this.url = url;
  }

  @Schema(description = "Event start time", required = true)
  public ZonedDateTime getEventStartTime() {
    return eventStartTime;
  }

  @UpdatableProperty
  public void setEventStartTime(ZonedDateTime eventStartTime) {
    this.eventStartTime = eventStartTime;
  }

  @Schema(description = "Event end time", required = true)
  public ZonedDateTime getEventEndTime() {
    return eventEndTime;
  }

  @UpdatableProperty
  public void setEventEndTime(ZonedDateTime eventEndTime) {
    this.eventEndTime = eventEndTime;
  }

  @Schema(description = "Estimated number of attendees")
  public int getAttendees() {
    return attendees;
  }

  @UpdatableProperty
  public void setAttendees(int attendees) {
    this.attendees = attendees;
  }

  @Schema(description = "Entry fee")
  public int getEntryFee() {
    return entryFee;
  }

  @UpdatableProperty
  public void setEntryFee(int entryFee) {
    this.entryFee = entryFee;
  }

  @Schema(description = "True if applicant has EcoCompass")
  public boolean isEcoCompass() {
    return ecoCompass;
  }

  @UpdatableProperty
  public void setEcoCompass(boolean ecoCompass) {
    this.ecoCompass = ecoCompass;
  }

  @Schema(description = "True if there's food sales in event")
  public boolean isFoodSales() {
    return foodSales;
  }

  @UpdatableProperty
  public void setFoodSales(boolean foodSales) {
    this.foodSales = foodSales;
  }

  @Schema(description = "Food providers")
  public String getFoodProviders() {
    return foodProviders;
  }

  @UpdatableProperty
  public void setFoodProviders(String foodProviders) {
    this.foodProviders = foodProviders;
  }

  @Schema(description = "Marketing providers")
  public String getMarketingProviders() {
    return marketingProviders;
  }

  @UpdatableProperty
  public void setMarketingProviders(String marketingProviders) {
    this.marketingProviders = marketingProviders;
  }

  @Schema(description = "Total area of structures in sq. meters")
  public float getStructureArea() {
    return structureArea;
  }

  @UpdatableProperty
  public void setStructureArea(float structureArea) {
    this.structureArea = structureArea;
  }

  @Schema(description = "Description of the structures")
  public String getStructureDescription() {
    return structureDescription;
  }

  @UpdatableProperty
  public void setStructureDescription(String structureDescription) {
    this.structureDescription = structureDescription;
  }


  @Schema(description = "Event time exceptions")
  public String getTimeExceptions() {
    return timeExceptions;
  }

  @UpdatableProperty
  public void setTimeExceptions(String timeExceptions) {
    this.timeExceptions = timeExceptions;
  }

  @Schema(description = "Surface type")
  public SurfaceHardness getSurfaceHardness() {
    return surfaceHardness;
  }

  @UpdatableProperty
  public void setSurfaceHardness(SurfaceHardness surfaceHardness) {
    this.surfaceHardness = surfaceHardness;
  }
}
