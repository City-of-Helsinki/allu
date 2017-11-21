package fi.hel.allu.servicecore.service;

import fi.hel.allu.model.domain.ApplicationWithContacts;
import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.model.domain.ContactChange;
import fi.hel.allu.search.domain.ApplicationWithContactsES;
import fi.hel.allu.search.domain.ContactES;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ContactJson;
import fi.hel.allu.servicecore.domain.QueryParametersJson;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.mapper.CustomerMapper;
import fi.hel.allu.servicecore.mapper.QueryParameterMapper;

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
public class ContactService {

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private CustomerMapper customerMapper;
  private SearchService searchService;
  private UserService userService;

  @Autowired
  public ContactService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      CustomerMapper customerMapper,
      SearchService searchService,
      UserService userService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.customerMapper = customerMapper;
    this.searchService = searchService;
    this.userService = userService;
  }

  public ContactJson findById(int id) {
    ResponseEntity<Contact> locationResult = restTemplate.getForEntity(
        applicationProperties.getContactByIdUrl(),
        Contact.class,
        id);
    return customerMapper.createContactJson(locationResult.getBody());
  }

  public List<ContactJson> findByCustomer(int customerId) {
    ResponseEntity<Contact[]> contactResult = restTemplate.getForEntity(
        applicationProperties.getContactsByCustomerUrl(),
        Contact[].class,
        customerId);
    List<ContactJson> results =
        Arrays.stream(contactResult.getBody()).map(c -> customerMapper.createContactJson(c)).collect(Collectors.toList());
    return results;
  }

  /**
   * Find contacts by given fields.
   *
   * @param queryParameters list of query parameters
   * @return List of found application with details
   */
  public List<ContactJson> search(QueryParametersJson queryParameters) {
    List<ContactJson> resultList = Collections.emptyList();
    if (!queryParameters.getQueryParameters().isEmpty()) {
      List<Integer> ids = searchService.searchContact(QueryParameterMapper.mapToQueryParameters(queryParameters));
      resultList = getContactsById(ids);
    }
    return resultList;
  }


  public List<ContactJson> createContacts(List<ContactJson> contactJsons) {
    ContactChange contactChange = new ContactChange(userService.getCurrentUser().getId(),
        contactJsons.stream().map(cJson -> customerMapper.createContactModel(cJson)).collect(Collectors.toList()));
    Contact[] contacts = restTemplate.postForObject(
        applicationProperties.getContactCreateUrl(),
        contactChange,
        Contact[].class);
    List<ContactJson> createdContactJsons =
        Arrays.stream(contacts).map(c -> customerMapper.createContactJson(c)).collect(Collectors.toList());
    searchService.insertContacts(createdContactJsons);
    return createdContactJsons;
  }

  public List<ContactJson> updateContacts(List<ContactJson> contactJsons) {
    ContactChange contactChange = new ContactChange(userService.getCurrentUser().getId(),
        contactJsons.stream().map(cJson -> customerMapper.createContactModel(cJson)).collect(Collectors.toList()));
    HttpEntity<ContactChange> requestEntity = new HttpEntity<>(contactChange);
    ResponseEntity<Contact[]> response = restTemplate.exchange(
        applicationProperties.getContactUpdateUrl(),
        HttpMethod.PUT,
        requestEntity,
        Contact[].class);
    List<ContactJson> updatedContactJsons =
        Arrays.stream(response.getBody()).map(c -> customerMapper.createContactJson(c)).collect(Collectors.toList());
    searchService.updateContacts(updatedContactJsons);
    List<ApplicationWithContactsES> applicationWithContactsESs =
        findRelatedApplicationsWithContacts(updatedContactJsons.stream().map(cJson -> cJson.getId()).collect(Collectors.toList()));
    searchService.updateContactsOfApplications(applicationWithContactsESs);
    return updatedContactJsons;
  }

  public List<ContactJson> getContactsById(List<Integer> contactIds) {
    Contact[] contacts = restTemplate.postForObject(
        applicationProperties.getContactsByIdUrl(),
        contactIds,
        Contact[].class);
    List<ContactJson> resultList = Arrays.stream(contacts).map(c -> customerMapper.createContactJson(c)).collect(Collectors.toList());
    SearchService.orderByIdList(contactIds, resultList, (contact) -> contact.getId());
    return resultList;
  }

  /**
   * Find all contacts of applications having given contacts.
   *
   * @param   contactIds of the contacts whose related applications with contacts are fetched.
   * @return  all contacts of applications having given contact.
   */
  private List<ApplicationWithContactsES> findRelatedApplicationsWithContacts(List<Integer> contactIds) {
    ParameterizedTypeReference<List<ApplicationWithContacts>> typeRef = new ParameterizedTypeReference<List<ApplicationWithContacts>>() {};
    HttpEntity<List<Integer>> requestEntity = new HttpEntity<>(contactIds);
    ResponseEntity<List<ApplicationWithContacts>> applicationWithContacts = restTemplate.exchange(
        applicationProperties.getContactsRelatedByApplicationUrl(),
        HttpMethod.POST,
        requestEntity,
        typeRef);
    List<ApplicationWithContactsES> applicationWithContactsESs = applicationWithContacts.getBody().stream()
        .map(atc -> new ApplicationWithContactsES(atc.getApplicationId(), atc.getCustomerRoleType(), mapContacts(atc.getContacts())))
        .collect(Collectors.toList());
    return applicationWithContactsESs;
  }

  private List<ContactES> mapContacts(List<Contact> contacts) {
    return contacts.stream().map(c -> mapContact(c)).collect(Collectors.toList());
  }

  private ContactES mapContact(Contact contact) {
    return new ContactES(contact.getId(), contact.getName(), contact.isActive());
  }
}
