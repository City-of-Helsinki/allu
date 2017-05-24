package fi.hel.allu.model.dao;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.types.CustomerRoleType;
import fi.hel.allu.common.types.CustomerType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.testUtils.SpeccyTestBase;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.*;
import java.util.stream.Collectors;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.junit.Assert.*;

@RunWith(Spectrum.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
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
      testCustomer.setPhone("12345");
      testCustomer.setPostalAddress(testPostalAddress);

      testContact.setCustomerId(null);
      testContact.setEmail("test@email.fi");
      testContact.setName("test name");
      testContact.setPostalAddress(testPostalAddress);
    });

    describe("Customer dao customers", () -> {
      beforeEach(() -> insertedCustomer = customerDao.insert(testCustomer));
      it("should find customer by id", () -> {
        Optional<Customer> customerOpt = customerDao.findById(insertedCustomer.getId());
        assertTrue(customerOpt.isPresent());
        Customer customer = customerOpt.get();
        assertEquals(testCustomer.getName(), customer.getName());
        assertEquals(testCustomer.getPhone(), customer.getPhone());
        assertEquals(testCustomer.getPostalAddress().getStreetAddress(), customer.getPostalAddress().getStreetAddress());
      });
      it("should find customers by id", () -> {
        List<Customer> customers = customerDao.findAll();
        assertFalse(customers.isEmpty());
        Customer customer = customers.get(0);
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
    });
    describe("Customer dao customers with empty contacts", () -> {
      beforeEach(() -> {
        insertedCustomer = customerDao.insert(testCustomer);
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
    describe("Customer dao customers with contacts", () -> {
      beforeEach(() -> {
        insertedCustomer = customerDao.insert(testCustomer);
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
  }
}
