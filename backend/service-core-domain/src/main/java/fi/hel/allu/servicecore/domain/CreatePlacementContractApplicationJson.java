package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import io.swagger.annotations.ApiModel;

import java.util.HashMap;
import java.util.Map;

@ApiModel(value = "CreatePlacementContractApplicationJson", description = "Model for creating new placement contracts")
public class CreatePlacementContractApplicationJson extends CreateApplicationJson {

  private Integer customerRepresentativeId;

  public Integer getCustomerRepresentativeId() {
    return customerRepresentativeId;
  }

  public void setCustomerRepresentativeId(Integer customerRepresentativeId) {
    this.customerRepresentativeId = customerRepresentativeId;
  }

  public Map<CustomerRoleType, Integer> getAllCustomerIdsByCustomerRoleType() {
    Map<CustomerRoleType, Integer> ids = new HashMap<>();
    ids.putAll(super.getAllCustomerIdsByCustomerRoleType());
    if (customerRepresentativeId != null) {
      ids.put(CustomerRoleType.REPRESENTATIVE, customerRepresentativeId);
    }
    return ids;
  }
}
