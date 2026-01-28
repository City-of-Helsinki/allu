package fi.hel.allu.model.dao;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    describe("findCustomersEligibleForDeletion", () -> {
      it("returns customer with no applications, projects or invoice references", () -> {
        Customer customer = customerDao.insert(testCustomer);

        List<DeletableCustomer> result =
          customerDao.findCustomersEligibleForDeletion();

        assertEquals(1, result.size());
        assertEquals(customer.getId(), result.get(0).getCustomerId());
      });

      it("does not return customer linked to application", () -> {
        Customer customer = customerDao.insert(testCustomer);

        Application app = testCommon.dummyOutdoorApplication("App", "Owner", customer);
        applicationDao.insert(app);

        List<DeletableCustomer> result =
          customerDao.findCustomersEligibleForDeletion();

        assertTrue(result.isEmpty());
      });

      it("does not return customer linked to project", () -> {
        Customer customer = customerDao.insert(testCustomer);

        Project project = new Project();
        project.setName("Test project");
        project.setCustomerId(customer.getId());
        project.setContactId(testCommon.insertContact(customer.getId()).getId());
        project.setStartTime(ZonedDateTime.now());
        project.setIdentifier("P1");
        project.setCreatorId(testCommon.insertUser("user").getId());

        projectDao.insert(project);

        List<DeletableCustomer> result =
          customerDao.findCustomersEligibleForDeletion();

        assertTrue(result.isEmpty());
      });

      it("does not return customer used as invoice recipient", () -> {
        Customer customer = customerDao.insert(testCustomer);

        Application app = testCommon.dummyOutdoorApplication("App", "Owner");
        app.setInvoiceRecipientId(customer.getId());
        applicationDao.insert(app);

        List<DeletableCustomer> result =
          customerDao.findCustomersEligibleForDeletion();

        assertTrue(result.isEmpty());
      });
    });

    describe("getDeletableCustomers", () -> {
      it("stores and gets deletable customers into/from database", () -> {
        Customer c1 = customerDao.insert(dummyCustomer(1));
        Customer c2 = customerDao.insert(dummyCustomer(2));

        DeletableCustomer dc1 =
          new DeletableCustomer(c1.getId(), c1.getSapCustomerNumber());
        DeletableCustomer dc2 =
          new DeletableCustomer(c2.getId(), c2.getSapCustomerNumber());

        customerDao.storeCustomersEligibleForDeletion(Arrays.asList(dc1, dc2));

        List<DeletableCustomer> stored =
          customerDao.getDeletableCustomers(PageRequest.of(0, 10)).getContent();

        assertEquals(2, stored.size());
      });

      it("returns paged deletable customers", () -> {
        List<DeletableCustomer> deletables = IntStream.range(0, 5)
          .mapToObj(i -> {
            Customer c = customerDao.insert(dummyCustomer(i));
            return new DeletableCustomer(c.getId(), c.getSapCustomerNumber());
          })
          .collect(Collectors.toList());

        customerDao.storeCustomersEligibleForDeletion(deletables);

        Page<DeletableCustomer> page =
          customerDao.getDeletableCustomers(PageRequest.of(1, 2));

        assertEquals(5, page.getTotalElements());
        assertEquals(2, page.getContent().size());
      });

      it("uses default pageable when pageable is null", () -> {
        Customer c = customerDao.insert(dummyCustomer(1));
        customerDao.storeCustomersEligibleForDeletion(
          Collections.singletonList(
            new DeletableCustomer(c.getId(), c.getSapCustomerNumber())
          )
        );

        Page<DeletableCustomer> page =
          customerDao.getDeletableCustomers(null);

        assertEquals(1, page.getTotalElements());
      });
    });

    describe("findNonDeletableCustomerIds", () -> {

      it("returns customer linked to application", () -> {
        Customer c = customerDao.insert(dummyCustomer(1));
        Application app = testCommon.dummyOutdoorApplication("Test", "User", c);
        applicationDao.insert(app);

        List<Integer> result =
          customerDao.findNonDeletableCustomerIds(List.of(c.getId()));

        assertEquals(List.of(c.getId()), result);
      });

      it("returns customer linked to project", () -> {
        Customer c = customerDao.insert(dummyCustomer(1));

        Project p = new Project();
        p.setName("Test project");
        p.setCustomerId(c.getId());
        p.setContactId(testCommon.insertContact(c.getId()).getId());
        p.setStartTime(ZonedDateTime.now());
        p.setIdentifier("P1");
        p.setCreatorId(testCommon.insertUser("user").getId());
        projectDao.insert(p);

        List<Integer> result =
          customerDao.findNonDeletableCustomerIds(List.of(c.getId()));

        assertEquals(List.of(c.getId()), result);
      });

      it("does not return customer with no relations", () -> {
        Customer c = customerDao.insert(dummyCustomer(1));

        List<Integer> result =
          customerDao.findNonDeletableCustomerIds(List.of(c.getId()));

        assertTrue(result.isEmpty());
      });
    });

    describe("archive and delete customers", () -> it("archives then deletes customer", () -> {
      Customer c = customerDao.insert(dummyCustomer(1));

      customerDao.archiveCustomers(Set.of(c.getId()));
      customerDao.deleteCustomers(Set.of(c.getId()));

      assertTrue(customerDao.findById(c.getId()).isEmpty());

      long archiveCount = customerDao.getArchivedCustomerCount();

      assertEquals(1, archiveCount);
    }));
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
