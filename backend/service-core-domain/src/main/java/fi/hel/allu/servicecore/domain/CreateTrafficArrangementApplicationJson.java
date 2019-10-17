package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import io.swagger.annotations.ApiModel;

import java.util.HashMap;
import java.util.Map;

@ApiModel(value = "CreateTrafficArrangementApplicationJson", description = "Model for creating new traffic arrangements")
public class CreateTrafficArrangementApplicationJson extends CreateApplicationJson {

  private CreateCustomerWithContactsJson customerPropertyDeveloperWithContacts;
  private CreateCustomerWithContactsJson customerContractorWithContacts;
  private CreateCustomerWithContactsJson customerRepresentativeWithContacts;

  public CreateCustomerWithContactsJson getCustomerPropertyDeveloperWithContacts() {
    return customerPropertyDeveloperWithContacts;
  }

  public void setCustomerPropertyDeveloperWithContacts(CreateCustomerWithContactsJson customerPropertyDeveloperWithContacts) {
    this.customerPropertyDeveloperWithContacts = customerPropertyDeveloperWithContacts;
  }

  public CreateCustomerWithContactsJson getCustomerContractorWithContacts() {
    return customerContractorWithContacts;
  }

  public void setCustomerContractorWithContacts(CreateCustomerWithContactsJson customerContractorWithContacts) {
    this.customerContractorWithContacts = customerContractorWithContacts;
  }

  public CreateCustomerWithContactsJson getCustomerRepresentativeWithContacts() {
    return customerRepresentativeWithContacts;
  }

  public void setCustomerRepresentativeWithContacts(CreateCustomerWithContactsJson customerRepresentativeWithContacts) {
    this.customerRepresentativeWithContacts = customerRepresentativeWithContacts;
  }

  public Map<CustomerRoleType, CreateCustomerWithContactsJson> getAllCustomersWithContactsByCustomerRoleType() {
    Map<CustomerRoleType, CreateCustomerWithContactsJson> customersWithContacts = new HashMap<>();
    customersWithContacts.putAll(super.getAllCustomersWithContactsByCustomerRoleType());
    if (customerPropertyDeveloperWithContacts != null) {
      customersWithContacts.put(CustomerRoleType.PROPERTY_DEVELOPER, customerPropertyDeveloperWithContacts);
    }
    if (customerContractorWithContacts != null) {
      customersWithContacts.put(CustomerRoleType.CONTRACTOR, customerContractorWithContacts);
    }
    if (customerRepresentativeWithContacts != null) {
      customersWithContacts.put(CustomerRoleType.REPRESENTATIVE, customerRepresentativeWithContacts);
    }
    return customersWithContacts;
  }
}
