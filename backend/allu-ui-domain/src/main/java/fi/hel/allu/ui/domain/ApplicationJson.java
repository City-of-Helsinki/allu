package fi.hel.allu.ui.domain;

import java.time.ZonedDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import fi.hel.allu.common.types.ApplicationType;

/**
 * in Finnish: Hakemus
 */
public class ApplicationJson {

  private Integer id;
  @Valid
  private ProjectJson project;
  private String handler;
  // @NotNull(message = "{application.customer}")
  // @Valid
  private CustomerJson customer; // TODO: Remove customer
  private String status;
  @NotNull(message = "{application.type}")
  private ApplicationType type;
  @NotNull(message = "{application.metadata}")
  private StructureMetaJson metadata;
  @NotBlank(message = "{application.name}")
  private String name;
  private ZonedDateTime creationTime;
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

  /**
   * in Finnish: Hakemuksen toimeksiantaja
   */
  public CustomerJson getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerJson customer) {
    this.customer = customer;
  }

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
}
