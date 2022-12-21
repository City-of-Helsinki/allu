package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.servicecore.domain.ContactJson;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.domain.CustomerWithContactsJson;
import fi.hel.allu.servicecore.domain.PostalAddressJson;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    CustomerJson anonymizedCustomer = new CustomerJson();
    if (customer.getType() == CustomerType.PERSON) {
      anonymizedCustomer.setType(CustomerType.PERSON);
      anonymizedCustomer.setName(ANONYMIZED_NAME);
      anonymizedCustomer.setPostalAddress(new PostalAddressJson());
    } else {
      anonymizedCustomer.setId(customer.getId());
      anonymizedCustomer.setName(customer.getName());
      anonymizedCustomer.setActive(customer.isActive());
      anonymizedCustomer.setCountry(customer.getCountry());
      anonymizedCustomer.setInvoicingOnly(customer.isInvoicingOnly());
      anonymizedCustomer.setInvoicingProhibited(customer.isInvoicingProhibited());
      anonymizedCustomer.setInvoicingOperator(customer.getInvoicingOperator());
      anonymizedCustomer.setOvt(customer.getOvt());
      anonymizedCustomer.setProjectIdentifierPrefix(customer.getProjectIdentifierPrefix());
      anonymizedCustomer.setRegistryKey(customer.getRegistryKey());
      anonymizedCustomer.setSapCustomerNumber(customer.getSapCustomerNumber());
      anonymizedCustomer.setType(customer.getType());
      anonymizedCustomer.setPostalAddress(new PostalAddressJson());
      anonymizedCustomer.setEmail("");
      anonymizedCustomer.setPhone("");
    }
    return anonymizedCustomer;
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