package fi.hel.allu.external.mapper;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.external.domain.*;
import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.CustomerWithContacts;
import fi.hel.allu.servicecore.domain.ContactJson;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.domain.CustomerWithContactsJson;
import fi.hel.allu.servicecore.domain.PostalAddressJson;
import fi.hel.allu.servicecore.service.CodeSetService;

@Component
public class CustomerExtMapper {

  @Autowired
  private CodeSetService codesetService;

  public  CustomerWithContactsJson mapCustomerWithContactsJson(CustomerWithContactsExt customerWithContactsExt, CustomerRoleType roleType) {
    if (customerWithContactsExt == null) {
      return null;
    }
    CustomerWithContactsJson customerWithContacts = new  CustomerWithContactsJson();
    customerWithContacts.setRoleType(roleType);
    customerWithContacts.setCustomer(mapCustomerJson(customerWithContactsExt.getCustomer()));
    for (ContactExt contact : customerWithContactsExt.getContacts()) {
      customerWithContacts.getContacts().add(mapContactJson(contact));
    }
    return customerWithContacts;
  }

  private ContactJson mapContactJson(ContactExt contactExt) {
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
     contact.setOrderer(contactExt.getOrderer());
     return contact;
  }

  public CustomerJson mapInvoicingCustomerJson(CustomerExt invoicingCustomer) {
    if (invoicingCustomer == null) {
      return null;
    }
    CustomerJson customerJson = mapCustomerJson(invoicingCustomer);
    customerJson.setInvoicingOnly(true);
    return customerJson;
  }

  public CustomerJson mapCustomerJson(CustomerExt customerExt) {
    CustomerJson customerJson = new CustomerJson();
    customerJson.setId(customerExt.getId());
    customerJson.setName(customerExt.getName());
    customerJson.setType(customerExt.getType());
    customerJson.setRegistryKey(customerExt.getRegistryKey());
    customerJson.setOvt(customerExt.getOvt());
    customerJson.setEmail(customerExt.getEmail());
    customerJson.setPhone(customerExt.getPhone());
    customerJson.setInvoicingOperator(customerExt.getInvoicingOperator());
    customerJson.setCountry(customerExt.getCountry());
    customerJson.setSapCustomerNumber(customerExt.getSapCustomerNumber());
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
  public CustomerJson mergeCustomerJson(CustomerJson currentCustomerJson, InvoicingCustomerExt customerExt) {
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

  public CustomerWithContactsExt mapCustomerWithContactsExt(CustomerWithContacts customerWithContacts) {
    CustomerWithContactsExt customerWithContactsExt = new CustomerWithContactsExt();
    customerWithContactsExt.setCustomer(mapCustomerExt(customerWithContacts.getCustomer()));
    customerWithContactsExt.setContacts(customerWithContacts.getContacts().stream().map(c -> mapContactExt(c)).collect(Collectors.toList()));
    return customerWithContactsExt;
  }

  private CustomerExt mapCustomerExt(Customer customer) {
    CustomerExt customerExt = new CustomerExt();
    customerExt.setCountry(getCountryCode(customer.getCountryId()));
    customerExt.setEmail(customer.getEmail());
    customerExt.setId(customer.getId());
    customerExt.setInvoicingOperator(customer.getInvoicingOperator());
    customerExt.setName(customer.getName());
    customerExt.setOvt(customer.getOvt());
    customerExt.setPhone(customer.getPhone());
    if (customer.getType() != CustomerType.PERSON) {
      customerExt.setRegistryKey(customer.getRegistryKey());
    }
    Optional.ofNullable(customer.getPostalAddress())
        .map(p -> new PostalAddressExt(new StreetAddressExt(p.getStreetAddress()), p.getPostalCode(), p.getCity()))
        .ifPresent(p -> customerExt.setPostalAddress(p));
    customerExt.setSapCustomerNumber(customer.getSapCustomerNumber());
    customerExt.setType(customer.getType());
    return customerExt;
  }

  private String getCountryCode(Integer countryId) {
    return Optional.ofNullable(countryId).map(id -> codesetService.findById(id).getCode()).orElse(null);
  }

  private static ContactExt mapContactExt(Contact contact) {
    ContactExt contactExt = new ContactExt();
    contactExt.setEmail(contact.getEmail());
    contactExt.setId(contact.getId());
    contactExt.setName(contact.getName());
    contactExt.setOrderer(contact.getOrderer());
    contactExt.setPhone(contact.getPhone());
    Optional.ofNullable(contact.getPostalAddress())
        .map(p -> new PostalAddressExt(new StreetAddressExt(p.getStreetAddress()), p.getPostalCode(), p.getCity()))
        .ifPresent(p -> contactExt.setPostalAddress(p));
    return contactExt;
  }

}
