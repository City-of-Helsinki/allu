package fi.hel.allu.servicecore.domain;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "CreateEventApplicationJson", description = "Model for creating new events")
public class CreateEventApplicationJson extends CreateApplicationJson {

  private Integer customerRepresentativeId;

  public Integer getCustomerRepresentativeId() {
    return customerRepresentativeId;
  }

  public void setCustomerRepresentativeId(Integer customerRepresentativeId) {
    this.customerRepresentativeId = customerRepresentativeId;
  }
}
