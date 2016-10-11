package fi.hel.allu.search.domain;


import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.StatusType;
import org.hibernate.validator.constraints.NotBlank;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * ElasticSearch application mapping.
 * <p>NOTE! Whenever you change this class, make sure that the ElasticSearch mapping (i.e. the data schema) is also changed accordingly,
 * because otherwise for example date mappings may not work.
 */
public class ApplicationES {
  @NotBlank
  private Integer id;
  private String applicationId;
  private Integer handler;
  private StatusType status;
  private ApplicationType type;
  private String name;
  private ZonedDateTime creationTime;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private List<ESFlatValue> applicationTypeData;
  private ProjectES project;
  private ZonedDateTime decisionTime;
  List<ContactES> contacts;
  LocationES location;
  ApplicantES applicant;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public Integer getHandler() {
    return handler;
  }

  public void setHandler(Integer handler) {
    this.handler = handler;
  }

  public StatusType getStatus() {
    return status;
  }

  public void setStatus(StatusType status) {
    this.status = status;
  }

  public ApplicationType getType() {
    return type;
  }

  public void setType(ApplicationType type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  public List<ESFlatValue> getApplicationTypeData() {
    return applicationTypeData;
  }

  public void setApplicationTypeData(List<ESFlatValue> applicationTypeData) {
    this.applicationTypeData = applicationTypeData;
  }

  public ProjectES getProject() {
    return project;
  }

  public void setProject(ProjectES project) {
    this.project = project;
  }

  public ZonedDateTime getDecisionTime() {
    return decisionTime;
  }

  public void setDecisionTime(ZonedDateTime decisionTime) {
    this.decisionTime = decisionTime;
  }

  public List<ContactES> getContacts() {
    return contacts;
  }

  public void setContacts(List<ContactES> contacts) {
    this.contacts = contacts;
  }

  public LocationES getLocation() {
    return location;
  }

  public void setLocation(LocationES location) {
    this.location = location;
  }

  public ApplicantES getApplicant() {
    return applicant;
  }

  public void setApplicant(ApplicantES applicantES) {
    this.applicant = applicantES;
  }
}
