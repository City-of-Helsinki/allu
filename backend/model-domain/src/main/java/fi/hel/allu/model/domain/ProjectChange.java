package fi.hel.allu.model.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ProjectChange {
  @NotNull
  private Integer userId;

  @Valid
  @NotNull
  private Project project;

  public ProjectChange() {
  }

  public ProjectChange(Integer userId, Project project) {
    this.userId = userId;
    this.project = project;
  }

  public Integer getUserId() {
    return userId;
  }

  public Project getProject() {
    return project;
  }
}
