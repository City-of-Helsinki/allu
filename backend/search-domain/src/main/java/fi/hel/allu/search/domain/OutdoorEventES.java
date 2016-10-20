package fi.hel.allu.search.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

// TODO: Perttu 13.7.16.: don't copy this approach for further types. Implement generic mapping instead of using type specifc classes!
public class OutdoorEventES extends ApplicationTypeDataES {
  @NotBlank
  private String nature;
  @NotBlank
  private String description;
  private String url;
  @NotNull
  private ZonedDateTime startTime;
  @NotNull  private ZonedDateTime endTime;
  private int attendees;
  private int entryFee;
  private String ecoCompass;
  private String noPriceReason;
  private String foodSales;
  private String foodProviders;
  private String marketingProviders;
  private float structureArea;
  private String structureDescription;
  private ZonedDateTime structureStartTime;
  private ZonedDateTime structureEndTime;
  private String timeExceptions;

  public String getNature() {
    return nature;
  }

  public void setNature(String nature) {
    this.nature = nature;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  public int getAttendees() {
    return attendees;
  }

  public void setAttendees(int attendees) {
    this.attendees = attendees;
  }

  public int getEntryFee() {
    return entryFee;
  }

  public void setEntryFee(int entryFee) {
    this.entryFee = entryFee;
  }

  public String getEcoCompass() {
    return ecoCompass;
  }

  public void setEcoCompass(String ecoCompass) {
    this.ecoCompass = ecoCompass;
  }

  public String getNoPriceReason() {
    return noPriceReason;
  }

  public void setNoPriceReason(String noPriceReason) {
    this.noPriceReason = noPriceReason;
  }

  public String getFoodSales() {
    return foodSales;
  }

  public void setFoodSales(String foodSales) {
    this.foodSales = foodSales;
  }

  public String getFoodProviders() {
    return foodProviders;
  }

  public void setFoodProviders(String foodProviders) {
    this.foodProviders = foodProviders;
  }

  public String getMarketingProviders() {
    return marketingProviders;
  }

  public void setMarketingProviders(String marketingProviders) {
    this.marketingProviders = marketingProviders;
  }

  public float getStructureArea() {
    return structureArea;
  }

  public void setStructureArea(float structureArea) {
    this.structureArea = structureArea;
  }

  public String getStructureDescription() {
    return structureDescription;
  }

  public void setStructureDescription(String structureDescription) {
    this.structureDescription = structureDescription;
  }

  public ZonedDateTime getStructureStartTime() {
    return structureStartTime;
  }

  public void setStructureStartTime(ZonedDateTime structureStartTime) {
    this.structureStartTime = structureStartTime;
  }

  public ZonedDateTime getStructureEndTime() {
    return structureEndTime;
  }

  public void setStructureEndTime(ZonedDateTime structureEndTime) {
    this.structureEndTime = structureEndTime;
  }

  public String getTimeExceptions() {
    return timeExceptions;
  }

  public void setTimeExceptions(String timeExceptions) {
    this.timeExceptions = timeExceptions;
  }
}
