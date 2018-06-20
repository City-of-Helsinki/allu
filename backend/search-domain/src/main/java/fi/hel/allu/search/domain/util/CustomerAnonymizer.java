package fi.hel.allu.search.domain.util;

import java.util.ArrayList;
import java.util.List;

import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.search.domain.ContactES;
import fi.hel.allu.search.domain.CustomerES;
import fi.hel.allu.search.domain.CustomerWithContactsES;
import fi.hel.allu.search.domain.RoleTypedCustomerES;

/**
 * Removes all personal data from customers and their contacts.
 */
public class CustomerAnonymizer {

  private static final String ANONYMIZED_NAME = "Yksityishenkilö";

  public static void anonymize(RoleTypedCustomerES customers) {
    customers.setApplicant(anonymizeCustomerWithContacts(customers.getApplicant()));
    customers.setContractor(anonymizeCustomerWithContacts(customers.getContractor()));
    customers.setPropertyDeveloper(anonymizeCustomerWithContacts(customers.getPropertyDeveloper()));
    customers.setRepresentative(anonymizeCustomerWithContacts(customers.getRepresentative()));
  }


  private static CustomerWithContactsES anonymizeCustomerWithContacts(CustomerWithContactsES cwc) {
    return cwc != null ?
        new CustomerWithContactsES(
        anonymizeCustomer(cwc.getCustomer()),
        anonymizeContacts(cwc.getContacts())) : null;
  }

  private static CustomerES anonymizeCustomer(CustomerES customer) {
    if (customer.getType() == CustomerType.PERSON) {
      CustomerES anonymizedCustomer = new CustomerES();
      anonymizedCustomer.setType(CustomerType.PERSON);
      anonymizedCustomer.setName(ANONYMIZED_NAME);
      return anonymizedCustomer;
    } else {
      return customer;
    }
  }

  private static List<ContactES> anonymizeContacts(List<ContactES> contacts) {
    List <ContactES> anonymizedContacts = new ArrayList<>();
    contacts.forEach(c -> anonymizedContacts.add(anonymizeContact(c)));
    return anonymizedContacts;
  }

  private static ContactES anonymizeContact(ContactES contact) {
    ContactES anonymizedContact = new ContactES();
    anonymizedContact.setName(ANONYMIZED_NAME);
    return anonymizedContact;
  }
}
