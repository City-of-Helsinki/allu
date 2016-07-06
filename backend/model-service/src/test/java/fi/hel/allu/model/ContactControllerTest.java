package fi.hel.allu.model;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.OrganizationDao;
import fi.hel.allu.model.dao.ProjectDao;
import fi.hel.allu.model.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
public class ContactControllerTest {

  @Autowired
  private WebTestCommon wtc;

  @Autowired
  private OrganizationDao organizationDao;
  @Autowired
  private ApplicationDao applicationDao;
  @Autowired
  private ProjectDao projectDao;

  private Integer orgId1;
  private Integer orgId2;

  @Before
  public void setup() throws Exception {
    wtc.setup();
    Organization org = new Organization();
    org.setName("Test organization 1");
    org.setBusinessId("12345676-89");
    org = organizationDao.insert(org);
    orgId1 = org.getId();
    org = new Organization();
    org.setName("Test organization 2");
    org.setBusinessId("99999999-999");
    org = organizationDao.insert(org);
    orgId2 = org.getId();
  }

  private Contact createDummyContact() {
    Contact contact = new Contact();
    contact.setOrganizationId(orgId1);
    contact.setName("Pho Soup");
    contact.setEmail("phosoup@soppa.org");
    contact.setPhone("555-SOUP");
    return contact;
  }

  @Test
  public void addContact() throws Exception {
    Contact contact = createDummyContact();
    wtc.perform(post("/contacts"), contact).andExpect(status().isOk());
  }

  @Test
  public void addContactWithId() throws Exception {
    // add contact with id. Should fail:
    Contact contact = createDummyContact();
    contact.setId(123);
    wtc.perform(post("/contacts"), contact).andExpect(status().isBadRequest());
  }

  @Test
  public void addContactWithBadOrganization() throws Exception {
    Contact contact = createDummyContact();
    contact.setOrganizationId(orgId1 + 100);
    wtc.perform(post("/contacts"), contact).andExpect(status().isBadRequest());
  }

  @Test
  public void addContactWithoutOrganization() throws Exception {
    Contact contact = createDummyContact();
    contact.setOrganizationId(null);
    wtc.perform(post("/contacts"), contact).andExpect(status().isBadRequest());
  }

  @Test
  public void addContactWithoutName() throws Exception {
    Contact contact = createDummyContact();
    contact.setName(null);
    wtc.perform(post("/contacts"), contact).andExpect(status().isBadRequest());
  }

  @Test
  public void getContact() throws Exception {
    // First add contact:
    Contact contact = createDummyContact();
    contact.setName("Lino Levy");
    ResultActions result = wtc.perform(post("/contacts"), contact).andExpect(status().isOk());
    Contact inserted = wtc.parseObjectFromResult(result, Contact.class);
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
    ResultActions result = wtc.perform(post("/contacts"), contact).andExpect(status().isOk());
    Contact inserted = wtc.parseObjectFromResult(result, Contact.class);
    int id = inserted.getId();
    // Then, change it:
    contact.setEmail("postmaster@mastposter.com");
    wtc.perform(put(String.format("/contacts/%d", id)), contact).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(id))).andExpect(jsonPath("$.email", is(contact.getEmail())));
  }

  @Test
  public void updateNonexistent() throws Exception {
    Contact contact = createDummyContact();
    wtc.perform(put("/contacts/314159"), contact).andExpect(status().isNotFound());
  }

  @Test
  public void findOrganizationContacts() throws Exception {
    // Add a few contacts for organization 1
    for (int i = 0; i < 10; ++i) {
      Contact contact = createDummyContact();
      contact.setName(String.format("Dummy contact %d", i));
      wtc.perform(post("/contacts"), contact).andExpect(status().isOk());
    }
    // Add a few contacts for organization 2
    for (int i = 0; i < 4; ++i) {
      Contact contact = createDummyContact();
      contact.setOrganizationId(orgId2);
      contact.setName(String.format("Dummier contact %d", i));
      wtc.perform(post("/contacts"), contact).andExpect(status().isOk());
    }
    // Now get all contacts for organization 1 and make sure there's enough:
    ResultActions resultActions = wtc.perform(get(String.format("/contacts?organizationId=%d", orgId1)))
        .andExpect(status().isOk());
    Contact[] contacts = wtc.parseObjectFromResult(resultActions, Contact[].class);
    assertEquals(10, contacts.length);

    // Try also with nonexistent organization:
    resultActions = wtc.perform(get(String.format("/contacts?organizationId=%d", orgId1 + orgId2 + 123)))
        .andExpect(status().isOk());
    contacts = wtc.parseObjectFromResult(resultActions, Contact[].class);
    assertEquals(0, contacts.length);
  }

  @Test
  public void testApplicationContacts() throws Exception {
    // Add a few contacts
    Contact[] contactsToInsert = new Contact[5];
    for (int i = 0; i < contactsToInsert.length; ++i) {
      Contact contact = createDummyContact();
      contact.setName(String.format("Dummy contact %d", i));
      ResultActions result = wtc.perform(post("/contacts"), contact).andExpect(status().isOk());
      contactsToInsert[i] = wtc.parseObjectFromResult(result, Contact.class);
    }

    Application appl = new Application();
    appl.setType(ApplicationType.OUTDOOREVENT);
    appl.setMetadataVersion(1);
    appl.setName("Dummy apllication");
    OutdoorEvent evt = new OutdoorEvent();
    evt.setDescription("Dummy outdoor event");
    appl.setEvent(evt);
    int applId = applicationDao.insert(appl).getId();

    ResultActions resultActions = wtc
        .perform(put(String.format("/contacts?applicationId=%d", applId)), contactsToInsert).andExpect(status().isOk());
    Contact[] inserted = wtc.parseObjectFromResult(resultActions, Contact[].class);

    // Verify that insertion returns the same objects
    assertEquals(inserted.length, contactsToInsert.length);
    for (int i = 0; i < inserted.length; ++i) {
      assertEquals(inserted[i].getName(), contactsToInsert[i].getName());
    }

    // Then get the contacts via find API:
    resultActions = wtc.perform(get(String.format("/contacts?applicationId=%d", applId))).andExpect(status().isOk());
    Contact[] found = wtc.parseObjectFromResult(resultActions, Contact[].class);

    // Verify that find returned the same objects
    assertEquals(found.length, contactsToInsert.length);
    for (int i = 0; i < found.length; ++i) {
      assertEquals(found[i].getName(), contactsToInsert[i].getName());
    }
  }

  @Test
  public void testProjectContacts() throws Exception {
    // Add a few contacts
    Contact[] contactsToInsert = new Contact[5];
    for (int i = 0; i < contactsToInsert.length; ++i) {
      Contact contact = createDummyContact();
      contact.setName(String.format("Dummy contact %d", i));
      ResultActions result = wtc.perform(post("/contacts"), contact).andExpect(status().isOk());
      contactsToInsert[i] = wtc.parseObjectFromResult(result, Contact.class);
    }

    Project proj = new Project();
    proj.setName("Dummy apllication");
    int projId = projectDao.insert(proj).getId();

    ResultActions resultActions = wtc.perform(put(String.format("/contacts?projectId=%d", projId)), contactsToInsert)
        .andExpect(status().isOk());
    Contact[] inserted = wtc.parseObjectFromResult(resultActions, Contact[].class);

    // Verify that insertion returns the same objects
    assertEquals(inserted.length, contactsToInsert.length);
    for (int i = 0; i < inserted.length; ++i) {
      assertEquals(inserted[i].getName(), contactsToInsert[i].getName());
    }

    // Then get the contacts via find API:
    resultActions = wtc.perform(get(String.format("/contacts?projectId=%d", projId))).andExpect(status().isOk());
    Contact[] found = wtc.parseObjectFromResult(resultActions, Contact[].class);

    // Verify that find returned the same objects
    assertEquals(found.length, contactsToInsert.length);
    for (int i = 0; i < found.length; ++i) {
      assertEquals(found[i].getName(), contactsToInsert[i].getName());
    }
  }
}
