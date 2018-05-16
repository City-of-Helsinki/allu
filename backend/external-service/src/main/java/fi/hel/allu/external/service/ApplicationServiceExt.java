package fi.hel.allu.external.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;

import fi.hel.allu.common.domain.ExternalApplication;
import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.InformationRequestStatus;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.external.domain.*;
import fi.hel.allu.external.mapper.AttachmentMapper;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.InformationRequest;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.InvoiceJson;
import fi.hel.allu.servicecore.mapper.ApplicationJsonMapper;
import fi.hel.allu.servicecore.service.*;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;

/**
 * Service class for application-related operations that are only needed in
 * external service.
 */
@Service
public class ApplicationServiceExt {

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;
  @Autowired
  private ApplicationHistoryService applicationHistoryService;
  @Autowired
  private InvoiceService invoiceService;
  @Autowired
  private ExternalUserService externalUserService;
  @Autowired
  private AttachmentService attachmentService;
  @Autowired
  private ApplicationProperties applicationProperties;
  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private InformationRequestService informationRequestService;



  public void releaseCustomersInvoices(Integer customerId) {
    List<Integer> applicationIds = applicationServiceComposer.findApplicationIdsByInvoiceRecipientId(customerId);
    applicationIds
        .forEach(id -> applicationServiceComposer.removeTagFromApplication(id, ApplicationTagType.SAP_ID_MISSING));
    applicationIds.forEach(id -> releaseInvoicesOfApplication(id));
  }

  private void releaseInvoicesOfApplication(Integer applicationId) {
    List<InvoiceJson> invoicesToRelease = invoiceService.findByApplication(applicationId);
    invoicesToRelease.forEach(i -> releaseInvoice(i));
  }

  private void releaseInvoice(InvoiceJson invoice) {
    if (invoice.isSapIdPending()) {
      invoiceService.releasePendingInvoice(invoice.getId());
    }
  }

  public Integer createPlacementContract(PlacementContractExt placementContract) throws JsonProcessingException {
    ApplicationJson applicationJson = ApplicationFactory.fromPlacementContractExt(placementContract, getExternalUserId());
    StatusType status = placementContract.isPendingOnClient() ? StatusType.PENDING_CLIENT : StatusType.PENDING;
    applicationJson.setExternalOwnerId(getExternalUserId());
    Integer applicationId = applicationServiceComposer.createApplication(applicationJson, status).getId();
    saveOriginalApplication(applicationId, null, applicationJson);
    return applicationId;
  }

  private Integer getExternalUserId() {
    User alluUser = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
    String username = alluUser.getUsername();
    return externalUserService.findUserByUserName(username).getId();
  }

  public List<ApplicationHistoryExt> searchApplicationHistory(ApplicationHistorySearchExt searchParameters) {
    Map<Integer, List<ChangeHistoryItem>> changeHistory = applicationHistoryService.getExternalOwnerApplicationHistory(getExternalUserId(),
        searchParameters.getEventsAfter(), searchParameters.getApplicationIds());
    return changeHistory.entrySet().stream().map(e -> new ApplicationHistoryExt(e.getKey(), toHistoryEvents(e.getValue()))).collect(Collectors.toList());

  }

  private List<ApplicationHistoryEventExt> toHistoryEvents(List<ChangeHistoryItem> items) {
    return items.stream().map(i ->
    new ApplicationHistoryEventExt(i.getChangeTime(), i.getNewStatus())).collect(Collectors.toList());
  }

  public Integer updatePlacementContract(Integer id, PlacementContractExt placementContract) throws JsonProcessingException {
    ApplicationJson applicationJson = ApplicationFactory.fromPlacementContractExt(placementContract, getExternalUserId());
    ApplicationJson application = applicationServiceComposer.updateApplication(id, applicationJson);
    StatusType status = placementContract.isPendingOnClient() ? StatusType.PENDING_CLIENT : StatusType.PENDING;
    applicationServiceComposer.changeStatus(id, status);
    saveOriginalApplication(id, null, application);
    return application.getId();
  }

  public void validateFullUpdateAllowed(Integer applicationId) {
    StatusType status = applicationServiceComposer.getApplicationStatus(applicationId);
    if (status != StatusType.PENDING_CLIENT) {
      throw new IllegalOperationException("Update of an application with status " + status + " is not allowed");
    }
  }

  public void validateOwnedByExternalUser(Integer applicationId) {
    Integer externalOwnerId = applicationServiceComposer.getApplicationExternalOwner(applicationId);
    getExternalUserId();
    if (!getExternalUserId().equals(externalOwnerId)) {
      throw new IllegalOperationException("Trying to modify application not owned by current user");
    }
  }

  public void addAttachment(Integer applicationId, AttachmentInfoExt metadata, MultipartFile file) throws IOException {
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

  public void addInformationRequestResponse(Integer applicationId, Integer requestId,
      InformationRequestResponseExt<PlacementContractExt> response) throws JsonProcessingException {
    validateOwnedByExternalUser(applicationId);
    validateInformationRequestOpen(requestId);
    ApplicationJson applicationJson = ApplicationFactory.fromPlacementContractExt(response.getApplicationData(), getExternalUserId());
    ExternalApplication extApp = createExternalApplication(applicationId, requestId, applicationJson);
    informationRequestService.addResponse(requestId, extApp, response.getUpdatedFields());
  }

  private void validateInformationRequestOpen(Integer requestId) {
    InformationRequest request =  informationRequestService.findById(requestId);
    if (request.getStatus() != InformationRequestStatus.OPEN) {
      throw new IllegalOperationException("Information request with id " + requestId + "  is not open.");
    }
  }

}
