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


  public List<ContactJson> createContacts(List<ContactJson> contactJsons) {
    Contact[] contacts = restTemplate.postForObject(
        applicationProperties.getContactCreateUrl(),
        contactJsons.stream().map(cJson -> applicationMapper.createContactModel(cJson)).collect(Collectors.toList()),
        Contact[].class);
    List<ContactJson> createdContactJsons =
        Arrays.stream(contacts).map(c -> applicationMapper.createContactJson(c)).collect(Collectors.toList());
    searchService.insertContacts(createdContactJsons);
    return createdContactJsons;
  }

  public List<ContactJson> updateContacts(List<ContactJson> contactJsons) {
    HttpEntity<List<Contact>> requestEntity =
        new HttpEntity<>(contactJsons.stream().map(cJson -> applicationMapper.createContactModel(cJson)).collect(Collectors.toList()));
    ResponseEntity<Contact[]> response = restTemplate.exchange(
        applicationProperties.getContactUpdateUrl(),
        HttpMethod.PUT,
        requestEntity,
        Contact[].class);
    List<ContactJson> updatedContactJsons =
        Arrays.stream(response.getBody()).map(c -> applicationMapper.createContactJson(c)).collect(Collectors.toList());
    searchService.updateContacts(updatedContactJsons);
    Map<Integer, List<ContactES>> applicationIdToContacts =
        findRelatedApplicationsWithContacts(updatedContactJsons.stream().map(cJson -> cJson.getId()).collect(Collectors.toList()));
    searchService.updateContactsOfApplications(applicationIdToContacts);
    return updatedContactJsons;
  }

  public List<ContactJson> setContactsForApplication(int applicationId, int applicantId, List<ContactJson> contacts) {
    contacts.forEach(c -> c.setApplicantId(applicantId));
    // create new contacts (the ones with missing id)
    Map<Boolean, List<ContactJson>> newOldContacts = contacts.stream().collect(Collectors.partitioningBy(c -> c.getId() != null));
    List<ContactJson> allContacts = new ArrayList<>(newOldContacts.get(true));
    if (!newOldContacts.get(false).isEmpty()) {
      List<ContactJson> addedContacts = createContacts(newOldContacts.get(false));
      allContacts.addAll(addedContacts);
    }

    List<Contact> contactsModel = allContacts.stream().map(cJson -> applicationMapper.createContactModel(cJson)).collect(Collectors.toList());
    restTemplate.put(
        applicationProperties.getContactsUpdateApplicationUrl(),
        contactsModel,
        applicationId);

    return allContacts;
  }

  /**
   * Find all contacts of applications having given contacts.
   *
   * @param   contactIds of the contacts whose related applications with contacts are fetched.
   * @return  all contacts of applications having given contact. The id of application is the map key and value contains all contacts
   *          of the application.
   */
  private Map<Integer, List<ContactES>> findRelatedApplicationsWithContacts(List<Integer> contactIds) {
    ParameterizedTypeReference<Map<Integer, List<Contact>>> typeRef = new ParameterizedTypeReference<Map<Integer, List<Contact>>>() {};
    HttpEntity<List<Integer>> requestEntity = new HttpEntity<>(contactIds);
    ResponseEntity<Map<Integer, List<Contact>>> applicationIdToContacts = restTemplate.exchange(
        applicationProperties.getContactsRelatedByApplicationUrl(),
        HttpMethod.POST,
        requestEntity,
        typeRef);

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
