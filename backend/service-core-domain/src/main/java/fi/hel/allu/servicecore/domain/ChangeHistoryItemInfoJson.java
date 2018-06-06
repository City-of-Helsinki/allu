package fi.hel.allu.servicecore.domain;

public class ChangeHistoryItemInfoJson {
  private Integer id;
  private String name;
  private String applicationId;

  public ChangeHistoryItemInfoJson() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }
}
