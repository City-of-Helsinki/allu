package fi.hel.allu.servicecore.service;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.RequiredTasks;
import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.util.ApplicationIdUtil;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;

@Service
public class ApplicationService {

  private ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;
  private final ApplicationMapper applicationMapper;
  private final UserService userService;
  private final PersonAuditLogService personAuditLogService;
  private final PaymentClassService paymentClassService;

  @Autowired
  public ApplicationService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      ApplicationMapper applicationMapper,
      UserService userService,
      PersonAuditLogService personAuditLogService,
      PaymentClassService paymentClassService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.applicationMapper = applicationMapper;
    this.userService = userService;
    this.personAuditLogService = personAuditLogService;
    this.paymentClassService = paymentClassService;
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
        .map(t -> restTemplate.postForEntity(applicationProperties.getTagsUrl(), t, ApplicationTag.class, id))
        .map(response -> applicationMapper.createTagJson(response.getBody()))
        .get();
  }

  public void removeTag(int id, ApplicationTagType tagType) {
    restTemplate.delete(applicationProperties.getTagsDeleteUrl(), id, tagType);
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
    setPaymentClasses(newApplication);
    Application applicationModel = restTemplate.postForObject(
        applicationProperties.getApplicationCreateUrl(),
        applicationMapper.createApplicationModel(newApplication),
        Application.class,
        userService.getCurrentUser().getId());
    return applicationModel;

  }

  /**
   * Update the given application by calling back-end service.
   *
   * @param applicationJson application that is going to be updated
   * @return Updated application
   */
  Application updateApplication(int applicationId, ApplicationJson applicationJson) {
    applicationJson.setId(applicationId);
    applicationJson.setApplicationTags(tagsWithUserInfo(applicationJson.getApplicationTags()));
    setPaymentClasses(applicationJson);
    HttpEntity<Application> requestEntity = new HttpEntity<>(applicationMapper.createApplicationModel(applicationJson));
    ResponseEntity<Application> responseEntity = restTemplate.exchange(applicationProperties.getApplicationUpdateUrl(),
        HttpMethod.PUT, requestEntity, Application.class, applicationId, userService.getCurrentUser().getId());
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

  void updateApplicationHandler(Integer applicationId, Integer updatedHandler) {
    restTemplate.put(applicationProperties.getApplicationHandlerUpdateUrl(), null, applicationId, updatedHandler);
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

  Application returnToStatus(int applicationId, StatusType statusType) {
    ResponseEntity<Application> responseEntity = restTemplate.exchange(
        applicationProperties.getApplicationStatusReturnUrl(),
        HttpMethod.PUT,
        new HttpEntity<>(statusType),
        Application.class,
        applicationId);
    return responseEntity.getBody();
  }

  private void setPaymentClasses(ApplicationJson application) {
    if (application.getType() == ApplicationType.EXCAVATION_ANNOUNCEMENT ||
        application.getType() == ApplicationType.AREA_RENTAL) {
      final List<LocationJson> locations = application.getLocations();
      locations.forEach(l -> l.setPaymentTariff(paymentClassService.getPaymentClass(l)));
    }
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

  public Integer getReplacingApplicationId(Integer applicationId) {
    return restTemplate.getForObject(applicationProperties.getReplacingApplicationIdUrl(), Integer.class, applicationId);
  }

  public Integer getApplicationExternalOwner(Integer applicationId) {
    return restTemplate.getForObject(applicationProperties.getApplicationExternalOwnerUrl(), Integer.class, applicationId);
  }

  public void setInvoiceRecipient(int id, Integer invoiceRecipientId) {
    Application application = findApplicationById(id);
    if (Objects.equals(invoiceRecipientId, application.getInvoiceRecipientId())) {
      return;
    }
    validateInvoiceRecipientChangeAllowed(application);
    URI uri = UriComponentsBuilder.fromHttpUrl(applicationProperties.getApplicationInvoiceRecipientUrl())
        .queryParam("invoicerecipientid", invoiceRecipientId)
        .queryParam("userid", userService.getCurrentUser().getId())
        .buildAndExpand(Collections.singletonMap("id", id)).toUri();
    restTemplate.exchange(uri, HttpMethod.PUT,
        new HttpEntity<>(null), Void.class);
  }

  private void validateInvoiceRecipientChangeAllowed(Application application) {
    boolean allowed = BooleanUtils.isTrue(application.getNotBillable())
        || (application.getInvoiceRecipientId() == null)
        || (invoiceRecipientChangeAllowedByStatus(application))
        || invoiceRecipientChangeAllowedAfterDecision(application);
    if (!allowed) {
      throw new IllegalOperationException("application.invoicing.invoicerecipient.notallowed");
    }
  }

  private boolean invoiceRecipientChangeAllowedByStatus(Application application) {
    return application.getStatus().isBeforeDecision();
  }

  private boolean invoiceRecipientChangeAllowedAfterDecision(Application application) {
    return application.getApplicationTags().stream().anyMatch(t -> t.getType() == ApplicationTagType.SAP_ID_MISSING) ||
        invoicingDateInFuture(application);
  }

  private boolean invoicingDateInFuture(Application application) {
    if (application.getInvoicingDate() == null) {
      return true;
    }
    ZonedDateTime tomorrow = TimeUtil.startOfDay(ZonedDateTime.now()).plusDays(1);
    return !application.getInvoicingDate().isBefore(tomorrow);
  }

  public Application setCustomerOperationalConditionDates(Integer id, ApplicationDateReport dateReport) {
    ResponseEntity<Application> responseEntity = restTemplate.exchange(
        applicationProperties.getCustomerOperationalConditionUrl(), HttpMethod.PUT,
        new HttpEntity<>(dateReport), Application.class, id);
    return responseEntity.getBody();
  }

  public Application setCustomerWorkFinishedDates(Integer id, ApplicationDateReport dateReport) {
    ResponseEntity<Application> responseEntity = restTemplate.exchange(
        applicationProperties.getCustomerWorkFinishedUrl(),
        HttpMethod.PUT, new HttpEntity<>(dateReport), Application.class, id);
    return responseEntity.getBody();
  }

  public Application setCustomerValidityDates(Integer id, ApplicationDateReport dateReport) {
    ResponseEntity<Application> responseEntity = restTemplate.exchange(
        applicationProperties.getCustomerValidityUrl(),
        HttpMethod.PUT, new HttpEntity<>(dateReport), Application.class, id);
    return responseEntity.getBody();
  }

  public void setOperationalConditionDate(Integer id, ZonedDateTime operationalConditionDate) {
    restTemplate.exchange(
        applicationProperties.getOperationalConditionUrl(),
        HttpMethod.PUT, new HttpEntity<>(operationalConditionDate), Void.class, id);
  }

  public void setWorkFinishedDate(Integer id, ZonedDateTime workFinishedDate) {
    restTemplate.exchange(
        applicationProperties.getWorkFinishedUrl(),
        HttpMethod.PUT, new HttpEntity<>(workFinishedDate), Void.class, id);
  }

  public void setRequiredTasks(Integer id, RequiredTasks tasks) {
    restTemplate.exchange(
        applicationProperties.getSetRequiredTasksUrl(),
        HttpMethod.PUT, new HttpEntity<>(tasks), Void.class, id);
  }


  public Application setTargetState(Integer id, StatusType targetState) {
    return restTemplate.exchange(
        applicationProperties.getSetTargetStateUrl(),
        HttpMethod.PUT, new HttpEntity<>(targetState), Application.class, id).getBody();
  }
}
