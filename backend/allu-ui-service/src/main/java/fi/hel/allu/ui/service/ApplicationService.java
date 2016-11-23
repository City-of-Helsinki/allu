package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.LocationSearchCriteria;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.*;
import fi.hel.allu.ui.mapper.ApplicationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
  private MetaService metaService;

  @Autowired
  public ApplicationService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      LocationService locationService,
      ApplicantService applicantService,
      ApplicationMapper applicationMapper,
      ContactService contactService,
      MetaService metaService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.locationService = locationService;
    this.applicantService = applicantService;
    this.applicationMapper = applicationMapper;
    this.contactService = contactService;
    this.metaService = metaService;
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
    ResponseEntity<Application[]> applicationResult = restTemplate.postForEntity(applicationProperties
        .getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATIONS_FIND_BY_ID), applicationIds, Application[].class);
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
   * Create applications by calling backend service.
   *
   * @param applicationJson Application that are going to be created
   * @return Transfer object that contains list of created applications and their identifiers
   */
  ApplicationJson createApplication(ApplicationJson applicationJson) {
    applicationJson.setApplicant(applicantService.createApplicant(applicationJson.getApplicant()));
    applicationJson.setLocation(locationService.createLocation(applicationJson.getLocation()));
    applicationJson.setMetadata(metaService.findMetadataForApplication(applicationJson.getType()));
    List<ContactJson> contacts = applicationJson.getContactList();
    setContactApplicant(contacts, applicationJson.getApplicant());
    Application applicationModel = restTemplate.postForObject(applicationProperties
            .getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_CREATE),
        applicationMapper.createApplicationModel(applicationJson), Application.class);
    applicationMapper.mapApplicationToJson(applicationJson, applicationModel);
    applicationJson.setContactList(contactService.setContactsForApplication(applicationJson.getId(), contacts));
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
    LocationJson locationJson = applicationJson.getLocation();
    if (locationJson != null) {
      applicationJson.setLocation(locationService.updateOrCreateLocation(locationJson));
    } else {
      locationService.deleteApplicationLocation(applicationId);
    }
    List<ContactJson> contacts = contactService.setContactsForApplication(applicationId,
        applicationJson.getContactList());
    restTemplate.put(applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_UPDATE), applicationMapper
        .createApplicationModel(applicationJson), applicationId);
    applicationJson.setContactList(contacts);
    return applicationJson;
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
}

