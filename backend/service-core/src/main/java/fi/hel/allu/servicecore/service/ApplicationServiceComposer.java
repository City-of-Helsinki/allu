package fi.hel.allu.servicecore.service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import fi.hel.allu.servicecore.mapper.CustomerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.ApplicationStatusInfo;
import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.CustomerWithContacts;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;

/**
 * Service for composing different application related services together. The main purpose of this class is to avoid circular references
 * between different services.
 */
@Service
public class ApplicationServiceComposer {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationServiceComposer.class);

  private final ApplicationService applicationService;
  private final ProjectService projectService;
  private final SearchService searchService;
  private final ApplicationJsonService applicationJsonService;
  private final ApplicationHistoryService applicationHistoryService;
  private final MailComposerService mailComposerService;
  private final UserService userService;
  private final InvoiceService invoiceService;
  private final CustomerService customerService;
  private final SupervisionTaskService supervisionTaskService;
  private final CustomerMapper customerMapper;

  @Autowired
  public ApplicationServiceComposer(
      ApplicationService applicationService,
      ProjectService projectService,
      SearchService searchService,
      ApplicationJsonService applicationJsonService,
      ApplicationHistoryService applicationHistoryService,
      @Lazy MailComposerService mailComposerService,
      UserService userService,
      InvoiceService invoiceService,
      CustomerService customerService,
      @Lazy SupervisionTaskService supervisionTaskService,
      CustomerMapper customerMapper) {
    this.applicationService = applicationService;
    this.projectService = projectService;
    this.searchService = searchService;
    this.applicationJsonService = applicationJsonService;
    this.applicationHistoryService = applicationHistoryService;
    this.mailComposerService = mailComposerService;
    this.userService = userService;
    this.invoiceService = invoiceService;
    this.customerService = customerService;
    this.supervisionTaskService = supervisionTaskService;
    this.customerMapper = customerMapper;
  }

  /**
   * Find given application details.
   *
   * @param applicationId application identifier that is used to find details
   * @return Application details or empty application list in DTO
   */
  public ApplicationJson findApplicationById(int applicationId) {
    return applicationJsonService.getFullyPopulatedApplication(applicationService.findApplicationById(applicationId));
  }

  /**
   * Get's compact populated application json based on
   * given application. Json is populated with application data and some
   * related data.
   *
   * @param application Application which is used to populate the json
   * @return Compact populated json representation of application
   */
  public ApplicationJson getCompactPopulatedApplication(Application application) {
    return applicationJsonService.getCompactPopulatedApplication(application);
  }

  public List<ApplicationJson> findApplicationsByIds(List<Integer> ids) {
    return applicationService.findApplicationsById(ids).stream()
        .map(app -> applicationJsonService.getFullyPopulatedApplication(app))
        .collect(Collectors.toList());
  }

  /**
   * Create applications by calling backend service.
   *
   * @param applicationJson Application that are going to be created
   * @return Transfer object that contains list of created applications and their identifiers
   */
  public ApplicationJson createApplication(ApplicationJson applicationJson) {
    return createApplication(applicationJson, StatusType.PENDING);
  }

  /**
   * Create application draft (alustava varaus).
   *
   * @param applicationDraftJson Draft to be created
   * @return Created draft.
   */
  public ApplicationJson createDraft(ApplicationJson applicationDraftJson) {
    return createApplication(applicationDraftJson, StatusType.PRE_RESERVED);
  }

  public  ApplicationJson createApplication(ApplicationJson applicationJson, StatusType status) {
    applicationJson.setStatus(getApplicationCreationStatus(applicationJson.getType(), status));
    Application createdApplication = applicationService.createApplication(applicationJson);
    ApplicationJson createdApplicationJson = applicationJsonService.getFullyPopulatedApplication(createdApplication);
    applicationHistoryService.addApplicationCreated(createdApplication.getId());
    searchService.insertApplication(createdApplicationJson);
    return createdApplicationJson;
  }

  /**
   * Update the given application by calling back-end service.
   *
   * @param applicationJson
   *          application that is going to be updated
   * @return Updated application
   */
  public ApplicationJson updateApplication(int applicationId, ApplicationJson applicationJson) {
    ApplicationJson oldApplication = findApplicationById(applicationId);

    ApplicationJson updatedApplication = updateApplicationNoTracking(applicationId, applicationJson);

    applicationHistoryService.addFieldChanges(applicationId, oldApplication, updatedApplication);
    return updatedApplication;
  }

  /**
   * Delete a note from model-service's and search-service's database.
   *
   * @param applicationId note application's database ID
   */
  public void deleteNote(int applicationId) {
    applicationService.deleteNote(applicationId);
    searchService.deleteNote(applicationId);
  }

  public CustomerWithContactsJson replaceCustomerWithContacts(Integer applicationId, CustomerWithContactsJson customerWithContacts) {
    CustomerRoleType roleType = customerWithContacts.getRoleType();
    CustomerWithContacts existingCustomer = applicationService.findApplicationCustomerByRoleType(applicationId, roleType);

    CustomerWithContacts updatedCustomer = applicationService.replaceCustomerWithContacts(applicationId,
      customerMapper.createSingleCustomerWithContactsModel(customerWithContacts));
    CustomerWithContactsJson updatedCustomerWithContactsJson = customerMapper.createWithContactsJson(updatedCustomer);

    applicationHistoryService.addCustomerChange(applicationId,
      customerMapper.createWithContactsJson(existingCustomer), updatedCustomerWithContactsJson, roleType);
    searchService.updateApplicationCustomerWithContacts(applicationId, updatedCustomerWithContactsJson);

    return updatedCustomerWithContactsJson;
  }

  /**
   * Update the given application by calling back-end service, don't track the
   * changes in application history. To make sure application changes are
   * tracked properly, the caller should handle them.
   *
   * @param applicationJson
   *          application that is going to be updated
   * @return Updated application
   */
  private ApplicationJson updateApplicationNoTracking(int applicationId, ApplicationJson applicationJson) {
    Application updatedApplication = applicationService.updateApplication(applicationId, applicationJson);
    ApplicationJson updatedApplicationJson = applicationJsonService.getFullyPopulatedApplication(updatedApplication);
    if (updatedApplicationJson.getProject() != null) {
      List<ProjectJson> updatedProjects = projectService
          .updateProjectInformation(Collections.singletonList(updatedApplicationJson.getProject().getId()));
      searchService.updateProjects(updatedProjects);
    }
    searchService.updateApplications(Collections.singletonList(updatedApplicationJson));
    return updatedApplicationJson;
  }

  /**
   * Updates owner of given applications.
   *
   * @param updatedOwner
   *          Owner to be set.
   * @param applicationIds
   *          Applications to be updated.
   */
  public void updateApplicationOwner(int updatedOwner, List<Integer> applicationIds, boolean waitRefresh) {
    applicationService.updateApplicationOwner(updatedOwner, applicationIds);
    applicationIds.forEach(id -> applicationHistoryService.addOwnerChange(id, updatedOwner));
    // read updated applications to be able to update ElasticSearch
    List<ApplicationJson> applicationJsons = getFullyPopulatedApplications(applicationIds);
    searchService.updateApplications(applicationJsons, waitRefresh);
  }

  public void updateApplicationHandler(Integer applicationId, Integer updatedHandler) {
    applicationService.updateApplicationHandler(applicationId, updatedHandler);
  }

  /**
   * Removes owner of given applications.
   *
   * @param applicationIds  Applications whose owner should be removed.
   */
  public void removeApplicationOwner(List<Integer> applicationIds, boolean waitRefresh) {
    applicationService.removeApplicationOwner(applicationIds);
    applicationIds.forEach(id -> applicationHistoryService.addOwnerChange(id, null));
    // read updated applications to be able to update ElasticSearch
    List<ApplicationJson> applicationJsons = getFullyPopulatedApplications(applicationIds);
    searchService.updateApplications(applicationJsons, waitRefresh);
  }

  public void clearTargetState(int applicationId) {
    applicationService.clearTargetState(applicationId);
  }

  /**
   * Find applications by given fields.
   *
   * @param queryParameters list of query parameters
   * @return List of found application with details
   */
  public Page<ApplicationES> search(ApplicationQueryParameters queryParameters, Pageable pageRequest, Boolean matchAny) {
    return searchService.searchApplication(
        queryParameters,
        pageRequest,
        matchAny);
  }

  /**
   * Change application's status to new status
   * @param applicationId application to change
   * @param newStatus new status for application
   * @return updated application
   */
  public ApplicationJson changeStatus(int applicationId, StatusType newStatus) {
    return changeStatus(applicationId, newStatus, null);
  }

  /**
   * Change application's status to new status
   * @param applicationId application to change
   * @param newStatus new status for application
   * @param info additional info for status change
   * @return updated application
   */
  public ApplicationJson changeStatus(int applicationId, StatusType newStatus, StatusChangeInfoJson info) {
    logger.debug("change status: application {}, new status {}", applicationId, newStatus);
    Application application = applicationService.changeApplicationStatus(applicationId, newStatus);
    changeOwnerOnStatusChange(application, info);
    return updateSearchServiceOnStatusChange(application, newStatus);
  }

  private ApplicationJson updateSearchServiceOnStatusChange(Application application, StatusType newStatus) {
    // Get application again so that updated owner is included
    ApplicationJson applicationJson = applicationJsonService.getFullyPopulatedApplication(
        applicationService.findApplicationById(application.getId()));
    applicationHistoryService.addStatusChange(application.getId(), newStatus, applicationJson.getTargetState());
    List<ApplicationJson> applicationsUpdated = new ArrayList<>();
    applicationsUpdated.add(applicationJson);
    // Update possible replaced applications to search service, also status of those might have changed
    if (application.getReplacesApplicationId() != null) {
      applicationsUpdated.add(applicationJsonService
        .getFullyPopulatedApplication(applicationService.findApplicationById(application.getReplacesApplicationId())));
    }
    searchService.updateApplications(applicationsUpdated);
    return applicationJson;
  }

  public ApplicationJson returnToEditing(int applicationId, StatusChangeInfoJson info) {
    Application application = applicationService.findApplicationById(applicationId);
    StatusType statusToReturn = getReturnStatus(applicationId, application.getTargetState());
    reopenSupervisionTaskByTarget(applicationId, application.getTargetState(), info.getComment());
    application = applicationService.returnToStatus(applicationId, statusToReturn);
    changeOwnerOnStatusChange(application, info);
    return updateSearchServiceOnStatusChange(application, statusToReturn);
  }

  private StatusType getReturnStatus(int applicationId, StatusType target) {
    switch (target) {
      case OPERATIONAL_CONDITION:
        return StatusType.DECISION;
      case TERMINATED:
        if (applicationHistoryService.hasStatusInHistory(applicationId, StatusType.FINISHED)) {
          return StatusType.FINISHED;
        } else {
          return StatusType.DECISION;
        }
      case FINISHED:
        if (applicationHistoryService.hasStatusInHistory(applicationId, StatusType.OPERATIONAL_CONDITION)) {
          return StatusType.OPERATIONAL_CONDITION;
        } else {
          return StatusType.DECISION;
        }
      default:
        return StatusType.RETURNED_TO_PREPARATION;
    }
  }

  private void reopenSupervisionTaskByTarget(int applicationId, StatusType target, String comment) {
    if (StatusType.OPERATIONAL_CONDITION.equals(target)) {
      reopenSupervisionTask(applicationId, SupervisionTaskType.OPERATIONAL_CONDITION, comment);
    } else if (StatusType.FINISHED.equals(target)) {
      reopenSupervisionTask(applicationId, SupervisionTaskType.FINAL_SUPERVISION, comment);
    }
  }

  private void reopenSupervisionTask(int applicationId, SupervisionTaskType taskType, String comment) {
    final List<SupervisionTaskJson> tasks = supervisionTaskService.findByApplicationId(applicationId);
    tasks.stream().filter(s -> s.getType() == taskType).forEach(s -> {
      s.setStatus(SupervisionTaskStatusType.OPEN);
      s.setActualFinishingTime(null);
      s.setDescription(appendCommentToDescription(s.getDescription(), comment));
      supervisionTaskService.update(s);
    });
  }

  private String appendCommentToDescription(String description, String comment) {
    if (comment != null) {
      final StringBuilder builder = new StringBuilder();
      builder
          .append(Optional.ofNullable(description).orElse(""))
          .append(" ")
          .append(userService.getCurrentUser().getRealName())
          .append(": ")
          .append(comment);
      return builder.toString();
    } else {
      return description;
    }
  }

  /**
   * Find applications of given project.
   *
   * @param   id    Id of the project whose applications should be returned.
   * @return  Applications of the given project. Never <code>null</code>.
   */
  public List<ApplicationJson> findApplicationsByProject(int id) {
    return projectService.findApplicationsByProject(id).stream()
        .map(a -> applicationJsonService.getFullyPopulatedApplication(a)).collect(Collectors.toList());
  }

  private List<ApplicationJson> getFullyPopulatedApplications(List<Integer> ids) {
    List<Application> foundApplications = applicationService.findApplicationsById(ids);
    return foundApplications.stream().map(a -> applicationJsonService.getFullyPopulatedApplication(a)).collect(Collectors.toList());
  }

  /**
   * Update (replace) applications tags with new ones
   * @param id Id of the application to be changed.
   * @param tags New tags as json
   * @return New stored tags
   */
  public List<ApplicationTagJson> updateTags(int id, List<ApplicationTagJson> tags) {
    List<ApplicationTagJson> oldTags = findTags(id);
    List<ApplicationTagJson> updatedTags = applicationService.updateTags(id, tags);
    onTagsChange(id, oldTags, tags);
    return updatedTags;
  }

  public List<ApplicationTagJson> findTags(int id) {
    return applicationService.findTagsByApplicationId(id);
  }

  public ApplicationTagJson addTag(int id, ApplicationTagJson tag) {
    List<ApplicationTagJson> oldTags = applicationService.findTagsByApplicationId(id);
    return oldTags.stream().filter(t -> t.getType().equals(tag.getType())).findFirst()
        .orElseGet(() -> addNewTag(id, tag, oldTags));
  }

  private ApplicationTagJson addNewTag(int id, ApplicationTagJson tag,
      List<ApplicationTagJson> oldTags) {
    ApplicationTagJson added = applicationService.addTag(id, tag);
    List<ApplicationTagJson> newTags = new ArrayList<>(oldTags);
    newTags.add(added);
    onTagsChange(id, oldTags, newTags);
    return added;
  }

  public void removeTag(int id, ApplicationTagType tagType) {
    List<ApplicationTagJson> oldTags = applicationService.findTagsByApplicationId(id);
    applicationService.removeTag(id, tagType);
    onTagsChange(id, oldTags, oldTags.stream().filter(t -> t.getType() != tagType).collect(Collectors.toList()));
  }

  /**
   * Updates search service with current tags for specified application
   * @param id id of application
   */
  public void refreshSearchTags(int id) {
    List<ApplicationTagJson> tags = applicationService.findTagsByApplicationId(id);
    searchService.updateTags(id, tags);
  }

  /**
   * Get change items for an application
   *
   * @param applicationId
   *          application ID
   * @return list of changes ordered from oldest to newest
   */
  public List<ChangeHistoryItemJson> getChanges(Integer applicationId) {
    return applicationHistoryService.getChanges(applicationId);
  }

  /**
   * Send the decision PDF for application as email to an specified distribution
   * list.
   *
   * @param applicationId        the application's Id.
   * @param decisionDetailsJson  details about the decision
   */
  public void sendDecision(int applicationId, DecisionDetailsJson decisionDetailsJson, DecisionDocumentType type) {
    if (hasPaperDistribution(decisionDetailsJson)) {
      ApplicationTagJson tag = new ApplicationTagJson(null, ApplicationTagType.DECISION_NOT_SENT, ZonedDateTime.now());
      applicationService.addTag(applicationId, tag);
    }
    mailComposerService.sendDecision(findApplicationById(applicationId), decisionDetailsJson, type);
  }

  private void onTagsChange(int id, List<ApplicationTagJson> oldTags, List<ApplicationTagJson> newTags) {
    searchService.updateTags(id, newTags);
    ApplicationJson withOldTags = new ApplicationJson();

    ApplicationJson withNewTags = new ApplicationJson();
    withOldTags.setApplicationTags(oldTags);
    withNewTags.setApplicationTags(newTags);
    applicationHistoryService.addFieldChanges(id, withOldTags, withNewTags);
  }

  /*
   * Replaces distribution list of an application.
   *
   * @param applicationId Application whose distribution list is replaced.
   *
   * @param distributionEntryJsons Replacing distribution list.
   *
   * @return The updated application
   */
  public ApplicationJson replaceDistributionList(int applicationId,
      List<DistributionEntryJson> distributionEntryJsons) {
    ApplicationJson oldApplication = findApplicationById(applicationId);
    applicationService.replaceDistributionList(applicationId, distributionEntryJsons);
    ApplicationJson updatedApplication = findApplicationById(applicationId);
    applicationHistoryService.addFieldChanges(applicationId, oldApplication, updatedApplication);
    return updatedApplication;
  }

  private boolean hasPaperDistribution(DecisionDetailsJson decisionDetailsJson) {
    return decisionDetailsJson.getDecisionDistributionList() != null
        && decisionDetailsJson.getDecisionDistributionList().stream().anyMatch(d -> DistributionType.PAPER.equals(d.getDistributionType()));
  }

  private void changeOwnerOnStatusChange(Application application, StatusChangeInfoJson info) {
    Integer newOwner = Optional.ofNullable(info).map(i -> i.getOwner()).orElse(null);
    if (newOwner != null) {
      updateApplicationOwner(newOwner, Collections.singletonList(application.getId()), false);
    } else if (StatusType.HANDLING.equals(application.getStatus())) {
      updateApplicationOwner(userService.getCurrentUser().getId(), Collections.singletonList(application.getId()), false);
    } else if (StatusType.CANCELLED.equals(application.getStatus()) || StatusType.ARCHIVED.equals(application.getStatus())) {
      removeApplicationOwner(Collections.singletonList(application.getId()), false);
    }
  }

  public List<Integer> findApplicationIdsByInvoiceRecipientId(int customerId) {
    return applicationService.findApplicationIdsByInvoiceRecipient(customerId);
  }

  /**
   * @deprecated use removeTag for its simpler implementation
   */
  public void removeTagFromApplication(int id, ApplicationTagType tagType) {
    List<ApplicationTagJson> updatedTags = applicationService.findTagsByApplicationId(id).stream()
        .filter(t -> !t.getType().equals(tagType)).collect(Collectors.toList());
    updateTags(id, updatedTags);
  }

  public ApplicationJson replaceApplication(int applicationId) {
    Integer newApplicationId = applicationService.replaceApplication(applicationId);
    applicationHistoryService.addApplicationReplaced(applicationId);
    ApplicationJson replacingApplication = findApplicationById(newApplicationId);
    ApplicationJson replacedApplication = findApplicationById(applicationId);
    searchService.updateApplications(Collections.singletonList(replacedApplication));
    searchService.insertApplication(replacingApplication);

    return replacingApplication;
  }

  public boolean isReplaced(int applicationId) {
    return applicationService.getReplacingApplicationId(applicationId) != null;
  }

  public List<ApplicationIdentifierJson> replacementHistory(int applicationId) {
    return applicationService.replacementHistory(applicationId);
  }

  /**
   * Delete application draft.
   * @param id Id of the application draft to be deleted
   */
  public void deleteDraft(int id) {
    applicationService.deleteDraft(id);
    searchService.deleteDraft(id);
  }

  /**
   * Finds finished applications having one of the given statuses
   * @param typesMovedToFinished
   */
  public List<Integer> findFinishedApplications(List<StatusType> statuses, List<ApplicationType> applicationTypes) {
    return applicationService.findFinishedApplications(statuses, applicationTypes);
  }

  public ApplicationStatusInfo getApplicationStatus(Integer applicationId) {
    return applicationService.getApplicationStatus(applicationId);
  }

  public Integer getApplicationExternalOwner(Integer applicationId) {
    return applicationService.getApplicationExternalOwner(applicationId);
  }

  public void setInvoiceRecipient(int id, Integer invoiceRecipientId) {
    final ApplicationJson oldApplication = findApplicationById(id);
    final CustomerJson oldInvoiceRecipient = getCustomer(oldApplication.getInvoiceRecipientId());
    final CustomerJson newInvoiceRecipient = getCustomer(invoiceRecipientId);
    applicationService.setInvoiceRecipient(id, invoiceRecipientId);
    applicationHistoryService.addInvoiceRecipientChange(id, oldInvoiceRecipient, newInvoiceRecipient);
  }

  public void releaseCustomersInvoices(Integer customerId) {
    List<Integer> applicationIds = findApplicationIdsByInvoiceRecipientId(customerId);
    applicationIds
        .forEach(id -> removeTagFromApplication(id, ApplicationTagType.SAP_ID_MISSING));
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

  private CustomerJson getCustomer(Integer id) {
    if (id == null) {
      return null;
    }
    return customerService.findCustomerById(id);
  }

  public UserJson getApplicationHandler(Integer applicationId) {
    return applicationService.getApplicationHandler(applicationId);
  }

  public UserJson getApplicationDecisionMaker(Integer applicationId) {
    return applicationService.getApplicationDecisionMaker(applicationId);
  }

  public Integer getApplicationIdForExternalId(Integer externalId) {
    return applicationService.getApplicationIdForExternalId(externalId);
  }

  public ApplicationJson removeClientApplicationData(Integer id) {
    Application application = applicationService.removeClientApplicationData(id);
    return applicationJsonService.getFullyPopulatedApplication(application);
  }

  public List<CustomerWithContacts> findApplicationCustomers(Integer applicationId) {
    return applicationService.findApplicationCustomers(applicationId);
  }

  private StatusType getApplicationCreationStatus(ApplicationType type, StatusType status) {
    return type == ApplicationType.NOTE ? StatusType.NOTE : status;
  }

  public Customer findInvoiceRecipient(Integer applicationId) {
    return applicationService.findInvoiceRecipient(applicationId);
  }

  public List<Integer> findFinishedNotes() {
    return applicationService.findFinishedNotes();
  }

  public Integer getApplicationVersion(Integer id) {
    return applicationService.getApplicationVersion(id);
  }

  public void addOwnerNotification(Integer id) {
    applicationService.addOwnerNotification(id);
    searchService.updateApplicationField(id, "ownerNotification", true, true);
  }

  public void removeOwnerNotification(Integer id) {
    applicationService.removeOwnerNotification(id);
    searchService.updateApplicationField(id, "ownerNotification", false, true);
  }

  public Integer getApplicationOwnerId(int applicationId) {
    return applicationService.getApplicationOwnerId(applicationId);
  }
}
