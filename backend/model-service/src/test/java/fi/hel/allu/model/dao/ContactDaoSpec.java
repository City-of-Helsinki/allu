package fi.hel.allu.model.dao;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.model.domain.PostalAddressItem;
import fi.hel.allu.model.testUtils.TestCommon;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.*;

@RunWith(Spectrum.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
public class ContactDaoSpec {
  @Autowired
  ContactDao contactDao;
  @Autowired
  PostalAddressDao postalAddressDao;
  @Autowired
  private TestCommon testCommon;
  @Autowired
  private PlatformTransactionManager transactionManager;

  private Contact testContact = new Contact();
  private PostalAddress testPostalAddress = new PostalAddress("foostreet", "001100", "Sometown");
  private Contact insertedContact;
  private int applicantId;
  private int applicationId;

  private TransactionStatus transaction;

  {
    beforeAll(() -> new TestContextManager(getClass()).prepareTestInstance(this));
    // manual transaction handling before and after tests, because Spectrum does not support the @Transactional
    beforeEach(() -> {
      transaction = testCommon.createTransactionStatus();
      applicantId = testCommon.insertPerson();
      applicationId = testCommon.insertApplication("test app", "käsittelijä");
      testContact.setApplicantId(applicantId);
      testContact.setEmail("test@email.fi");
      testContact.setName("test name");
      testContact.setPostalAddress(testPostalAddress);
    });
    afterEach(() -> transactionManager.rollback(transaction));

    describe("Contact dao", () -> {
      beforeEach(() -> insertedContact = contactDao.insert(testContact));
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
      it("should find contacts by applicant id", () -> {
        List<Contact> contacts = contactDao.findByApplicant(insertedContact.getApplicantId());
        Assert.assertEquals(1, contacts.size());
        assertContact(contacts.get(0));
        assertPostalAddress(contacts.get(0));
      });
      it("should find contacts by application id", () -> {
        contactDao.setApplicationContacts(applicationId, Collections.singletonList(insertedContact));
        List<Contact> contacts = contactDao.findByApplication(applicationId);
        Assert.assertEquals(1, contacts.size());
        assertContact(contacts.get(0));
        assertPostalAddress(contacts.get(0));
      });
      it("should update contacts", () -> {
        insertedContact.setName("updated");
        testContact.setName("updated");
        Contact updatedContact = contactDao.update(insertedContact.getId(), insertedContact);
        assertContact(updatedContact);
        assertPostalAddress(updatedContact);
      });
      it("should update contact mail address", () -> {
        insertedContact.getPostalAddress().setCity("updated");
        testPostalAddress.setCity("updated");
        Contact updatedContact = contactDao.update(insertedContact.getId(), insertedContact);
        assertContact(updatedContact);
        assertPostalAddress(updatedContact);
      });
      it("should delete contact mail address", () -> {
        int postalAddressId = insertedContact.getPostalAddress().getId();
        insertedContact.setPostalAddress(null);
        Contact updatedContact = contactDao.update(insertedContact.getId(), insertedContact);
        assertContact(updatedContact);
        Assert.assertNull(updatedContact.getPostalAddress());
        Optional<PostalAddress> postalAddress = postalAddressDao.findById(postalAddressId);
        Assert.assertFalse(postalAddress.isPresent());
      });
    });
  }

  private void assertContact(Contact assertedContact) {
    Assert.assertEquals(testContact.getApplicantId(), assertedContact.getApplicantId());
    Assert.assertEquals(testContact.getEmail(), assertedContact.getEmail());
    Assert.assertEquals(testContact.getPhone(), assertedContact.getPhone());
  }

  private void assertPostalAddress(PostalAddressItem postalAddressItem) {
    Assert.assertEquals(testPostalAddress.getStreetAddress(), postalAddressItem.getPostalAddress().getStreetAddress());
    Assert.assertEquals(testPostalAddress.getPostalCode(), postalAddressItem.getPostalAddress().getPostalCode());
    Assert.assertEquals(testPostalAddress.getCity(), postalAddressItem.getPostalAddress().getCity());
  }
}
