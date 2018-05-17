package fi.hel.allu.external.mapper;

import java.util.Optional;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.external.domain.ContactExt;
import fi.hel.allu.external.domain.CustomerExt;
import fi.hel.allu.external.domain.CustomerWithContactsExt;
import fi.hel.allu.external.domain.InvoicingCustomerExt;
import fi.hel.allu.servicecore.domain.ContactJson;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.domain.CustomerWithContactsJson;
import fi.hel.allu.servicecore.domain.PostalAddressJson;

public class CustomerExtMapper {

  public static CustomerWithContactsJson mapCustomerWithContactsJson(CustomerWithContactsExt customerWithContactsExt) {
    CustomerWithContactsJson customerWithContacts = new  CustomerWithContactsJson();
    customerWithContacts.setRoleType(CustomerRoleType.APPLICANT);
    customerWithContacts.setCustomer(mapCustomerJson(customerWithContactsExt.getCustomer()));
    for (ContactExt contact : customerWithContactsExt.getContacts()) {
      customerWithContacts.getContacts().add(mapContactJson(contact));
    }
    return customerWithContacts;
  }

  private static ContactJson mapContactJson(ContactExt contactExt) {
     ContactJson contact = new ContactJson();
     contact.setActive(true);
     contact.setEmail(contactExt.getEmail());
     contact.setName(contactExt.getName());
     contact.setPhone(contactExt.getPhone());
     if (contactExt.getPostalAddress() != null) {
       contact.setCity(contactExt.getPostalAddress().getCity());
       contact.setPostalCode(contactExt.getPostalAddress().getPostalCode());
       contact.setStreetAddress(contactExt.getPostalAddress().getStreetAddressAsString());
     }
     return contact;
  }

  public static CustomerJson mapCustomerJson(CustomerExt customerExt) {
    CustomerJson customerJson = new CustomerJson();
    customerJson.setId(customerExt.getId());
    customerJson.setName(customerExt.getName());
    customerJson.setType(customerExt.getType());
    customerJson.setRegistryKey(customerExt.getRegistryKey());
    customerJson.setOvt(customerExt.getOvt());
    customerJson.setEmail(customerExt.getEmail());
    customerJson.setPhone(customerExt.getPhone());
    customerJson.setInvoicingOperator(customerExt.getInvoicingOperator());
    if (customerExt.getPostalAddress() != null) {
      customerJson.setPostalAddress(new PostalAddressJson(
          customerExt.getPostalAddress().getStreetAddressAsString(),
          customerExt.getPostalAddress().getPostalCode(),
          customerExt.getPostalAddress().getCity()));
    }
    customerJson.setActive(true);
    return customerJson;
  }

  /**
   * Update current customerJson with the customerExt data. Updates only properties having non-null value
   * in customerExt
   */
  public static CustomerJson mergeCustomerJson(CustomerJson currentCustomerJson, InvoicingCustomerExt customerExt) {
    Optional.ofNullable(customerExt.getName()).ifPresent(s -> currentCustomerJson.setName(s));
    Optional.ofNullable(customerExt.getType()).ifPresent(s -> currentCustomerJson.setType(s));
    Optional.ofNullable(customerExt.getRegistryKey()).ifPresent(s -> currentCustomerJson.setRegistryKey(s));
    Optional.ofNullable(customerExt.getOvt()).ifPresent(s -> currentCustomerJson.setOvt(s));
    Optional.ofNullable(customerExt.getEmail()).ifPresent(s -> currentCustomerJson.setEmail(s));
    Optional.ofNullable(customerExt.getPhone()).ifPresent(s -> currentCustomerJson.setPhone(s));
    Optional.ofNullable(customerExt.getSapCustomerNumber()).ifPresent(s -> currentCustomerJson.setSapCustomerNumber(s));
    Optional.ofNullable(customerExt.getInvoicingProhibited()).ifPresent(s -> currentCustomerJson.setInvoicingProhibited(s.booleanValue()));
    Optional.ofNullable(customerExt.getInvoicingOperator()).ifPresent(s -> currentCustomerJson.setInvoicingOperator(s));
    Optional.ofNullable(customerExt.getPostalAddress())
       .map(a -> new PostalAddressJson(
          customerExt.getPostalAddress().getStreetAddressAsString(),
          customerExt.getPostalAddress().getPostalCode(),
          customerExt.getPostalAddress().getCity()))
       .ifPresent(a -> currentCustomerJson.setPostalAddress(a));
    return currentCustomerJson;
  }
}
