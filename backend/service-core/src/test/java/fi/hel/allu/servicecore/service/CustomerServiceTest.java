package fi.hel.allu.servicecore.service;

import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.mapper.CustomerMapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.mockito.Mockito.when;

public class CustomerServiceTest extends MockServices {
  private static Validator validator;

  @Mock
  private UserService userService;

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
        props, restTemplate, new CustomerMapper(userService), Mockito.mock(SearchService.class),
        Mockito.mock(ContactService.class), userService);
    when(userService.getCurrentUser()).thenReturn(new UserJson());
  }

  @Test
  public void testValidationWithValidCustomer() {
    Set<ConstraintViolation<CustomerJson>> constraintViolations =
        validator.validate(createCustomerJson(1));
    Assert.assertEquals(0, constraintViolations.size());
  }

  @Test
  public void createValidCustomer() {
    CustomerJson customerJson = customerService.createCustomer(createCustomerJson(null));
    Assert.assertNotNull(customerJson);
    Assert.assertNotNull(customerJson.getId());
    Assert.assertEquals(103, customerJson.getId().intValue());
  }

  @Test
  public void createValidCustomerWithId() {
    CustomerJson customerJson = customerService.createCustomer(createCustomerJson(1));
    Assert.assertNotNull(customerJson);
    Assert.assertNotNull(customerJson.getId());
    Assert.assertEquals(103, customerJson.getId().intValue());
  }

  @Test
  public void testFindById() {
    CustomerJson customerJson = customerService.findCustomerById(103);
    Assert.assertNotNull(customerJson);
    Assert.assertNotNull(customerJson.getId());
    Assert.assertEquals(103, customerJson.getId().intValue());
  }
}
