package fi.hel.allu.model.controller;

import fi.hel.allu.common.types.ApplicantType;
import fi.hel.allu.common.types.ApplicationKind;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.ApplicantDao;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.testUtils.WebTestCommon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class ContactControllerTest {

  @Autowired
  private WebTestCommon wtc;

  @Autowired
  private ApplicantDao applicantDao;
  @Autowired
  private ApplicationDao applicationDao;

  private Integer applicantId1;
  private Integer applicantId2;

  @Before
  public void setup() throws Exception {
    wtc.setup();
    Applicant applicant = new Applicant();
    applicant.setType(ApplicantType.PERSON);
    applicant.setName("Test person 1");
    applicant.setRegistryKey("12345676-89");
    applicant = applicantDao.insert(applicant);
    applicantId1 = applicant.getId();
    applicant = new Applicant();
    applicant.setType(ApplicantType.PERSON);
    applicant.setName("Test person 2");
    applicant.setRegistryKey("99999999-999");
    applicant = applicantDao.insert(applicant);
    applicantId2 = applicant.getId();
  }

  private Contact createDummyContact() {
    Contact contact = new Contact();
    contact.setApplicantId(applicantId1);
    contact.setName("Pho Soup");
    contact.setEmail("phosoup@soppa.org");
    contact.setPhone("555-SOUP");
    return contact;
  }

  @Test
  public void addContact() throws Exception {
    Contact contact = createDummyContact();
    wtc.perform(post("/contacts"), Collections.singletonList(contact)).andExpect(status().isOk());
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
    contact.setApplicantId(applicantId1 + 100);
    wtc.perform(post("/contacts"), contact).andExpect(status().isBadRequest());
  }

  @Test
  public void addContactWithoutOrganization() throws Exception {
    Contact contact = createDummyContact();
    contact.setApplicantId(null);
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
    ResultActions result = wtc.perform(post("/contacts"), Collections.singletonList(contact)).andExpect(status().isOk());
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
    ResultActions result = wtc.perform(post("/contacts"), Collections.singletonList(contact)).andExpect(status().isOk());
    Contact inserted = wtc.parseObjectFromResult(result, Contact[].class)[0];
    int id = inserted.getId();
    // Then, change it:
    contact.setEmail("postmaster@mastposter.com");
    contact.setId(id);
    wtc.perform(put(String.format("/contacts")), Collections.singletonList(contact)).andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id", is(id))).andExpect(jsonPath("$[0].email", is(contact.getEmail())));
  }

  @Test
  public void updateNonexistent() throws Exception {
    Contact contact = createDummyContact();
    contact.setId(314159);
    wtc.perform(put("/contacts"), Collections.singletonList(contact)).andExpect(status().isNotFound());
  }

  @Test
  public void findApplicantContacts() throws Exception {
    // Add a few contacts for applicant 1
    for (int i = 0; i < 10; ++i) {
      Contact contact = createDummyContact();
      contact.setName(String.format("Dummy contact %d", i));
      wtc.perform(post("/contacts"), Collections.singletonList(contact)).andExpect(status().isOk());
    }
    // Add a few contacts for applicant 2
    for (int i = 0; i < 4; ++i) {
      Contact contact = createDummyContact();
      contact.setApplicantId(applicantId2);
      contact.setName(String.format("Dummier contact %d", i));
      wtc.perform(post("/contacts"), Collections.singletonList(contact)).andExpect(status().isOk());
    }
    // Now get all contacts for applicant 1 and make sure there's enough:
    ResultActions resultActions = wtc.perform(get(String.format("/contacts/applicant/%d", applicantId1)))
        .andExpect(status().isOk());
    Contact[] contacts = wtc.parseObjectFromResult(resultActions, Contact[].class);
    assertEquals(10, contacts.length);

    // Try also with nonexistent applicant:
    resultActions = wtc.perform(get(String.format("/contacts/applicant/%d", applicantId1 + applicantId2 + 123)))
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
      ResultActions result = wtc.perform(post("/contacts"), Collections.singletonList(contact)).andExpect(status().isOk());
      contactsToInsert[i] = wtc.parseObjectFromResult(result, Contact[].class)[0];
    }

    Application appl = new Application();
    appl.setType(ApplicationType.EVENT);
    appl.setKind(ApplicationKind.OUTDOOREVENT);
    appl.setMetadataVersion(1);
    appl.setName("Dummy apllication");
    appl.setStartTime(ZonedDateTime.parse("2015-01-03T10:15:30+02:00"));
    appl.setEndTime(ZonedDateTime.parse("2015-02-03T10:15:30+02:00"));
    appl.setRecurringEndTime(ZonedDateTime.parse("2015-02-03T10:15:30+02:00"));
    Event evt = new Event();
    evt.setDescription("Dummy event");
    appl.setExtension(evt);
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
    resultActions = wtc.perform(get(String.format("/contacts/application/%d", applId))).andExpect(status().isOk());
    Contact[] found = wtc.parseObjectFromResult(resultActions, Contact[].class);

    // Verify that find returned the same objects
    assertEquals(found.length, contactsToInsert.length);
    for (int i = 0; i < found.length; ++i) {
      assertEquals(found[i].getName(), contactsToInsert[i].getName());
    }
  }
}
