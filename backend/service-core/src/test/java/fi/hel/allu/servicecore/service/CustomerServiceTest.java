package fi.hel.allu.servicecore.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import fi.hel.allu.servicecore.domain.ContactJson;
import fi.hel.allu.servicecore.domain.CustomerWithContactsJson;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import fi.hel.allu.common.domain.types.CodeSetType;
import fi.hel.allu.model.domain.CodeSet;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.mapper.ChangeHistoryMapper;
import fi.hel.allu.servicecore.mapper.CustomerMapper;

import static org.mockito.Mockito.*;

public class CustomerServiceTest extends MockServices {
  private static Validator validator;

  @Mock
  private UserService userService;
  @Mock
  private CodeSetService codeSetService;
  @Mock
  private ChangeHistoryMapper changeHistoryMapper;
  @Mock
  private ContactService contactService;

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
    CustomerService realCustomerService = new CustomerService(
      TestProperties.getProperties(),
      restTemplate,
      new CustomerMapper(userService, codeSetService),
      Mockito.mock(SearchService.class),
      contactService,
      userService,
      Mockito.mock(PersonAuditLogService.class),
      changeHistoryMapper);
    customerService = Mockito.spy(realCustomerService);
    when(userService.getCurrentUser()).thenReturn(new UserJson());
    when(codeSetService.findByTypeAndCode(Mockito.eq(CodeSetType.Country), Mockito.anyString()))
      .thenReturn(new CodeSet(CodeSetType.Country, "FI", "Suomi", null));
    when(codeSetService.findById(Mockito.anyInt()))
      .thenReturn(new CodeSet(CodeSetType.Country, "FI", "Suomi", null));
    CustomerJson customer = new CustomerJson();
    customer.setId(1);
    customer.setActive(false);
    doReturn(customer).when(customerService).updateCustomer(Mockito.anyInt(), Mockito.any());
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

  @Test
  public void updateCustomerWithContacts_customerInactive_emptyContactList_setRelatedContactsInactive() {
    CustomerJson inactiveCustomer = new CustomerJson();
    inactiveCustomer.setId(1);
    inactiveCustomer.setActive(false);
    List<ContactJson> mockContacts = Arrays.asList(
      new ContactJson(1, inactiveCustomer.getId(), "Contact 1", "", "", "", "", "", true, false),
      new ContactJson(2, inactiveCustomer.getId(), "Contact 2", "", "", "", "", "", true, false)
    );
    CustomerWithContactsJson customerWithContactsJson = new CustomerWithContactsJson();
    customerWithContactsJson.setCustomer(inactiveCustomer);
    customerWithContactsJson.setContacts(new ArrayList<>());

    when(contactService.findByCustomer(inactiveCustomer.getId())).thenReturn(mockContacts);
    when(contactService.updateContacts(any())).thenAnswer(invocation -> {
      List<ContactJson> inputContacts = invocation.getArgument(0);
      inputContacts.forEach(c -> c.setActive(false));
      return inputContacts;
    });

    CustomerWithContactsJson updatedCustomerWithContactsJson = customerService.updateCustomerWithContacts(inactiveCustomer.getId(), customerWithContactsJson);

    Assert.assertNotNull(updatedCustomerWithContactsJson.getContacts());
    Assert.assertEquals(2, updatedCustomerWithContactsJson.getContacts().size());
    updatedCustomerWithContactsJson.getContacts().forEach(contact -> Assert.assertFalse(contact.isActive()));
    verify(contactService, times(1)).findByCustomer(1);
    verify(contactService, times(1)).updateContacts(any());
    verify(contactService, never()).createContacts(any());
  }

  @Test
  public void updateCustomerWithContacts_customerInactive_withContactList_setRelatedContactsInactive() {
    CustomerJson inactiveCustomer = new CustomerJson();
    inactiveCustomer.setId(1);
    inactiveCustomer.setActive(false);
    List<ContactJson> contacts = Arrays.asList(
      new ContactJson(1, inactiveCustomer.getId(), "Contact 1", "", "", "", "", "", true, false),
      new ContactJson(2, inactiveCustomer.getId(), "Contact 2", "", "", "", "", "", true, false)
    );
    CustomerWithContactsJson customerWithContactsJson = new CustomerWithContactsJson();
    customerWithContactsJson.setCustomer(inactiveCustomer);
    customerWithContactsJson.setContacts(contacts);

    when(contactService.updateContacts(any())).thenAnswer(invocation -> {
      List<ContactJson> inputContacts = invocation.getArgument(0);
      inputContacts.forEach(c -> c.setActive(false));
      return inputContacts;
    });

    CustomerWithContactsJson updatedCustomerWithContactsJson = customerService.updateCustomerWithContacts(inactiveCustomer.getId(), customerWithContactsJson);

    Assert.assertNotNull(updatedCustomerWithContactsJson.getContacts());
    Assert.assertEquals(2, updatedCustomerWithContactsJson.getContacts().size());
    updatedCustomerWithContactsJson.getContacts().forEach(contact -> Assert.assertFalse(contact.isActive()));
    Assert.assertEquals(contacts.size(), updatedCustomerWithContactsJson.getContacts().size());
    verify(contactService, never()).findByCustomer(anyInt());
    verify(contactService, times(1)).updateContacts(any());
    verify(contactService, never()).createContacts(any());
  }
}
