package fi.hel.allu.external.domain;
import io.swagger.v3.oas.annotations.media.Schema;

public class EventAdditionalDetails {
  private String url;
  private int attendees;
  private int entryFee;
  private Boolean ecoCompass;
  private String foodProviders;
  private String marketingProviders;
  private String timeExceptions;

  @Schema(description = "Event's web page url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Schema(description = "Number of attendees (Yleisömäärä)")
  public int getAttendees() {
    return attendees;
  }

  public void setAttendees(int attendees) {
    this.attendees = attendees;
  }

  @Schema(description = "Entry fee for the event if applicable")
  public int getEntryFee() {
    return entryFee;
  }

  @Schema(description = "Entry fee for the event if applicable")
  public void setEntryFee(int entryFee) {
    this.entryFee = entryFee;
  }

  @Schema(description = "Applicant has EcoCompass event sertificate")
  public Boolean getEcoCompass() {
    return ecoCompass;
  }

  public void setEcoCompass(boolean ecoCompass) {
    this.ecoCompass = ecoCompass;
  }

  @Schema(description = "Description of food sales or services")
  public String getFoodProviders() {
    return foodProviders;
  }

  public void setFoodProviders(String foodProviders) {
    this.foodProviders = foodProviders;
  }

  @Schema(description = "Description of marketing or commercials")
  public String getMarketingProviders() {
    return marketingProviders;
  }

  public void setMarketingProviders(String marketingProviders) {
    this.marketingProviders = marketingProviders;
  }

  @Schema(description = "Description of event time exceptions")
  public String getTimeExceptions() {
    return timeExceptions;
  }

  public void setTimeExceptions(String timeExceptions) {
    this.timeExceptions = timeExceptions;
  }
}
