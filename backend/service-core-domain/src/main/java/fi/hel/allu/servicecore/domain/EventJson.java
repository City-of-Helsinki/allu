package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.EventNature;

public class EventJson extends ApplicationExtensionJson {
  private EventNature nature;
  @NotBlank(message = "{event.description}")
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
  private ZonedDateTime structureStartTime;
  private ZonedDateTime structureEndTime;
  private String timeExceptions;

  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.EVENT;
  }

  /**
   * in Finnish: Tapahtuman luonne
   */
  public EventNature getNature() {
    return nature;
  }

  public void setNature(EventNature nature) {
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
  public ZonedDateTime getEventStartTime() {
    return eventStartTime;
  }

  public void setEventStartTime(ZonedDateTime eventStartTime) {
    this.eventStartTime = eventStartTime;
  }

  /**
   * in Finnish: Tapahtuman päättymisaika
   */
  public ZonedDateTime getEventEndTime() {
    return eventEndTime;
  }

  public void setEventEndTime(ZonedDateTime eventEndTime) {
    this.eventEndTime = eventEndTime;
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
   * in Finnish: Tapahtuma sisältää elintarvikemyyntiä- tai tarjoilua
   *
   * @return
   */
  public boolean isFoodSales() {
    return foodSales;
  }

  public void setFoodSales(boolean foodSales) {
    this.foodSales = foodSales;
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
