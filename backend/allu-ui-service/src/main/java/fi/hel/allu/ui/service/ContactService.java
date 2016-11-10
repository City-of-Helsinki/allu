package fi.hel.allu.ui.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ContactJson;

@Service
public class ContactService {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

  private ApplicationProperties applicationProperties;

  private RestTemplate restTemplate;

  @Autowired
  public ContactService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
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
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_CONTACT_FIND_BY_APPLICATION), Contact[].class,
        applicationId);
    List<ContactJson> results = Arrays.stream(locationResult.getBody()).map(c -> mapContactToJson(c))
        .collect(Collectors.toList());
    return results;
  }

  public List<ContactJson> setContactsForApplication(int applicationId, List<ContactJson> contacts) {
    List<Contact> modelData = new ArrayList<>();
    Optional.ofNullable(contacts).ifPresent(l -> l.forEach(c -> modelData.add(mapContactFromJson(c))));
    ResponseEntity<Contact[]> locationResult = restTemplate.exchange(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_CONTACT_FIND_BY_APPLICATION),
        HttpMethod.PUT, new HttpEntity<>(modelData), Contact[].class, applicationId);

    List<ContactJson> results = Arrays.stream(locationResult.getBody()).map(c -> mapContactToJson(c))
        .collect(Collectors.toList());
    return results;
  }

  private Contact mapContactFromJson(ContactJson json) {
    Contact contact = new Contact();
    contact.setId(json.getId());
    contact.setApplicantId(json.getApplicantId());
    contact.setName(json.getName());
    contact.setStreetAddress(json.getStreetAddress());
    contact.setPostalCode(json.getPostalCode());
    contact.setCity(json.getCity());
    contact.setEmail(json.getEmail());
    contact.setPhone(json.getPhone());
    return contact;
  }

  private ContactJson mapContactToJson(Contact c) {
    ContactJson json = new ContactJson();
    json.setId(c.getId());
    json.setApplicantId(c.getApplicantId());
    json.setName(c.getName());
    json.setStreetAddress(c.getStreetAddress());
    json.setPostalCode(c.getPostalCode());
    json.setCity(c.getCity());
    json.setEmail(c.getEmail());
    json.setPhone(c.getPhone());
    return json;
  }

}
