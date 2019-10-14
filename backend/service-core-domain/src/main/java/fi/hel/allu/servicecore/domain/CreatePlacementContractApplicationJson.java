package fi.hel.allu.servicecore.domain;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "CreatePlacementContractApplicationJson", description = "Model for creating new placement contracts")
public class CreatePlacementContractApplicationJson extends CreateApplicationJson {

  private Integer customerRepresentativeId;

  public Integer getCustomerRepresentativeId() {
    return customerRepresentativeId;
  }

  public void setCustomerRepresentativeId(Integer customerRepresentativeId) {
    this.customerRepresentativeId = customerRepresentativeId;
  }
}
