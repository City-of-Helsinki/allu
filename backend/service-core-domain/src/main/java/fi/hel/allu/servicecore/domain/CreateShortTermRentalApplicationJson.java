package fi.hel.allu.servicecore.domain;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "CreateShortTermRentalApplicationJson", description = "Model for creating short term rentals")
public class CreateShortTermRentalApplicationJson extends CreateApplicationJson {

  private Integer customerRepresentativeId;

  public Integer getCustomerRepresentativeId() {
    return customerRepresentativeId;
  }

  public void setCustomerRepresentativeId(Integer customerRepresentativeId) {
    this.customerRepresentativeId = customerRepresentativeId;
  }
}
