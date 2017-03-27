package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ContactJson;
import fi.hel.allu.ui.mapper.ApplicationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContactService {

  private ApplicationProperties applicationProperties;

  private RestTemplate restTemplate;

  private ApplicationMapper applicationMapper;

  @Autowired
  public ContactService(ApplicationProperties applicationProperties, RestTemplate restTemplate,
      ApplicationMapper applicationMapper) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.applicationMapper = applicationMapper;
  }

  public ContactJson findById(int id) {
    ResponseEntity<Contact> locationResult = restTemplate.getForEntity(
        applicationProperties.getContactByIdUrl(),
        Contact.class,
        id);
    return applicationMapper.createContactJson(locationResult.getBody());
  }

  public List<ContactJson> findByApplicant(int applicantId) {
    ResponseEntity<Contact[]> locationResult = restTemplate.getForEntity(
        applicationProperties.getContactsByApplicantUrl(),
        Contact[].class,
        applicantId);
    List<ContactJson> results =
        Arrays.stream(locationResult.getBody()).map(c -> applicationMapper.createContactJson(c)).collect(Collectors.toList());
    return results;
  }

  public ContactJson createContact(ContactJson contactJson) {
    // TODO: update ElasticSearch
    Contact contact = restTemplate.postForObject(
        applicationProperties.getContactCreateUrl(),
        applicationMapper.createContactModel(contactJson),
        Contact.class);
    return applicationMapper.createContactJson(contact);
  }

  public ContactJson updateContact(int id, ContactJson contactJson) {
    // TODO: update ElasticSearch
    HttpEntity<Contact> requestEntity = new HttpEntity<>(applicationMapper.createContactModel(contactJson));
    ResponseEntity<Contact> response = restTemplate.exchange(
        applicationProperties.getContactUpdateUrl(),
        HttpMethod.PUT,
        requestEntity,
        Contact.class,
        id);
    return applicationMapper.createContactJson(response.getBody());
  }


  /**
   * Find contacts for an application
   *
   * @param applicationId
   *          The application's ID
   * @return List of contact items
   */
  public List<ContactJson> findContactsForApplication(int applicationId) {
    ResponseEntity<Contact[]> locationResult = restTemplate.getForEntity(
        applicationProperties.getContactsByApplicationUrl(), Contact[].class,
        applicationId);
    List<ContactJson> results = Arrays.stream(locationResult.getBody()).map(c -> applicationMapper.createContactJson(c))
        .collect(Collectors.toList());
    return results;
  }

  public List<ContactJson> setContactsForApplication(int applicationId, List<ContactJson> contacts) {
    List<Contact> modelData = new ArrayList<>();
    Optional.ofNullable(contacts)
        .ifPresent(l -> l.forEach(c -> modelData.add(applicationMapper.createContactModel(c))));
    ResponseEntity<Contact[]> locationResult = restTemplate.exchange(
        applicationProperties.getContactsUpdateApplicationUrl(),
        HttpMethod.PUT,
        new HttpEntity<>(modelData),
        Contact[].class, applicationId);

    List<ContactJson> results = Arrays.stream(locationResult.getBody()).map(c -> applicationMapper.createContactJson(c))
        .collect(Collectors.toList());
    return results;
  }


}
