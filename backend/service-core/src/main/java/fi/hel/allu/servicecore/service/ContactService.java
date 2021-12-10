package fi.hel.allu.servicecore.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.model.domain.ApplicationWithContacts;
import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.model.domain.ContactChange;
import fi.hel.allu.search.domain.ApplicationWithContactsES;
import fi.hel.allu.search.domain.ContactES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ContactJson;
import fi.hel.allu.servicecore.mapper.CustomerMapper;

@Service
public class ContactService {

  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;
  private final CustomerMapper customerMapper;
  private final SearchService searchService;
  private final UserService userService;
  private final PersonAuditLogService personAuditLogService;

  public ContactService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      CustomerMapper customerMapper,
      SearchService searchService,
      UserService userService,
      PersonAuditLogService personAuditLogService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.customerMapper = customerMapper;
    this.searchService = searchService;
    this.userService = userService;
    this.personAuditLogService = personAuditLogService;
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
        Arrays.stream(contactResult.getBody()).map(customerMapper::createContactJson).collect(Collectors.toList());
    results.forEach(c -> personAuditLogService.log(c, "ContactService"));
    return results;
  }

  /**
   * Find contacts by given fields.
   *
   * @param queryParameters list of query parameters
   * @return List of found application with details
   */
  public Page<ContactJson> search(QueryParameters queryParameters, Pageable pageRequest) {
    return searchService.searchContact(queryParameters, pageRequest,
        ids -> getContactsById(ids).stream().filter(ContactJson::isActive).collect(Collectors.toList()));
  }


  public List<ContactJson> createContacts(List<ContactJson> contactJsons) {
    // Created contacts active by default
    contactJsons.forEach(c -> c.setActive(true));
    ContactChange contactChange = new ContactChange(userService.getCurrentUser().getId(),
        contactJsons.stream().map(customerMapper::createContactModel).collect(Collectors.toList()));
    Contact[] contacts = restTemplate.postForObject(
        applicationProperties.getContactCreateUrl(),
        contactChange,
        Contact[].class);
    List<ContactJson> createdContactJsons =
        Arrays.stream(contacts).map(customerMapper::createContactJson).collect(Collectors.toList());
    searchService.insertContacts(createdContactJsons);
    return createdContactJsons;
  }

  public ContactJson createContact(ContactJson contact) {
    return createContacts(Collections.singletonList(contact)).get(0);
  }

  public List<ContactJson> updateContacts(List<ContactJson> contactJsons) {
    ContactChange contactChange = new ContactChange(userService.getCurrentUser().getId(),
        contactJsons.stream().map(customerMapper::createContactModel).collect(Collectors.toList()));
    HttpEntity<ContactChange> requestEntity = new HttpEntity<>(contactChange);
    ResponseEntity<Contact[]> response = restTemplate.exchange(
        applicationProperties.getContactUpdateUrl(),
        HttpMethod.PUT,
        requestEntity,
        Contact[].class);
    List<ContactJson> updatedContactJsons =
        Arrays.stream(response.getBody()).map(customerMapper::createContactJson).collect(Collectors.toList());
    searchService.updateContacts(updatedContactJsons);
    List<ApplicationWithContactsES> applicationWithContactsESs =
        findRelatedApplicationsWithContacts(updatedContactJsons.stream().map(ContactJson::getId).collect(Collectors.toList()));
    searchService.updateContactsOfApplications(applicationWithContactsESs);
    return updatedContactJsons;
  }

  public ContactJson updateContact(int id, ContactJson contact) {
    contact.setId(id);
    return updateContacts(Collections.singletonList(contact)).get(0);
  }


  public List<ContactJson> getContactsById(List<Integer> contactIds) {
    Contact[] contacts = restTemplate.postForObject(
        applicationProperties.getContactsByIdUrl(),
        contactIds,
        Contact[].class);
    List<ContactJson> resultList = Arrays.stream(contacts).map(customerMapper::createContactJson).collect(Collectors.toList());
    SearchService.orderByIdList(contactIds, resultList, ContactJson::getId);
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
    return applicationWithContacts.getBody().stream()
        .map(atc -> new ApplicationWithContactsES(atc.getApplicationId(), atc.getCustomerRoleType(), mapContacts(atc.getContacts())))
        .collect(Collectors.toList());
  }

  private List<ContactES> mapContacts(List<Contact> contacts) {
    return contacts.stream().map(this::mapContact).collect(Collectors.toList());
  }

  private ContactES mapContact(Contact contact) {
    return new ContactES(contact.getId(), contact.getName(), contact.isActive());
  }

}
