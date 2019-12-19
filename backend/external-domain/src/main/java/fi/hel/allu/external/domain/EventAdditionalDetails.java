package fi.hel.allu.external.domain;

import io.swagger.annotations.ApiModelProperty;

public class EventAdditionalDetails {
  private String url;
  private int attendees;
  private int entryFee;
  private Boolean ecoCompass;
  private Boolean foodSales;
  private String foodProviders;
  private String marketingProviders;
  private String timeExceptions;

  @ApiModelProperty(value = "Event's web page url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @ApiModelProperty(value = "Number of attendees (Yleisömäärä)")
  public int getAttendees() {
    return attendees;
  }

  public void setAttendees(int attendees) {
    this.attendees = attendees;
  }

  @ApiModelProperty(value = "Entry fee for the event if applicable")
  public int getEntryFee() {
    return entryFee;
  }

  @ApiModelProperty(value = "Entry fee for the event if applicable")
  public void setEntryFee(int entryFee) {
    this.entryFee = entryFee;
  }

  @ApiModelProperty(value = "Applicant has EcoCompass event sertificate")
  public Boolean getEcoCompass() {
    return ecoCompass;
  }

  public void setEcoCompass(boolean ecoCompass) {
    this.ecoCompass = ecoCompass;
  }

  @ApiModelProperty(value = "Description of food sales or services")
  public String getFoodProviders() {
    return foodProviders;
  }

  public void setFoodProviders(String foodProviders) {
    this.foodProviders = foodProviders;
  }

  @ApiModelProperty(value = "Description of marketing or commercials")
  public String getMarketingProviders() {
    return marketingProviders;
  }

  public void setMarketingProviders(String marketingProviders) {
    this.marketingProviders = marketingProviders;
  }

  @ApiModelProperty(value = "Description of event time exceptions")
  public String getTimeExceptions() {
    return timeExceptions;
  }

  public void setTimeExceptions(String timeExceptions) {
    this.timeExceptions = timeExceptions;
  }
}
