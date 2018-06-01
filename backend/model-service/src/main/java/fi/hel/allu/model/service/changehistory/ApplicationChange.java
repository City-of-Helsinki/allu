package fi.hel.allu.model.service.changehistory;

import fi.hel.allu.model.domain.Application;

public class ApplicationChange {
  private final String applicationName;
  private final Integer id;
  private final String applicationId;

  public ApplicationChange(Application application) {
    this.applicationName = application.getName();
    this.id = application.getId();
    this.applicationId = application.getApplicationId();
  }

  public String getApplicationName() {
    return applicationName;
  }

  public Integer getId() {
    return id;
  }

  public String getApplicationId() {
    return applicationId;
  }
}
