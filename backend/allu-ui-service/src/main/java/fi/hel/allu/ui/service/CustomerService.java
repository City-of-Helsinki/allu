package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.CustomerJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomerService {
  private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private PersonService personService;
  private OrganizationService organizationService;


  @Autowired
  public CustomerService(ApplicationProperties applicationProperties, RestTemplate restTemplate, PersonService personService,
                         OrganizationService organizationService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.personService = personService;
    this.organizationService = organizationService;
  }

  /**
   * Create a new customer. Customer id must null to create a new customer.
   *
   * @param customerJson Customer that is going to be created
   * @return Created customer
   */
  public CustomerJson createCustomer(CustomerJson customerJson) {
    if (customerJson != null && customerJson.getId() == null) {
      switch (customerJson.getType()) {
        case PERSON:
          customerJson.setPerson(personService.createPerson(customerJson.getPerson()));
          break;
        case COMPANY:
          customerJson.setOrganization(organizationService.createOrganization(customerJson.getOrganization()));
          break;
      }
      Customer customerModel = restTemplate.postForObject(applicationProperties
              .getModelServiceUrl(ApplicationProperties.PATH_MODEL_CUSTOMER_CREATE), createCustomerModel(customerJson),
          Customer.class);
      mapCustomerToJson(customerJson, customerModel);
    }
    return customerJson;
  }

  /**
   * Update the given customer. Customer is updated if the id is given.
   *
   * @param customerJson customer that is going to be updated
   */
  public void updateCustomer(CustomerJson customerJson) {
    if (customerJson != null && customerJson.getId() != null && customerJson.getId() > 0) {
      switch (customerJson.getType()) {
        case PERSON:
          personService.updatePerson(customerJson.getPerson());
          break;
        case COMPANY:
          organizationService.updateOrganization(customerJson.getOrganization());
          break;
      }
      restTemplate.put(applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_CUSTOMER_UPDATE), createCustomerModel
          (customerJson), customerJson.getId().intValue());
    }
  }

  /**
   * Find given customer details.
   *
   * @param customerId customer identifier that is used to find details
   * @return Customer details or empty customer object
   */
  public CustomerJson findCustomerById(int customerId) {
    CustomerJson customerJson = new CustomerJson();
    ResponseEntity<Customer> customerResult = restTemplate.getForEntity(applicationProperties
        .getModelServiceUrl(ApplicationProperties.PATH_MODEL_CUSTOMER_FIND_BY_ID), Customer.class, customerId);
    mapCustomerToJson(customerJson, customerResult.getBody());

    switch (customerResult.getBody().getType()) {
      case PERSON:
        customerJson.setPerson(personService.findPersonById(customerResult.getBody().getPersonId()));
        break;
      case COMPANY:
        customerJson.setOrganization(organizationService.findOrganizationById(customerResult.getBody().getOrganizationId()));
        break;
    }
    return customerJson;
  }

  private Customer createCustomerModel(CustomerJson customerJson) {
    Customer customerModel = new Customer();
    if (customerJson.getId() != null) {
      customerModel.setId(customerJson.getId());
    }
    customerModel.setSapId(customerJson.getSapId());
    customerModel.setType(customerJson.getType());
    if (customerJson.getOrganization() != null) {
      customerModel.setOrganizationId(customerJson.getOrganization().getId());
    }
    if (customerJson.getPerson() != null) {
      customerModel.setPersonId(customerJson.getPerson().getId());
    }
    return customerModel;
  }

  private void mapCustomerToJson(CustomerJson customerJson, Customer customer) {
    customerJson.setId(customer.getId());
    customerJson.setType(customer.getType());
    customerJson.setSapId(customer.getSapId());
  }
}
