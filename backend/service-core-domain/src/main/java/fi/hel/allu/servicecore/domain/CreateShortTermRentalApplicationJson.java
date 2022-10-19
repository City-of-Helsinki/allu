package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Schema(name = "CreateShortTermRentalApplicationJson", description = "Model for creating short term rentals")
public class CreateShortTermRentalApplicationJson extends CreateApplicationJson {

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
