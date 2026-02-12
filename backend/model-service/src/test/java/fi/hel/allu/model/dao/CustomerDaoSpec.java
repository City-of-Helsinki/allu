package fi.hel.allu.model.dao;

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
          project.setStartTime(java.time.ZonedDateTime.now());
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
          change.setChangeTime(java.time.ZonedDateTime.now()); // liian uusi
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
          change.setChangeTime(java.time.ZonedDateTime.now().minusDays(2)); // vanhempi kuin cutoff
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
          project.setStartTime(java.time.ZonedDateTime.now());
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
          change.setChangeTime(java.time.ZonedDateTime.now()); // liian uusi
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
          change.setChangeTime(java.time.ZonedDateTime.now().minusDays(2)); // vanhempi kuin cutoff
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
      });

      describe("Archived SAP customers", () -> {

        it("should find only archived SAP customers without notificationSentAt", () -> {
          Customer customer1 = customerDao.insert(dummyCustomer(500));
          customer1.setSapCustomerNumber("SAP-500");
          customerDao.update(customer1.getId(), customer1);

          Customer customer2 = customerDao.insert(dummyCustomer(501));
          customer2.setSapCustomerNumber("SAP-501");
          customerDao.update(customer2.getId(), customer2);

          Customer customerWithoutSap = customerDao.insert(dummyCustomer(502));

          // Arkistoidaan kaikki
          customerDao.archiveCustomers(Set.of(customer1.getId(), customer2.getId(), customerWithoutSap.getId()));

          // Merkitään toinen notifiedksi
          List<ArchivedCustomer> archived = customerDao.findUnnotifiedArchivedSapCustomers();
          Integer idToMark = archived.stream()
            .filter(a -> "SAP-501".equals(a.getSapCustomerNumber()))
            .findFirst()
            .get()
            .getId();
          customerDao.markArchivedSapCustomersNotified(List.of(idToMark));

          List<ArchivedCustomer> result = customerDao.findUnnotifiedArchivedSapCustomers();

          assertEquals(1, result.size());
          assertEquals("SAP-500", result.get(0).getSapCustomerNumber());
          assertNotNull(result.get(0).getDeletedAt());
          assertNull(result.get(0).getNotificationSentAt());
        });

        it("should mark archived SAP customers as notified", () -> {
          Customer customer = customerDao.insert(dummyCustomer(600));
          customer.setSapCustomerNumber("SAP-600");
          customerDao.update(customer.getId(), customer);

          customerDao.archiveCustomers(Set.of(customer.getId()));

          List<ArchivedCustomer> archived = customerDao.findUnnotifiedArchivedSapCustomers();

          assertEquals(1, archived.size());

          customerDao.markArchivedSapCustomersNotified(List.of(archived.get(0).getId()));
          List<ArchivedCustomer> result = customerDao.findUnnotifiedArchivedSapCustomers();

          assertTrue(result.isEmpty());
        });

        it("should not return archived customers without sap number", () -> {
          Customer customer = customerDao.insert(dummyCustomer(700));

          customerDao.archiveCustomers(Set.of(customer.getId()));

          List<ArchivedCustomer> result = customerDao.findUnnotifiedArchivedSapCustomers();

          assertTrue(result.isEmpty());
        });
      });
    });

    describe("CustomerDao.findAll", () -> {
      beforeEach(() -> {
        for (int i = 0; i < 15; ++i) {
          Customer customer = customerDao.insert(dummyCustomer(i));
          assertNotNull(customer.getId());
        }
      });

      it("Can fetch 5 customers in ascendind ID order", () -> {
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
