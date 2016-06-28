package fi.hel.allu.model.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public class OutdoorEvent extends Event {
  @NotBlank
  private String nature;
  @NotBlank
  private String description;
  private String url;
  @NotNull
  private ZonedDateTime startTime;
  @NotNull
  private ZonedDateTime endTime;
  private int attendees;
  private int entryFee;
  private boolean ecoCompass;
  private boolean salesActivity;
  private String pricing;
  private String foodProviders;
  private String marketingProviders;
  private float structureArea;
  private String structureDescription;
  private ZonedDateTime structureStartTime;
  private ZonedDateTime structureEndTime;
  private String timeExceptions;

  /**
   * in Finnish: Tapahtuman luonne
   */
  public String getNature() {
    return nature;
  }

  public void setNature(String nature) {
    this.nature = nature;
  }

  /**
   * in Finnish: Tapahtuman kuvaus
   */
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * in Finnish: Tapahtuman WWW-sivu
   */
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * in Finnish: Tapahtuman alkuaika
   */
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  /**
   * in Finnish: Tapahtuman päättymisaika
   */
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  /**
   * in Finnish: Tapahtuman arvioitu yleisömäärä
   */
  public int getAttendees() {
    return attendees;
  }

  public void setAttendees(int attendees) {
    this.attendees = attendees;
  }

  /**
   * in Finnish: Osallistumismaksu
   */
  public int getEntryFee() {
    return entryFee;
  }

  public void setEntryFee(int entryFee) {
    this.entryFee = entryFee;
  }

  /**
   * in Finnish: Hakijalla ekokompassi
   */
  public boolean isEcoCompass() {
    return ecoCompass;
  }

  public void setEcoCompass(boolean ecoCompass) {
    this.ecoCompass = ecoCompass;
  }

  /**
   * in Finnish: Sisältääkö teemaan sisältymätöntä myynti- tai mainostoimintaa
   */
  public boolean isSalesActivity() {
    return salesActivity;
  }

  public void setSalesActivity(boolean salesActivity) {
    this.salesActivity = salesActivity;
  }

  /**
   * in Finnish: Hinnoitteluperusteet
   */
  public String getPricing() {
    return pricing;
  }

  public void setPricing(String pricing) {
    this.pricing = pricing;
  }

  /**
   * in Finnish: Tapahtuman elintarviketoimijat
   */
  public String getFoodProviders() {
    return foodProviders;
  }

  public void setFoodProviders(String foodProviders) {
    this.foodProviders = foodProviders;
  }

  /**
   * in Finnish: Tapahtuman markkinointitoimijat
   */
  public String getMarketingProviders() {
    return marketingProviders;
  }

  public void setMarketingProviders(String marketingProviders) {
    this.marketingProviders = marketingProviders;
  }

  /**
   * in Finnish: Rakenteiden kokonaisneliömäärä
   */
  public float getStructureArea() {
    return structureArea;
  }

  public void setStructureArea(float structureArea) {
    this.structureArea = structureArea;
  }

  /**
   * in Finnish: Rakenteiden kuvaus
   */
  public String getStructureDescription() {
    return structureDescription;
  }

  public void setStructureDescription(String structureDescription) {
    this.structureDescription = structureDescription;
  }

  /**
   * in Finnish: Rakenteiden rakennuspäivämäärä
   */
  public ZonedDateTime getStructureStartTime() {
    return structureStartTime;
  }

  public void setStructureStartTime(ZonedDateTime structureStartTime) {
    this.structureStartTime = structureStartTime;
  }

  /**
   * in Finnish: Rakenteiden purkupäivämäärä
   */
  public ZonedDateTime getStructureEndTime() {
    return structureEndTime;
  }

  public void setStructureEndTime(ZonedDateTime structureEndTime) {
    this.structureEndTime = structureEndTime;
  }

  /**
   * in Finnish: Tapahtuma-ajan poikkeukset
   */
  public String getTimeExceptions() {
    return timeExceptions;
  }

  public void setTimeExceptions(String timeExceptions) {
    this.timeExceptions = timeExceptions;
  }
}