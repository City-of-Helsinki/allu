package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import io.swagger.annotations.ApiModel;

import java.util.HashMap;
import java.util.Map;

@ApiModel(value = "CreateCableReportApplication", description = "Model for creating new cable reports")
public class CreateCableReportApplicationJson extends CreateApplicationJson {

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

  public Map<CustomerRoleType, Integer> getAllCustomerIdsByCustomerRoleType() {
    Map<CustomerRoleType, Integer> ids = new HashMap<>();
    ids.putAll(super.getAllCustomerIdsByCustomerRoleType());
    if (customerPropertyDeveloperId != null) {
      ids.put(CustomerRoleType.PROPERTY_DEVELOPER, customerPropertyDeveloperId);
    }
    if (customerContractorId != null) {
      ids.put(CustomerRoleType.CONTRACTOR, customerContractorId);
    }
    if (customerRepresentativeId != null) {
      ids.put(CustomerRoleType.REPRESENTATIVE, customerRepresentativeId);
    }
    return ids;
  }
}
