package fi.hel.allu.servicecore.domain.search;

import java.util.List;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "Application search result")
public class ApplicationSearchResult {

  private Integer id;
  private String applicationId;
  private String projectIdentier;
  private Integer projectId;
  private String projectIdentifer;
  private String ownerUserName;
  private String ownerRealName;
  private StatusType status;
  private ApplicationType type;
  private List<ApplicationTagType> applicationTags;
  private Integer applicantId;
  private String applicantName;
  private List<LocationSearchResult> locations;

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

  public String getProjectIdentier() {
    return projectIdentier;
  }

  public void setProjectIdentier(String projectIdentier) {
    this.projectIdentier = projectIdentier;
  }

  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  public String getProjectIdentifer() {
    return projectIdentifer;
  }

  public void setProjectIdentifer(String projectIdentifer) {
    this.projectIdentifer = projectIdentifer;
  }

  public String getOwnerUserName() {
    return ownerUserName;
  }

  public void setOwnerUserName(String ownerUserName) {
    this.ownerUserName = ownerUserName;
  }

  public String getOwnerRealName() {
    return ownerRealName;
  }

  public void setOwnerRealName(String ownerRealName) {
    this.ownerRealName = ownerRealName;
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

  public List<ApplicationTagType> getApplicationTags() {
    return applicationTags;
  }

  public void setApplicationTags(List<ApplicationTagType> applicationTags) {
    this.applicationTags = applicationTags;
  }

  public Integer getApplicantId() {
    return applicantId;
  }

  public void setApplicantId(Integer applicantId) {
    this.applicantId = applicantId;
  }

  public String getApplicantName() {
    return applicantName;
  }

  public void setApplicantName(String applicantName) {
    this.applicantName = applicantName;
  }

  public List<LocationSearchResult> getLocations() {
    return locations;
  }

  public void setLocations(List<LocationSearchResult> locations) {
    this.locations = locations;
  }

}
