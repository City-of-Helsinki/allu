package fi.hel.allu.ui.service;

import fi.hel.allu.common.types.CustomerRoleType;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.CustomerJson;
import fi.hel.allu.ui.domain.CustomerWithContactsJson;
import fi.hel.allu.ui.domain.ContactJson;
import fi.hel.allu.ui.domain.QueryParametersJson;
import fi.hel.allu.ui.mapper.ApplicationMapper;
import fi.hel.allu.ui.mapper.QueryParameterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerService {

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private ApplicationMapper applicationMapper;
  private SearchService searchService;
  private ContactService contactService;

  @Autowired
  public CustomerService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      ApplicationMapper applicationMapper,
      SearchService searchService,
      ContactService contactService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.applicationMapper = applicationMapper;
    this.searchService = searchService;
    this.contactService = contactService;
  }


  /**
   * Create a new customer. Customer id must null to create a new customer.
   *
   * @param customerJson Customer that is going to be created
   * @return Created customer.
   */
  public CustomerJson createCustomer(CustomerJson customerJson) {
    Customer customerModel = restTemplate.postForObject(
        applicationProperties.getCustomerCreateUrl(),
        applicationMapper.createCustomerModel(customerJson),
        Customer.class);
    CustomerJson createdCustomer = applicationMapper.createCustomerJson(customerModel);
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

    return createdCustomerWithContacts;
  }

  /**
   * Update the given customer. Customer is updated if the id is given.
   *
   * @param customerJson customer that is going to be updated
   */
  public CustomerJson updateCustomer(int customerId, CustomerJson customerJson) {
    HttpEntity<Customer> requestEntity = new HttpEntity<>(applicationMapper.createCustomerModel(customerJson));
    ResponseEntity<Customer> response = restTemplate.exchange(
        applicationProperties.getCustomerUpdateUrl(),
        HttpMethod.PUT,
        requestEntity,
        Customer.class,
        customerId);
    CustomerJson updatedCustomer = applicationMapper.createCustomerJson(response.getBody());
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

  public CustomerWithContactsJson updateCustomerWithContacts(int customerId, CustomerWithContactsJson customerWithContactsJson) {
    CustomerWithContactsJson updatedCustomerWithContactsJson = new CustomerWithContactsJson();
    if (customerWithContactsJson.getCustomer() != null) {
      CustomerJson updatedCustomer = customerWithContactsJson.getCustomer();
      updatedCustomer.setId(customerId);
      updatedCustomerWithContactsJson.setCustomer(updateCustomer(updatedCustomer.getId(), updatedCustomer));
    }
    if (customerWithContactsJson.getContacts() != null) {
      customerWithContactsJson.getContacts().forEach(c -> c.setCustomerId(customerId));
      Map<Boolean, List<ContactJson>> newOldContacts =
          customerWithContactsJson.getContacts().stream().collect(Collectors.partitioningBy(c -> c.getId() != null));
      ArrayList<ContactJson> allContacts = new ArrayList<>();
      if (!newOldContacts.get(false).isEmpty()) {
        List<ContactJson> newContacts = contactService.createContacts(newOldContacts.get(false));
        searchService.insertContacts(newContacts);
        allContacts.addAll(newContacts);
      }
      if (!newOldContacts.get(true).isEmpty()) {
        List<ContactJson> oldContacts = contactService.updateContacts(newOldContacts.get(true));
        searchService.updateContacts(oldContacts);
        allContacts.addAll(oldContacts);
      }
      updatedCustomerWithContactsJson.setContacts(allContacts);
    }

    return updatedCustomerWithContactsJson;
  }

  public CustomerJson findCustomerById(int customerId) {
    ResponseEntity<Customer> customerResult =
        restTemplate.getForEntity(applicationProperties.getCustomerByIdUrl(), Customer.class, customerId);
    return applicationMapper.createCustomerJson(customerResult.getBody());
  }

  public List<CustomerJson> findAllCustomers() {
    ResponseEntity<Customer[]> customerResult =
        restTemplate.getForEntity(
            applicationProperties.getCustomersUrl(),
            Customer[].class);
    return Arrays.stream(customerResult.getBody())
        .map(customer -> applicationMapper.createCustomerJson(customer))
        .collect(Collectors.toList());
  }

  /**
   * Find customers by given fields.
   *
   * @param queryParameters list of query parameters
   * @return List of found application with details
   */
  public List<CustomerJson> search(QueryParametersJson queryParameters) {
    List<CustomerJson> resultList = Collections.emptyList();
    if (!queryParameters.getQueryParameters().isEmpty()) {
      List<Integer> ids = searchService.searchCustomer(QueryParameterMapper.mapToQueryParameters(queryParameters));
      resultList = getCustomersById(ids);
    }
    return resultList;
  }

  private List<CustomerJson> getCustomersById(List<Integer> customerIds) {
    Customer[] customers = restTemplate.postForObject(
        applicationProperties.getCustomersByIdUrl(),
        customerIds,
        Customer[].class);
    List<CustomerJson> resultList = Arrays.asList(customers).stream().map(a -> applicationMapper.createCustomerJson(a)).collect(Collectors.toList());
    SearchService.orderByIdList(customerIds, resultList, (customer) -> customer.getId());
    return resultList;
  }
}
