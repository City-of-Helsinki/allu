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

  /**
   * Truncates a customer name at the first occurrence of ';' or 'c/o'
   * (case-insensitive, optional whitespace around '/'). This ensures that
   * SAP name rows 2–5 and c/o suffixes are not visible in publishable
   * anonymized decisions.
   *
   * Examples:
   *   "Acme Oy; Toinen rivi"       → "Acme Oy"
   *   "Acme Oy c/o Joku Henkilö"   → "Acme Oy"
   *   "Acme Oy C / O Joku Henkilö" → "Acme Oy"
   *   "Pelkkä Nimi"                → "Pelkkä Nimi"
   */
  static String sanitizeName(String name) {
    if (name == null) {
      return null;
    }
    return name.split("(?i);|c\\s*/\\s*o")[0].trim();
  }

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
      anonymizedCustomer.setName(sanitizeName(customer.getName()));
      anonymizedCustomer.setActive(customer.isActive());
      anonymizedCustomer.setCountry(customer.getCountry());
      anonymizedCustomer.setInvoicingOnly(customer.isInvoicingOnly());
      anonymizedCustomer.setInvoicingProhibited(customer.isInvoicingProhibited());
      anonymizedCustomer.setInvoicingOperator(customer.getInvoicingOperator());
      anonymizedCustomer.setOvt(customer.getOvt());
      anonymizedCustomer.setProjectIdentifierPrefix(customer.getProjectIdentifierPrefix());
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
