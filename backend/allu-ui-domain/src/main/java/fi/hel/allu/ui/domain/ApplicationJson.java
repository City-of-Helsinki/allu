package fi.hel.allu.ui.domain;

import java.time.ZonedDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.StatusType;

/**
 * in Finnish: Hakemus
 */
public class ApplicationJson {

  private Integer id;
  @Valid
  private ProjectJson project;
  private String handler;
  private StatusType status;
  @NotNull(message = "{application.type}")
  private ApplicationType type;
  @NotNull(message = "{application.metadata}")
  private StructureMetaJson metadata;
  @NotBlank(message = "{application.name}")
  private String name;
  private ZonedDateTime creationTime;
  @NotNull(message = "{application.startTime}")
  private ZonedDateTime startTime;
  @NotNull(message = "{application.endTime}")
  private ZonedDateTime endTime;

  @NotNull(message = "{application.applicant}")
  @Valid
  private ApplicantJson applicant;
  @Valid
  private List<ContactJson> contactList;
  @Valid
  private LocationJson location;
  @NotNull(message = "{application.event}")
  @Valid
  private EventJson event;
  private ZonedDateTime decisionTime;
  @Valid
  private List<AttachmentInfoJson> attachmentList;

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
   * in Finnish: Hanke, johon hakemus liittyy
   */
  public ProjectJson getProject() {
    return project;
  }

  public void setProject(ProjectJson project) {
    this.project = project;
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
   * @return  Metadata related to the application.
   */
  public StructureMetaJson getMetadata() {
    return metadata;
  }

  public void setMetadata(StructureMetaJson metadata) {
    this.metadata = metadata;
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
   * in Finnish: Hakemuksen luontiaika
   */
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  /**
   * The starting time the application is active i.e. the starting time certain land area is reserved by the application.
   *
   * @return  starting time the application is active i.e. the starting time certain land area is reserved by the application.
   */
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  /**
   * The ending time the application is active i.e. the time certain land area stops being reserved by the application.
   *
   * @return  ending time the application is active i.e. the time certain land area stops being reserved by the application.
   */
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  /**
   * in Finnish: Hakemuksen hakija
   */
  public ApplicantJson getApplicant() {
    return applicant;
  }

  public void setApplicant(ApplicantJson applicant) {
    this.applicant = applicant;
  }

  /**
   * in Finnish: Hakemuksen yhteyshenkilöt
   */
  public List<ContactJson> getContactList() {
    return contactList;
  }

  public void setContactList(List<ContactJson> contactList) {
    this.contactList = contactList;
  }

  /**
   * in Finnish: Hakemuksen sijainti
   */
  public LocationJson getLocation() {
    return location;
  }

  public void setLocation(LocationJson location) {
    this.location = location;
  }

  /**
   * in Finnish: Tapahtuma
   */
  public EventJson getEvent() {
    return event;
  }

  public void setEvent(EventJson event) {
    this.event = event;
  }

  /**
   *
   * in Finnish: Päätöksen aikaleima
   */
  public ZonedDateTime getDecisionTime() {
    return decisionTime;
  }

  public void setDecisionTime(ZonedDateTime decisionTime) {
    this.decisionTime = decisionTime;
  }

  /**
   * in Finnish: Hakemuksen liitteet
   */
  public List<AttachmentInfoJson> getAttachmentList() {
    return attachmentList;
  }

  public void setAttachmentList(List<AttachmentInfoJson> attachmentList) {
    this.attachmentList = attachmentList;
  }
}
