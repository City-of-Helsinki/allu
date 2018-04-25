package fi.hel.allu.external.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.external.domain.ApplicationHistoryEventExt;
import fi.hel.allu.external.domain.ApplicationHistoryExt;
import fi.hel.allu.external.domain.ApplicationHistorySearchExt;
import fi.hel.allu.external.domain.PlacementContractExt;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.InvoiceJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.ExternalUserService;
import fi.hel.allu.servicecore.service.InvoiceService;
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

  public Integer createPlacementContract(PlacementContractExt placementContract) {
    ApplicationJson applicationJson = ApplicationFactory.fromPlacementContractExt(placementContract);
    StatusType status = placementContract.isPendingOnClient() ? StatusType.PENDING_CLIENT : StatusType.PENDING;
    applicationJson.setExternalOwnerId(getExternalUserId());
    return applicationServiceComposer.createApplication(applicationJson, status).getId();
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

}
