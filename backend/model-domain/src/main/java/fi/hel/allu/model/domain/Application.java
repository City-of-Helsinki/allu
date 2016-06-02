package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

/**
 * In Finnish: hakemus
 */
public class Application {

  private Integer id;
  private Integer projectId;
  private String handler;
  private Integer customerId;
  private Integer applicantId;
  private String status;
  private String type;
  private String name;
  private ZonedDateTime creationTime;
  private Integer locationId;

  /**
   * in Finnish: Hakemuksen tunniste
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * in Finnish: Hakemukseen liittyvän hankkeen tunniste
   */
  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  /**
   * in Finnish: Hakemuksen nimi
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * in Finnish: Hakemuksen käsittelijä
   */
  public String getHandler() {
    return handler;
  }

  public void setHandler(String handler) {
    this.handler = handler;
  }

  /**
   * in Finnish: Hakemukseen liittyvän toimeksiantajan tunniste
   */
  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  /**
   * in Finnish: Hakemuksen tila
   */
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * in Finnish: Hakemuksen tyyppi
   */
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  /**
   * in Finnish: Hakemuksen luontiaika
   */
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  /**
   * in Finnish: Hakemukseen liittyvän hakijan tunniste
   */
  public Integer getApplicantId() {
    return applicantId;
  }

  public void setApplicantId(Integer applicantId) {
    this.applicantId = applicantId;
  }

  /**
   * In Finnish: Hakemuksen sijainnin tunniste
   */
  public Integer getLocationId() {
    return locationId;
  }

  public void setLocationId(Integer locationId) {
    this.locationId = locationId;
  }
}
