package fi.hel.allu.external.service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Sets;

import fi.hel.allu.common.domain.ExternalApplication;
import fi.hel.allu.common.domain.types.InformationRequestStatus;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.external.domain.*;
import fi.hel.allu.external.mapper.ApplicationExtMapper;
import fi.hel.allu.external.mapper.AttachmentMapper;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.InformationRequest;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.StatusChangeInfoJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.mapper.ApplicationJsonMapper;
import fi.hel.allu.servicecore.service.*;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;

/**
 * Service with application-related operations that are only needed in
 * external service.
 */
@Service
public class ApplicationServiceExt {

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

  public <T extends BaseApplicationExt> Integer createApplication(T application, ApplicationExtMapper<T> mapper) throws JsonProcessingException {
    ApplicationJson applicationJson = mapper.mapExtApplication(application, getExternalUserId());
    applicationJson.setReceivedTime(ZonedDateTime.now());
    StatusType status = application.isPendingOnClient() ? StatusType.PENDING_CLIENT : StatusType.PENDING;
    applicationJson.setExternalOwnerId(getExternalUserId());
    Integer applicationId = applicationServiceComposer.createApplication(applicationJson, status).getId();
    saveOriginalApplication(applicationId, null, applicationJson);
    return applicationId;
  }

  public List<ApplicationHistoryExt> searchApplicationHistory(ApplicationHistorySearchExt searchParameters) {
    Integer externalUserId = getExternalUserId();
    Map<Integer, List<ChangeHistoryItem>> changeHistory = applicationHistoryService.getExternalOwnerApplicationHistory(externalUserId,
        searchParameters.getEventsAfter(), searchParameters.getApplicationIds());
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
            .map(i -> new ApplicationStatusEventExt(i.getChangeTime(), StatusType.valueOf(i.getChangeSpecifier()), i.getInfo().getApplicationId()))
            .collect(Collectors.toList());
  }

  public <T extends BaseApplicationExt> Integer updateApplication(Integer id, T applicationExt, ApplicationExtMapper<T> mapper) throws JsonProcessingException {
    ApplicationJson application = mapper.mapExtApplication(applicationExt, getExternalUserId());
    application.setReceivedTime(ZonedDateTime.now());
    application = applicationServiceComposer.updateApplication(id, application);
    StatusType status = applicationExt.isPendingOnClient() ? StatusType.PENDING_CLIENT : StatusType.PENDING;
    if (application.getStatus() != status) {
      applicationServiceComposer.changeStatus(id, status);
    }
    saveOriginalApplication(id, null, application);
    return application.getId();
  }

  public void validateFullUpdateAllowed(Integer applicationId) {
    StatusType status = applicationServiceComposer.getApplicationStatus(applicationId).getStatus();
    if (status != StatusType.PENDING_CLIENT) {
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
    ApplicationJson applicationJson = mapper.mapExtApplication(response.getApplicationData(), getExternalUserId());
    ExternalApplication extApp = createExternalApplication(applicationId, requestId, applicationJson);
    informationRequestService.addResponse(requestId, extApp, response.getUpdatedFields());
    applicationServiceComposer.changeStatus(applicationId, StatusType.INFORMATION_RECEIVED);
  }

  private void validateInformationRequestOpen(Integer requestId) {
    InformationRequest request =  informationRequestService.findById(requestId);
    if (request.getStatus() != InformationRequestStatus.OPEN) {
      throw new IllegalOperationException("informationrequest.notopen");
    }
  }

  public void cancelApplication(Integer id) {
    applicationServiceComposer.changeStatus(
        id, StatusType.CANCELLED, new StatusChangeInfoJson());
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

  public ApplicationExt findById(Integer applicationId) {
    ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
    return ApplicationExtMapper.mapToApplicationExt(application);

  }
}