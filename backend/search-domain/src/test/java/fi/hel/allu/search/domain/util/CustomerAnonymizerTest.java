package fi.hel.allu.search.domain.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.search.domain.ContactES;
import fi.hel.allu.search.domain.CustomerES;
import fi.hel.allu.search.domain.CustomerWithContactsES;
import fi.hel.allu.search.domain.RoleTypedCustomerES;

import static org.junit.Assert.assertEquals;

public class CustomerAnonymizerTest {

  private CustomerES personCustomer;
  private CustomerES companyCustomer;
  private ContactES contact;
  private List<ContactES> contacts;

  @Before
  public void setup() {
    personCustomer = new CustomerES();
    personCustomer.setType(CustomerType.PERSON);
    personCustomer.setName("Person Customer");
    personCustomer.setRegistryKey("010188-012C");

    companyCustomer = new CustomerES();
    companyCustomer.setType(CustomerType.COMPANY);
    companyCustomer.setName("Company");
    companyCustomer.setRegistryKey("010188-012C");

    contact = new ContactES();
    contact.setName("Person Contact");

    contacts = new ArrayList<>();
    contacts.add(contact);
  }

  @Test
  public void testAnonymizingPersonCustomer() {

    final CustomerWithContactsES personCwc = new CustomerWithContactsES(personCustomer, contacts);
    final RoleTypedCustomerES customers = new RoleTypedCustomerES();
    customers.setApplicant(personCwc);
    CustomerAnonymizer.anonymize(customers);

    final CustomerWithContactsES anonymizedPersonCwc = customers.getApplicant();
    final CustomerES anonymizedCustomer = anonymizedPersonCwc.getCustomer();
    assertEquals(CustomerType.PERSON, anonymizedCustomer.getType());
    assertEquals("Yksityishenkilö", anonymizedCustomer.getName());
    assertEquals(null, anonymizedCustomer.getRegistryKey());

    assertEquals(1, anonymizedPersonCwc.getContacts().size());
    testContact(anonymizedPersonCwc.getContacts().get(0));
  }

  @Test
  public void testAnonymizingCompanyCustomer() {
    final CustomerWithContactsES companyCwc = new CustomerWithContactsES(companyCustomer, contacts);
    final RoleTypedCustomerES customers = new RoleTypedCustomerES();
    customers.setApplicant(companyCwc);
    CustomerAnonymizer.anonymize(customers);

    final CustomerWithContactsES anonymizedCompanyCwc = customers.getApplicant();
    final CustomerES anonymizedCustomer = anonymizedCompanyCwc.getCustomer();
    assertEquals(CustomerType.COMPANY, anonymizedCustomer.getType());
    assertEquals(companyCustomer.getName(), anonymizedCustomer.getName());
    assertEquals(companyCustomer.getRegistryKey(), anonymizedCustomer.getRegistryKey());

    assertEquals(1, anonymizedCompanyCwc.getContacts().size());
    testContact(anonymizedCompanyCwc.getContacts().get(0));
  }

  private void testContact(ContactES anonymizedContact) {
    assertEquals("Yksityishenkilö", anonymizedContact.getName());
  }
}
