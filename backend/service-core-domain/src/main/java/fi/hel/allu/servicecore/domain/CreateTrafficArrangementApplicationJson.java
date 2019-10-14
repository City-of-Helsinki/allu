package fi.hel.allu.servicecore.domain;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "CreateTrafficArrangementApplicationJson", description = "Model for creating new traffic arrangements")
public class CreateTrafficArrangementApplicationJson extends CreateApplicationJson {

  private Integer customerPropertyDeveloperId;
  private Integer customerContractorId;
  private Integer customerRepresentativeId;

  public Integer getCustomerPropertyDeveloperId() {
    return customerPropertyDeveloperId;
  }

  public void setCustomerPropertyDeveloperId(Integer customerPropertyDeveloperId) {
    this.customerPropertyDeveloperId = customerPropertyDeveloperId;
  }

  public Integer getCustomerContractorId() {
    return customerContractorId;
  }

  public void setCustomerContractorId(Integer customerContractorId) {
    this.customerContractorId = customerContractorId;
  }

  public Integer getCustomerRepresentativeId() {
    return customerRepresentativeId;
  }

  public void setCustomerRepresentativeId(Integer customerRepresentativeId) {
    this.customerRepresentativeId = customerRepresentativeId;
  }
}
