package fi.hel.allu.ui.service;

import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.AttachmentInfoJson;
import fi.hel.allu.ui.domain.LocationQueryJson;
import fi.hel.allu.ui.domain.QueryParametersJson;
import fi.hel.allu.ui.mapper.ApplicationMapper;
import fi.hel.allu.ui.mapper.QueryParameterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for composing different application related services together. The main purpose of this class is to avoid circular references
 * between different services.
 */
@Service
public class ApplicationServiceComposer {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationServiceComposer.class);

  private RestTemplate restTemplate;
  private ApplicationProperties applicationProperties;
  private ApplicationMapper applicationMapper;
  private ApplicationService applicationService;
  private ProjectService projectService;
  private ApplicantService applicantService;
  private ContactService contactService;
  private MetaService metaService;
  private UserService userService;
  private LocationService locationService;
  private SearchService searchService;

  @Autowired
  public ApplicationServiceComposer(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      ApplicationMapper applicationMapper,
      ApplicationService applicationService,
      ProjectService projectService,
      ApplicantService applicantService,
      ContactService contactService,
      MetaService metaService,
      UserService userService,
      LocationService locationService,
      SearchService searchService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.applicationMapper = applicationMapper;
    this.applicationService = applicationService;
    this.projectService = projectService;
    this.applicantService = applicantService;
    this.contactService = contactService;
    this.metaService = metaService;
    this.userService = userService;
    this.locationService = locationService;
    this.searchService = searchService;
  }

  /**
   * Find given application details.
   *
   * @param applicationId application identifier that is used to find details
   * @return Application details or empty application list in DTO
   */
  public ApplicationJson findApplicationById(int applicationId) {
    return getFullyPopulatedApplication(applicationService.findApplicationById(applicationId));
  }

  /**
   * Find applications using given location query.
   *
   * @param query   the location query
   * @return list of found applications with details
   */
  public List<ApplicationJson> findApplicationByLocation(LocationQueryJson query) {
    return applicationService.findApplicationByLocation(query)
        .stream().map(a -> getFullyPopulatedApplication(a)).collect(Collectors.toList());
  }

  /**
   * Create applications by calling backend service.
   *
   * @param applicationJson Application that are going to be created
   * @return Transfer object that contains list of created applications and their identifiers
   */
  public ApplicationJson createApplication(ApplicationJson applicationJson) {
    ApplicationJson createdApplication = applicationService.createApplication(applicationJson);
    searchService.insertApplicationToES(applicationJson);
    return createdApplication;
  }

  /**
   * Update the given application by calling back-end service.
   *
   * @param applicationJson
   *          application that is going to be updated
   * @return Updated application
   */
  public ApplicationJson updateApplication(int applicationId, ApplicationJson applicationJson) {
    ApplicationJson updatedApplication = applicationService.updateApplication(applicationId, applicationJson);
    searchService.updateApplication(applicationJson);
    return updatedApplication;
  }

  /**
   * Updates handler of given applications.
   *
   * @param updatedHandler  Handler to be set.
   * @param applicationIds  Applications to be updated.
   */
  public void updateApplicationHandler(int updatedHandler, List<Integer> applicationIds) {
    applicationService.updateApplicationHandler(updatedHandler, applicationIds);
    // read updated applications to be able to update ElasticSearch
    List<ApplicationJson> applicationJsons = getFullyPopulatedApplications(applicationIds);
    applicationJsons.forEach(a -> searchService.updateApplication(a));
  }

  /**
   * Removes handler of given applications.
   *
   * @param applicationIds  Applications whose handler should be removed.
   */
  public void removeApplicationHandler(List<Integer> applicationIds) {
    applicationService.removeApplicationHandler(applicationIds);
    // read updated applications to be able to update ElasticSearch
    List<ApplicationJson> applicationJsons = getFullyPopulatedApplications(applicationIds);
    applicationJsons.forEach(a -> searchService.updateApplication(a));
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
      resultList = getFullyPopulatedApplications(ids);
      orderByIdList(ids, resultList);
    }
    return resultList;
  }

  public ApplicationJson changeStatus(int applicationId, StatusType newStatus) {
    logger.debug("change status: application {}, new status {}", applicationId, newStatus);
    Application application = applicationService.findApplicationById(applicationId);
    ApplicationJson applicationJson = getFullyPopulatedApplication(application);
    logger.debug("found application {}, current status {}, handler {}",
        applicationJson.getId(), applicationJson.getStatus(), applicationJson.getHandler());
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
    ApplicationJson applicationJson = applicationMapper.mapApplicationToJson(applicationModel);

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

  /**
   * Find applications of given project.
   *
   * @param   id    Id of the project whose applications should be returned.
   * @return  Applications of the given project. Never <code>null</code>.
   */
  public List<ApplicationJson> findApplicationsByProject(int id) {
    return projectService.findApplicationsByProject(id).stream().map(a -> getFullyPopulatedApplication(a)).collect(Collectors.toList());
  }

  private List<ApplicationJson> getFullyPopulatedApplications(List<Integer> ids) {
    List<Application> foundApplications = applicationService.findApplicationsById(ids);
    return foundApplications.stream().map(a -> getFullyPopulatedApplication(a)).collect(Collectors.toList());
  }

  private List<AttachmentInfoJson> findAttachmentsForApplication(Integer applicationId) {
    List<AttachmentInfoJson> resultList = new ArrayList<>();
    ResponseEntity<AttachmentInfo[]> attachmentResult = restTemplate.getForEntity(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_ATTACHMENTS_BY_APPLICATION),
        AttachmentInfo[].class,
        applicationId);
    for (AttachmentInfo attachmentInfo : attachmentResult.getBody()) {
      AttachmentInfoJson attachmentInfoJson = new AttachmentInfoJson();
      applicationMapper.mapAttachmentInfoToJson(attachmentInfoJson, attachmentInfo);
      resultList.add(attachmentInfoJson);
    }
    return resultList;
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
