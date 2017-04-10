package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.search.domain.ContactES;
import fi.hel.allu.ui.config.ApplicationProperties;
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
public class ContactService {

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private ApplicationMapper applicationMapper;
  private SearchService searchService;

  @Autowired
  public ContactService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      ApplicationMapper applicationMapper,
      SearchService searchService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.applicationMapper = applicationMapper;
    this.searchService = searchService;
  }

  public ContactJson findById(int id) {
    ResponseEntity<Contact> locationResult = restTemplate.getForEntity(
        applicationProperties.getContactByIdUrl(),
        Contact.class,
        id);
    return applicationMapper.createContactJson(locationResult.getBody());
  }

  public List<ContactJson> findByApplicant(int applicantId) {
    ResponseEntity<Contact[]> contactResult = restTemplate.getForEntity(
        applicationProperties.getContactsByApplicantUrl(),
        Contact[].class,
        applicantId);
    List<ContactJson> results =
        Arrays.stream(contactResult.getBody()).map(c -> applicationMapper.createContactJson(c)).collect(Collectors.toList());
    return results;
  }

  /**
   * Find contacts for an application
   *
   * @param applicationId
   *          The application's ID
   * @return List of contact items
   */
  public List<ContactJson> findContactsForApplication(int applicationId) {
    ResponseEntity<Contact[]> contactResult = restTemplate.getForEntity(
        applicationProperties.getContactsByApplicationUrl(), Contact[].class,
        applicationId);
    List<ContactJson> results = Arrays.stream(contactResult.getBody()).map(c -> applicationMapper.createContactJson(c))
        .collect(Collectors.toList());
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


  public ContactJson createContact(ContactJson contactJson) {
    Contact contact = restTemplate.postForObject(
        applicationProperties.getContactCreateUrl(),
        applicationMapper.createContactModel(contactJson),
        Contact.class);
    ContactJson createdContactJson = applicationMapper.createContactJson(contact);
    searchService.insertContact(createdContactJson);
    return createdContactJson;
  }

  public ContactJson updateContact(int id, ContactJson contactJson) {
    HttpEntity<Contact> requestEntity = new HttpEntity<>(applicationMapper.createContactModel(contactJson));
    ResponseEntity<Contact> response = restTemplate.exchange(
        applicationProperties.getContactUpdateUrl(),
        HttpMethod.PUT,
        requestEntity,
        Contact.class,
        id);
    ContactJson updatedContactJson = applicationMapper.createContactJson(response.getBody());
    searchService.updateContacts(Collections.singletonList(updatedContactJson));
    Map<Integer, List<ContactES>> applicationIdToContacts = findRelatedApplicationsWithContacts(id);
    searchService.updateContactsOfApplications(applicationIdToContacts);
    return updatedContactJson;
  }

  public List<ContactJson> setContactsForApplication(int applicationId, List<ContactJson> contacts) {

    // create new contacts (the ones with missing id)
    Map<Boolean, List<ContactJson>> newOldContacts = contacts.stream().collect(Collectors.partitioningBy(c -> c.getId() != null));
    List<ContactJson> addedContacts = newOldContacts.get(false).stream().map(c -> createContact(c)).collect(Collectors.toList());
    List<ContactJson> allContacts = new ArrayList<>(newOldContacts.get(true));
    allContacts.addAll(addedContacts);

    List<Contact> contactsModel = allContacts.stream().map(cJson -> applicationMapper.createContactModel(cJson)).collect(Collectors.toList());
    restTemplate.put(
        applicationProperties.getContactsUpdateApplicationUrl(),
        contactsModel,
        applicationId);

    return allContacts;
  }

  /**
   * Find all contacts of applications having given contact.
   *
   * @param   contactId of the contact whose related applications with contacts are fetched.
   * @return  all contacts of applications having given contact. The id of application is the map key and value contains all contacts
   *          of the application.
   */
  private Map<Integer, List<ContactES>> findRelatedApplicationsWithContacts(int contactId) {
    ParameterizedTypeReference<Map<Integer, List<Contact>>> typeRef = new ParameterizedTypeReference<Map<Integer, List<Contact>>>() {};
    ResponseEntity<Map<Integer, List<Contact>>> applicationIdToContacts = restTemplate.exchange(
        applicationProperties.getContactsRelatedByApplicationUrl(),
        HttpMethod.GET,
        null,
        typeRef,
        contactId);

    Map<Integer, List<ContactES>> applicationIdToContactJsons = applicationIdToContacts.getBody().entrySet().stream().collect(
        Collectors.toMap(
            entry -> entry.getKey(),
            entry -> entry.getValue().stream().map(contact -> new ContactES(contact.getId(), contact.getName())).collect(Collectors.toList())));
    return applicationIdToContactJsons;
  }

  private List<ContactJson> getContactsById(List<Integer> contactIds) {
    Contact[] contacts = restTemplate.postForObject(
        applicationProperties.getContactsByIdUrl(),
        contactIds,
        Contact[].class);
    List<ContactJson> resultList = Arrays.stream(contacts).map(c -> applicationMapper.createContactJson(c)).collect(Collectors.toList());
    SearchService.orderByIdList(contactIds, resultList, (contact) -> contact.getId());
    return resultList;
  }

}
