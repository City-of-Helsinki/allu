package fi.hel.allu.ui.service;

import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.ui.mapper.ApplicationMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CustomerServiceTest extends MockServices {
  private static Validator validator;
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
    customerService = new CustomerService(
        props, restTemplate, new ApplicationMapper(), Mockito.mock(SearchService.class), Mockito.mock(ContactService.class));
  }

  @Test
  public void testValidationWithValidCustomer() {
    Set<ConstraintViolation<CustomerJson>> constraintViolations =
        validator.validate(createCustomerJson(1));
    assertEquals(0, constraintViolations.size());
  }

  @Test
  public void createValidCustomer() {
    CustomerJson customerJson = customerService.createCustomer(createCustomerJson(null));
    assertNotNull(customerJson);
    assertNotNull(customerJson.getId());
    assertEquals(103, customerJson.getId().intValue());
  }

  @Test
  public void createValidCustomerWithId() {
    CustomerJson customerJson = customerService.createCustomer(createCustomerJson(1));
    assertNotNull(customerJson);
    assertNotNull(customerJson.getId());
    assertEquals(103, customerJson.getId().intValue());
  }

  @Test
  public void testFindById() {
    CustomerJson customerJson = customerService.findCustomerById(103);
    assertNotNull(customerJson);
    assertNotNull(customerJson.getId());
    assertEquals(103, customerJson.getId().intValue());
  }
}
