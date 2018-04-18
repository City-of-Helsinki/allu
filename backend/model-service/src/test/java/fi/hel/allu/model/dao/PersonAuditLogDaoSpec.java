package fi.hel.allu.model.dao;

import com.greghaskins.spectrum.Spectrum;
import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.PersonAuditLogLog;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.testUtils.SpeccyTestBase;
import fi.hel.allu.model.testUtils.TestCommon;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@RunWith(Spectrum.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
public class PersonAuditLogDaoSpec extends SpeccyTestBase {

  @Autowired
  private PersonAuditLogDao personAuditLogDao;
  @Autowired
  private CustomerDao customerDao;
  @Autowired
  private ContactDao contactDao;
  @Autowired
  private UserDao userDao;
  @Autowired
  private TestCommon testCommon;

  private Customer testCustomer;
  private Contact testContact;
  private Customer insertedCustomer;
  private Contact insertedContact;
  private PostalAddress testPostalAddress = new PostalAddress("foostreet", "001100", "Sometown");
  private User insertedUser;

  {
    // transaction setup is done in SpeccyTestBase
    beforeEach(() -> {
      testCommon.deleteAllData();
      testCustomer = new Customer();
      testCustomer.setType(CustomerType.PERSON);
      testCustomer.setName("appl name");
      testCustomer.setRegistryKey("111111-1111");
      testCustomer.setPhone("12345");
      testCustomer.setPostalAddress(testPostalAddress);
      testCustomer.setCountryId(testCommon.getCountryIdOfFinland());

      testContact = new Contact();
      testContact.setCustomerId(null);
      testContact.setEmail("test@email.fi");
      testContact.setName("test name");
      testContact.setPostalAddress(testPostalAddress);

      User user = createDummyUser("username");
      insertedUser = userDao.insert(user);
    });

    describe("Person audit log dao", () -> {
      beforeEach(() -> {
        insertedCustomer = customerDao.insert(testCustomer);
        testContact.setCustomerId(insertedCustomer.getId());
        insertedContact = contactDao.insert(Collections.singletonList(testContact)).get(0);
      });

      context("with customers", () -> {
        it("should add customer log entry", () -> {
          PersonAuditLogLog logEntry = new PersonAuditLogLog(insertedCustomer.getId(), null, insertedUser.getId(), "test", ZonedDateTime.now());
          PersonAuditLogLog insertedLogEntry = personAuditLogDao.insert(logEntry);
          Optional<PersonAuditLogLog> foundLogEntry = personAuditLogDao.findById(insertedLogEntry.getId());
          assertEquals(true, foundLogEntry.isPresent());
          assertEquals(insertedCustomer.getId(), foundLogEntry.get().getCustomerId());
          assertEquals(null, foundLogEntry.get().getContactId());
          assertEquals("test", foundLogEntry.get().getSource());
        });
        it("should add contact log entry", () -> {
          PersonAuditLogLog logEntry = new PersonAuditLogLog(null, insertedContact.getId(), insertedUser.getId(), "test", ZonedDateTime.now());
          PersonAuditLogLog insertedLogEntry = personAuditLogDao.insert(logEntry);
          Optional<PersonAuditLogLog> foundLogEntry = personAuditLogDao.findById(insertedLogEntry.getId());
          assertEquals(true, foundLogEntry.isPresent());
          assertEquals(insertedContact.getId(), foundLogEntry.get().getContactId());
          assertEquals(null, foundLogEntry.get().getCustomerId());
          assertEquals("test", foundLogEntry.get().getSource());
        });
        it("should not add a log entry with both customer and contact", () -> {
          boolean failed = false;
          try {
            PersonAuditLogLog logEntry = new PersonAuditLogLog(insertedCustomer.getId(), insertedContact.getId(), insertedUser.getId(), "test", ZonedDateTime.now());
            PersonAuditLogLog insertedLogEntry = personAuditLogDao.insert(logEntry);
          } catch (RuntimeException e) {
            failed = true;
          }
          assertEquals(true, failed);
        });
      });
    });
  }

  private User createDummyUser(String userName) {
    User user = new User();
    user.setAssignedRoles(Arrays.asList(RoleType.ROLE_ADMIN, RoleType.ROLE_VIEW));
    user.setIsActive(true);
    user.setAllowedApplicationTypes(Arrays.asList(ApplicationType.EVENT));
    user.setEmailAddress("email");
    user.setRealName("realname");
    user.setTitle("title");
    user.setUserName(userName);
    return user;
  }

}
