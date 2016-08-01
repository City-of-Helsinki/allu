package fi.hel.allu.ui.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.model.domain.Person;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.PersonJson;
import fi.hel.allu.ui.domain.PostalAddressJson;

@Service
public class PersonService {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

  private ApplicationProperties applicationProperties;

  private RestTemplate restTemplate;

  @Autowired
  public PersonService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  /**
   * Create a new person.
   *
   * @param personJson Person that is going to be created
   * @return Created person
   */
  public PersonJson createPerson(PersonJson personJson) {
    if (personJson != null && (personJson.getId() == null || personJson.getId() == 0)) {
      Person personModel = restTemplate.postForObject(applicationProperties
              .getModelServiceUrl(ApplicationProperties.PATH_MODEL_PERSON_CREATE), createPersonModel(personJson),
          Person.class);
      mapPersonToJson(personJson, personModel);
    }
    return personJson;
  }

  /**
   * Update given person. Person id is needed to update the given person.
   *
   * @param personJson Person that is going to be updated
   */
  public void updatePerson(PersonJson personJson) {
    if (personJson != null && personJson.getId() != null && personJson.getId() > 0) {
      restTemplate.put(applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_PERSON_UPDATE), createPersonModel(personJson),
          personJson.getId().intValue());
    }
  }

  /**
   * Find given person details.
   *
   * @param personId Person identifier that is used to find details
   * @return Person details or empty person object
   */
  public PersonJson findPersonById(int personId) {
    PersonJson personJson = new PersonJson();
    ResponseEntity<Person> personResult = restTemplate.getForEntity(applicationProperties
        .getModelServiceUrl(ApplicationProperties.PATH_MODEL_PERSON_FIND_BY_ID), Person.class, personId);
    mapPersonToJson(personJson, personResult.getBody());
    return personJson;
  }


  private Person createPersonModel(PersonJson personJson) {
    Person personModel = new Person();
    if (personJson.getId() != null) {
      personModel.setId(personJson.getId());
    }
    if (personJson.getPostalAddress() != null) {
      personModel.setStreetAddress(personJson.getPostalAddress().getStreetAddress());
      personModel.setCity(personJson.getPostalAddress().getCity());
      personModel.setPostalCode(personJson.getPostalAddress().getPostalCode());
    }
    personModel.setPhone(personJson.getPhone());
    personModel.setName(personJson.getName());
    personModel.setSsn(personJson.getSsn());
    personModel.setEmail(personJson.getEmail());
    return personModel;
  }


  private void mapPersonToJson(PersonJson personJson, Person personDomain) {
    personJson.setId(personDomain.getId());
    PostalAddressJson postalAddressJson = new PostalAddressJson();
    postalAddressJson.setPostalCode(personDomain.getPostalCode());
    postalAddressJson.setStreetAddress(personDomain.getStreetAddress());
    postalAddressJson.setCity(personDomain.getCity());
    personJson.setPostalAddress(postalAddressJson);
    personJson.setSsn(personDomain.getSsn());
    personJson.setPhone(personDomain.getPhone());
    personJson.setName(personDomain.getName());
    personJson.setEmail(personDomain.getEmail());
  }

}
