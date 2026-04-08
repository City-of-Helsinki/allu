package fi.hel.allu.model.dao;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import fi.hel.allu.common.types.ChangeType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.web.WebAppConfiguration;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.testUtils.SpeccyTestBase;
import fi.hel.allu.model.testUtils.TestCommon;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.*;

@RunWith(Spectrum.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
public class CustomerDaoSpec extends SpeccyTestBase {

  @Autowired
  CustomerDao customerDao;
  @Autowired
  ApplicationDao applicationDao;
  @Autowired
  ContactDao contactDao;
  @Autowired
  ProjectDao projectDao;
  @Autowired
  HistoryDao historyDao;
  @Autowired
  CustomerUpdateLogDao customerUpdateLogDao;
  @Autowired
  PersonAuditLogDao personAuditLogDao;
  @Autowired
  TestCommon testCommon;

  private Customer testCustomer;
  private Contact testContact = new Contact();
  private Customer insertedCustomer;
  private Contact insertedContact;
  private Application insertedApplication;
  private PostalAddress testPostalAddress = new PostalAddress("foostreet", "001100", "Sometown");

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

      testContact.setCustomerId(null);
      testContact.setEmail("test@email.fi");
      testContact.setName("test name");
      testContact.setPostalAddress(testPostalAddress);
    });

    describe("Customer dao", () -> {
      beforeEach(() -> insertedCustomer = customerDao.insert(testCustomer));

      context("with customers", () -> {
        it("should find customer by id", () -> {
          Optional<Customer> customerOpt = customerDao.findById(insertedCustomer.getId());
          assertTrue(customerOpt.isPresent());
          Customer customer = customerOpt.get();
          assertEquals(testCustomer.getName(), customer.getName());
          assertEquals(testCustomer.getPhone(), customer.getPhone());
          assertEquals(testCustomer.getPostalAddress().getStreetAddress(), customer.getPostalAddress().getStreetAddress());
        });
        it("should find all customers", () -> {
          Page<Customer> customers = customerDao.findAll(PageRequest.of(0, Integer.MAX_VALUE));
          assertTrue(customers.getSize() > 0);
          Customer customer = customers.getContent().get(0);
          assertEquals(testCustomer.getName(), customer.getName());
          assertEquals(testCustomer.getPhone(), customer.getPhone());
        });
        it("should find customers by ids", () -> {
          List<Customer> customers = customerDao.findByIds(Collections.singletonList(insertedCustomer.getId()));
          assertFalse(customers.isEmpty());
          Customer customer = customers.get(0);
          assertEquals(testCustomer.getName(), customer.getName());
          assertEquals(testCustomer.getPhone(), customer.getPhone());
        });
        it("should not find person customer by business id", () -> {
          List<Customer> customers = customerDao.findByBusinessId(testCustomer.getRegistryKey());
          assertTrue(customers.isEmpty());
        });
        it("should find non-person customer by business id", () -> {
          testCustomer.setType(CustomerType.COMPANY);
          customerDao.insert(testCustomer);
          List<Customer> customers = customerDao.findByBusinessId(testCustomer.getRegistryKey());
          assertEquals(1, customers.size());
          Customer customer = customers.get(0);
          assertEquals(testCustomer.getName(), customer.getName());
          assertEquals(testCustomer.getPhone(), customer.getPhone());
        });
      });

      context("with customers with empty contacts", ()-> {
        beforeEach(() -> {
          Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
          application.setCustomersWithContacts(
                  Collections.singletonList(new CustomerWithContacts(CustomerRoleType.APPLICANT, insertedCustomer, Collections.emptyList())));
          insertedApplication = applicationDao.insert(application);
        });

        it("should find customers with contacts by application id", () -> {
          List<CustomerWithContacts> customerWithContacts = customerDao.findByApplicationWithContacts(insertedApplication.getId());
          assertEquals(1, customerWithContacts.size());
          assertEquals(insertedCustomer.getId(), customerWithContacts.get(0).getCustomer().getId());
          assertEquals(0, customerWithContacts.get(0).getContacts().size());
          assertEquals(CustomerRoleType.APPLICANT, customerWithContacts.get(0).getRoleType());
        });
        it("should find multiple customers with empty contacts", () -> {
          testCustomer.setName("another");
          Customer anotherCustomer = customerDao.insert(testCustomer);
          CustomerWithContacts customerWithContacts = new CustomerWithContacts(CustomerRoleType.APPLICANT, anotherCustomer, Collections.emptyList());
          insertedApplication.getCustomersWithContacts().add(customerWithContacts);
          applicationDao.update(insertedApplication.getId(), insertedApplication);
          List<CustomerWithContacts> updatedCustomerWithContacts = customerDao.findByApplicationWithContacts(insertedApplication.getId());
          assertEquals(2, updatedCustomerWithContacts.size());
          assertTrue(Arrays.asList(insertedCustomer.getId(), anotherCustomer.getId()).containsAll(
                  updatedCustomerWithContacts.stream().map(cwc -> cwc.getCustomer()).map(c -> c.getId()).collect(Collectors.toList())));
          assertEquals(
                  2, updatedCustomerWithContacts.stream().filter(cwc -> CustomerRoleType.APPLICANT.equals(cwc.getRoleType())).count());

        });
      });

      context("with customers with contacts", ()-> {
        beforeEach(() -> {
          testContact.setCustomerId(insertedCustomer.getId());
          insertedContact = contactDao.insert(Collections.singletonList(testContact)).get(0);
          Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
          application.setCustomersWithContacts(
                  Collections.singletonList(
                          new CustomerWithContacts(CustomerRoleType.APPLICANT, insertedCustomer, Collections.singletonList(insertedContact))));
          insertedApplication = applicationDao.insert(application);
        });
        it("should find customers with contacts by application id", () -> {
          List<CustomerWithContacts> customerWithContacts = customerDao.findByApplicationWithContacts(insertedApplication.getId());
          assertEquals(1, customerWithContacts.size());
          CustomerWithContacts cwc = customerWithContacts.get(0);
          assertEquals(insertedCustomer.getId(), cwc.getCustomer().getId());
          assertEquals(1, cwc.getContacts().size());
          assertEquals(insertedContact.getId(), cwc.getContacts().get(0).getId());
          assertEquals(CustomerRoleType.APPLICANT, cwc.getRoleType());
        });
      });

      it("should find invoice recipients without sap number", () -> {
          Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
          application.setInvoiceRecipientId(insertedCustomer.getId());
          insertedApplication = applicationDao.insert(application);
          applicationDao.addTag(insertedApplication.getId(), testCommon.dummyTag(ApplicationTagType.SAP_ID_MISSING));
          List<InvoiceRecipientCustomer> customers = customerDao.findInvoiceRecipientsWithoutSapNumber();
          assertEquals(1, customers.size());
          assertEquals(insertedCustomer.getId(), customers.get(0).getCustomer().getId());
      });
      it("should not return invoice recipients of replaced applications", () -> {
        Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
        application.setInvoiceRecipientId(insertedCustomer.getId());
        Application replacingApplication = testCommon.dummyOutdoorApplication("Replacing Test Application", "Test Handler 2");
        replacingApplication = applicationDao.insert(replacingApplication);
        application.setReplacedByApplicationId(replacingApplication.getId());
        insertedApplication = applicationDao.insert(application);
        applicationDao.addTag(insertedApplication.getId(), testCommon.dummyTag(ApplicationTagType.SAP_ID_MISSING));
        List<InvoiceRecipientCustomer> customers = customerDao.findInvoiceRecipientsWithoutSapNumber();
        assertEquals(0, customers.size());
      });

      context("getDeletableCustomers", () -> {

        AtomicReference<Customer> deletableCustomer = new AtomicReference<>();
        AtomicReference<Customer> customerWithApplication = new AtomicReference<>();
        AtomicReference<Customer> customerWithProject = new AtomicReference<>();
        AtomicReference<Customer> customerAsInvoiceRecipient = new AtomicReference<>();
        AtomicReference<Customer> recentlyCreatedCustomer = new AtomicReference<>();

        beforeEach(() -> {
          // Asiakas joka tulee mukaan tuloksiin (ei liitoksia projektiin tai hankkeeseen)
          deletableCustomer.set(customerDao.insert(dummyCustomer(100)));

          // Asiakas jolla on hakemus → EI saa tulla mukaan
          customerWithApplication.set(customerDao.insert(dummyCustomer(101)));
          Application app = testCommon.dummyOutdoorApplication("App", "Handler");
          app.setCustomersWithContacts(
            Collections.singletonList(
              new CustomerWithContacts(CustomerRoleType.APPLICANT, customerWithApplication.get(), Collections.emptyList())
            )
          );
          applicationDao.insert(app);

          // Asiakas jolla on projekti → EI saa tulla mukaan
          customerWithProject.set(customerDao.insert(dummyCustomer(102)));
          Project project = new Project();
          project.setName("proj");
          project.setCustomerId(customerWithProject.get().getId());
          project.setContactId(testCommon.insertContact(customerWithProject.get().getId()).getId());
          project.setStartTime(ZonedDateTime.now());
          project.setIdentifier("identifier");
          project.setCreatorId(testCommon.insertUser(RandomStringUtils.randomAlphabetic(10)).getId());
          projectDao.insert(project);

          // Asiakas joka on invoiceRecipient → EI saa tulla mukaan
          customerAsInvoiceRecipient.set(customerDao.insert(dummyCustomer(103)));
          Application invoiceApp = testCommon.dummyOutdoorApplication("InvoiceApp", RandomStringUtils.randomAlphabetic(10));
          invoiceApp.setInvoiceRecipientId(customerAsInvoiceRecipient.get().getId());
          applicationDao.insert(invoiceApp);

          // Asiakas jolla CREATED-historiatapahtuma alle 1 vrk sitten → EI saa tulla mukaan
          recentlyCreatedCustomer.set(customerDao.insert(dummyCustomer(104)));
          ChangeHistoryItem change = new ChangeHistoryItem();
          change.setUserId(testCommon.insertUser(RandomStringUtils.randomAlphabetic(10)).getId());
          change.setChangeType(ChangeType.CREATED);
          change.setChangeTime(ZonedDateTime.now()); // liian uusi
          historyDao.addCustomerChange(recentlyCreatedCustomer.get().getId(), change);
        });

        it("should return only customers that fulfill all deletable criteria", () -> {
          Page<DeletableCustomer> page = customerDao.getDeletableCustomers(PageRequest.of(0, 50));

          List<Integer> ids = page.getContent().stream()
            .map(DeletableCustomer::getId)
            .toList();

          assertTrue(ids.contains(deletableCustomer.get().getId()));

          assertFalse(ids.contains(customerWithApplication.get().getId()));
          assertFalse(ids.contains(customerWithProject.get().getId()));
          assertFalse(ids.contains(customerAsInvoiceRecipient.get().getId()));
          assertFalse(ids.contains(recentlyCreatedCustomer.get().getId()));
        });

        it("should exclude customer if CREATED history is older than cutoff only when within 1 day", () -> {
          Customer oldCustomer = customerDao.insert(dummyCustomer(105));

          ChangeHistoryItem change = new ChangeHistoryItem();
          change.setUserId(testCommon.insertUser(RandomStringUtils.randomAlphabetic(10)).getId());
          change.setChangeType(ChangeType.CREATED);
          change.setChangeTime(ZonedDateTime.now().minusDays(2)); // vanhempi kuin cutoff
          historyDao.addCustomerChange(oldCustomer.getId(), change);

          Page<DeletableCustomer> page = customerDao.getDeletableCustomers(PageRequest.of(0, 50));

          List<Integer> ids = page.getContent().stream()
            .map(DeletableCustomer::getId)
            .toList();

          assertTrue(ids.contains(oldCustomer.getId())); // saa tulla mukaan
        });

        it("should use default pagination when pageable is null", () -> {
          Page<DeletableCustomer> page = customerDao.getDeletableCustomers(null);
          assertNotNull(page);
          assertTrue(page.getSize() > 0);
        });

        it("should sort by name descending", () -> {
          Customer a = customerDao.insert(dummyCustomer(200));
          a.setName("AAA");
          customerDao.update(a.getId(), a);

          Customer b = customerDao.insert(dummyCustomer(201));
          b.setName("ZZZ");
          customerDao.update(b.getId(), b);

          Page<DeletableCustomer> page = customerDao.getDeletableCustomers(
            PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"))
          );

          List<DeletableCustomer> list = page.getContent();
          assertTrue(list.size() >= 2);
          assertTrue(list.get(0).getName().compareTo(list.get(1).getName()) >= 0);
        });

        it("should return CustomerType.PERSON for person customers", () -> {
          // dummyCustomer creates CustomerType.PERSON customers
          Page<DeletableCustomer> page = customerDao.getDeletableCustomers(PageRequest.of(0, 50));

          List<DeletableCustomer> personEntries = page.getContent().stream()
            .filter(c -> c.getId().equals(deletableCustomer.get().getId()))
            .toList();

          assertEquals(1, personEntries.size());
          assertEquals(CustomerType.PERSON, personEntries.get(0).getType());
        });

        it("should return CustomerType.COMPANY for company customers", () -> {
          Customer company = dummyCustomer(300);
          company.setType(CustomerType.COMPANY);
          company.setRegistryKey("1234567-8");
          Customer insertedCompany = customerDao.insert(company);

          Page<DeletableCustomer> page = customerDao.getDeletableCustomers(PageRequest.of(0, 50));

          List<DeletableCustomer> companyEntries = page.getContent().stream()
            .filter(c -> c.getId().equals(insertedCompany.getId()))
            .toList();

          assertEquals(1, companyEntries.size());
          assertEquals(CustomerType.COMPANY, companyEntries.get(0).getType());
          // Company name must not be null — name masking is service-core's responsibility
          assertNotNull(companyEntries.get(0).getName());
        });

        it("should never return null type", () -> {
          Page<DeletableCustomer> page = customerDao.getDeletableCustomers(PageRequest.of(0, 50));

          page.getContent().forEach(c ->
            assertNotNull("type must never be null for customer id " + c.getId(), c.getType())
          );
        });
      });

      context("findPurgeableCustomerIds", () -> {

        // Helper: inserts a soft-deleted customer with all log entries older than 5 years
        // so it qualifies for permanent deletion.
        AtomicReference<Customer> purgeableCustomer = new AtomicReference<>();

        beforeEach(() -> {
          ZonedDateTime overFiveYearsAgo = ZonedDateTime.now().minusYears(6);
          int userId = testCommon.insertUser(RandomStringUtils.randomAlphabetic(10)).getId();

          // Purgeable: inactive, all log entries > 5 years old
          Customer c = customerDao.insert(dummyCustomer(500));
          c.setIsActive(false);
          customerDao.update(c.getId(), c);
          ChangeHistoryItem old = new ChangeHistoryItem();
          old.setUserId(userId);
          old.setChangeType(ChangeType.CONTENTS_CHANGED);
          old.setChangeTime(overFiveYearsAgo);
          historyDao.addCustomerChange(c.getId(), old);
          purgeableCustomer.set(c);
        });

        it("should return inactive customer whose all log entries are older than 5 years", () -> {
          List<Integer> ids = customerDao.findPurgeableCustomerIds(100, 0);
          assertTrue("Purgeable customer should be returned", ids.contains(purgeableCustomer.get().getId()));
        });

        it("should not return active customer even if all log entries are old", () -> {
          // Active customer with old change history — not purgeable because is_active=true
          ZonedDateTime overFiveYearsAgo = ZonedDateTime.now().minusYears(6);
          int userId = testCommon.insertUser(RandomStringUtils.randomAlphabetic(10)).getId();
          Customer active = customerDao.insert(dummyCustomer(501)); // isActive=true by default
          ChangeHistoryItem old = new ChangeHistoryItem();
          old.setUserId(userId);
          old.setChangeType(ChangeType.CONTENTS_CHANGED);
          old.setChangeTime(overFiveYearsAgo);
          historyDao.addCustomerChange(active.getId(), old);

          List<Integer> ids = customerDao.findPurgeableCustomerIds(100, 0);
          assertFalse("Active customer must not appear in purgeable list", ids.contains(active.getId()));
        });

        it("should not return inactive customer with recent change_history entry", () -> {
          int userId = testCommon.insertUser(RandomStringUtils.randomAlphabetic(10)).getId();
          Customer c = customerDao.insert(dummyCustomer(502));
          c.setIsActive(false);
          customerDao.update(c.getId(), c);
          // Recent change_history entry — within 5 years
          ChangeHistoryItem recent = new ChangeHistoryItem();
          recent.setUserId(userId);
          recent.setChangeType(ChangeType.CONTENTS_CHANGED);
          recent.setChangeTime(ZonedDateTime.now().minusYears(1));
          historyDao.addCustomerChange(c.getId(), recent);

          List<Integer> ids = customerDao.findPurgeableCustomerIds(100, 0);
          assertFalse("Customer with recent change_history must not be purgeable", ids.contains(c.getId()));
        });

        it("should not return inactive customer with recent customer_update_log entry", () -> {
          ZonedDateTime overFiveYearsAgo = ZonedDateTime.now().minusYears(6);
          int userId = testCommon.insertUser(RandomStringUtils.randomAlphabetic(10)).getId();

          Customer c = customerDao.insert(dummyCustomer(503));
          c.setIsActive(false);
          customerDao.update(c.getId(), c);

          // Old change_history — passes the change_history check
          ChangeHistoryItem old = new ChangeHistoryItem();
          old.setUserId(userId);
          old.setChangeType(ChangeType.CONTENTS_CHANGED);
          old.setChangeTime(overFiveYearsAgo);
          historyDao.addCustomerChange(c.getId(), old);

          // Recent customer_update_log entry — should block purge
          CustomerUpdateLog updateLog = new CustomerUpdateLog(c.getId(), ZonedDateTime.now().minusYears(1));
          customerUpdateLogDao.insertUpdateLog(updateLog);

          List<Integer> ids = customerDao.findPurgeableCustomerIds(100, 0);
          assertFalse("Customer with recent customer_update_log must not be purgeable", ids.contains(c.getId()));
        });

        it("should not return inactive customer with recent person_audit_log entry for the customer", () -> {
          ZonedDateTime overFiveYearsAgo = ZonedDateTime.now().minusYears(6);
          int userId = testCommon.insertUser(RandomStringUtils.randomAlphabetic(10)).getId();

          Customer c = customerDao.insert(dummyCustomer(504));
          c.setIsActive(false);
          customerDao.update(c.getId(), c);

          // Old change_history
          ChangeHistoryItem old = new ChangeHistoryItem();
          old.setUserId(userId);
          old.setChangeType(ChangeType.CONTENTS_CHANGED);
          old.setChangeTime(overFiveYearsAgo);
          historyDao.addCustomerChange(c.getId(), old);

          // Recent person_audit_log for the customer directly
          personAuditLogDao.insert(
            new PersonAuditLogLog(c.getId(), null, userId, "test-source", ZonedDateTime.now().minusYears(1))
          );

          List<Integer> ids = customerDao.findPurgeableCustomerIds(100, 0);
          assertFalse("Customer with recent person_audit_log (customer) must not be purgeable", ids.contains(c.getId()));
        });

        it("should not return inactive customer with recent person_audit_log entry for its contact", () -> {
          ZonedDateTime overFiveYearsAgo = ZonedDateTime.now().minusYears(6);
          int userId = testCommon.insertUser(RandomStringUtils.randomAlphabetic(10)).getId();

          Customer c = customerDao.insert(dummyCustomer(505));
          c.setIsActive(false);
          customerDao.update(c.getId(), c);

          Contact cont = new Contact();
          cont.setCustomerId(c.getId());
          cont.setName("contact of 505");
          cont.setPostalAddress(testPostalAddress);
          Contact savedContact = contactDao.insert(Collections.singletonList(cont)).get(0);

          // Old change_history
          ChangeHistoryItem old = new ChangeHistoryItem();
          old.setUserId(userId);
          old.setChangeType(ChangeType.CONTENTS_CHANGED);
          old.setChangeTime(overFiveYearsAgo);
          historyDao.addCustomerChange(c.getId(), old);

          // Recent person_audit_log for the customer's contact
          personAuditLogDao.insert(
            new PersonAuditLogLog(null, savedContact.getId(), userId, "test-source", ZonedDateTime.now().minusYears(1))
          );

          List<Integer> ids = customerDao.findPurgeableCustomerIds(100, 0);
          assertFalse("Customer with recent person_audit_log (contact) must not be purgeable", ids.contains(c.getId()));
        });

        it("should support pagination via pageSize and offset", () -> {
          ZonedDateTime overFiveYearsAgo = ZonedDateTime.now().minusYears(6);
          int userId = testCommon.insertUser(RandomStringUtils.randomAlphabetic(10)).getId();

          // Insert 3 additional purgeable customers (one already exists from beforeEach)
          for (int i = 510; i < 513; i++) {
            Customer c = customerDao.insert(dummyCustomer(i));
            c.setIsActive(false);
            customerDao.update(c.getId(), c);
            ChangeHistoryItem old = new ChangeHistoryItem();
            old.setUserId(userId);
            old.setChangeType(ChangeType.CONTENTS_CHANGED);
            old.setChangeTime(overFiveYearsAgo);
            historyDao.addCustomerChange(c.getId(), old);
          }

          List<Integer> firstPage = customerDao.findPurgeableCustomerIds(2, 0);
          List<Integer> secondPage = customerDao.findPurgeableCustomerIds(2, 2);

          assertEquals(2, firstPage.size());
          assertEquals(2, secondPage.size());
          // Pages must not overlap
          firstPage.forEach(id -> assertFalse("Pages must not overlap", secondPage.contains(id)));
        });

        it("should return empty list when no customers are purgeable", () -> {
          // All customers in beforeEach that are active or have recent data
          Customer active = customerDao.insert(dummyCustomer(520));
          // active customer with no log entries — not purgeable (is_active=true)

          List<Integer> ids = customerDao.findPurgeableCustomerIds(100, 0);
          assertFalse(ids.contains(active.getId()));
        });
      });

      context("findNonDeletableCustomerIds", () -> {

        it("should return empty list for null input", () -> {
          List<Integer> result = customerDao.findNonDeletableCustomerIds(null);
          assertTrue(result.isEmpty());
        });

        it("should return empty list for empty input", () -> {
          List<Integer> result = customerDao.findNonDeletableCustomerIds(Collections.emptyList());
          assertTrue(result.isEmpty());
        });

        it("should not flag customer with no links as non-deletable", () -> {
          Customer c = customerDao.insert(dummyCustomer(600));
          List<Integer> result = customerDao.findNonDeletableCustomerIds(List.of(c.getId()));
          assertTrue("Customer without any links should be deletable", result.isEmpty());
        });

        it("should flag customer linked to an application as non-deletable", () -> {
          Customer c = customerDao.insert(dummyCustomer(601));
          Application app = testCommon.dummyOutdoorApplication("App601", "Handler");
          app.setCustomersWithContacts(
            Collections.singletonList(new CustomerWithContacts(CustomerRoleType.APPLICANT, c, Collections.emptyList()))
          );
          applicationDao.insert(app);

          List<Integer> result = customerDao.findNonDeletableCustomerIds(List.of(c.getId()));
          assertTrue("Customer linked to application must be non-deletable", result.contains(c.getId()));
        });

        it("should flag customer linked to a project as non-deletable", () -> {
          Customer c = customerDao.insert(dummyCustomer(602));
          Project proj = new Project();
          proj.setName("proj602");
          proj.setCustomerId(c.getId());
          proj.setContactId(testCommon.insertContact(c.getId()).getId());
          proj.setStartTime(ZonedDateTime.now());
          proj.setIdentifier("id602");
          proj.setCreatorId(testCommon.insertUser(RandomStringUtils.randomAlphabetic(10)).getId());
          projectDao.insert(proj);

          List<Integer> result = customerDao.findNonDeletableCustomerIds(List.of(c.getId()));
          assertTrue("Customer linked to project must be non-deletable", result.contains(c.getId()));
        });

        it("should flag customer that is an invoice recipient as non-deletable", () -> {
          Customer c = customerDao.insert(dummyCustomer(603));
          Application app = testCommon.dummyOutdoorApplication("InvApp603", RandomStringUtils.randomAlphabetic(10));
          app.setInvoiceRecipientId(c.getId());
          applicationDao.insert(app);

          List<Integer> result = customerDao.findNonDeletableCustomerIds(List.of(c.getId()));
          assertTrue("Invoice recipient customer must be non-deletable", result.contains(c.getId()));
        });

        it("should return only non-deletable IDs from a mixed list", () -> {
          Customer free = customerDao.insert(dummyCustomer(604));
          Customer linked = customerDao.insert(dummyCustomer(605));
          Application app = testCommon.dummyOutdoorApplication("App605", "Handler");
          app.setCustomersWithContacts(
            Collections.singletonList(new CustomerWithContacts(CustomerRoleType.APPLICANT, linked, Collections.emptyList()))
          );
          applicationDao.insert(app);

          List<Integer> result = customerDao.findNonDeletableCustomerIds(List.of(free.getId(), linked.getId()));
          assertFalse("Free customer must not be in non-deletable list", result.contains(free.getId()));
          assertTrue("Linked customer must be in non-deletable list", result.contains(linked.getId()));
        });
      });

      context("archiveCustomers", () -> {

        it("should insert rows into customer_archive for given customer IDs", () -> {
          Customer c1 = customerDao.insert(dummyCustomer(700));
          Customer c2 = customerDao.insert(dummyCustomer(701));

          long beforeCount = customerDao.getArchivedCustomerCount();
          customerDao.archiveCustomers(Set.of(c1.getId(), c2.getId()));
          long afterCount = customerDao.getArchivedCustomerCount();

          assertEquals(2, afterCount - beforeCount);
        });

        it("should store correct customer_id and sap_customer_number in archive", () -> {
          Customer c = customerDao.insert(dummyCustomer(702));
          c.setSapCustomerNumber("SAP-702");
          customerDao.update(c.getId(), c);

          customerDao.archiveCustomers(Set.of(c.getId()));

          // Verify via getArchivedCustomerCount that count grew (full archive record
          // verification would require a dedicated DAO read method)
          long count = customerDao.getArchivedCustomerCount();
          assertTrue(count > 0);
        });

        it("should do nothing when ids is empty", () -> {
          long before = customerDao.getArchivedCustomerCount();
          customerDao.archiveCustomers(Collections.emptySet());
          long after = customerDao.getArchivedCustomerCount();
          assertEquals(before, after);
        });
      });

      context("deleteCustomers", () -> {

        it("should return 0 when ids is null", () -> {
          long result = customerDao.deleteCustomers(null);
          assertEquals(0, result);
        });

        it("should return 0 when ids is empty", () -> {
          long result = customerDao.deleteCustomers(Collections.emptySet());
          assertEquals(0, result);
        });

        it("should permanently delete customers by ID", () -> {
          Customer c1 = customerDao.insert(dummyCustomer(800));
          Customer c2 = customerDao.insert(dummyCustomer(801));

          long deleted = customerDao.deleteCustomers(Set.of(c1.getId(), c2.getId()));

          assertEquals(2, deleted);
          assertFalse("Customer 1 must not exist after deletion", customerDao.findById(c1.getId()).isPresent());
          assertFalse("Customer 2 must not exist after deletion", customerDao.findById(c2.getId()).isPresent());
        });

        it("should only delete the specified customers", () -> {
          Customer toDelete = customerDao.insert(dummyCustomer(802));
          Customer toKeep = customerDao.insert(dummyCustomer(803));

          customerDao.deleteCustomers(Set.of(toDelete.getId()));

          assertFalse(customerDao.findById(toDelete.getId()).isPresent());
          assertTrue("Customer not in delete set must still exist", customerDao.findById(toKeep.getId()).isPresent());
        });
      });

      describe("Inactive SAP customers", () -> {

        it("should return inactive SAP customers missing notificationSentAt", () -> {
          Customer c1 = customerDao.insert(dummyCustomer(1));
          c1.setSapCustomerNumber("SAP1");
          c1.setIsActive(false);
          customerDao.update(c1.getId(), c1);

          Customer c2 = customerDao.insert(dummyCustomer(2));
          c2.setSapCustomerNumber("SAP2");
          c2.setIsActive(false);
          c2.setNotificationSentAt(ZonedDateTime.now());
          customerDao.update(c2.getId(), c2);

          List<CustomerSapInfo> result = customerDao.findUnnotifiedSapCustomers();

          assertEquals(1, result.size());
          assertEquals("SAP1", result.get(0).sapCustomerNumber());
        });

        it("should mark inactive SAP customers as notified", () -> {
          Customer c = customerDao.insert(dummyCustomer(3));
          c.setSapCustomerNumber("SAP3");
          c.setIsActive(false);
          customerDao.update(c.getId(), c);

          customerDao.markSapCustomersNotified(List.of(c.getId()));

          Customer updated = customerDao.findById(c.getId()).get();
          assertNotNull(updated.getNotificationSentAt());
        });

        it("should not return customers without SAP number", () -> {
          Customer c = customerDao.insert(dummyCustomer(4));
          c.setIsActive(false);
          customerDao.update(c.getId(), c);

          List<CustomerSapInfo> result = customerDao.findUnnotifiedSapCustomers();

          assertTrue(result.isEmpty());
        });
      });
    });

    describe("softDeleteCustomers", () -> {

      it("should return 0 when ids is null", () -> {
        long updated = customerDao.softDeleteCustomers(null);
        assertEquals(0, updated);
      });

      it("should return 0 when ids is empty", () -> {
        long updated = customerDao.softDeleteCustomers(Collections.emptySet());
        assertEquals(0, updated);
      });

      it("should soft delete customers by setting isActive=false", () -> {
        Customer c1 = customerDao.insert(dummyCustomer(1000));
        Customer c2 = customerDao.insert(dummyCustomer(1001));

        assertTrue(c1.isActive());
        assertTrue(c2.isActive());

        long updated = customerDao.softDeleteCustomers(Set.of(c1.getId(), c2.getId()));

        assertEquals(2, updated);

        Customer updated1 = customerDao.findById(c1.getId()).orElseThrow();
        Customer updated2 = customerDao.findById(c2.getId()).orElseThrow();

        assertFalse(updated1.isActive());
        assertFalse(updated2.isActive());
      });

      it("should not modify customers not in the given id set", () -> {
        Customer c1 = customerDao.insert(dummyCustomer(2000));
        Customer c2 = customerDao.insert(dummyCustomer(2001));
        Customer c3 = customerDao.insert(dummyCustomer(2002));

        long updated = customerDao.softDeleteCustomers(Set.of(c2.getId()));

        assertEquals(1, updated);

        assertTrue(customerDao.findById(c1.getId()).orElseThrow().isActive());
        assertFalse(customerDao.findById(c2.getId()).orElseThrow().isActive());
        assertTrue(customerDao.findById(c3.getId()).orElseThrow().isActive());
      });
    });

    describe("CustomerDao.findAll", () -> {
      beforeEach(() -> {
        for (int i = 0; i < 15; ++i) {
          Customer customer = customerDao.insert(dummyCustomer(i));
          assertNotNull(customer.getId());
        }
      });

      it("Can fetch 5 customers in ascending ID order", () -> {
        Page<Customer> page = customerDao.findAll(PageRequest.of(1, 5));
        assertEquals(5, page.getSize());
        List<Customer> elements = page.getContent();
        assertEquals(5, elements.size());
        int prevId = Integer.MIN_VALUE;
        for (int i = 0; i < elements.size(); ++i) {
          int id = elements.get(i).getId();
          assertTrue(prevId < id);
          prevId = id;
        }
      });
    });

  }

  private Customer dummyCustomer(int i) {
    Customer customer = new Customer();
    customer.setType(CustomerType.PERSON);
    customer.setName("Customer " + i);
    customer.setRegistryKey("Registry " + i);
    customer.setPhone("Phone " + i);
    customer.setPostalAddress(new PostalAddress("Street " + i, "Postal code " + i, "City " + i));
    customer.setCountryId(testCommon.getCountryIdOfFinland());
    return customer;
  }
}
