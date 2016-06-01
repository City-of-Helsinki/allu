package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

/**
 * In Finnish: hakemus
 */
public class Application {

  /**
   * in Finnish: Hakemuksen tunniste
   */
  private Integer id;

  /**
   * in Finnish: Hakemukseen liittyvän hankkeen tunniste
   */
  private Integer projectId;

  /**
   * in Finnish: Hakemuksen käsittelijä
   */
  private String handler;

  /**
   * in Finnish: Hakemukseen liittyvän toimeksiantajan tunniste
   */
  private Integer customerId;

  /**
   * in Finnish: Hakemukseen liittyvän hakijan tunniste
   */
  private Integer applicantId;

  /**
   * in Finnish: Hakemuksen tila
   */
  private String status;

  /**
   * in Finnish: Hakemuksen tyyppi
   */
  private String type;

  /**
   * in Finnish: Hakemuksen nimi
   */
  private String name;

  /**
   * in Finnish: Hakemuksen luontiaika
   */
  private ZonedDateTime creationTime;

  /*
   * Application ID, database primary key
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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
   * Application name, in Finnish: Tapahtuman nimi
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public Integer getApplicantId() {
    return applicantId;
  }

  public void setApplicantId(Integer applicantId) {
    this.applicantId = applicantId;
  }
}
