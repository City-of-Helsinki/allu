package fi.hel.allu.servicecore.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import fi.hel.allu.servicecore.domain.ContactJson;
import fi.hel.allu.servicecore.domain.CustomerWithContactsJson;
import fi.hel.allu.servicecore.domain.DeleteIdsResult;
import fi.hel.allu.model.domain.Customer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import fi.hel.allu.common.domain.types.CodeSetType;
import fi.hel.allu.model.domain.CodeSet;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.mapper.ChangeHistoryMapper;
import fi.hel.allu.servicecore.mapper.CustomerMapper;

import static org.mockito.ArgumentMatchers.*;
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
  @Mock
  private SearchService searchService;

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
      searchService,
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

  // --- helper ---

  /**
   * Stubs restTemplate.exchange(DELETE) to return a DeleteIdsResult with
   * the given deletedIds and empty skippedIds.
   */
  private void mockSoftDeleteResponse(List<Integer> deletedIds) {
    mockSoftDeleteResponse(deletedIds, Collections.emptyList());
  }

  private void mockSoftDeleteResponse(List<Integer> deletedIds, List<Integer> skippedIds) {
    DeleteIdsResult result = new DeleteIdsResult(deletedIds, skippedIds);
    when(restTemplate.exchange(
      anyString(),
      eq(HttpMethod.DELETE),
      any(HttpEntity.class),
      eq(DeleteIdsResult.class)
    )).thenReturn(new ResponseEntity<>(result, HttpStatus.OK));
  }

  /**
   * Stubs restTemplate.postForObject(POST, ids, Customer[]) — used by getCustomersById.
   */
  private void mockGetCustomersByIds(Customer... customers) {
    when(restTemplate.postForObject(
      anyString(),
      any(),
      eq(Customer[].class)
    )).thenReturn(customers);
  }

  // --- existing tests (preserved) ---

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

  // --- softDeleteCustomers: Elasticsearch update tests ---

  @Test
  public void softDeleteCustomers_updatesCustomersInElasticsearch() {
    // Arrange: two customers are successfully soft-deleted
    mockSoftDeleteResponse(List.of(1, 2));
    Customer c1 = new Customer(); c1.setId(1);
    Customer c2 = new Customer(); c2.setId(2);
    mockGetCustomersByIds(c1, c2);
    when(contactService.findByCustomer(anyInt())).thenReturn(Collections.emptyList());

    // Act
    customerService.softDeleteCustomers(List.of(1, 2));

    // Assert: searchService.updateCustomers must be called once with both customers
    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<CustomerJson>> captor = ArgumentCaptor.forClass(List.class);
    verify(searchService, times(1)).updateCustomers(captor.capture());
    List<CustomerJson> updatedCustomers = captor.getValue();
    Assert.assertEquals(2, updatedCustomers.size());
  }

  @Test
  public void softDeleteCustomers_updatesContactsInElasticsearch() {
    // Arrange: one customer deleted, with two contacts
    mockSoftDeleteResponse(List.of(10));
    Customer c = new Customer(); c.setId(10);
    mockGetCustomersByIds(c);

    ContactJson contact1 = new ContactJson(); contact1.setId(100); contact1.setCustomerId(10);
    ContactJson contact2 = new ContactJson(); contact2.setId(101); contact2.setCustomerId(10);
    when(contactService.findByCustomer(10)).thenReturn(List.of(contact1, contact2));

    // Act
    customerService.softDeleteCustomers(List.of(10));

    // Assert: searchService.updateContacts must be called once with both contacts
    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<ContactJson>> captor = ArgumentCaptor.forClass(List.class);
    verify(searchService, times(1)).updateContacts(captor.capture());
    Assert.assertEquals(2, captor.getValue().size());
  }

  @Test
  public void softDeleteCustomers_noContacts_doesNotCallUpdateContacts() {
    // Arrange: customer has no contacts
    mockSoftDeleteResponse(List.of(20));
    Customer c = new Customer(); c.setId(20);
    mockGetCustomersByIds(c);
    when(contactService.findByCustomer(20)).thenReturn(Collections.emptyList());

    // Act
    customerService.softDeleteCustomers(List.of(20));

    // Assert: updateContacts must never be called when there are no contacts to update
    verify(searchService, never()).updateContacts(any());
  }

  @Test
  public void softDeleteCustomers_fetchesContactsForEachDeletedCustomer() {
    // Arrange: three customers deleted, each with one contact
    mockSoftDeleteResponse(List.of(1, 2, 3));
    mockGetCustomersByIds(
      createCustomerWithId(1), createCustomerWithId(2), createCustomerWithId(3)
    );
    when(contactService.findByCustomer(1)).thenReturn(List.of(contactOf(1, 101)));
    when(contactService.findByCustomer(2)).thenReturn(List.of(contactOf(2, 102)));
    when(contactService.findByCustomer(3)).thenReturn(List.of(contactOf(3, 103)));

    // Act
    customerService.softDeleteCustomers(List.of(1, 2, 3));

    // Assert: findByCustomer called once per deleted customer
    verify(contactService, times(1)).findByCustomer(1);
    verify(contactService, times(1)).findByCustomer(2);
    verify(contactService, times(1)).findByCustomer(3);

    // All three contacts are pushed to ES in a single updateContacts call
    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<ContactJson>> captor = ArgumentCaptor.forClass(List.class);
    verify(searchService, times(1)).updateContacts(captor.capture());
    Assert.assertEquals(3, captor.getValue().size());
  }

  @Test
  public void softDeleteCustomers_nullResponse_doesNotUpdateElasticsearch() {
    // Arrange: model-service returns null body
    when(restTemplate.exchange(
      anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(DeleteIdsResult.class)
    )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

    // Act
    DeleteIdsResult result = customerService.softDeleteCustomers(List.of(1));

    // Assert: no ES calls, no NPE
    Assert.assertNull(result);
    verify(searchService, never()).updateCustomers(any());
    verify(searchService, never()).updateContacts(any());
    verify(contactService, never()).findByCustomer(anyInt());
  }

  @Test
  public void softDeleteCustomers_allSkipped_doesNotUpdateElasticsearch() {
    // Arrange: all customers are skipped (e.g. became linked to application)
    mockSoftDeleteResponse(Collections.emptyList(), List.of(5, 6));

    // Act
    customerService.softDeleteCustomers(List.of(5, 6));

    // Assert: nothing to update in ES when deletedIds is empty
    verify(searchService, never()).updateCustomers(any());
    verify(searchService, never()).updateContacts(any());
    verify(contactService, never()).findByCustomer(anyInt());
  }

  @Test
  public void softDeleteCustomers_partialSuccess_updatesOnlyDeletedCustomers() {
    // Arrange: customer 1 deleted, customer 2 skipped
    mockSoftDeleteResponse(List.of(1), List.of(2));
    Customer c = new Customer(); c.setId(1);
    mockGetCustomersByIds(c);
    when(contactService.findByCustomer(1)).thenReturn(Collections.emptyList());

    // Act
    DeleteIdsResult result = customerService.softDeleteCustomers(List.of(1, 2));

    // Assert: only customer 1 (deletedIds) causes ES update
    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<CustomerJson>> captor = ArgumentCaptor.forClass(List.class);
    verify(searchService, times(1)).updateCustomers(captor.capture());
    Assert.assertEquals(1, captor.getValue().size());

    // contactService must only be called for the deleted customer
    verify(contactService, times(1)).findByCustomer(1);
    verify(contactService, never()).findByCustomer(2);

    // Result must report both deleted and skipped IDs correctly
    Assert.assertEquals(List.of(1), result.deletedIds());
    Assert.assertEquals(List.of(2), result.skippedIds());
  }

  @Test
  public void softDeleteCustomers_returnsResultFromModelService() {
    // Arrange
    mockSoftDeleteResponse(List.of(7), List.of(8));
    mockGetCustomersByIds(createCustomerWithId(7));
    when(contactService.findByCustomer(anyInt())).thenReturn(Collections.emptyList());

    // Act
    DeleteIdsResult result = customerService.softDeleteCustomers(List.of(7, 8));

    // Assert: the result from model-service is returned unchanged
    Assert.assertNotNull(result);
    Assert.assertEquals(List.of(7), result.deletedIds());
    Assert.assertEquals(List.of(8), result.skippedIds());
  }

  // --- private test helpers ---

  private Customer createCustomerWithId(int id) {
    Customer c = new Customer();
    c.setId(id);
    return c;
  }

  private ContactJson contactOf(int customerId, int contactId) {
    ContactJson c = new ContactJson();
    c.setId(contactId);
    c.setCustomerId(customerId);
    return c;
  }
}
