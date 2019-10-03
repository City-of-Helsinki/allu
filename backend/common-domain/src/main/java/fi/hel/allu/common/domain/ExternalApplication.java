package fi.hel.allu.common.domain;

public class ExternalApplication {
  Integer applicationId;
  Integer informationRequestId;
  String applicationData;

  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  public String getApplicationData() {
    return applicationData;
  }

  public void setApplicationData(String applicationData) {
    this.applicationData = applicationData;
  }

  public Integer getInformationRequestId() {
    return informationRequestId;
  }

  public void setInformationRequestId(Integer informationRequestId) {
    this.informationRequestId = informationRequestId;
  }

}
