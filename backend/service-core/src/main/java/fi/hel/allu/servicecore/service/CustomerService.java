package fi.hel.allu.servicecore.service;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.domain.DeleteIdsResult;
import fi.hel.allu.servicecore.util.PageRequestBuilder;
import fi.hel.allu.servicecore.util.RestResponsePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.mapper.ChangeHistoryMapper;
import fi.hel.allu.servicecore.mapper.CustomerMapper;

@Service
public class CustomerService {

  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;
  private final CustomerMapper customerMapper;
  private final SearchService searchService;
  private final ContactService contactService;
  private final UserService userService;
  private final PersonAuditLogService personAuditLogService;
  private final ChangeHistoryMapper changeHistoryMapper;

  private final Logger logger = LoggerFactory.getLogger(CustomerService.class);

  @Autowired
  public CustomerService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      CustomerMapper customerMapper,
      SearchService searchService,
      ContactService contactService,
      UserService userService,
      PersonAuditLogService personAuditLogService,
      ChangeHistoryMapper changeHistoryMapper) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.customerMapper = customerMapper;
    this.searchService = searchService;
    this.contactService = contactService;
    this.userService = userService;
    this.personAuditLogService = personAuditLogService;
    this.changeHistoryMapper = changeHistoryMapper;
  }

  /**
   * Create a new customer. Customer id must null to create a new customer.
   *
   * @param customerJson Customer that is going to be created
   * @return Created customer.
   */
  public CustomerJson createCustomer(CustomerJson customerJson) {
    CustomerChange customerChange = new CustomerChange(userService.getCurrentUser().getId(),
        customerMapper.createCustomerModel(customerJson));
    Customer customerModel = restTemplate.postForObject(
        applicationProperties.getCustomerCreateUrl(),
        customerChange,
        Customer.class);
    CustomerJson createdCustomer = customerMapper.createCustomerJson(customerModel);
    // all created customers will be set active
    createdCustomer.setActive(true);
    searchService.insertCustomer(createdCustomer);
    return createdCustomer;
  }

  /**
   * Create a new customer with contacts. Customer id must null to create a new customer.
   *
   * @param customerWithContactsJson Customer with conctacts to be created.
   * @return Created customer with contacts.
   */
  public CustomerWithContactsJson createCustomerWithContacts(CustomerWithContactsJson customerWithContactsJson) {
    if (customerWithContactsJson.getCustomer() == null) {
      throw new IllegalArgumentException("Customer cannot be null when creating a new customer!");
    }
    CustomerJson customerJson = createCustomer(customerWithContactsJson.getCustomer());
    CustomerWithContactsJson createdCustomerWithContacts = new CustomerWithContactsJson();
    createdCustomerWithContacts.setCustomer(customerJson);

    if (customerWithContactsJson.getContacts() != null && !customerWithContactsJson.getContacts().isEmpty()) {
      List<ContactJson> newContacts = customerWithContactsJson.getContacts();
      newContacts.forEach(c -> c.setCustomerId(customerJson.getId()));
      List<ContactJson> contacts = contactService.createContacts(newContacts);
      searchService.insertContacts(contacts);
      createdCustomerWithContacts.setContacts(contacts);
    }

    createdCustomerWithContacts.setRoleType(customerWithContactsJson.getRoleType());
    return createdCustomerWithContacts;
  }

  /**
   * Updates customer with given data but preserving existing invoicing info
   * @return updated customer
   */
  public CustomerJson updateCustomer(int customerId, CustomerJson customerJson) {
    CustomerJson existing = findCustomerById(customerId);
    customerJson.setSapCustomerNumber(existing.getSapCustomerNumber());
    customerJson.setInvoicingProhibited(existing.isInvoicingProhibited());
    return updateCustomerWithInvoicingInfo(customerId, customerJson);
  }

  /**
   * Updates customer and customer's contacts
   * @return updated customer and contacts
   */
  public CustomerWithContactsJson updateCustomerWithContacts(int customerId, CustomerWithContactsJson customerWithContactsJson) {
    CustomerWithContactsJson updatedCustomerWithContactsJson = new CustomerWithContactsJson();
    updatedCustomerWithContactsJson.setRoleType(customerWithContactsJson.getRoleType());
    if (customerWithContactsJson.getCustomer() != null) {
      CustomerJson updatedCustomer = customerWithContactsJson.getCustomer();
      updatedCustomer.setId(customerId);
      updatedCustomerWithContactsJson.setCustomer(updateCustomer(updatedCustomer.getId(), updatedCustomer));
      if (!customerWithContactsJson.getCustomer().isActive()) {
        if (customerWithContactsJson.getContacts().isEmpty()) {
          List<ContactJson> contactsToSetInactive = contactService.findByCustomer(customerId);
          contactsToSetInactive.forEach(c -> c.setActive(false));
          customerWithContactsJson.setContacts(contactsToSetInactive);
        } else {
          customerWithContactsJson.getContacts().forEach(c -> c.setActive(false));
        }
      }
    }
    if (customerWithContactsJson.getContacts() != null) {
      customerWithContactsJson.getContacts().forEach(c -> c.setCustomerId(customerId));
      Map<Boolean, List<ContactJson>> newOldContacts =
          customerWithContactsJson.getContacts().stream().collect(Collectors.partitioningBy(c -> c.getId() != null));
      ArrayList<ContactJson> allContacts = new ArrayList<>();
      if (!newOldContacts.get(true).isEmpty()) {
        List<ContactJson> oldContacts = contactService.updateContacts(newOldContacts.get(true));
        searchService.updateContacts(oldContacts);
        allContacts.addAll(oldContacts);
      }
      if (!newOldContacts.get(false).isEmpty()) {
        List<ContactJson> newContacts = contactService.createContacts(newOldContacts.get(false));
        searchService.insertContacts(newContacts);
        allContacts.addAll(newContacts);
      }
      updatedCustomerWithContactsJson.setContacts(allContacts);
    }

    return updatedCustomerWithContactsJson;
  }

  /**
   * Update the given customer. Customer is updated if the id is given.
   * Updates also invoicing info (sap-id and invoicing prohibited)
   *
   * @param customerJson customer that is going to be updated
   */
  public CustomerJson updateCustomerWithInvoicingInfo(int customerId, CustomerJson customerJson) {
    CustomerChange customerChange = new CustomerChange(userService.getCurrentUser().getId(),
        customerMapper.createCustomerModel(customerJson));
    HttpEntity<CustomerChange> requestEntity = new HttpEntity<>(customerChange);
    ResponseEntity<Customer> response = restTemplate.exchange(
        applicationProperties.getCustomerUpdateUrl(),
        HttpMethod.PUT,
        requestEntity,
        Customer.class,
        customerId);
    CustomerJson updatedCustomer = customerMapper.createCustomerJson(response.getBody());
    searchService.updateCustomers(Collections.singletonList(updatedCustomer));
    // update search index of applications having this customer
    ParameterizedTypeReference<Map<Integer, List<CustomerRoleType>>> typeRef =
        new ParameterizedTypeReference<Map<Integer, List<CustomerRoleType>>>() {};
    ResponseEntity<Map<Integer, List<CustomerRoleType>>> applicationIdToCustomerRoleType =
        restTemplate.exchange(
            applicationProperties.getCustomerApplicationsUrl(),
            HttpMethod.GET,
            new HttpEntity<>((Integer) null), // dummy request entity, not used for anything. Just satisfying interface requirements
            typeRef,
            customerId);
    if (applicationIdToCustomerRoleType.getBody().size() != 0) {
      searchService.updateCustomerOfApplications(updatedCustomer, applicationIdToCustomerRoleType.getBody());
    }

    return updatedCustomer;
  }

  public CustomerJson findCustomerById(int customerId) {
    ResponseEntity<Customer> customerResult =
        restTemplate.getForEntity(applicationProperties.getCustomerByIdUrl(), Customer.class, customerId);
    personAuditLogService.log(customerResult.getBody(), "CustomerService");
    return customerMapper.createCustomerJson(customerResult.getBody());
  }

  public List<CustomerJson> findCustomerByBusinessId(String businessId) {
    ResponseEntity<Customer[]> customerResult =
        restTemplate.getForEntity(applicationProperties.getCustomerByBusinessIdUrl(), Customer[].class, businessId);
    return Arrays.stream(customerResult.getBody())
        .map(customer -> customerMapper.createCustomerJson(customer))
        .collect(Collectors.toList());
  }

  /**
   * Find customers by given fields.
   *
   * @param queryParameters list of query parameters
   * @return List of found application with details
   */
  public Page<CustomerJson> search(QueryParameters queryParameters, Pageable pageRequest) {
    Page<CustomerJson> customers = searchService.searchCustomer(queryParameters, pageRequest,
        ids -> getCustomersById(ids));
    customers.forEach(c -> personAuditLogService.log(c, "CustomerService"));
    return customers;
  }

  public Page<CustomerJson> searchByType(CustomerType type, QueryParameters queryParameters, Pageable pageRequest, Boolean matchAny) {
    Page<CustomerJson> customers = searchService.searchCustomerByType(type, queryParameters, pageRequest, matchAny,
        ids -> getCustomersById(ids));
    customers.forEach(c -> personAuditLogService.log(c, "CustomerService"));
    return customers;
  }


  public List<CustomerJson> getCustomersById(List<Integer> customerIds) {
    Customer[] customers = restTemplate.postForObject(
        applicationProperties.getCustomersByIdUrl(),
        customerIds,
        Customer[].class);
    List<CustomerJson> resultList = Arrays.asList(customers).stream().map(a -> customerMapper.createCustomerJson(a)).collect(Collectors.toList());
    SearchService.orderByIdList(customerIds, resultList, (customer) -> customer.getId());
    return resultList;
  }

  /**
   * Get change items for a customer
   *
   * @param customerId acustomer's database ID
   * @return list of changes ordered from oldest to newest
   */
  public List<ChangeHistoryItemJson> getChanges(Integer customerId) {
    return Arrays.stream(
        restTemplate.getForObject(applicationProperties.getCustomerHistoryUrl(), ChangeHistoryItem[].class, customerId))
        .map(c -> changeHistoryMapper.mapToJson(c))
        .collect(Collectors.toList());
  }

  public List<InvoiceRecipientCustomer> findInvoiceRecipientsWithoutSapNumber() {
    ParameterizedTypeReference<List<InvoiceRecipientCustomer>> typeRef = new ParameterizedTypeReference<List<InvoiceRecipientCustomer>>() {};
    return restTemplate.exchange(applicationProperties.getInvoiceRecipientsWithoutSapNumberUrl(), HttpMethod.GET, null, typeRef).getBody();
  }

  public Integer getNumberInvoiceRecipientsWithoutSapNumber() {
    return restTemplate.getForEntity(applicationProperties.getNrOfInvoiceRecipientsWithoutSapNumberUrl(), Integer.class).getBody();
  }

  public List<CustomerUpdateLog> getCustomerUpdateLog() {
    ParameterizedTypeReference<List<CustomerUpdateLog>> typeRef =
        new ParameterizedTypeReference<List<CustomerUpdateLog>>() {};
    return restTemplate.exchange(applicationProperties.getCustomerUpdateLogUrl(), HttpMethod.GET, null, typeRef).getBody();
  }

  public void setUpdateLogsProcessed(List<Integer> logIds) {
    restTemplate.put(applicationProperties.getCustomerUpdateLogProcessedUrl(), logIds);
  }

  /**
   * Get customers that are eligible for permanent deletion by calling model-service endpoint.
   * Data is retrieved from the model-service's database.
   * A customer is deletable if it is not linked to any application or project in Allu.
   *
   * @param pageable page request for the search
   * @return list of customers eligible for permanent deletion
   */
  public Page<CustomerSummaryRecord> getDeletableCustomers(Pageable pageable) {
    URI url = PageRequestBuilder.fromUriString(
      applicationProperties.getDeletableCustomersUrl(), pageable);

    logger.debug(
      "Requesting deletable customers from model-service, page={}, size={}",
      pageable.getPageNumber(),
      pageable.getPageSize()
    );

    return restTemplate.exchange(
      url,
      HttpMethod.GET,
      null,
      new ParameterizedTypeReference<RestResponsePage<CustomerSummaryRecord>>() {}
    ).getBody();
  }

  /**
   * Delete customers and their associated contacts by calling model-service endpoint.
   *
   * @param ids List of customer IDs to delete
   * @return Result of the deletion operation, including deleted and skipped IDs
   */
  public DeleteIdsResult deleteCustomers(List<Integer> ids) {
    ResponseEntity<DeleteIdsResult> response =
      restTemplate.exchange(
        applicationProperties.getDeleteCustomersUrl(),
        HttpMethod.DELETE,
        new HttpEntity<>(ids),
        DeleteIdsResult.class
      );

    return response.getBody();
  }
}
