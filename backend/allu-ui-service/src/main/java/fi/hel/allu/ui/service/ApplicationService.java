package fi.hel.allu.ui.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.LocationSearchCriteria;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicantJson;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.ContactJson;
import fi.hel.allu.ui.domain.LocationQueryJson;
import fi.hel.allu.ui.mapper.ApplicationMapper;

@Service
public class ApplicationService {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private LocationService locationService;
  private CustomerService customerService;
  private ApplicantService applicantService;
  private ProjectService projectService;
  private ApplicationMapper applicationMapper;
  private ContactService contactService;

  @Autowired
  ApplicationService(ApplicationProperties applicationProperties, RestTemplate restTemplate, LocationService
      locationService, CustomerService customerService, ApplicantService applicantService, ProjectService projectService,
      ApplicationMapper applicationMapper, ContactService contactService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.locationService = locationService;
    this.customerService = customerService;
    this.applicantService = applicantService;
    this.projectService = projectService;
    this.applicationMapper = applicationMapper;
    this.contactService = contactService;
  }


  /**
   * Create applications by calling backend service.
   *
   * @param applicationJson Application that are going to be created
   * @return Transfer object that contains list of created applications and their identifiers
   */
  public ApplicationJson createApplication(ApplicationJson applicationJson) {
    applicationJson.setCustomer(customerService.createCustomer(applicationJson.getCustomer()));
    applicationJson.setProject(projectService.createProject(applicationJson.getProject()));
    applicationJson.setApplicant(applicantService.createApplicant(applicationJson.getApplicant()));
    applicationJson.setLocation(locationService.createLocation(applicationJson.getLocation()));
    List<ContactJson> contacts = applicationJson.getContactList();
    setContactOrganization(contacts, applicationJson.getApplicant());
    Application applicationModel = restTemplate.postForObject(applicationProperties
            .getUrl(ApplicationProperties.PATH_MODEL_APPLICATION_CREATE),
        applicationMapper.createApplicationModel(applicationJson), Application.class);
    applicationMapper.mapApplicationToJson(applicationJson, applicationModel);
    applicationJson.setContactList(contactService.setContactsForApplication(applicationJson.getId(), contacts));
    return applicationJson;
  }

  /**
   * Update the given application by calling backend service.
   *
   * @param applicationJson application that is going to be updated
   * @return Updated application
   */
  public ApplicationJson updateApplication(int applicationId, ApplicationJson applicationJson) {
    customerService.updateCustomer(applicationJson.getCustomer());
    applicantService.updateApplicant(applicationJson.getApplicant());
    projectService.updateProject(applicationJson.getProject());
    locationService.updateLocation(applicationJson.getLocation());
    List<ContactJson> contacts = contactService.setContactsForApplication(applicationId,
        applicationJson.getContactList());
    restTemplate.put(applicationProperties.getUrl(ApplicationProperties.PATH_MODEL_APPLICATION_UPDATE), applicationMapper
        .createApplicationModel(applicationJson), applicationId);
    applicationJson.setContactList(contacts);
    return applicationJson;
  }


  /**
   * Find given application details.
   *
   * @param applicationId application identifier that is used to find details
   * @return Application details or empty application list in DTO
   */
  public ApplicationJson findApplicationById(String applicationId) {
    Application applicationModel = restTemplate.getForObject(applicationProperties
        .getUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_BY_ID), Application.class, applicationId);
    return getApplication(applicationModel);
  }

  /**
   * Find given handler's active applications.
   *
   * @param handlerId handler identifier that is used to find details
   * @return List of found application with details
   */
  public List<ApplicationJson> findApplicationByHandler(String handlerId) {
    List<ApplicationJson> resultList = new ArrayList<>();
    ResponseEntity<Application[]> applicationResult = restTemplate.getForEntity(applicationProperties
        .getUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_BY_HANDLER), Application[].class, handlerId);
    for (Application applicationModel : applicationResult.getBody()) {
      resultList.add(getApplication(applicationModel));
    }
    return resultList;
  }

  /**
   * Find applications using given location query.
   *
   * @param query
   *          the location query
   * @return list of found applications with details
   */
  public List<ApplicationJson> findApplicationByLocation(LocationQueryJson query) {
    List<ApplicationJson> resultList = new ArrayList<>();
    LocationSearchCriteria lsc = new LocationSearchCriteria();
    mapLocationQueryToSearchCriteria(query, lsc);
    ResponseEntity<Application[]> applicationResult = restTemplate.postForEntity(
        applicationProperties.getUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_BY_LOCATION),
        lsc,
        Application[].class);
    for (Application applicationModel : applicationResult.getBody()) {
      resultList.add(getApplication(applicationModel));
    }
    return resultList;
  }

  private ApplicationJson getApplication(Application applicationModel) {
    ApplicationJson applicationJson = new ApplicationJson();
    applicationMapper.mapApplicationToJson(applicationJson, applicationModel);
    applicationJson.setCustomer(customerService.findCustomerById(applicationModel.getCustomerId()));
    applicationJson.setProject(projectService.findProjectById(applicationModel.getProjectId()));
    applicationJson.setApplicant(applicantService.findApplicantById(applicationModel.getApplicantId()));
    applicationJson.setContactList(contactService.findContactsForApplication(applicationModel.getId()));

    if (applicationModel.getLocationId() != null && applicationModel.getLocationId() > 0) {
      applicationJson.setLocation(locationService.findLocationById(applicationModel.getLocationId()));
    }
    return applicationJson;
  }

  private void mapLocationQueryToSearchCriteria(LocationQueryJson query, LocationSearchCriteria lsc) {
    lsc.setIntersects(query.getIntesectingGeometry());
  }

  // If contacts don't have organization, assume they are new contacts for the
  // new organization
  private void setContactOrganization(List<ContactJson> contacts, ApplicantJson applicant) {
    if (contacts == null || applicant.getOrganization() == null)
      return;
    Integer organizationId = applicant.getOrganization().getId();
    for (ContactJson cj : contacts) {
      if (cj.getOrganizationId() == null) {
        cj.setOrganizationId(organizationId);
      }
    }
  }

}

