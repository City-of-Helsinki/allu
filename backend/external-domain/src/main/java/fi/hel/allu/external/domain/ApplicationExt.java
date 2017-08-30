package fi.hel.allu.external.domain;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationSpecifier;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Allu application, which is exposed to external users.
 */
public class ApplicationExt {

  private Integer id;
  private Integer projectId;
  @NotEmpty // TODO: add validation: CustomerRoleType.APPLICANT always is required (this could be also added to Application model or ApplicationJson
  private List<CustomerWithContactsExt> customersWithContacts;
  @NotEmpty
  private List<LocationExt> locations;
  @NotNull
  private StatusType status;
  private ApplicationType type;
  private List<ApplicationTagExt> applicationTags;
  private String name;
  private ZonedDateTime creationTime;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  @NotNull
  @Valid
  private ApplicationExtensionExt extension;
  private ZonedDateTime decisionTime;
  @NotEmpty
  private Map<ApplicationKind, List<ApplicationSpecifier>> kindsWithSpecifiers;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  public List<CustomerWithContactsExt> getCustomersWithContacts() {
    return customersWithContacts;
  }

  public void setCustomersWithContacts(List<CustomerWithContactsExt> customersWithContacts) {
    this.customersWithContacts = customersWithContacts;
  }

  public List<LocationExt> getLocations() {
    return locations;
  }

  public void setLocations(List<LocationExt> locations) {
    this.locations = locations;
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

  public List<ApplicationTagExt> getApplicationTags() {
    return applicationTags;
  }

  public void setApplicationTags(List<ApplicationTagExt> applicationTags) {
    this.applicationTags = applicationTags;
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

  public ApplicationExtensionExt getExtension() {
    return extension;
  }

  public void setExtension(ApplicationExtensionExt extension) {
    this.extension = extension;
  }

  public ZonedDateTime getDecisionTime() {
    return decisionTime;
  }

  public void setDecisionTime(ZonedDateTime decisionTime) {
    this.decisionTime = decisionTime;
  }

  /**
   * @return the kindsWithSpecifiers
   */
  public Map<ApplicationKind, List<ApplicationSpecifier>> getKindsWithSpecifiers() {
    return kindsWithSpecifiers;
  }

  /**
   * @param kindsWithSpecifiers the kindsWithSpecifiers to set
   */
  public void setKindsWithSpecifiers(Map<ApplicationKind, List<ApplicationSpecifier>> kindsWithSpecifiers) {
    this.kindsWithSpecifiers = kindsWithSpecifiers;
  }
}
