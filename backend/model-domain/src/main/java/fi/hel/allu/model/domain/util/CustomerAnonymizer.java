package fi.hel.allu.model.domain.util;

import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.CustomerWithContacts;

import java.util.ArrayList;
import java.util.List;

/**
 * Removes all personal data from customers and their contacts.
 */
public class CustomerAnonymizer {

  private static final String ANONYMIZED_NAME = "Yksityishenkilö";

  public static List<CustomerWithContacts> anonymize(List<CustomerWithContacts> customers) {
    List<CustomerWithContacts> anonymizedCustomers = new ArrayList<>();
    customers.forEach((c) -> anonymizedCustomers.add(anonymizeCustomerWithContacts(c)));
    return anonymizedCustomers;
  }

  private static CustomerWithContacts anonymizeCustomerWithContacts(CustomerWithContacts cwc) {
    return new CustomerWithContacts(
        cwc.getRoleType(),
        anonymizeCustomer(cwc.getCustomer()),
        anonymizeContacts(cwc.getContacts()));
  }

  private static Customer anonymizeCustomer(Customer customer) {
    if (customer.getType() == CustomerType.PERSON) {
      Customer anonymizedCustomer = new Customer();
      anonymizedCustomer.setType(CustomerType.PERSON);
      anonymizedCustomer.setName(ANONYMIZED_NAME);
      return anonymizedCustomer;
    } else {
      return customer;
    }
  }

  private static List<Contact> anonymizeContacts(List<Contact> contacts) {
    List <Contact> anonymizedContacts = new ArrayList<>();
    contacts.forEach(c -> anonymizedContacts.add(anonymizeContact(c)));
    return anonymizedContacts;
  }

  private static Contact anonymizeContact(Contact contact) {
    Contact anonymizedContact = new Contact();
    anonymizedContact.setName(ANONYMIZED_NAME);
    return anonymizedContact;
  }
}
