package fi.hel.allu.model.controller;

import fi.hel.allu.common.types.CustomerType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.model.domain.ContactChange;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.testUtils.TestCommon;
import fi.hel.allu.model.testUtils.WebTestCommon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class ContactControllerTest {

  @Autowired
  private WebTestCommon wtc;
  @Autowired
  private TestCommon testCommon;

  @Autowired
  private CustomerDao customerDao;

  private Integer customerId1;
  private Integer customerId2;
  private Integer userId;

  @Before
  public void setup() throws Exception {
    wtc.setup();
    userId = testCommon.insertUser("dummyuser").getId();
    Customer customer = new Customer();
    customer.setType(CustomerType.PERSON);
    customer.setName("Test person 1");
    customer.setRegistryKey("12345676-89");
    customer = customerDao.insert(customer);
    customerId1 = customer.getId();
    customer = new Customer();
    customer.setType(CustomerType.PERSON);
    customer.setName("Test person 2");
    customer.setRegistryKey("99999999-999");
    customer = customerDao.insert(customer);
    customerId2 = customer.getId();
  }

  private Contact createDummyContact() {
    Contact contact = new Contact();
    contact.setCustomerId(customerId1);
    contact.setName("Pho Soup");
    contact.setEmail("phosoup@soppa.org");
    contact.setPhone("555-SOUP");
    return contact;
  }

  @Test
  public void addContact() throws Exception {
    Contact contact = createDummyContact();
    ContactChange contactChange = new ContactChange(userId, Collections.singletonList(contact));
    wtc.perform(post("/contacts"), contactChange).andExpect(status().isOk());
  }

  @Test
  public void addContactWithBadOrganization() throws Exception {
    Contact contact = createDummyContact();
    contact.setCustomerId(customerId1 + 100);
    ContactChange contactChange = new ContactChange(userId, Collections.singletonList(contact));
    wtc.perform(post("/contacts"), contactChange).andExpect(status().isBadRequest());
  }

  @Test
  public void addContactWithoutOrganization() throws Exception {
    Contact contact = createDummyContact();
    contact.setCustomerId(null);
    ContactChange contactChange = new ContactChange(userId, Collections.singletonList(contact));
    wtc.perform(post("/contacts"), contactChange).andExpect(status().isBadRequest());
  }

  @Test
  public void addContactWithoutName() throws Exception {
    Contact contact = createDummyContact();
    contact.setName(null);
    ContactChange contactChange = new ContactChange(userId, Collections.singletonList(contact));
    wtc.perform(post("/contacts"), contactChange).andExpect(status().isBadRequest());
  }

  @Test
  public void getContact() throws Exception {
    // First add contact:
    Contact contact = createDummyContact();
    contact.setName("Lino Levy");
    ContactChange contactChange = new ContactChange(userId, Collections.singletonList(contact));
    ResultActions result = wtc.perform(post("/contacts"), contactChange).andExpect(status().isOk());
    Contact inserted = wtc.parseObjectFromResult(result, Contact[].class)[0];
    int id = inserted.getId();
    // Read back, check that name matches
    wtc.perform(get(String.format("/contacts/%d", id))).andExpect(status().isOk()).andExpect(jsonPath("$.id", is(id)))
        .andExpect(jsonPath("$.name", is(contact.getName())));
  }

  @Test
  public void getNonexistentContact() throws Exception {
    wtc.perform(get("/contacts/31337")).andExpect(status().isNotFound());
  }

  @Test
  public void updateContact() throws Exception {
    // First add contact:
    Contact contact = createDummyContact();
    contact.setName("Lino Levy");
    ContactChange contactChange = new ContactChange(userId, Collections.singletonList(contact));
    ResultActions result = wtc.perform(post("/contacts"), contactChange).andExpect(status().isOk());
    Contact inserted = wtc.parseObjectFromResult(result, Contact[].class)[0];
    int id = inserted.getId();
    // Then, change it:
    contact.setEmail("postmaster@mastposter.com");
    contact.setId(id);
    wtc.perform(put(String.format("/contacts")), contactChange).andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id", is(id))).andExpect(jsonPath("$[0].email", is(contact.getEmail())));
  }

  @Test
  public void updateNonexistent() throws Exception {
    Contact contact = createDummyContact();
    contact.setId(314159);
    ContactChange contactChange = new ContactChange(userId, Collections.singletonList(contact));
    wtc.perform(put("/contacts"), contactChange).andExpect(status().isNotFound());
  }

  @Test
  public void findCustomerContacts() throws Exception {
    // Add a few contacts for customer 1
    for (int i = 0; i < 10; ++i) {
      Contact contact = createDummyContact();
      contact.setName(String.format("Dummy contact %d", i));
      ContactChange contactChange = new ContactChange(userId, Collections.singletonList(contact));
      wtc.perform(post("/contacts"), contactChange).andExpect(status().isOk());
    }
    // Add a few contacts for customer 2
    for (int i = 0; i < 4; ++i) {
      Contact contact = createDummyContact();
      contact.setCustomerId(customerId2);
      contact.setName(String.format("Dummier contact %d", i));
      ContactChange contactChange = new ContactChange(userId, Collections.singletonList(contact));
      wtc.perform(post("/contacts"), contactChange).andExpect(status().isOk());
    }
    // Now get all contacts for customer 1 and make sure there's enough:
    ResultActions resultActions = wtc.perform(get(String.format("/contacts/customer/%d", customerId1)))
        .andExpect(status().isOk());
    Contact[] contacts = wtc.parseObjectFromResult(resultActions, Contact[].class);
    assertEquals(10, contacts.length);

    // Try also with nonexistent customer:
    resultActions = wtc.perform(get(String.format("/contacts/customer/%d", customerId1 + customerId2 + 123)))
        .andExpect(status().isOk());
    contacts = wtc.parseObjectFromResult(resultActions, Contact[].class);
    assertEquals(0, contacts.length);
  }
}
