package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

/**
 * In Finnish: hakemus
 */
public class Application {

  private Integer applicationId;
  private Integer projectId;
  private String name;
  private String description;
  private String handler;
  private Integer customerId;
  private String status;
  private String type;
  private ZonedDateTime creationTime;

  @JsonSerialize(using = ZonedDateTimeSerializer.class)
  private ZonedDateTime startTime;

  /*
   * Application ID, database primary key
   */
  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  /*
   * Project ID, refers Project.projectId
   */
  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  /*
   * Application name, in Finnish: Hakemuksen nimi
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /*
   * Application description, in Finnish: Hakemuksen kuvaus
   */
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /*
   * Application handler, in Finnish: Hakemuksen käsittelijä
   */
  public String getHandler() {
    return handler;
  }

  public void setHandler(String handler) {
    this.handler = handler;
  }

  /*
   * Application's customer, in Finnish: Hakemuksen asiakas
   */
  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  /*
   * Application's status, in Finnish: Hakemuksen tila
   */
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  /*
   * Application type, in Finnish: Hakemuksen tyyppi
   */
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  /*
   * Creation time, in Finnish: Hakemuksen luontipäivämäärä
   */
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  /*
   * Start time, in Finnish: Hakemuksen aloituspäivämäärä
   */
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

}
