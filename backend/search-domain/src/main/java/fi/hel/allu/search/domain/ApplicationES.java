package fi.hel.allu.search.domain;


import fi.hel.allu.common.types.ApplicationType;
import org.hibernate.validator.constraints.NotBlank;

import java.time.ZonedDateTime;

public class ApplicationES {
  @NotBlank
  private Integer id;
  private String handler;
  private String status;
  private ApplicationType type;
  private String name;
  private ZonedDateTime creationTime;
  private ApplicationTypeDataES applicationTypeData;
  private ProjectES project;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getHandler() {
    return handler;
  }

  public void setHandler(String handler) {
    this.handler = handler;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
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

  public ApplicationTypeDataES getApplicationTypeData() {
    return applicationTypeData;
  }

  public void setApplicationTypeData(ApplicationTypeDataES applicationTypeData) {
    this.applicationTypeData = applicationTypeData;
  }

  public ProjectES getProject() {
    return project;
  }

  public void setProject(ProjectES project) {
    this.project = project;
  }
}
