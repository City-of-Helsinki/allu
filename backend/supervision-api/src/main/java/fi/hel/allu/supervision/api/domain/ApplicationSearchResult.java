package fi.hel.allu.supervision.api.domain;

import java.time.ZonedDateTime;
import java.util.List;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Application search result")
public class ApplicationSearchResult {

  private Integer id;
  private String applicationId;
  private Integer projectId;
  private String projectIdentifier;
  private String ownerUserName;
  private String ownerRealName;
  private StatusType status;
  private ApplicationType type;
  private List<ApplicationTagType> applicationTags;
  private Integer applicantId;
  private String applicantName;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private List<LocationSearchResult> locations;

  @Schema(description = "Id of the application")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(description = "Application identifier (hakemustunniste)")
  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  @Schema(description = "Project identifier (hanketunniste)")
  public String getProjectIdentfier() {
    return projectIdentifier;
  }

  public void setProjectIdentifier(String projectIdentifier) {
    this.projectIdentifier = projectIdentifier;
  }

  @Schema(description = "Id of the project")
  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  @Schema(description = "Application owner username")
  public String getOwnerUserName() {
    return ownerUserName;
  }

  public void setOwnerUserName(String ownerUserName) {
    this.ownerUserName = ownerUserName;
  }

  @Schema(description = "Application owner real name")
  public String getOwnerRealName() {
    return ownerRealName;
  }

  public void setOwnerRealName(String ownerRealName) {
    this.ownerRealName = ownerRealName;
  }

  @Schema(description = "Status of the application")
  public StatusType getStatus() {
    return status;
  }

  public void setStatus(StatusType status) {
    this.status = status;
  }

  @Schema(description = "Application type")
  public ApplicationType getType() {
    return type;
  }

  public void setType(ApplicationType type) {
    this.type = type;
  }

  @Schema(description = "Application tags")
  public List<ApplicationTagType> getApplicationTags() {
    return applicationTags;
  }

  public void setApplicationTags(List<ApplicationTagType> applicationTags) {
    this.applicationTags = applicationTags;
  }

  @Schema(description = "Id of the applicant")
  public Integer getApplicantId() {
    return applicantId;
  }

  public void setApplicantId(Integer applicantId) {
    this.applicantId = applicantId;
  }

  @Schema(description = "Name of the applicant")
  public String getApplicantName() {
    return applicantName;
  }

  public void setApplicantName(String applicantName) {
    this.applicantName = applicantName;
  }

  @Schema(description = "Application locations")
  public List<LocationSearchResult> getLocations() {
    return locations;
  }

  public void setLocations(List<LocationSearchResult> locations) {
    this.locations = locations;
  }

  @Schema(description = "Starting time of the application")
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  @Schema(description = "Ending time of the application")
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

}
