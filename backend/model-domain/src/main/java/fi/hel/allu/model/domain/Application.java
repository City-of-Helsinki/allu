package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.StatusType;
/**
 * In Finnish: hakemus
 */
public class Application {

  private Integer id;
  @NotNull
  private Integer projectId;
  private String handler;
  @NotNull
  private Integer applicantId;
  private StatusType status;
  @NotNull
  private ApplicationType type;
  @NotNull
  private Integer metadataVersion;
  @NotBlank
  private String name;
  private ZonedDateTime creationTime;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private Integer locationId;
  @NotNull
  private Event event;
  private ZonedDateTime decisionTime;

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
   * in Finnish: Hakemuksen tila
   */
  public StatusType getStatus() {
    return status;
  }

  public void setStatus(StatusType status) {
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
   * Returns the metadata version of the application.
   *
   * @return
   */
  public Integer getMetadataVersion() {
    return metadataVersion;
  }

  public void setMetadataVersion(Integer metadataVersion) {
    this.metadataVersion = metadataVersion;
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
   * The time reservation of a land area begins.
   *
   * @return  time reservation of a land area begins.
   */
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  /**
   * The time reservation of a land area ends.
   *
   * @return  time reservation of a land area ends.
   */
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
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

  /**
   * in Finnish: Päätöksen aikaleima
   */
  public ZonedDateTime getDecisionTime() {
    return decisionTime;
  }

  public void setDecisionTime(ZonedDateTime decisionTime) {
    this.decisionTime = decisionTime;
  }
}
