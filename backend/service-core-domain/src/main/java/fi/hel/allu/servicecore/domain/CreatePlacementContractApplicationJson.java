package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import io.swagger.annotations.ApiModel;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@ApiModel(value = "CreatePlacementContractApplicationJson", description = "Model for creating new placement contracts")
public class CreatePlacementContractApplicationJson extends CreateApplicationJson {

  @Valid
  private CreateCustomerWithContactsJson customerRepresentativeWithContacts;

  public CreateCustomerWithContactsJson getCustomerRepresentativeWithContacts() {
    return customerRepresentativeWithContacts;
  }

  public void setCustomerRepresentativeWithContacts(CreateCustomerWithContactsJson customerRepresentativeWithContacts) {
    this.customerRepresentativeWithContacts = customerRepresentativeWithContacts;
  }

  public Map<CustomerRoleType, CreateCustomerWithContactsJson> getAllCustomersWithContactsByCustomerRoleType() {
    Map<CustomerRoleType, CreateCustomerWithContactsJson> customersWithContacts = new HashMap<>();
    customersWithContacts.putAll(super.getAllCustomersWithContactsByCustomerRoleType());
    if (customerRepresentativeWithContacts != null) {
      customersWithContacts.put(CustomerRoleType.REPRESENTATIVE, customerRepresentativeWithContacts);
    }
    return customersWithContacts;
  }
}
