package fi.hel.allu.model.domain.util;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.CustomerWithContacts;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CustomerAnonymizerTest {

  private Customer personCustomer;
  private Customer companyCustomer;
  private Contact contact;
  private List<Contact> contacts;

  @Before
  public void setup() {
    personCustomer = new Customer();
    personCustomer.setType(CustomerType.PERSON);
    personCustomer.setName("Person Customer");
    personCustomer.setEmail("person.customer@example.com");
    personCustomer.setPhone("0401234567");
    personCustomer.setRegistryKey("010188-012C");

    companyCustomer = new Customer();
    companyCustomer.setType(CustomerType.COMPANY);
    companyCustomer.setName("Company");
    companyCustomer.setEmail("company.customer@example.com");
    companyCustomer.setPhone("0401234567");
    companyCustomer.setRegistryKey("010188-012C");

    contact = new Contact();
    contact.setName("Person Contact");
    contact.setEmail("person.contact@example.com");
    contact.setPhone("0401234567");

    contacts = new ArrayList<>();
    contacts.add(contact);
  }

  @Test
  public void testAnonymizingPersonCustomer() {

    final CustomerWithContacts personCwc = new CustomerWithContacts(CustomerRoleType.APPLICANT, personCustomer, contacts);
    final List<CustomerWithContacts> cwcs = new ArrayList<>();
    cwcs.add(personCwc);
    final List<CustomerWithContacts> anonymizedCwcs = CustomerAnonymizer.anonymize(cwcs);

    assertEquals(1, anonymizedCwcs.size());
    final CustomerWithContacts anonymizedPersonCwc = anonymizedCwcs.get(0);
    final Customer anonymizedCustomer = anonymizedPersonCwc.getCustomer();
    assertEquals(CustomerType.PERSON, anonymizedCustomer.getType());
    assertEquals("Yksityishenkilö", anonymizedCustomer.getName());
    assertEquals(null, anonymizedCustomer.getEmail());
    assertEquals(null, anonymizedCustomer.getPhone());
    assertEquals(null, anonymizedCustomer.getRegistryKey());

    assertEquals(1, anonymizedPersonCwc.getContacts().size());
    testContact(anonymizedPersonCwc.getContacts().get(0));
  }

  @Test
  public void testAnonymizingCompanyCustomer() {
    final CustomerWithContacts companyCwc = new CustomerWithContacts(CustomerRoleType.APPLICANT, companyCustomer, contacts);
    final List<CustomerWithContacts> cwcs = new ArrayList<>();
    cwcs.add(companyCwc);
    final List<CustomerWithContacts> anonymizedCwcs = CustomerAnonymizer.anonymize(cwcs);

    assertEquals(1, anonymizedCwcs.size());
    final CustomerWithContacts anonymizedCompanyCwc = anonymizedCwcs.get(0);
    final Customer anonymizedCustomer = anonymizedCompanyCwc.getCustomer();
    assertEquals(CustomerType.COMPANY, anonymizedCustomer.getType());
    assertEquals(companyCustomer.getName(), anonymizedCustomer.getName());
    assertEquals(companyCustomer.getEmail(), anonymizedCustomer.getEmail());
    assertEquals(companyCustomer.getPhone(), anonymizedCustomer.getPhone());
    assertEquals(companyCustomer.getRegistryKey(), anonymizedCustomer.getRegistryKey());

    assertEquals(1, anonymizedCompanyCwc.getContacts().size());
    testContact(anonymizedCompanyCwc.getContacts().get(0));
  }

  private void testContact(Contact anonymizedContact) {
    assertEquals("Yksityishenkilö", anonymizedContact.getName());
    assertEquals(null, anonymizedContact.getEmail());
    assertEquals(null, anonymizedContact.getPhone());
  }
}
