package fi.hel.allu.servicecore.domain;

public abstract class CreateApplicationJson extends BaseApplicationJson {

  private Integer customerApplicantId;

  public Integer getCustomerApplicantId() {
    return customerApplicantId;
  }

  public void setCustomerApplicantId(Integer customerApplicantId) {
    this.customerApplicantId = customerApplicantId;
  }

}
