package fi.hel.allu.ui.domain;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public class OutdoorEventJson extends EventJson {
  private String nature;
  private String description;
  private String url;
  @NotNull(message = "{event.starttime}")
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private int audience;

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
  public int getAudience() {
    return audience;
  }

  public void setAudience(int audience) {
    this.audience = audience;
  }
}
