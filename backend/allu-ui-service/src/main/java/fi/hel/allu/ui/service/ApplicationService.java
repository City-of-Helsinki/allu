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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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

  // Stylesheet name for decision PDF generation:
  private static final String DECISION_STYLESHEET = "paatos";

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
  public List<ApplicationJson> search(QueryParametersJson queryParameters) {
    List<ApplicationJson> resultList = Collections.emptyList();
    if (!queryParameters.getQueryParameters().isEmpty()) {
      List<Integer> ids = searchService.search(QueryParameterMapper.mapToQueryParameters(queryParameters));
      resultList = findApplicationsById(ids);
      orderByIdList(ids, resultList);
    }
    return resultList;
  }

  public void changeStatus(int applicationId, StatusType newStatus) {
    logger.debug("change status: application {}, new status {}", applicationId, newStatus);
    ApplicationJson applicationJson = findApplicationById(applicationId);
    logger.debug("found application {}, current status {}, handler {}", applicationJson.getId(), applicationJson.getStatus(),
        applicationJson.getHandler());

    applicationJson.setStatus(newStatus);
    updateApplication(applicationId, applicationJson);
  }

  /**
   * Generate the decision PDF for given application and save it to model
   * service
   *
   * @param applicationId
   *          the application's ID
   * @throws IOException
   *           when model-service responds with error
   */
  public void generateDecision(int applicationId) throws IOException {
    // Get the application's data and call pdf-service to create PDF:
    ApplicationJson application = findApplicationById(applicationId);
    DecisionJson decisionJson = new DecisionJson();
    decisionJson.setApplication(application);
    byte[] pdfData = restTemplate.postForObject(
        applicationProperties.getPdfServiceUrl(ApplicationProperties.PATH_PDF_GENERATE), decisionJson, byte[].class,
        DECISION_STYLESHEET);
    // Store the generated PDF to model:
    MultiValueMap<String, Object> requestParts = new LinkedMultiValueMap<>();
    requestParts.add("file", new ByteArrayResource(pdfData) {
      @Override // return some filename so that Spring handles this as file
      public String getFilename() {
        return "file.pdf";
      }
    });
    HttpHeaders requestHeader = new HttpHeaders();
    requestHeader.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<?> requestEntity = new HttpEntity<>(requestParts, requestHeader);
    // ...then execute the request
    ResponseEntity<String> response = restTemplate.exchange(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_DECISION_STORE), HttpMethod.POST,
        requestEntity, String.class, applicationId);
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new IOException(response.getBody());
    }
  }

  /**
   * Get the decision PDF for given application from the model service
   *
   * @param applicationId
   *          the application's ID
   * @return PDF data
   */
  public byte[] getDecision(int applicationId) {
    return restTemplate.getForObject(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_DECISION_GET), byte[].class,
        applicationId);
  }

  private ApplicationJson getApplication(Application applicationModel) {
    ApplicationJson applicationJson = new ApplicationJson();
    applicationMapper.mapApplicationToJson(applicationJson, applicationModel);

    applicationJson.setProject(projectService.findProjectById(applicationModel.getProjectId()));
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

