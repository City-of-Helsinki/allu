package fi.hel.allu.model.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import java.time.ZonedDateTime;

public class OutdoorEvent extends Event {
  private String nature;
  private String description;
  private String url;
  @JsonSerialize(using = ZonedDateTimeSerializer.class)
  private ZonedDateTime startTime;
  @JsonSerialize(using = ZonedDateTimeSerializer.class)
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
