package fi.hel.allu.model.dao;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.model.domain.PostalAddressItem;
import fi.hel.allu.model.testUtils.SpeccyTestBase;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.beforeEach;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;

@RunWith(Spectrum.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
public class ContactDaoSpec extends SpeccyTestBase {
  @Autowired
  ContactDao contactDao;
  @Autowired
  PostalAddressDao postalAddressDao;

  private Contact testContact = new Contact();
  private PostalAddress testPostalAddress = new PostalAddress("foostreet", "001100", "Sometown");
  private Contact insertedContact;
  private int customerId;
  private int applicationId;

  {
    // manual transaction handling done in SpeccyTestBase
    beforeEach(() -> {
      testCommon.deleteAllData();
      customerId = testCommon.insertPerson().getId();
      applicationId = testCommon.insertApplication("test app", "käsittelijä");
      testContact.setCustomerId(customerId);
      testContact.setEmail("test@email.fi");
      testContact.setName("test name");
      testContact.setPostalAddress(testPostalAddress);
    });

    describe("Contact dao", () -> {
      beforeEach(() -> insertedContact = contactDao.insert(Collections.singletonList(testContact)).get(0));
      it("should return updated object from insert", () -> {
        assertContact(insertedContact);
        assertPostalAddress(insertedContact);
      });
      it("should find contacts by id", () -> {
        Optional<Contact> findContact = contactDao.findById(insertedContact.getId());
        Assert.assertTrue(findContact.isPresent());
        assertContact(findContact.get());
        assertPostalAddress(findContact.get());
      });
      it("should find contacts by multiple ids", () -> {
        List<Contact> findContacts = contactDao.findByIds(Collections.singletonList(insertedContact.getId()));
        Assert.assertFalse(findContacts.isEmpty());
        assertContact(findContacts.get(0));
        assertPostalAddress(findContacts.get(0));
      });
      it("should find contacts by customer id", () -> {
        List<Contact> contacts = contactDao.findByCustomer(insertedContact.getCustomerId());
        Assert.assertEquals(1, contacts.size());
        assertContact(contacts.get(0));
        assertPostalAddress(contacts.get(0));
      });
      it("should update contacts", () -> {
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
      it("should delete contact mail address", () -> {
        int postalAddressId = insertedContact.getPostalAddress().getId();
        insertedContact.setPostalAddress(null);
        Contact updatedContact = contactDao.update(Collections.singletonList(insertedContact)).get(0);
        assertContact(updatedContact);
        Assert.assertNull(updatedContact.getPostalAddress());
        Optional<PostalAddress> postalAddress = postalAddressDao.findById(postalAddressId);
        Assert.assertFalse(postalAddress.isPresent());
      });
    });
    describe("Contact dao bulk insert", () -> {
      it("should insert bulk ok", () -> {
        List<Contact> contacts = contactDao.insert(Arrays.asList(testContact, testContact, testContact));
        Assert.assertEquals(3, contacts.size());
        contacts.forEach(c -> assertContact(c));
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
}
