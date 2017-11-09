package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.EventNature;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Event extends ApplicationExtension {
  @NotNull
  private EventNature nature;
  @NotBlank
  private String description;
  private String url;
  private int attendees;
  private int entryFee;
  private boolean ecoCompass;
  private boolean foodSales;
  private String foodProviders;
  private String marketingProviders;
  private float structureArea;
  private String structureDescription;
  private String timeExceptions;
  @Min(value = 0)
  private long buildSeconds;
  @Min(value = 0)
  private long teardownSeconds;

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
   * in Finnish: Tapahtuma-ajan poikkeukset
   */
  public String getTimeExceptions() {
    return timeExceptions;
  }

  public void setTimeExceptions(String timeExceptions) {
    this.timeExceptions = timeExceptions;
  }

  /**
   * Build time in seconds. 0 if there's no build time needed.
   *
   * @return
   */
  public long getBuildSeconds() {
    return buildSeconds;
  }

  public void setBuildSeconds(long buildSeconds) {
    this.buildSeconds = buildSeconds;
  }

  /**
   * Teardown time in seconds or 0 if no teardown is needed.
   *
   * @return
   */
  public long getTeardownSeconds() {
    return teardownSeconds;
  }

  public void setTeardownSeconds(long teardownSeconds) {
    this.teardownSeconds = teardownSeconds;
  }
}
