package fi.hel.allu.ui.service;

import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.model.domain.LocationSearchCriteria;
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
import java.util.List;

@Service
public class ApplicationService {
  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private LocationService locationService;
  private ApplicantService applicantService;
  private ApplicationMapper applicationMapper;
  private ContactService contactService;
  private UserService userService;

  @Autowired
  public ApplicationService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      LocationService locationService,
      ApplicantService applicantService,
      ApplicationMapper applicationMapper,
      ContactService contactService,
      UserService userService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.locationService = locationService;
    this.applicantService = applicantService;
    this.applicationMapper = applicationMapper;
    this.contactService = contactService;
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
   * @param newApplication  Application to be added to backend.
   * @return Application with possibly updated information from backend.
   */
  Application createApplication(ApplicationJson newApplication) {
    if (newApplication.getApplicant().getId() == null) {
      // if given applicant is new, create it and set it to the application
      newApplication.setApplicant(applicantService.createApplicant(newApplication.getApplicant()));
    }
    List<ContactJson> contacts = newApplication.getContactList();
    if (newApplication.getApplicationTags() != null) {
      UserJson currentUser = userService.getCurrentUser();
      newApplication.getApplicationTags().forEach(t -> updateTag(currentUser, t));
    }
    Application applicationModel = restTemplate.postForObject(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_CREATE),
        applicationMapper.createApplicationModel(newApplication),
        Application.class);

    if (newApplication.getLocations() != null) {
      locationService.createLocations(applicationModel.getId(), newApplication.getLocations());
    }

    contactService.setContactsForApplication(applicationModel.getId(), newApplication.getApplicant().getId(), contacts);

    // need to fetch fresh Application from model, because at least setting location may change both handler and application start and end times
    return findApplicationById(applicationModel.getId());
  }

  /**
   * Update the given application by calling back-end service.
   *
   * @param applicationJson application that is going to be updated
   * @return Updated application
   */
  Application updateApplication(int applicationId, ApplicationJson applicationJson) {
    if (applicationJson.getApplicant().getId() == null) {
      // if given applicant is new, create it and set it to the application
      applicationJson.setApplicant(applicantService.createApplicant(applicationJson.getApplicant()));
    }

    if (applicationJson.getLocations() != null) {
      List<LocationJson> locationJsons = locationService.updateApplicationLocations(applicationId,
          applicationJson.getLocations());
      applicationJson.setLocations(locationJsons);
    } else {
      locationService.deleteApplicationLocation(applicationId);
    }
    contactService.setContactsForApplication(applicationId, applicationJson.getApplicant().getId(),
        applicationJson.getContactList());
    if (applicationJson.getApplicationTags() != null) {
      UserJson currentUser = userService.getCurrentUser();
      applicationJson.getApplicationTags().forEach(t -> updateTag(currentUser, t));
    }
    HttpEntity<Application> requestEntity = new HttpEntity<>(applicationMapper.createApplicationModel(applicationJson));
    ResponseEntity<Application> responseEntity = restTemplate.exchange(applicationProperties.getApplicationUpdateUrl(),
        HttpMethod.PUT, requestEntity, Application.class, applicationId);

    return responseEntity.getBody();
  }

  void updateApplicationHandler(int updatedHandler, List<Integer> applicationIds) {
    restTemplate.put(applicationProperties.getApplicationHandlerUpdateUrl(), applicationIds, updatedHandler);
  }

  void removeApplicationHandler(List<Integer> applicationIds) {
    restTemplate.put(applicationProperties.getApplicationHandlerRemoveUrl(), applicationIds);
  }

  Application changeApplicationStatus(int applicationId, StatusType statusType) {
    HttpEntity<Integer> requestEntity;
    if (StatusType.DECISION.equals(statusType) || StatusType.REJECTED.equals(statusType)) {
      UserJson currentUser = userService.getCurrentUser();
      requestEntity = new HttpEntity<>(currentUser.getId());
    } else {
      requestEntity = new HttpEntity<>((Integer) null);
    }

    ResponseEntity<Application> responseEntity = restTemplate.exchange(
        applicationProperties.getApplicationStatusUpdateUrl(statusType),
        HttpMethod.PUT,
        requestEntity,
        Application.class,
        applicationId);
    return responseEntity.getBody();
  }

  private void mapLocationQueryToSearchCriteria(LocationQueryJson query, LocationSearchCriteria lsc) {
    lsc.setIntersects(query.getIntersectingGeometry());
    lsc.setAfter(query.getAfter());
    lsc.setBefore(query.getBefore());
  }

  private void updateTag(UserJson currentUser, ApplicationTagJson tag) {
    if (tag.getAddedBy() == null) {
      tag.setAddedBy(currentUser.getId());
      tag.setCreationTime(ZonedDateTime.now());
    }
  }
}

