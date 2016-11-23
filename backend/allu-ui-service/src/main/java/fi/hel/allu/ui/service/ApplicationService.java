package fi.hel.allu.ui.service;

import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.domain.LocationSearchCriteria;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.*;
import fi.hel.allu.ui.mapper.ApplicationMapper;
import fi.hel.allu.ui.mapper.QueryParameterMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ApplicationService {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private LocationService locationService;
  private ApplicantService applicantService;
  private ProjectService projectService;
  private ApplicationMapper applicationMapper;
  private ContactService contactService;
  private SearchService searchService;
  private MetaService metaService;
  private UserService userService;

  @Autowired
  public ApplicationService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      LocationService locationService,
      ApplicantService applicantService,
      ProjectService projectService,
      ApplicationMapper applicationMapper,
      ContactService contactService,
      SearchService searchService,
      MetaService metaService,
      UserService userService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.locationService = locationService;
    this.applicantService = applicantService;
    this.projectService = projectService;
    this.applicationMapper = applicationMapper;
    this.contactService = contactService;
    this.searchService = searchService;
    this.metaService = metaService;
    this.userService = userService;
  }


  void setApplicationProperties(ApplicationProperties applicationProperties) {
    this.applicationProperties = applicationProperties;
  }

  /**
   * Create applications by calling backend service.
   *
   * @param applicationJson Application that are going to be created
   * @return Transfer object that contains list of created applications and their identifiers
   */
  public ApplicationJson createApplication(ApplicationJson applicationJson) {
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
    searchService.updateApplication(applicationJson);
    return applicationJson;
  }

  public void updateApplicationHandler(int updatedHandler, List<Integer> applicationIds) {
    // update applications in database
    restTemplate.put(applicationProperties.getApplicationHandlerUpdateUrl(), applicationIds, updatedHandler);
    // read updated applications to be able to update ElasticSearch
    List<ApplicationJson> applications = findApplicationsById(applicationIds);
    applications.forEach(a -> searchService.updateApplication(a));
  }

  public void removeApplicationHandler(List<Integer> applicationIds) {
    // remove handler from applications in database
    restTemplate.put(applicationProperties.getApplicationHandlerRemoveUrl(), applicationIds);
    // read updated applications to be able to update ElasticSearch
    List<ApplicationJson> applications = findApplicationsById(applicationIds);
    applications.forEach(a -> searchService.updateApplication(a));
  }

  /**
   * Find given application details.
   *
   * @param applicationId application identifier that is used to find details
   * @return Application details or empty application list in DTO
   */
  public ApplicationJson findApplicationById(int applicationId) {
    Application applicationModel = restTemplate.getForObject(applicationProperties
        .getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_BY_ID), Application.class, applicationId);
    return getFullyPopulatedApplication(applicationModel);
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
      applications.add(getFullyPopulatedApplication(applicationModel));
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
      resultList.add(getFullyPopulatedApplication(applicationModel));
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
      resultList.add(getFullyPopulatedApplication(applicationModel));
    }
    return resultList;
  }

  /**
   * Find applications by given fields.
   *
   * @param queryParameters list of query parameters
   * @return List of found application with details
   */
  public List<ApplicationJson> search(QueryParametersJson queryParameters) {
    List<ApplicationJson> resultList = Collections.emptyList();
    if (!queryParameters.getQueryParameters().isEmpty()) {
      List<Integer> ids = searchService.search(QueryParameterMapper.mapToQueryParameters(queryParameters));
      resultList = findApplicationsById(ids);
      orderByIdList(ids, resultList);
    }
    return resultList;
  }

  public ApplicationJson changeStatus(int applicationId, StatusType newStatus) {
    logger.debug("change status: application {}, new status {}", applicationId, newStatus);
    ApplicationJson applicationJson = findApplicationById(applicationId);
    logger.debug("found application {}, current status {}, handler {}", applicationJson.getId(), applicationJson.getStatus(),
        applicationJson.getHandler());

    applicationJson.setStatus(newStatus);
    return updateApplication(applicationId, applicationJson);
  }

  /**
   * Returns fully populated application json i.e. having all related data structures like applicant and project populated.
   *
   * @param   applicationModel  Application to be mapped to fully populated application json.
   * @return  fully populated application json.
   */
  public ApplicationJson getFullyPopulatedApplication(Application applicationModel) {
    ApplicationJson applicationJson = new ApplicationJson();
    applicationMapper.mapApplicationToJson(applicationJson, applicationModel);

    if (applicationModel.getProjectId() != null) {
      applicationJson.setProject(projectService.findById(applicationModel.getProjectId()));
    }
    applicationJson.setApplicant(applicantService.findApplicantById(applicationModel.getApplicantId()));
    applicationJson.setContactList(contactService.findContactsForApplication(applicationModel.getId()));
    applicationJson.setMetadata(metaService.findMetadataForApplication(applicationModel.getType(), applicationModel.getMetadataVersion()));
    applicationJson.setHandler(applicationModel.getHandler() != null ? userService.findUserById(applicationModel.getHandler()) : null);

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

  /**
   * Orders given application list by the order of id list.
   *
   * @param ids               Order of applications.
   * @param applicationList   Applications to be ordered.
   */
  private void orderByIdList(List<Integer> ids, List<ApplicationJson> applicationList) {
    // use the application order returned by search service
    Map<Integer, Integer> idToOrder = new HashMap<>();
    for (int i = 0; i < ids.size(); ++i) {
      idToOrder.put(ids.get(i), i);
    }
    Collections.sort(applicationList, Comparator.comparing(application -> idToOrder.get(application.getId())));
  }
}

