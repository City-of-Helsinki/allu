package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;

import java.util.HashMap;
import java.util.Map;

public abstract class CreateApplicationJson extends BaseApplicationJson {

  private Integer customerApplicantId;

  public Integer getCustomerApplicantId() {
    return customerApplicantId;
  }

  public void setCustomerApplicantId(Integer customerApplicantId) {
    this.customerApplicantId = customerApplicantId;
  }

  public Map<CustomerRoleType, Integer> getAllCustomerIdsByCustomerRoleType() {
    Map<CustomerRoleType, Integer> ids = new HashMap<>();
    if (customerApplicantId != null) {
      ids.put(CustomerRoleType.APPLICANT, customerApplicantId);
    }
    return ids;
  }
}
