package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.ApplicationStatusInfo;
import fi.hel.allu.common.domain.RequiredTasks;
import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.types.ApplicationNotificationType;
import fi.hel.allu.common.util.ApplicationIdUtil;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.event.ApplicationEventDispatcher;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.mapper.UserMapper;
import fi.hel.allu.servicecore.util.PageRequestBuilder;
import fi.hel.allu.servicecore.util.RestResponsePage;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class ApplicationService {

  private ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;
  private final ApplicationMapper applicationMapper;
  private final UserService userService;
  private final PersonAuditLogService personAuditLogService;
  private final PaymentClassService paymentClassService;
  private final PaymentZoneService paymentZoneService;
  private final InvoicingPeriodService invoicingPeriodService;
  private final InvoiceService invoiceService;
  private final ApplicationEventDispatcher applicationEventDispatcher;

  @Autowired
  public ApplicationService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      ApplicationMapper applicationMapper,
      UserService userService,
      PersonAuditLogService personAuditLogService,
      PaymentClassService paymentClassService,
      PaymentZoneService paymentZoneService,
      InvoicingPeriodService invoicingPeriodService,
      InvoiceService invoiceService,
      ApplicationEventDispatcher applicationEventDispatcher) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    // Needed for PATCH support
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    restTemplate.setRequestFactory(requestFactory);
    this.applicationMapper = applicationMapper;
    this.userService = userService;
    this.personAuditLogService = personAuditLogService;
    this.paymentClassService = paymentClassService;
    this.paymentZoneService = paymentZoneService;
    this.invoicingPeriodService = invoicingPeriodService;
    this.invoiceService = invoiceService;
    this.applicationEventDispatcher = applicationEventDispatcher;
  }

  void setApplicationProperties(ApplicationProperties applicationProperties) {
    this.applicationProperties = applicationProperties;
  }

  Logger log = LoggerFactory.getLogger(ApplicationService.class);

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
        distributionEntryJsons.stream().map(applicationMapper::createDistributionEntryModel).toList();
    restTemplate.postForEntity(
        applicationProperties.getApplicationDistributionListUrl(),
        distributionEntries,
        Void.class,
        id);
  }

  /**
   * Fetches application distribution list
   *
   * @param id application id
   * @return Distribution list for specified application
   */
  public List<DistributionEntryJson> getDistributionList(int id) {
    DistributionEntry[] result = restTemplate.getForObject(
      applicationProperties.getApplicationDistributionListUrl(),
      DistributionEntry[].class,
      id
    );
    return Arrays.stream(result)
      .map(applicationMapper::createDistributionEntryJson)
            .toList();
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
            .toList();

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
    return setInvoicingPeriods(applicationModel);

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
    if(applicationJson.getStatus() != StatusType.DECISION || applicationJson.getStatus() != StatusType.OPERATIONAL_CONDITION){
        setPaymentClasses(applicationJson);
    }
    Integer currentUserId = userService.getCurrentUser().getId();
    HttpEntity<Application> requestEntity = new HttpEntity<>(applicationMapper.createApplicationModel(applicationJson));
    Application application = restTemplate.exchange(applicationProperties.getApplicationUpdateUrl(),
        HttpMethod.PUT, requestEntity, Application.class, applicationId, currentUserId).getBody();

    applicationEventDispatcher.dispatchUpdateEvent(applicationId, currentUserId, ApplicationNotificationType.CONTENT_CHANGED, applicationJson.getStatus());
    return setInvoicingPeriods(application);
  }

  /**
   * Check that all given applications have previously been marked as anonymizable
   *
   * @param applicationIds list of application IDs to check
   *
   * @return true if all given applications are anonymizable, otherwise false
   */
  boolean checkApplicationAnonymizability(List<Integer> applicationIds) {
    ParameterizedTypeReference<List<Integer>> typeRef = new ParameterizedTypeReference<List<Integer>>() {};
    List<Integer> nonanonymizableApplicationIds =
      restTemplate.exchange(applicationProperties.getApplicationAnonymizabilityCheckUrl(), HttpMethod.POST, new HttpEntity<>(applicationIds), typeRef).getBody();

    return nonanonymizableApplicationIds == null || nonanonymizableApplicationIds.isEmpty();
  }

  /**
   * Anonymize given applications
   *
   * @param applicationIds list of application IDs to anonymize
   */
  void anonymizeApplications(List<Integer> applicationIds) {
    restTemplate.exchange(applicationProperties.getAnonymizeApplicationsUrl(),
      HttpMethod.PATCH, new HttpEntity<>(applicationIds), Void.class);
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


  void updateApplicationOwner(int updatedOwner, List<Integer> applicationIds, boolean dispatchEvent) {
    restTemplate.put(applicationProperties.getApplicationOwnerUpdateUrl(), applicationIds, updatedOwner);
    Integer currentUserId = userService.getCurrentUser().getId();
    if (dispatchEvent) {
      applicationIds.forEach(id -> applicationEventDispatcher.dispatchOwnerChangeEvent(id, currentUserId, updatedOwner));
    } else {
      applicationIds.forEach(id -> applicationEventDispatcher.dispatchNotificationRemoval(id));
    }
  }

  void removeApplicationOwner(List<Integer> applicationIds) {
    restTemplate.put(applicationProperties.getApplicationOwnerRemoveUrl(), applicationIds);
  }

  void updateApplicationHandler(Integer applicationId, Integer updatedHandler) {
    restTemplate.put(applicationProperties.getApplicationHandlerUpdateUrl(), null, applicationId, updatedHandler);
  }

  CustomerWithContacts replaceCustomerWithContacts(Integer applicationId,
                                                   CustomerWithContacts customerWithContacts) {
    ResponseEntity<CustomerWithContacts> responseEntity = restTemplate.exchange(
      applicationProperties.getReplaceCustomerWithContactsUrl(), HttpMethod.PUT,
      new HttpEntity<>(customerWithContacts), CustomerWithContacts.class, applicationId);

    Integer currentUserId = userService.getCurrentUser().getId();
    applicationEventDispatcher.dispatchUpdateEvent(applicationId, currentUserId, ApplicationNotificationType.CONTENT_CHANGED);

    return responseEntity.getBody();
  }

  void removeCustomerWithContacts(Integer applicationId,
      CustomerRoleType roleType) {
    restTemplate.delete(applicationProperties.getApplicationCustomerByRoleUrl(), applicationId, roleType);
    Integer currentUserId = userService.getCurrentUser().getId();
    applicationEventDispatcher.dispatchUpdateEvent(applicationId, currentUserId,
        ApplicationNotificationType.CONTENT_CHANGED);
  }

  Application changeApplicationStatus(int applicationId, StatusType statusType) {
    HttpEntity<Integer> userIdRequest = getUserIdRequest(statusType);

    ResponseEntity<Application> responseEntity = restTemplate.exchange(
        applicationProperties.getApplicationStatusUpdateUrl(statusType),
        HttpMethod.PUT,
        userIdRequest,
        Application.class,
        applicationId);
    applicationEventDispatcher.dispatchUpdateEvent(applicationId, userService.getCurrentUser().getId(), ApplicationNotificationType.STATUS_CHANGED, statusType);
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
      application.getLocations().forEach(l -> l.setPaymentTariff(paymentClassService.getPaymentClass(l, application)));
    } else if (needsPaymentZone(application)) {
      application.getLocations().forEach(l -> l.setPaymentTariff(paymentZoneService.getPaymentZone(l, application)));
    }
  }

  private boolean needsPaymentZone(ApplicationJson application) {
    return isShortTermRental(application.getType()) && application.getKind().isTerrace();
  }

  private HttpEntity<Integer> getUserIdRequest(StatusType statusType) {
    HttpEntity<Integer> requestEntity;
    if (StatusType.DECISION.equals(statusType) || StatusType.REJECTED.equals(statusType) || StatusType.DECISIONMAKING.equals(statusType)
        || StatusType.TERMINATED.equals(statusType)) {
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
              .map(t -> tagWithUserInfo(currentUser, t)).toList();
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
        .map(applicationMapper::mapApplicationIdentifierToJson).toList();
  }


  /**
   * Finds finished applications having one of the given statuses
   * @param applicationTypes
   */
  public List<Integer> findFinishedApplications(List<StatusType> statuses, List<ApplicationType> applicationTypes) {
    DeadlineCheckParams params = new DeadlineCheckParams(applicationTypes, statuses, null, ZonedDateTime.now());
    ParameterizedTypeReference<List<Integer>> typeRef = new ParameterizedTypeReference<List<Integer>>() {};
    return restTemplate.exchange(applicationProperties.getFinishedApplicationsUrl(),
        HttpMethod.POST, new HttpEntity<>(params), typeRef).getBody();
  }

  /**
   * Finds active excavation announcements
   */
  public List<Application> findActiveExcavationAnnouncements() {
    ParameterizedTypeReference<List<Application>> typeRef = new ParameterizedTypeReference<List<Application>>() {};
    return restTemplate.exchange(applicationProperties.getActiveExcavationAnnouncementsUrl(),
      HttpMethod.GET, null, typeRef).getBody();
  }

  public List<Application> fetchPotentiallyAnonymizableApplications() {
    ParameterizedTypeReference<List<Application>> typeRef = new ParameterizedTypeReference<List<Application>>() {};
    return restTemplate.exchange(applicationProperties.getFetchPotentiallyAnonymizableApplicationsUrl(),
      HttpMethod.GET, null, typeRef).getBody();
  }

  public void resetAnonymizableApplications(List<Integer> applicationIds) {
    restTemplate.exchange(applicationProperties.getResetAnonymizableApplicationsUrl(),
      HttpMethod.PATCH, new HttpEntity<>(applicationIds), Void.class);
  }

  public List<Integer> findFinishedNotes() {
    ParameterizedTypeReference<List<Integer>> typeRef = new ParameterizedTypeReference<List<Integer>>() {};
    return restTemplate.exchange(applicationProperties.getFinishedNotesUrl(), HttpMethod.GET, null, typeRef).getBody();
  }

  private Application findApplicationByIdWithoutPersonAuditLogging(int applicationId) {
    return restTemplate.getForObject(applicationProperties
        .getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_BY_ID), Application.class, applicationId);
  }

  public ApplicationStatusInfo getApplicationStatus(Integer applicationId) {
    return restTemplate.getForObject(applicationProperties.getApplicationStatusUrl(), ApplicationStatusInfo.class, applicationId);
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
    Integer currentUserId = userService.getCurrentUser().getId();
    URI uri = UriComponentsBuilder.fromHttpUrl(applicationProperties.getApplicationInvoiceRecipientUrl())
        .queryParam("invoicerecipientid", invoiceRecipientId)
        .queryParam("userid", currentUserId)
        .buildAndExpand(Collections.singletonMap("id", id)).toUri();
    restTemplate.exchange(uri, HttpMethod.PUT,
        new HttpEntity<>(null), Void.class);
    applicationEventDispatcher.dispatchUpdateEvent(id, userService.getCurrentUser().getId(),
        ApplicationNotificationType.INVOICE_RECIPIENT_CHANGED, application.getStatus());
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
    ZonedDateTime firstInvoicingDate = getFirstInvoicingDateForApplication(application.getId());
    if (firstInvoicingDate == null) {
      return true;
    }
    ZonedDateTime tomorrow = TimeUtil.startOfDay(ZonedDateTime.now()).plusDays(1);
    return !firstInvoicingDate.isBefore(tomorrow);
  }

  private ZonedDateTime getFirstInvoicingDateForApplication(Integer id) {
    return invoiceService.findByApplication(id).stream()
      .map(InvoiceJson::getInvoicableTime)
      .filter(Objects::nonNull)
      .sorted()
      .findFirst()
      .orElse(null);
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

  public void setCustomerLocationValidity(Integer id, Integer locationId, ApplicationDateReport dateReport) {
    restTemplate.put(
        applicationProperties.getCustomerLocationValidityUrl(),
        new HttpEntity<>(dateReport),
        id, locationId);
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

  public Application clearTargetState(Integer id) {
    return restTemplate.exchange(
            applicationProperties.getClearTargetStateUrl(),
            HttpMethod.PUT, null, Application.class, id).getBody();
  }

  public UserJson getApplicationHandler(Integer applicationId) {
    return getApplicationUser(applicationProperties.getApplicationHandlerUrl(), applicationId);
  }

  public UserJson getApplicationDecisionMaker(Integer applicationId) {
    return getApplicationUser(applicationProperties.getApplicationDecisionMakerUrl(), applicationId);
  }

  private UserJson getApplicationUser(String url, Integer applicationId) {
    User user = restTemplate.getForObject(url, User.class, applicationId);
    return Optional.ofNullable(user).map(UserMapper::mapToUserJson).orElse(null);
  }


  public Integer getApplicationIdForExternalId(Integer externalId) {
    return restTemplate.getForObject(applicationProperties.getApplicationIdForExternalIdUrl(), Integer.class, externalId);
  }

  public Application removeClientApplicationData(Integer id) {
    restTemplate.delete(applicationProperties.getClientApplicationDataDeleteUrl(), id);
    return findApplicationById(id);
  }

  private List<CustomerWithContacts> fetchApplicationCustomers(Integer applicationId) {
    ParameterizedTypeReference<List<CustomerWithContacts>> typeRef =
        new ParameterizedTypeReference<List<CustomerWithContacts>>() {};
    return restTemplate
        .exchange(applicationProperties.getCustomerByApplicationIdUrl(), HttpMethod.GET, null, typeRef, applicationId)
        .getBody();
  }

  public List<CustomerWithContacts> findApplicationCustomers(Integer applicationId) {
    List<CustomerWithContacts> customers = fetchApplicationCustomers(applicationId);
    customers.forEach(c -> personAuditLogService.log(c, "ApplicationService"));
    return customers;
  }

  public Optional<CustomerWithContacts> findApplicationCustomerByRoleType(Integer applicationId, CustomerRoleType customerRoleType) {
    List<CustomerWithContacts> customers = fetchApplicationCustomers(applicationId);
    Optional<CustomerWithContacts> customerForType = customers.stream()
      .filter(customerWithContacts -> customerRoleType.equals(customerWithContacts.getRoleType()))
      .findFirst();
    customerForType.ifPresent(c -> personAuditLogService.log(c, "ApplicationService"));
    return customerForType;
  }

  public Customer findInvoiceRecipient(Integer applicationId) {
    return restTemplate.getForObject(applicationProperties.getApplicationInvoiceRecipientUrl(), Customer.class, applicationId);
  }

  public Integer getApplicationVersion(Integer id) {
    return restTemplate.getForObject(applicationProperties.getApplicationVersionUrl(), Integer.class, id);
  }

  private Application setInvoicingPeriods(Application application) {
    if (isRecurringTerraceApplication(application)) {
      invoicingPeriodService.createPeriodsForRecurringApplication(application.getId());
      return findApplicationById(application.getId());
    } else if (isShortTermRental(application.getType()) && application.getKind().isTerrace()) {
      // Recurring application possibly changed to not recurring
      invoicingPeriodService.deleteInvoicingPeriods(application.getId());
      return findApplicationById(application.getId());
    } else if (isExcavationAnnouncement(application)) {
      invoicingPeriodService.setPeriodsForExcavationAnnouncement(application.getId());
      return findApplicationById(application.getId());
    }
    else {
      return application;
    }
  }

  private boolean isExcavationAnnouncement(Application application) {
    return application.getType() == ApplicationType.EXCAVATION_ANNOUNCEMENT;
  }

  private boolean isShortTermRental(ApplicationType type) {
    return type == ApplicationType.SHORT_TERM_RENTAL;
  }

  private boolean isRecurringTerraceApplication(Application application) {
    return isShortTermRental(application.getType()) && application.getKind().isTerrace() && isRecurringApplication(application);
  }

  private boolean isRecurringApplication(Application application) {
    return application.getRecurringEndTime() != null &&
           application.getEndTime() != null &&
           application.getRecurringEndTime().getYear() > application.getEndTime().getYear();
  }

  public boolean isBillable(Integer id) {
    return BooleanUtils.isNotTrue(findApplicationById(id).getNotBillable());
  }

  public void addOwnerNotification(Integer id) {
    restTemplate.postForEntity(applicationProperties.getOwnerNotificationUrl(), null, Void.class, id);
  }

  public void removeOwnerNotification(Integer id) {
    restTemplate.delete(applicationProperties.getOwnerNotificationUrl(), id);
  }

  public Integer getReplacingApplicationId(Integer applicationId) {
    return restTemplate.getForObject(applicationProperties.getReplacingApplicationIdUrl(), Integer.class,
        applicationId);
  }

  public Integer getApplicationOwnerId(int applicationId) {
    return restTemplate.getForObject(applicationProperties.getApplicationOwnerUrl(), Integer.class, applicationId);
  }

  public List<String> getPricelistPaymentClasses(ApplicationType type, ApplicationKind kind) {
    ParameterizedTypeReference<List<String>> typeRef =
        new ParameterizedTypeReference<List<String>>() {};
    return restTemplate
        .exchange(applicationProperties.getPricelistPaymentClassesUrl(), HttpMethod.GET, null, typeRef, type, kind)
        .getBody();
  }

  /**
   * Get list of anonymizable/"deletable" applications by calling model-service endpoint. Data is retrieved from model-service's database.
   * @param pageable page request for the search
   * @param type application type
   * @return list of anonymizable/"deletable" applications with paging
   */
  public Page<AnonymizableApplicationJson> getAnonymizableApplications(Pageable pageable, ApplicationType type) {
    if (type == null) {
      throw new IllegalArgumentException("ApplicationType cannot be null");
    }

    String baseUrl = applicationProperties.getAnonymizableApplicationsUrl();
    baseUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
      .buildAndExpand(type.name().toLowerCase())
      .toUriString();

    URI url = PageRequestBuilder.fromUriString(baseUrl, pageable);

    ResponseEntity<RestResponsePage<AnonymizableApplicationJson>> response = restTemplate.exchange(
      url,
      HttpMethod.GET,
      null,
      new ParameterizedTypeReference<RestResponsePage<AnonymizableApplicationJson>>() {}
    );
    return response.getBody();
  }
}
