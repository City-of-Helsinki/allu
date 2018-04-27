package fi.hel.allu.servicecore.service;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.security.auth.callback.CallbackHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.ApplicationIdUtil;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ApplicationIdentifier;
import fi.hel.allu.model.domain.ApplicationTag;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.DistributionEntry;
import fi.hel.allu.model.domain.LocationSearchCriteria;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ApplicationIdentifierJson;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ApplicationTagJson;
import fi.hel.allu.servicecore.domain.DistributionEntryJson;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.domain.LocationQueryJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;

@Service
public class ApplicationService {
  private ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;
  private final LocationService locationService;
  private final ApplicationMapper applicationMapper;
  private final UserService userService;
  private final PersonAuditLogService personAuditLogService;

  @Autowired
  public ApplicationService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      LocationService locationService,
      ApplicationMapper applicationMapper,
      UserService userService,
      PersonAuditLogService personAuditLogService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.locationService = locationService;
    this.applicationMapper = applicationMapper;
    this.userService = userService;
    this.personAuditLogService = personAuditLogService;
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
    Application applicationModel = findApplicationByIdWithoutPersonAuditLogging(applicationId);
    applicationModel.getCustomersWithContacts().forEach(c -> personAuditLogService.log(c, "ApplicationService"));
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
    LocationSearchCriteria lsc = applicationMapper.createLocationSearchCriteria(query);
    ResponseEntity<Application[]> applicationResult = restTemplate.postForEntity(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_BY_LOCATION),
        lsc,
        Application[].class);
    return Arrays.asList(applicationResult.getBody());
  }

  /**
   * Replaces distribution list of the given application.
   *
   * @param id                      Id of the application.
   * @param distributionEntryJsons  New distribution list for the application.
   */
  public void replaceDistributionList(int id, List<DistributionEntryJson> distributionEntryJsons) {
    List<DistributionEntry> distributionEntries =
        distributionEntryJsons.stream().map(entry -> applicationMapper.createDistributionEntryModel(entry)).collect(Collectors.toList());
    restTemplate.postForEntity(
        applicationProperties.getApplicationReplaceDistributionListUrl(),
        distributionEntries,
        Void.class,
        id);
  }

  /**
   * Adds single tag to given application
   * @param id Id of the application.
   * @param tagJson new tag to be added
   * @return added tag
   */
  public ApplicationTagJson addTag(int id, ApplicationTagJson tagJson) {
    UserJson currentUser = userService.getCurrentUser();
    return Optional.of(tagJson)
        .map(t -> tagWithUserInfo(currentUser, tagJson))
        .map(t -> new ApplicationTag(t.getAddedBy(), t.getType(), t.getCreationTime()))
        .map(t -> restTemplate.postForEntity(applicationProperties.getTagUrl(), t, ApplicationTag.class, id))
        .map(response -> applicationMapper.createTagJson(response.getBody()))
        .get();
  }

  /**
   * Update (replace) applications tags with new ones
   * @param id Id of the application to be changed.
   * @param tags New tags
   * @return New stored tags
   */
  public List<ApplicationTagJson> updateTags(int id, List<ApplicationTagJson> tags) {
    List<ApplicationTag> tagsWithUserInfo = tagsWithUserInfo(tags).stream()
            .map(t -> new ApplicationTag(t.getAddedBy(), t.getType(), t.getCreationTime()))
            .collect(Collectors.toList());

    ResponseEntity<ApplicationTag[]> response = restTemplate.exchange(
            applicationProperties.getTagsUrl(),
            HttpMethod.PUT,
            new HttpEntity<>(tagsWithUserInfo),
            ApplicationTag[].class,
            id);
    return applicationMapper.createTagsJson(Arrays.asList(response.getBody()));
  }

  /**
   * Fetches tags for specified application
   *
   * @param id id of application which tags are fetched for
   * @return tags for specified application
   */
  public List<ApplicationTagJson> findTagsByApplicationId(int id) {
    ResponseEntity<ApplicationTag[]> tagsResult = restTemplate.getForEntity(
        applicationProperties.getTagsUrl(), ApplicationTag[].class, id);
    return applicationMapper.createTagsJson(Arrays.asList(tagsResult.getBody()));
  }

  /**
   * Create applications by calling backend service.
   *
   * @param newApplication  Application to be added to backend.
   * @return Application with possibly updated information from backend.
   */
  Application createApplication(ApplicationJson newApplication) {
    newApplication.setApplicationTags(tagsWithUserInfo(newApplication.getApplicationTags()));
    Application applicationModel = restTemplate.postForObject(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION),
        applicationMapper.createApplicationModel(newApplication),
        Application.class);

    if (newApplication.getLocations() != null) {
      locationService.createLocations(applicationModel.getId(), newApplication.getLocations());
    }

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
    if (applicationJson.getLocations() != null) {
      List<LocationJson> locationJsons = locationService.updateApplicationLocations(applicationId,
          applicationJson.getLocations());
      applicationJson.setLocations(locationJsons);
    } else {
      locationService.deleteApplicationLocation(applicationId);
    }
    applicationJson.setApplicationTags(tagsWithUserInfo(applicationJson.getApplicationTags()));
    HttpEntity<Application> requestEntity = new HttpEntity<>(applicationMapper.createApplicationModel(applicationJson));
    ResponseEntity<Application> responseEntity = restTemplate.exchange(applicationProperties.getApplicationUpdateUrl(),
        HttpMethod.PUT, requestEntity, Application.class, applicationId);

    return responseEntity.getBody();
  }

  /**
   * Delete a note from model-service's database.
   *
   * @param applicationId note application's database ID
   */
  void deleteNote(int applicationId) {
    restTemplate.delete(applicationProperties.getNoteDeleteUrl(), applicationId);
  }

  /**
   * Delete a draft
   *
   * @param applicationId draft application's database ID
   */
  void deleteDraft(int applicationId) {
    restTemplate.delete(applicationProperties.getDraftDeleteUrl(), applicationId);
  }


  void updateApplicationOwner(int updatedOwner, List<Integer> applicationIds) {
    restTemplate.put(applicationProperties.getApplicationOwnerUpdateUrl(), applicationIds, updatedOwner);
  }

  void removeApplicationOwner(List<Integer> applicationIds) {
    restTemplate.put(applicationProperties.getApplicationOwnerRemoveUrl(), applicationIds);
  }

  Application changeApplicationStatus(int applicationId, StatusType statusType) {
    HttpEntity<Integer> userIdRequest = getUserIdRequest(statusType);

    ResponseEntity<Application> responseEntity = restTemplate.exchange(
        applicationProperties.getApplicationStatusUpdateUrl(statusType),
        HttpMethod.PUT,
        userIdRequest,
        Application.class,
        applicationId);
    return responseEntity.getBody();
  }

  private HttpEntity<Integer> getUserIdRequest(StatusType statusType) {
    HttpEntity<Integer> requestEntity;
    if (StatusType.DECISION.equals(statusType) || StatusType.REJECTED.equals(statusType) || StatusType.DECISIONMAKING.equals(statusType)) {
      UserJson currentUser = userService.getCurrentUser();
      requestEntity = new HttpEntity<>(currentUser.getId());
    } else {
      requestEntity = new HttpEntity<>(null);
    }
    return requestEntity;
  }

  private List<ApplicationTagJson> tagsWithUserInfo(List<ApplicationTagJson> tags) {
    if (tags != null) {
      UserJson currentUser = userService.getCurrentUser();
      return tags.stream()
              .map(t -> tagWithUserInfo(currentUser, t))
              .collect(Collectors.toList());
    }
    return tags;
  }

  private ApplicationTagJson tagWithUserInfo(UserJson currentUser, ApplicationTagJson tag) {
    ApplicationTagJson updatedTag = new ApplicationTagJson(tag.getAddedBy(), tag.getType(), tag.getCreationTime());
    if (updatedTag.getAddedBy() == null) {
      updatedTag.setAddedBy(currentUser.getId());
      updatedTag.setCreationTime(ZonedDateTime.now());
    }
    return updatedTag;
  }


  public List<Integer> findApplicationIdsByInvoiceRecipient(int customerId) {
    ParameterizedTypeReference<List<Integer>> typeRef =
        new ParameterizedTypeReference<List<Integer>>() {};
    return restTemplate.exchange(applicationProperties.getInvoiceRecipientsApplicationsUrl(), HttpMethod.GET, null, typeRef, customerId).getBody();
  }


  // Replace application with given ID and return ID of the replacing application
  public int replaceApplication(int applicationId) {
    return restTemplate.exchange(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_REPLACE), HttpMethod.POST,
        null, Integer.class, applicationId, userService.getCurrentUser().getId()).getBody();
  }

  public List<ApplicationIdentifierJson> replacementHistory(int id) {
    Application application = findApplicationByIdWithoutPersonAuditLogging(id);
    String baseApplicationId = ApplicationIdUtil.getBaseApplicationId(application.getApplicationId());

    URI uri = UriComponentsBuilder.fromHttpUrl(applicationProperties.getApplicationIdentifierUrl())
        .queryParam("applicationIdStartsWith", baseApplicationId)
        .buildAndExpand().toUri();

    HttpEntity<ApplicationIdentifier[]> result = restTemplate.getForEntity(uri, ApplicationIdentifier[].class);

    return Arrays.stream(result.getBody())
        .map(applicationMapper::mapApplicationIdentifierToJson)
        .collect(Collectors.toList());
  }


  /**
   * Finds finished applications having one of the given statuses
   */
  public List<Integer> findFinishedApplications(List<StatusType> statuses) {
    ParameterizedTypeReference<List<Integer>> typeRef = new ParameterizedTypeReference<List<Integer>>() {};
    return restTemplate.exchange(applicationProperties.getFinishedApplicationsUrl(),
        HttpMethod.POST, new HttpEntity<>(statuses), typeRef).getBody();
  }

  private Application findApplicationByIdWithoutPersonAuditLogging(int applicationId) {
    return restTemplate.getForObject(applicationProperties
        .getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_BY_ID), Application.class, applicationId);
  }

  public StatusType getApplicationStatus(Integer applicationId) {
    return restTemplate.getForObject(applicationProperties.getApplicationStatusUrl(), StatusType.class, applicationId);
  }


  public Integer getApplicationExternalOwner(Integer applicationId) {
    return restTemplate.getForObject(applicationProperties.getApplicationExternalOwnerUrl(), Integer.class, applicationId);
  }

}
