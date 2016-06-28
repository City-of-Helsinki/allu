package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import fi.hel.allu.common.types.ApplicationType;
/**
 * In Finnish: hakemus
 */
public class Application {

  private Integer id;
  @NotNull
  private Integer projectId;
  private String handler;
  // @NotNull
  private Integer customerId; // TODO:Remove?
  @NotNull
  private Integer applicantId;
  private String status;
  @NotNull
  private ApplicationType type;
  @NotBlank
  private String name;
  private ZonedDateTime creationTime;
  private Integer locationId;
  @NotNull
  private Event event;

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
  public ApplicationType getType() {
    return type;
  }

  public void setType(ApplicationType type) {
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

  /**
   * in Finnish: Tapahtuman tunniste
   */
  public Event getEvent() {
    return event;
  }

  public void setEvent(Event event) {
    this.event = event;
  }
}
