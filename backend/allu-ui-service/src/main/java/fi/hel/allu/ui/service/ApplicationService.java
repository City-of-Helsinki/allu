package fi.hel.allu.ui.service;

import fi.hel.allu.common.types.CableInfoType;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.*;
import fi.hel.allu.ui.mapper.ApplicationMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {
  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private LocationService locationService;
  private ApplicantService applicantService;
  private ApplicationMapper applicationMapper;
  private ContactService contactService;
  private MetaService metaService;
  private UserService userService;

  @Autowired
  public ApplicationService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      LocationService locationService,
      ApplicantService applicantService,
      ApplicationMapper applicationMapper,
      ContactService contactService,
      MetaService metaService,
      UserService userService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.locationService = locationService;
    this.applicantService = applicantService;
    this.applicationMapper = applicationMapper;
    this.contactService = contactService;
    this.metaService = metaService;
    this.userService = userService;
  }


  void setApplicationProperties(ApplicationProperties applicationProperties) {
    this.applicationProperties = applicationProperties;
  }

  /**
   * Find given application details.
   *
   * @param applicationId application identifier that is used to find details
   * @return Application details or empty application list in DTO
   */
  public Application findApplicationById(int applicationId) {
    Application applicationModel = restTemplate.getForObject(applicationProperties
        .getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_BY_ID), Application.class, applicationId);
    return applicationModel;
  }

  /**
   * Find given application details.
   *
   * @param   applicationIds    Application identifier that is used to find details
   *
   * @return  List of applications or empty application list
   */
  public List<Application> findApplicationsById(List<Integer> applicationIds) {
    ResponseEntity<Application[]> applicationResult =
        restTemplate.postForEntity(applicationProperties.getApplicationsByIdUrl(), applicationIds, Application[].class);
    return Arrays.asList(applicationResult.getBody());
  }

  /**
   * Find applications using given location query.
   *
   * @param query   the location query
   * @return list of found applications with details
   */
  public List<Application> findApplicationByLocation(LocationQueryJson query) {
    LocationSearchCriteria lsc = new LocationSearchCriteria();
    mapLocationQueryToSearchCriteria(query, lsc);
    ResponseEntity<Application[]> applicationResult = restTemplate.postForEntity(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_BY_LOCATION),
        lsc,
        Application[].class);
    return Arrays.asList(applicationResult.getBody());
  }

  /**
   * Get the standard texts for cable infos
   *
   * @return List of texts and their cable info types
   */
  public List<CableInfoText> getCableInfoTexts() {
    ResponseEntity<CableInfoText[]> restResult = restTemplate
        .getForEntity(applicationProperties.getCableInfoTextListUrl(), CableInfoText[].class);
    return Arrays.asList(restResult.getBody());
  }

  /**
   * Delete a standard cable info text
   *
   * @param id the ID of the key to delete
   */
  public void deleteCableInfoText(int id) {
    restTemplate.delete(applicationProperties.getCableInfoTextDeleteUrl(), id);
  }

  /**
   * Create a new cable info text
   *
   * @param type the cable info type for the text
   * @param text the text
   * @return the resulting CableInfoText entry
   */
  public CableInfoText createCableInfoText(CableInfoType type, String text) {
    CableInfoText cit = new CableInfoText();
    cit.setCableInfoType(type);
    cit.setTextValue(text);
    ResponseEntity<CableInfoText> restResult = restTemplate.postForEntity(
        applicationProperties.getCableInfoTextAddUrl(), cit, CableInfoText.class);
    return restResult.getBody();
  }

  /**
   * Update a cable info text
   *
   * @param id id of the text entry to update
   * @param text new text for the entry
   * @return the resulting CableInfoText entry after update
   */
  public CableInfoText updateCableInfoText(int id, String text) {
    CableInfoText cit = new CableInfoText();
    cit.setTextValue(text);
    ResponseEntity<CableInfoText> restResult =
        restTemplate.exchange(
            applicationProperties.getCableInfoTextUpdateUrl(),
            HttpMethod.PUT,
            new HttpEntity<>(cit),
            CableInfoText.class,
            id);
    return restResult.getBody();
  }

  /**
   * Get the invoice rows for an application
   *
   * @param id the application ID
   * @return the invoice rows for the application
   */
  public List<InvoiceRow> getInvoiceRows(int id) {
    ResponseEntity<InvoiceRow[]> restResult = restTemplate.getForEntity(applicationProperties.getInvoiceRowsUrl(),
        InvoiceRow[].class, id);
    return Arrays.asList(restResult.getBody());
  }

  /**
   * Create applications by calling backend service.
   *
   * @param newApplication
   *          Application to be added to backend.
   * @return Application with possibly updated information from backend.
   */
  ApplicationJson createApplication(ApplicationJson newApplication) {
    newApplication.setApplicant(applicantService.createApplicant(newApplication.getApplicant()));
    newApplication.setMetadata(metaService.findMetadataForApplication(newApplication.getType()));
    List<ContactJson> contacts = newApplication.getContactList();
    setContactApplicant(contacts, newApplication.getApplicant());
    if (newApplication.getApplicationTags() != null) {
      UserJson currentUser = userService.getCurrentUser();
      newApplication.getApplicationTags().forEach(t -> updateTag(currentUser, t));
    }
    Application applicationModel = restTemplate.postForObject(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_CREATE),
        applicationMapper.createApplicationModel(newApplication),
        Application.class);

    List<LocationJson> newLocations = newApplication.getLocations();
    List<LocationJson> locations = Collections.emptyList();
    if (newLocations != null) {
      locations = newApplication.getLocations().stream()
          .map(l -> locationService.createLocation(applicationModel.getId(), l)).collect(Collectors.toList());
    }

    ApplicationJson applicationJson = applicationMapper.mapApplicationToJson(applicationModel);
    applicationJson.setContactList(contactService.setContactsForApplication(applicationJson.getId(), contacts));
    applicationJson.setApplicant(newApplication.getApplicant());
    applicationJson.setLocations(locations);
    applicationJson.setMetadata(newApplication.getMetadata());
    return applicationJson;
  }

  /**
   * Update the given application by calling back-end service.
   *
   * @param applicationJson application that is going to be updated
   * @return Updated application
   */
  ApplicationJson updateApplication(int applicationId, ApplicationJson applicationJson) {
    applicantService.updateApplicant(applicationJson.getApplicant());
    if (applicationJson.getLocations() != null) {
      locationService.deleteApplicationLocation(applicationId);
      List<LocationJson> locationJsons =
          applicationJson.getLocations().stream().map(l -> locationService.updateOrCreateLocation(applicationId, l)).collect(Collectors.toList());
      applicationJson.setLocations(locationJsons);
    } else {
      locationService.deleteApplicationLocation(applicationId);
    }
    List<ContactJson> contacts = contactService.setContactsForApplication(applicationId,
        applicationJson.getContactList());
    if (applicationJson.getApplicationTags() != null) {
      UserJson currentUser = userService.getCurrentUser();
      applicationJson.getApplicationTags().forEach(t -> updateTag(currentUser, t));
    }
    HttpEntity<Application> requestEntity = new HttpEntity<>(applicationMapper.createApplicationModel(applicationJson));
    ResponseEntity<Application> responseEntity = restTemplate.exchange(applicationProperties.getApplicationUpdateUrl(),
        HttpMethod.PUT, requestEntity, Application.class, applicationId);
    ApplicationJson resultJson = applicationMapper.mapApplicationToJson(responseEntity.getBody());

    resultJson.setContactList(contacts);
    resultJson.setApplicant(applicationJson.getApplicant());
    resultJson.setMetadata(metaService.findMetadataForApplication(resultJson.getType()));
    return resultJson;
  }

  void updateApplicationHandler(int updatedHandler, List<Integer> applicationIds) {
    restTemplate.put(applicationProperties.getApplicationHandlerUpdateUrl(), applicationIds, updatedHandler);
  }

  void removeApplicationHandler(List<Integer> applicationIds) {
    restTemplate.put(applicationProperties.getApplicationHandlerRemoveUrl(), applicationIds);
  }


  private void mapLocationQueryToSearchCriteria(LocationQueryJson query, LocationSearchCriteria lsc) {
    lsc.setIntersects(query.getIntersectingGeometry());
    lsc.setAfter(query.getAfter());
    lsc.setBefore(query.getBefore());
  }

  private void setContactApplicant(List<ContactJson> contacts, ApplicantJson applicant) {
    if (contacts == null) {
      return;
    }
    Integer applicantId = applicant.getId();
    for (ContactJson cj : contacts) {
      if (cj.getApplicantId() == null) {
        cj.setApplicantId(applicantId);
      }
    }
  }

  private void updateTag(UserJson currentUser, ApplicationTagJson tag) {
    if (tag.getAddedBy() == null) {
      tag.setAddedBy(currentUser.getId());
      tag.setCreationTime(ZonedDateTime.now());
    }
  }
}

