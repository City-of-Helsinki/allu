package fi.hel.allu.model.dao;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.testUtils.SpeccyTestBase;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.ZonedDateTime;
import java.util.*;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.*;

@RunWith(Spectrum.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
public class ContactDaoSpec extends SpeccyTestBase {

  @Autowired
  ContactDao contactDao;
  @Autowired
  PostalAddressDao postalAddressDao;
  @Autowired
  ProjectDao projectDao;

  private Contact testContact = new Contact();
  private PostalAddress testPostalAddress = new PostalAddress("foostreet", "001100", "Sometown");
  private Contact insertedContact;
  private int customerId;

  {
    // manual transaction handling done in SpeccyTestBase
    beforeEach(() -> {
      testCommon.deleteAllData();
      customerId = testCommon.insertPerson().getId();
      testCommon.insertApplication("test app", "käsittelijä");
      testContact.setCustomerId(customerId);
      testContact.setEmail("test@email.fi");
      testContact.setName("test name");
      testContact.setPostalAddress(testPostalAddress);
    });

    describe("Contact dao", () -> {

      describe("insert", () -> {
        it("should return updated object from single insert", () -> {
          insertedContact = contactDao.insert(Collections.singletonList(testContact)).get(0);
          assertContact(insertedContact);
          assertPostalAddress(insertedContact);
        });

        it("should return multiple contacts from bulk insert", () -> {
          List<Contact> contacts = contactDao.insert(Arrays.asList(testContact, testContact, testContact));
          Assert.assertEquals(3, contacts.size());
          contacts.forEach(c -> assertContact(c));
        });
      });

      context("when finding contacts", () -> {
        beforeEach(() -> insertedContact = contactDao.insert(Collections.singletonList(testContact)).get(0));

        describe("findById", () -> {
          it("should find contact by id", () -> {
            Optional<Contact> findContact = contactDao.findById(insertedContact.getId());
            Assert.assertTrue(findContact.isPresent());
            assertContact(findContact.get());
            assertPostalAddress(findContact.get());
          });
        });
        describe("findByIds", () -> {
          it("should find contacts by multiple ids", () -> {
            List<Contact> findContacts = contactDao.findByIds(Collections.singletonList(insertedContact.getId()));
            assertFalse(findContacts.isEmpty());
            assertContact(findContacts.get(0));
            assertPostalAddress(findContacts.get(0));
          });
        });
        describe("findByCustomer", () -> {
          it("should find contact by customer id", () -> {
            List<Contact> contacts = contactDao.findByCustomer(insertedContact.getCustomerId());
            Assert.assertEquals(1, contacts.size());
            assertContact(contacts.get(0));
            assertPostalAddress(contacts.get(0));
          });

          it("should not find removed contacts", () -> {
            Contact removedContact = new Contact();
            removedContact.setCustomerId(customerId);
            removedContact.setName("removed name");
            removedContact.setPostalAddress(testPostalAddress);
            removedContact.setActive(false);
            contactDao.insert(Arrays.asList(removedContact));

            List<Contact> contacts = contactDao.findByCustomer(insertedContact.getCustomerId());
            Assert.assertEquals(1, contacts.size());
            Assert.assertEquals(insertedContact.getId(), contacts.get(0).getId());
          });
        });
      });

      describe("update", () -> {
        beforeEach(() -> insertedContact = contactDao.insert(Collections.singletonList(testContact)).get(0));

        it("should update contact", () -> {
          insertedContact.setName("updated");
          testContact.setName("updated");
          Contact updatedContact = contactDao.update(Collections.singletonList(insertedContact)).get(0);
          assertContact(updatedContact);
          assertPostalAddress(updatedContact);
        });
        it("should update contact mail address", () -> {
          insertedContact.getPostalAddress().setCity("updated");
          testPostalAddress.setCity("updated");
          Contact updatedContact = contactDao.update(Collections.singletonList(insertedContact)).get(0);
          assertContact(updatedContact);
          assertPostalAddress(updatedContact);
        });
        context("with address set to null", () -> {
          it("should delete contact mail address", () -> {
            int postalAddressId = insertedContact.getPostalAddress().getId();
            insertedContact.setPostalAddress(null);
            Contact updatedContact = contactDao.update(Collections.singletonList(insertedContact)).get(0);
            assertContact(updatedContact);
            Assert.assertNull(updatedContact.getPostalAddress());
            Optional<PostalAddress> postalAddress = postalAddressDao.findById(postalAddressId);
            assertFalse(postalAddress.isPresent());
          });
        });
      });
    });

    describe("ContactDao.findAll", () -> {
      beforeEach(() -> {
        List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < 15; ++i) {
          contacts.add(dummyContact(i));
        }
        List<Contact> inserted = contactDao.insert(contacts);
        assertEquals(inserted.size(), contacts.size());
        inserted.forEach(c -> assertNotNull(c.getId()));
      });

      it("Can fetch 5 contacts in ascendind ID order", () -> {
        Page<Contact> page = contactDao.findAll(PageRequest.of(1, 5));
        assertEquals(5, page.getSize());
        List<Contact> elements = page.getContent();
        assertEquals(5, elements.size());
        int prevId = Integer.MIN_VALUE;
        for (int i = 0; i < elements.size(); ++i) {
          int id = elements.get(i).getId();
          assertTrue(prevId < id);
          prevId = id;
        }
      });
    });

    describe("Contact deletion safety", () -> {
      it("finds only deletable contacts for given customers", () -> {
        Customer c1 = testCommon.insertPerson();
        Customer c2 = testCommon.insertPerson();

        // A: deletable (no links)
        Contact a = testCommon.insertContact(c1.getId());

        // B: linked to application
        Contact b = testCommon.insertContact(c1.getId());
        Application app = testCommon.dummyOutdoorApplication("app", "owner", c1);
        List<Contact> mutableContacts = new ArrayList<>(app.getCustomersWithContacts().get(0).getContacts());
        mutableContacts.add(b);
        app.getCustomersWithContacts().get(0).setContacts(mutableContacts);
        testCommon.insertApplication(app);

        // C: linked to project
        Contact c = testCommon.insertContact(c1.getId());
        Project project = new Project();
        project.setCustomerId(c1.getId());
        project.setContactId(c.getId());
        project.setIdentifier("P1");
        project.setName("proj");
        project.setStartTime(ZonedDateTime.now());
        project.setCreatorId(testCommon.insertUser("creator").getId());
        projectDao.insert(project);

        // D: another customer
        testCommon.insertContact(c2.getId());

        // Run query
        List<Integer> deletable = contactDao.findDeletableContactIdsByCustomerIds(Set.of(c1.getId()));

        // Only A should be returned
        assertEquals(1, deletable.size());
        assertEquals(a.getId(), deletable.get(0));
      });

      it("deletes only safe-to-delete contacts", () -> {
        Customer customer = testCommon.insertPerson();

        // A: deletable
        Contact a = testCommon.insertContact(customer.getId());

        // B: linked to application
        Contact b = testCommon.insertContact(customer.getId());
        Application app = testCommon.dummyOutdoorApplication("app", "owner", customer);
        List<Contact> mutableContacts = new ArrayList<>(app.getCustomersWithContacts().get(0).getContacts());
        mutableContacts.add(b);
        app.getCustomersWithContacts().get(0).setContacts(mutableContacts);
        testCommon.insertApplication(app);

        List<Integer> deletable =
          contactDao.findDeletableContactIdsByCustomerIds(Set.of(customer.getId()));

        long deleted = contactDao.deleteContactsByIds(deletable);

        assertEquals(1, deleted);
        assertFalse(contactDao.findById(a.getId()).isPresent());
        assertTrue(contactDao.findById(b.getId()).isPresent());
      });
    });
  }

  private void assertContact(Contact assertedContact) {
    Assert.assertEquals(testContact.getCustomerId(), assertedContact.getCustomerId());
    Assert.assertEquals(testContact.getEmail(), assertedContact.getEmail());
    Assert.assertEquals(testContact.getPhone(), assertedContact.getPhone());
  }

  private void assertPostalAddress(PostalAddressItem postalAddressItem) {
    Assert.assertEquals(testPostalAddress.getStreetAddress(), postalAddressItem.getPostalAddress().getStreetAddress());
    Assert.assertEquals(testPostalAddress.getPostalCode(), postalAddressItem.getPostalAddress().getPostalCode());
    Assert.assertEquals(testPostalAddress.getCity(), postalAddressItem.getPostalAddress().getCity());
  }

  private Contact dummyContact(int i) {
    int customerId = testCommon.insertPerson().getId();
    Contact contact = new Contact();
    contact.setCustomerId(customerId);
    contact.setEmail("test@email.fi");
    contact.setName("test name");
    contact.setPostalAddress(testPostalAddress);
    return contact;
  }
}
