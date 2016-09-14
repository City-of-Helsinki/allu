package fi.hel.allu.ui.service;

import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.domain.LocationSearchCriteria;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.*;
import fi.hel.allu.ui.mapper.ApplicationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
  private SearchService searchService;
  private MetaService metaService;

  @Autowired
  public ApplicationService(ApplicationProperties applicationProperties, RestTemplate restTemplate, LocationService
      locationService, CustomerService customerService, ApplicantService applicantService, ProjectService projectService,
      ApplicationMapper applicationMapper, ContactService contactService, SearchService searchService, MetaService metaService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.locationService = locationService;
    this.customerService = customerService;
    this.applicantService = applicantService;
    this.projectService = projectService;
    this.applicationMapper = applicationMapper;
    this.contactService = contactService;
    this.searchService = searchService;
    this.metaService = metaService;
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
    applicationJson.setMetadata(metaService.findMetadataForApplication(applicationJson.getType()));
    List<ContactJson> contacts = applicationJson.getContactList();
    setContactOrganization(contacts, applicationJson.getApplicant());
    Application applicationModel = restTemplate.postForObject(applicationProperties
            .getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_CREATE),
        applicationMapper.createApplicationModel(applicationJson), Application.class);
    applicationMapper.mapApplicationToJson(applicationJson, applicationModel);
    applicationJson.setContactList(contactService.setContactsForApplication(applicationJson.getId(), contacts));
    searchService.insertApplicationToES(applicationJson);
    return applicationJson;
  }

  /**
   * Update the given application by calling back-end service.
   *
   * @param applicationJson
   *          application that is going to be updated
   * @return Updated application
   */
  public ApplicationJson updateApplication(int applicationId, ApplicationJson applicationJson) {
    customerService.updateCustomer(applicationJson.getCustomer());
    applicantService.updateApplicant(applicationJson.getApplicant());
    projectService.updateProject(applicationJson.getProject());
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
    searchService.updateApplication(applicationJson);
    return applicationJson;
  }

  /**
   * Find given application details.
   *
   * @param applicationId application identifier that is used to find details
   * @return Application details or empty application list in DTO
   */
  // TODO: refactor... applicationId should be integer, no string!
  public ApplicationJson findApplicationById(String applicationId) {
    Application applicationModel = restTemplate.getForObject(applicationProperties
        .getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_BY_ID), Application.class, applicationId);
    return getApplication(applicationModel);
  }

  /**
   * Find given application details.
   *
   * @param   applicationIds    Application identifier that is used to find details
   *
   * @return  List of applications or empty application list
   */
  public List<ApplicationJson> findApplicationsById(List<Integer> applicationIds) {
    ResponseEntity<Application[]> applicationResult = restTemplate.postForEntity(applicationProperties
        .getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATIONS_FIND_BY_ID), applicationIds, Application[].class);
    List<ApplicationJson> applications = new ArrayList<>();
    for (Application applicationModel : applicationResult.getBody()) {
      applications.add(getApplication(applicationModel));
    }
    return applications;
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
        .getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_BY_HANDLER), Application[].class, handlerId);
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
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_BY_LOCATION),
        lsc,
        Application[].class);
    for (Application applicationModel : applicationResult.getBody()) {
      resultList.add(getApplication(applicationModel));
    }
    return resultList;
  }

  /**
   * Find applications by given fields.
   *
   * @param queryParameters list of query parameters
   * @return List of found application with details
   */
  public List<ApplicationJson> search(QueryParameters queryParameters) {
    List<ApplicationJson> resultList = Collections.emptyList();
    if (!queryParameters.getQueryParameters().isEmpty()) {
      List<Integer> ids = searchService.search(queryParameters);
      resultList = findApplicationsById(ids);
    }
    return resultList;
  }


  public void changeStatus(int applicationId, StatusType newStatus) {
    logger.debug("change status: application {}, new status {}", applicationId, newStatus);
    ApplicationJson applicationJson = findApplicationById(String.valueOf(applicationId));
    logger.debug("found application {}, current status {}, handler {}", applicationJson.getId(), applicationJson.getStatus(),
        applicationJson.getHandler());

    applicationJson.setStatus(newStatus);
    updateApplication(applicationId, applicationJson);
  }

  private ApplicationJson getApplication(Application applicationModel) {
    ApplicationJson applicationJson = new ApplicationJson();
    applicationMapper.mapApplicationToJson(applicationJson, applicationModel);

    if (applicationModel.getCustomerId() != null) {
      applicationJson.setCustomer(customerService.findCustomerById(applicationModel.getCustomerId()));
    }
    applicationJson.setProject(projectService.findProjectById(applicationModel.getProjectId()));
    applicationJson.setApplicant(applicantService.findApplicantById(applicationModel.getApplicantId()));
    applicationJson.setContactList(contactService.findContactsForApplication(applicationModel.getId()));
    applicationJson.setMetadata(metaService.findMetadataForApplication(applicationModel.getType(), applicationModel.getMetadataVersion()));

    if (applicationModel.getLocationId() != null && applicationModel.getLocationId() > 0) {
      applicationJson.setLocation(locationService.findLocationById(applicationModel.getLocationId()));
    }
    applicationJson.setAttachmentList(findAttachmentsForApplication(applicationModel.getId()));
    return applicationJson;
  }

  private List<AttachmentInfoJson> findAttachmentsForApplication(Integer applicationId) {
    List<AttachmentInfoJson> resultList = new ArrayList<>();
    ResponseEntity<AttachmentInfo[]> attachmentResult = restTemplate.getForEntity(
        applicationProperties
            .getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_ATTACHMENTS_BY_APPLICATION),
        AttachmentInfo[].class, applicationId);
    for (AttachmentInfo attachmentInfo : attachmentResult.getBody()) {
      AttachmentInfoJson attachmentInfoJson = new AttachmentInfoJson();
      applicationMapper.mapAttachmentInfoToJson(attachmentInfoJson, attachmentInfo);
      resultList.add(attachmentInfoJson);
    }
    return resultList;
  }

  private void mapLocationQueryToSearchCriteria(LocationQueryJson query, LocationSearchCriteria lsc) {
    lsc.setIntersects(query.getIntersectingGeometry());
    lsc.setAfter(query.getAfter());
    lsc.setBefore(query.getBefore());
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

