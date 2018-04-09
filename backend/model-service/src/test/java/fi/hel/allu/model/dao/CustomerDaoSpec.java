package fi.hel.allu.model.dao;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.testUtils.SpeccyTestBase;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
          Page<Customer> customers = customerDao.findAll(new PageRequest(0, Integer.MAX_VALUE));
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
          Customer businessCustomer = customerDao.insert(testCustomer);
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
          List<Customer> customers = customerDao.findInvoiceRecipientsWithoutSapNumber();
          assertEquals(1, customers.size());
          assertEquals(insertedCustomer.getId(), customers.get(0).getId());
      });
      it("should not return invoice recipients of replaced applications", () -> {
        Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
        application.setInvoiceRecipientId(insertedCustomer.getId());
        Application replacingApplication = testCommon.dummyOutdoorApplication("Replacing Test Application", "Test Handler 2");
        replacingApplication = applicationDao.insert(replacingApplication);
        application.setReplacedByApplicationId(replacingApplication.getId());
        insertedApplication = applicationDao.insert(application);
        applicationDao.addTag(insertedApplication.getId(), testCommon.dummyTag(ApplicationTagType.SAP_ID_MISSING));
        List<Customer> customers = customerDao.findInvoiceRecipientsWithoutSapNumber();
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
        Page<Customer> page = customerDao.findAll(new PageRequest(1, 5));
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
    return customer;
  }
}
