package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {
  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private LocationService locationService;
  private CustomerService customerService;
  private ApplicationMapper applicationMapper;
  private ContactService contactService;
  private UserService userService;

  @Autowired
  public ApplicationService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      LocationService locationService,
      CustomerService customerService,
      ApplicationMapper applicationMapper,
      ContactService contactService,
      UserService userService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.locationService = locationService;
    this.customerService = customerService;
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
    LocationSearchCriteria lsc = applicationMapper.createLocationSearchCriteria(query);
    ResponseEntity<Application[]> applicationResult = restTemplate.postForEntity(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_BY_LOCATION),
        lsc,
        Application[].class);
    return Arrays.asList(applicationResult.getBody());
  }

  /**
   * Get the charge basis entries for an application
   *
   * @param id the application ID
   * @return the charge basis entries for the application
   */
  public List<ChargeBasisEntry> getChargeBasis(int id) {
    ResponseEntity<ChargeBasisEntry[]> restResult = restTemplate.getForEntity(applicationProperties.getChargeBasisUrl(),
        ChargeBasisEntry[].class, id);
    return Arrays.asList(restResult.getBody());
  }

  /**
   * Set the manual charge basis entries for an application
   *
   * @param id the application ID
   * @param chargeBasisEntries the charge basis entries to store. Only entries
   *          that are marked as manually set will be used
   * @return the new charge basis entries for the application
   */
  public List<ChargeBasisEntry> setChargeBasis(int id, List<ChargeBasisEntry> chargeBasisEntries) {
    HttpEntity<List<ChargeBasisEntry>> requestEntity = new HttpEntity<>(chargeBasisEntries);
    ResponseEntity<ChargeBasisEntry[]> restResult = restTemplate.exchange(applicationProperties.setChargeBasisUrl(),
        HttpMethod.PUT, requestEntity, ChargeBasisEntry[].class, id);
    return Arrays.asList(restResult.getBody());
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
            applicationProperties.getUpdateTagsUrl(),
            HttpMethod.PUT,
            new HttpEntity<>(tagsWithUserInfo),
            ApplicationTag[].class,
            id);
    return applicationMapper.createTagJson(Arrays.asList(response.getBody()));
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
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_CREATE),
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

  void updateApplicationHandler(int updatedHandler, List<Integer> applicationIds) {
    restTemplate.put(applicationProperties.getApplicationHandlerUpdateUrl(), applicationIds, updatedHandler);
  }

  void removeApplicationHandler(List<Integer> applicationIds) {
    restTemplate.put(applicationProperties.getApplicationHandlerRemoveUrl(), applicationIds);
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
    if (StatusType.DECISION.equals(statusType) || StatusType.REJECTED.equals(statusType)) {
      UserJson currentUser = userService.getCurrentUser();
      requestEntity = new HttpEntity<>(currentUser.getId());
    } else {
      requestEntity = new HttpEntity<>((Integer) null);
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
}

