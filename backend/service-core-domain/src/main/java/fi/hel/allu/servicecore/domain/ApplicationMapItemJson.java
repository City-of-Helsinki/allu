package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;
import java.util.List;

import fi.hel.allu.common.domain.types.ApplicationType;

public class ApplicationMapItemJson {
  private Integer id;
  private String applicationId;
  private String name;
  private ApplicationType type;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private ProjectJson project;
  private List<LocationJson> locations;


  public ApplicationMapItemJson() {
  }

  public ApplicationMapItemJson(Integer id, String applicationId, String name, ApplicationType type,
      ZonedDateTime startTime, ZonedDateTime endTime, ProjectJson project, List<LocationJson> locations) {
    this.id = id;
    this.applicationId = applicationId;
    this.name = name;
    this.type = type;
    this.startTime = startTime;
    this.endTime = endTime;
    this.project = project;
    this.locations = locations;
  }

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

  public ProjectJson getProject() {
    return project;
  }

  public void setProject(ProjectJson project) {
    this.project = project;
  }

  public List<LocationJson> getLocations() {
    return locations;
  }

  public void setLocations(List<LocationJson> locations) {
    this.locations = locations;
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

}
