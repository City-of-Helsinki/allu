package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;

import java.util.HashMap;
import java.util.Map;

public abstract class CreateApplicationJson extends BaseApplicationJson {

  private CreateCustomerWithContactsJson customerApplicantWithContacts;

  public CreateCustomerWithContactsJson getCustomerApplicantWithContacts() {
    return customerApplicantWithContacts;
  }

  public void setCustomerApplicantWithContacts(CreateCustomerWithContactsJson customerApplicantWithContacts) {
    this.customerApplicantWithContacts = customerApplicantWithContacts;
  }

  public Map<CustomerRoleType, CreateCustomerWithContactsJson> getAllCustomersWithContactsByCustomerRoleType() {
    Map<CustomerRoleType, CreateCustomerWithContactsJson> customersWithContacts = new HashMap<>();
    if (customerApplicantWithContacts != null) {
      customersWithContacts.put(CustomerRoleType.APPLICANT, customerApplicantWithContacts);
    }
    return customersWithContacts;
  }
}
