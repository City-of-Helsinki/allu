package fi.hel.allu.servicecore.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.servicecore.domain.ContactJson;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.domain.CustomerWithContactsJson;
import fi.hel.allu.servicecore.domain.PostalAddressJson;

@Component
public class CustomerAnonymizer {

  private static final String ANONYMIZED_NAME = "Yksityishenkilö";

  public Optional<CustomerWithContactsJson> anonymizeCustomerWithContacts(Optional<CustomerWithContactsJson> cwc) {
    return cwc.map(c ->
        new CustomerWithContactsJson(
        c.getRoleType(),
        anonymizeCustomer(c.getCustomer()),
        anonymizeContacts(c.getContacts())));
  }

  public CustomerJson anonymizeCustomer(CustomerJson customer) {
    if (customer.getType() == CustomerType.PERSON) {
      CustomerJson anonymizedCustomer = new CustomerJson();
      anonymizedCustomer.setType(CustomerType.PERSON);
      anonymizedCustomer.setName(ANONYMIZED_NAME);
      anonymizedCustomer.setPostalAddress(new PostalAddressJson());
      return anonymizedCustomer;
    } else {
      return customer;
    }
  }

  private List<ContactJson> anonymizeContacts(List<ContactJson> contacts) {
    return contacts.stream().map(c -> anonymizeContact(c)).collect(Collectors.toList());
  }

  public ContactJson anonymizeContact(ContactJson contact) {
    ContactJson anonymizedContact = new ContactJson();
    anonymizedContact.setName(ANONYMIZED_NAME);
    anonymizedContact.setCustomerId(contact.getCustomerId());
    return anonymizedContact;
  }


}
