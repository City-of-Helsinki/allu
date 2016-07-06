package fi.hel.allu.search.domain;


import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.StatusType;
import org.hibernate.validator.constraints.NotBlank;

import java.time.ZonedDateTime;

public class ApplicationES {
  @NotBlank
  private Integer id;
  private String handler;
  private StatusType status;
  private ApplicationType type;
  private String name;
  private ZonedDateTime creationTime;
  private ApplicationTypeDataES applicationTypeData;
  private ProjectES project;
  private ZonedDateTime decisionTime;

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

  public ZonedDateTime getDecisionTime() {
    return decisionTime;
  }

  public void setDecisionTime(ZonedDateTime decisionTime) {
    this.decisionTime = decisionTime;
  }
}
