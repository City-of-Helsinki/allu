package fi.hel.allu.ui.service;


import fi.hel.allu.common.types.CustomerType;
import fi.hel.allu.ui.domain.CustomerJson;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.*;

public class CustomerServiceTest extends MockServices {
  private static Validator validator;
  @Mock
  protected PersonService personService;
  @Mock
  protected OrganizationService organizationService;
  @InjectMocks
  protected CustomerService customerService;

  @BeforeClass
  public static void setUpBeforeClass() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    initSaveMocks();
    initSearchMocks();
  }

  @Test
  public void testValidationWithValidCustomer() {
    Set<ConstraintViolation<CustomerJson>> constraintViolations =
        validator.validate(createCustomerJson(1, 1));
    assertEquals(0, constraintViolations.size());
  }

  @Test
  public void testTypeOrganizationHasOrganization() {
    CustomerJson customerJson = new CustomerJson();
    customerJson.setType(CustomerType.COMPANY);
    customerJson.setOrganization(createOrganizationJson(1));
    Set<ConstraintViolation<CustomerJson>> constraintViolations =
        validator.validate(customerJson);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  public void testTypeOrganizationHasNotOrganization() {
    CustomerJson customerJson = new CustomerJson();
    customerJson.setType(CustomerType.COMPANY);
    customerJson.setOrganization(null);
    Set<ConstraintViolation<CustomerJson>> constraintViolations =
        validator.validate(customerJson);
    assertEquals(1, constraintViolations.size());
    assertEquals("organization is required, type is Organization", constraintViolations.iterator().next().getMessage());
  }

  @Test
  public void testTypeOrganizationHasPerson() {
    CustomerJson customerJson = new CustomerJson();
    customerJson.setType(CustomerType.COMPANY);
    customerJson.setOrganization(createOrganizationJson(1));
    customerJson.setPerson(createPersonJson(1));
    Set<ConstraintViolation<CustomerJson>> constraintViolations =
        validator.validate(customerJson);
    assertEquals(1, constraintViolations.size());
    assertEquals("person must be null, type is Organization", constraintViolations.iterator().next().getMessage());
  }

  @Test
  public void testTypePersonHasPerson() {
    CustomerJson customerJson = new CustomerJson();
    customerJson.setType(CustomerType.PERSON);
    customerJson.setPerson(createPersonJson(1));
    Set<ConstraintViolation<CustomerJson>> constraintViolations =
        validator.validate(customerJson);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  public void testTypePersonHasNotPerson() {
    CustomerJson customerJson = new CustomerJson();
    customerJson.setType(CustomerType.PERSON);
    Set<ConstraintViolation<CustomerJson>> constraintViolations =
        validator.validate(customerJson);
    assertEquals(1, constraintViolations.size());
    assertEquals("person is required, type is Person", constraintViolations.iterator().next().getMessage());
  }

  @Test
  public void testTypePersonHasOrganization() {
    CustomerJson customerJson = new CustomerJson();
    customerJson.setType(CustomerType.PERSON);
    customerJson.setPerson(createPersonJson(1));
    customerJson.setOrganization(createOrganizationJson(1));
    Set<ConstraintViolation<CustomerJson>> constraintViolations =
        validator.validate(customerJson);
    assertEquals(1, constraintViolations.size());
    assertEquals("organization must be null, type is Person", constraintViolations.iterator().next().getMessage());
  }

  @Test
  public void createValidCustomer() {
    CustomerJson customerJson = customerService.createCustomer(createCustomerJson(null, null));
    assertNotNull(customerJson);
    assertNotNull(customerJson.getId());
    assertEquals(101, customerJson.getId().intValue());
    assertEquals("444-1, Model", customerJson.getSapId());
  }

  @Test
  public void createValidCustomerWithId() {
    CustomerJson customerJson = customerService.createCustomer(createCustomerJson(1, null));
    assertNotNull(customerJson);
    assertNotNull(customerJson.getId());
    assertEquals(1, customerJson.getId().intValue());
    assertEquals("444-1, Json", customerJson.getSapId());
  }

  @Test
  public void updateValidCustomer() {
    CustomerJson customerJson = createCustomerJson(1, 1);
    customerService.updateCustomer(customerJson);
    assertNotNull(customerJson);
    assertNotNull(customerJson.getId());
    assertEquals(1, customerJson.getId().intValue());
    assertEquals("444-1, Json", customerJson.getSapId());
  }

  @Test
  public void updateCustomerWithoutId() {
    CustomerJson customerJson = createCustomerJson(null, 1);
    customerService.updateCustomer(customerJson);
    assertNotNull(customerJson);
    assertNull(customerJson.getId());
    assertEquals("444-1, Json", customerJson.getSapId());
  }

  @Test
  public void testFindById() {
    CustomerJson customerJson = customerService.findCustomerById(101);
    assertNotNull(customerJson);
    assertNotNull(customerJson.getId());
    assertEquals(101, customerJson.getId().intValue());
    assertEquals("444-1, Model", customerJson.getSapId());
  }
}
