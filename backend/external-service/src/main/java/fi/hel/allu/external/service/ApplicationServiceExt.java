package fi.hel.allu.external.service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Sets;

import fi.hel.allu.common.domain.ApplicationStatusInfo;
import fi.hel.allu.common.domain.ExternalApplication;
import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.InformationRequestStatus;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.ApplicationNotificationType;
import fi.hel.allu.common.types.ChangeType;
import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.external.domain.*;
import fi.hel.allu.external.mapper.ApplicationExtMapper;
import fi.hel.allu.external.mapper.AttachmentMapper;
import fi.hel.allu.external.mapper.CustomerExtMapper;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.changehistory.HistorySearchCriteria;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ApplicationTagJson;
import fi.hel.allu.servicecore.domain.StatusChangeInfoJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.event.ApplicationEventDispatcher;
import fi.hel.allu.servicecore.mapper.ApplicationJsonMapper;
import fi.hel.allu.servicecore.service.*;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;

/**
 * Service with application-related operations that are only needed in
 * external service.
 */
@Service
public class ApplicationServiceExt {

  private static final List<ChangeType> HISTORY_CHANGE_TYPES_INCLUDED = Arrays.asList(ChangeType.STATUS_CHANGED, ChangeType.CONTRACT_STATUS_CHANGED,
      ChangeType.COMMENT_ADDED);
  private static final String HISTORY_INCLUDED_COMMENT_TYPE = CommentType.TO_EXTERNAL_SYSTEM.name();

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;
  @Autowired
  private ApplicationHistoryService applicationHistoryService;
  @Autowired
  private ExternalUserService externalUserService;
  @Autowired
  private AttachmentService attachmentService;
  @Autowired
  private ApplicationProperties applicationProperties;
  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private SupervisionTaskService supervisionTaskService;
  @Autowired
  private InformationRequestService informationRequestService;
  @Autowired
  private CustomerExtMapper customerMapper;
  @Autowired
  private ApplicationEventDispatcher applicationEventDispatcher;
  @Autowired
  private UserService userService;


  public <T extends BaseApplicationExt> Integer createApplication(T application, ApplicationExtMapper<T> mapper) throws JsonProcessingException {
    ApplicationJson applicationJson = mapper.mapExtApplication(application, getExternalUserId());
    applicationJson.setReceivedTime(ZonedDateTime.now());
    StatusType status = application.isPendingOnClient() ? StatusType.PENDING_CLIENT : StatusType.PENDING;
    applicationJson.setExternalOwnerId(getExternalUserId());
    Integer applicationId = applicationServiceComposer.createApplication(applicationJson, status).getId();
    saveOriginalApplication(applicationId, null, applicationJson);
    setDefaultImages(applicationId, application.getTrafficArrangementImages());
    return applicationId;
  }

  public List<ApplicationHistoryExt> searchApplicationHistory(ApplicationHistorySearchExt searchParameters) {
    Integer externalUserId = getExternalUserId();
    HistorySearchCriteria searchCriteria = new HistorySearchCriteria(searchParameters.getApplicationIds(), HISTORY_CHANGE_TYPES_INCLUDED, searchParameters.getEventsAfter());
    Map<Integer, List<ChangeHistoryItem>> changeHistory = applicationHistoryService.getExternalOwnerApplicationHistory(externalUserId, searchCriteria);
    Map<Integer, List<SupervisionTask>> supervisionTaskHistory = supervisionTaskService.getSupervisionTaskHistoryForExternalOwner(externalUserId, searchParameters.getEventsAfter(), searchParameters.getApplicationIds());
    return Sets.union(changeHistory.keySet(), supervisionTaskHistory.keySet()).stream()
      .map(id -> new ApplicationHistoryExt(id, toStatusEvents(id, changeHistory.get(id)), toSupervisionEvents(id, supervisionTaskHistory.get(id))))
      .collect(Collectors.toList());
  }

  private List<SupervisionEventExt> toSupervisionEvents(Integer id, List<SupervisionTask> tasks) {
    return Optional.ofNullable(tasks).orElse(Collections.emptyList())
            .stream()
            .map(t -> new SupervisionEventExt(t.getActualFinishingTime(), t.getType(), t.getStatus(), t.getResult()))
            .collect(Collectors.toList());
  }

  private List<ApplicationStatusEventExt> toStatusEvents(Integer applicationId, List<ChangeHistoryItem> items) {
    return Optional.ofNullable(items).orElse(Collections.emptyList())
            .stream()
            .filter(i -> isChangeIncluded(i))
            .map(i ->
              new ApplicationStatusEventExt(i.getChangeTime(),
                  getEventName(i),
                  i.getInfo().getApplicationId(),
                  i.getChangeSpecifier2())
             )
            .collect(Collectors.toList());
  }

  private String getEventName(ChangeHistoryItem item) {
    // For status change events return new status from change specifier as event name, for comments change type.
    return item.getChangeType() == ChangeType.COMMENT_ADDED ? ChangeType.COMMENT_ADDED.name() : item.getChangeSpecifier();
  }

  private boolean isChangeIncluded(ChangeHistoryItem item) {
    // If change type is "comment added" include it in history only if comment is targeted to external system
    return item.getChangeType() != ChangeType.COMMENT_ADDED || item.getChangeSpecifier().equals(HISTORY_INCLUDED_COMMENT_TYPE);
  }

  public <T extends BaseApplicationExt> Integer updateApplication(Integer id, T applicationExt, ApplicationExtMapper<T> mapper) throws JsonProcessingException {
    ApplicationJson application = mapper.mapExtApplication(applicationExt, getExternalUserId());
    // Set optimistic lock version since it's currently not available from ext api
    application.setVersion(applicationServiceComposer.getApplicationVersion(id));
    application.setStatus(applicationServiceComposer.getApplicationStatus(id).getStatus());
    application.setReceivedTime(ZonedDateTime.now());
    application = applicationServiceComposer.updateApplication(id, application);
    StatusType status = applicationExt.isPendingOnClient() ? StatusType.PENDING_CLIENT : StatusType.PENDING;
    if (application.getStatus() != status) {
      applicationServiceComposer.changeStatus(id, status);
    }
    saveOriginalApplication(id, null, application);
    setDefaultImages(id, applicationExt.getTrafficArrangementImages());

    return application.getId();
  }

  public void validateFullUpdateAllowed(Integer applicationId) {
    StatusType status = applicationServiceComposer.getApplicationStatus(applicationId).getStatus();
    if (!(status == StatusType.PENDING_CLIENT || status == StatusType.PENDING)) {
      throw new IllegalOperationException("application.ext.notpending");
    }
  }

  public Integer getApplicationIdForExternalId(Integer externalId) {
    Integer applicationId = applicationServiceComposer.getApplicationIdForExternalId(externalId);
    if (applicationId == null) {
      throw new NoSuchEntityException("application.notfound");
    }
    return applicationId;
  }

  public void validateOwnedByExternalUser(Integer applicationId) {
    Integer externalOwnerId = applicationServiceComposer.getApplicationExternalOwner(applicationId);
    getExternalUserId();
    if (!getExternalUserId().equals(externalOwnerId)) {
      throw new IllegalOperationException("application.ext.notowner");
    }
  }

  public void addAttachment(Integer applicationId, AttachmentInfoExt metadata, MultipartFile file) throws IOException {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("attachment.empty");
    }
   attachmentService.addAttachment(applicationId, AttachmentMapper.toAttachmentInfoJson(metadata), file);
  }

  public void saveOriginalApplication(Integer id, Integer informationRequestId, ApplicationJson originalApplication) throws JsonProcessingException {
    ExternalApplication externalApplication = createExternalApplication(id, informationRequestId, originalApplication);
    restTemplate.postForObject(
        applicationProperties.getExternalApplicationCreateUrl(),
        externalApplication, Void.class, id);
  }

  public ExternalApplication createExternalApplication(Integer id, Integer informationRequestId, ApplicationJson originalApplication)
      throws JsonProcessingException {
    ExternalApplication externalApplication = new ExternalApplication();
    externalApplication.setApplicationId(id);
    externalApplication.setInformationRequestId(informationRequestId);
    externalApplication.setApplicationData(ApplicationJsonMapper.getApplicationAsJson(originalApplication));
    return externalApplication;
  }

  public <T extends BaseApplicationExt> void addInformationRequestResponse(Integer applicationId, Integer requestId,
      InformationRequestResponseExt<T> response, ApplicationExtMapper<T> mapper) throws JsonProcessingException {
    validateOwnedByExternalUser(applicationId);
    validateInformationRequestOpen(requestId);
    addResponseForRequest(applicationId, requestId, response, mapper);
    applicationServiceComposer.changeStatus(applicationId, StatusType.INFORMATION_RECEIVED);
  }

  public <T extends BaseApplicationExt> void reportApplicationChange(Integer applicationId,
      InformationRequestResponseExt<T> response, ApplicationExtMapper<T> mapper) throws JsonProcessingException {
    validateOwnedByExternalUser(applicationId);
    validateApplicationChangePossible(applicationId);
    InformationRequest request = informationRequestService.createForResponse(applicationId, Collections.emptyList());
    addResponseForRequest(applicationId, request.getId(), response, mapper);
    applicationServiceComposer.addTag(applicationId, new ApplicationTagJson(null, ApplicationTagType.OTHER_CHANGES, ZonedDateTime.now()));
    applicationEventDispatcher.dispatchUpdateEvent(applicationId, userService.getCurrentUser().getId(), ApplicationNotificationType.EXTERNAL_OTHER_CHANGE);
  }

  private void validateInformationRequestOpen(Integer requestId) {
    InformationRequest request =  informationRequestService.findById(requestId);
    if (request.getStatus() != InformationRequestStatus.OPEN) {
      throw new IllegalOperationException("informationrequest.notopen");
    }
  }

  private void validateApplicationChangePossible(Integer applicationId) {
    ApplicationStatusInfo statusInfo = applicationServiceComposer.getApplicationStatus(applicationId);
    if (!(statusInfo.getStatus() == StatusType.DECISION || statusInfo.getStatus() == StatusType.OPERATIONAL_CONDITION)) {
      throw new IllegalOperationException("application.addChanges.notAllowed");
    }
  }

  private <T extends BaseApplicationExt> void addResponseForRequest(Integer applicationId, Integer requestId,
      InformationRequestResponseExt<T> response, ApplicationExtMapper<T> mapper) throws JsonProcessingException {
    ApplicationJson applicationJson = mapper.mapExtApplication(response.getApplicationData(), getExternalUserId());
    ExternalApplication extApp = createExternalApplication(applicationId, requestId, applicationJson);
    informationRequestService.addResponse(requestId, extApp, response.getUpdatedFields());
  }

  public void cancelApplication(Integer id) {
    applicationServiceComposer.changeStatus(
        id, StatusType.CANCELLED, new StatusChangeInfoJson());
  }

  public void markSurveyDone(Integer id) {
    applicationServiceComposer.removeTag(id, ApplicationTagType.SURVEY_REQUIRED);
  }

  private Integer getExternalUserId() {
    User alluUser = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
    String username = alluUser.getUsername();
    return externalUserService.findUserByUserName(username).getId();
  }

  public UserExt getHandler(Integer applicationId) {
    UserJson handler = applicationServiceComposer.getApplicationHandler(applicationId);
    return Optional.ofNullable(handler).map(h -> new UserExt(h.getRealName(), h.getTitle())).orElse(null);
  }

  public UserExt getDecisionMaker(Integer applicationId) {
    UserJson decisionMaker = applicationServiceComposer.getApplicationDecisionMaker(applicationId);
    return Optional.ofNullable(decisionMaker).map(u -> new UserExt(u.getRealName(), u.getTitle())).orElse(null);
  }

  public <T extends ApplicationExt> T findById(Integer applicationId, Function<ApplicationJson, T> mapper) {
    ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
    return mapper.apply(application);
  }

  public List<byte[]> getDecisionAttachmentDocuments(Integer applicationId) {
    return getDecisionAttachments(applicationId)
        .stream()
        .map(a -> attachmentService.getAttachmentData(a.getId()))
        .collect(Collectors.toList());
  }

  public AttachmentInfoExt getDecisionAttachmentInfo(Integer applicationId, int attachmentId) {
    return getDecisionAttachments(applicationId)
        .stream()
        .filter(a -> a.getId().equals(attachmentId))
        .findFirst()
        .orElseThrow(() -> new NoSuchEntityException("attachment.decision.notFound"));
  }

  public byte[] getDecisionAttachmentData(int attachmentId) {
    return attachmentService.getAttachmentData(attachmentId);
  }

  public List<AttachmentInfoExt> getDecisionAttachments(Integer applicationId) {
    return attachmentService.findDecisionAttachmentsForApplication(applicationId)
        .stream()
        .map(a -> new AttachmentInfoExt(a.getId(), a.getMimeType(), a.getName(), a.getDescription()))
        .collect(Collectors.toList());
  }

  private void setDefaultImages(Integer applicationId, List<Integer> trafficArrangementImages) {
    attachmentService.setDefaultImagesForApplication(applicationId, trafficArrangementImages);
  }

  public Map<CustomerRoleType, CustomerWithContactsExt> findApplicationCustomers(Integer applicationId) {
    return applicationServiceComposer.findApplicationCustomers(applicationId).stream().collect(
        Collectors.toMap(CustomerWithContacts::getRoleType, c -> customerMapper.mapCustomerWithContactsExt(c)));
  }

  public CustomerExt findInvoiceRecipient(Integer applicationId) {
    Customer invoiceRecipient = applicationServiceComposer.findInvoiceRecipient(applicationId);
    return invoiceRecipient != null ? customerMapper.mapCustomerExt(invoiceRecipient) : null;
  }
}
