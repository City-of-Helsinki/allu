package fi.hel.allu.ui.service;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.mail.model.MailMessage.Attachment;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.ui.mapper.QueryParameterMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service for composing different application related services together. The main purpose of this class is to avoid circular references
 * between different services.
 */
@Service
public class ApplicationServiceComposer {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationServiceComposer.class);

  private ApplicationService applicationService;
  private ProjectService projectService;
  private SearchService searchService;
  private ApplicationJsonService applicationJsonService;
  private ApplicationHistoryService applicationHistoryService;
  private AttachmentService attachmentService;
  private AlluMailService alluMailService;

  @Autowired
  public ApplicationServiceComposer(
      ApplicationService applicationService,
      ProjectService projectService,
      SearchService searchService,
      ApplicationJsonService applicationJsonService,
      ApplicationHistoryService applicationHistoryService,
      AttachmentService attachmentService,
      @Lazy AlluMailService alluMailService) {
    this.applicationService = applicationService;
    this.projectService = projectService;
    this.searchService = searchService;
    this.applicationJsonService = applicationJsonService;
    this.applicationHistoryService = applicationHistoryService;
    this.attachmentService = attachmentService;
    this.alluMailService = alluMailService;
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
   * Find applications using given location query.
   *
   * @param query   the location query
   * @return list of found applications with details
   */
  public List<ApplicationJson> findApplicationByLocation(LocationQueryJson query) {
    return applicationService.findApplicationByLocation(query)
        .stream().map(a -> applicationJsonService.getFullyPopulatedApplication(a)).collect(Collectors.toList());
  }

  /**
   * Create applications by calling backend service.
   *
   * @param applicationJson Application that are going to be created
   * @return Transfer object that contains list of created applications and their identifiers
   */
  public ApplicationJson createApplication(ApplicationJson applicationJson) {
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
   * Updates handler of given applications.
   *
   * @param updatedHandler
   *          Handler to be set.
   * @param applicationIds
   *          Applications to be updated.
   */
  public void updateApplicationHandler(int updatedHandler, List<Integer> applicationIds) {
    applicationService.updateApplicationHandler(updatedHandler, applicationIds);
    // read updated applications to be able to update ElasticSearch
    List<ApplicationJson> applicationJsons = getFullyPopulatedApplications(applicationIds);
    searchService.updateApplications(applicationJsons);
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
    searchService.updateApplications(applicationJsons);
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
      List<Integer> ids = searchService.searchApplication(QueryParameterMapper.mapToQueryParameters(queryParameters));
      resultList = getFullyPopulatedApplications(ids);
      SearchService.orderByIdList(ids, resultList, (applicationJson) -> applicationJson.getId());
    }
    return resultList;
  }

  public ApplicationJson changeStatus(int applicationId, StatusType newStatus) {
    logger.debug("change status: application {}, new status {}", applicationId, newStatus);
    Application application = applicationService.changeApplicationStatus(applicationId, newStatus);
    ApplicationJson applicationJson = applicationJsonService.getFullyPopulatedApplication(application);
    logger.debug("found application {}, current status {}, handler {}",
        applicationJson.getId(), applicationJson.getStatus(), applicationJson.getHandler());
    applicationJson.setStatus(newStatus);
    // TODO: add person who made the decision to somewhere
    ApplicationJson result = updateApplicationNoTracking(applicationId, applicationJson);
    applicationHistoryService.addStatusChange(applicationId, newStatus);
    return result;
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
   * Get the invoice rows for an application
   *
   * @param id the application ID
   * @return the invoice rows for the application
   */
  public List<InvoiceRow> getInvoiceRows(int id) {
    return applicationService.getInvoiceRows(id);
  }

  /**
   * Set the manual invoice rows for an application
   *
   * @param id             the application ID
   * @param invoiceRows    the invoice rows to store. Only rows that are marked as
   *                       manually set will be used
   * @return the new invoice rows for the application
   */
  public List<InvoiceRow> setInvoiceRows(int id, List<InvoiceRow> invoiceRows) {
    return applicationService.setInvoiceRows(id, invoiceRows);
  }

  /**
   * Update (replace) applications tags with new ones
   * @param id Id of the application to be changed.
   * @param tags New tags as json
   * @return New stored tags
   */
  public List<ApplicationTagJson> updateTags(int id, List<ApplicationTagJson> tags) {
    List<ApplicationTagJson> updatedTags = applicationService.updateTags(id, tags);
    searchService.updateTags(id, tags);
    return updatedTags;
  }

  /**
   * Get change items for an application
   *
   * @param applicationId
   *          application ID
   * @return list of changes ordered from oldest to newest
   */
  public List<ApplicationChangeJson> getChanges(Integer applicationId) {
    return applicationHistoryService.getChanges(applicationId);
  }

  /**
   * Send the decision PDF for application as email to an updated distribution
   * list.
   *
   * @param applicationId        the application's Id.
   * @param decisionDetailsJson  details about the decision
   */
  public void sendDecision(int applicationId, DecisionDetailsJson decisionDetailsJson)
  {
    ApplicationJson application = replaceDistributionList(applicationId,
        decisionDetailsJson.getDecisionDistributionList());
    String subject = "Aluevarauspäätös " + application.getApplicationId();
    List<String> emailRecipients = decisionDetailsJson.getDecisionDistributionList().stream()
        .filter(entry -> entry.getEmail() != null).map(entry -> entry.getEmail()).collect(Collectors.toList());
    Stream<Attachment> attachments = application.getAttachmentList().stream()
        .map(ai -> new Attachment(ai.getName(), attachmentService.getAttachmentData(ai.getId())));
    alluMailService.sendDecision(applicationId, emailRecipients, subject, decisionDetailsJson.getMessageBody(),
        attachments);
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
  private ApplicationJson replaceDistributionList(int applicationId,
      List<DistributionEntryJson> distributionEntryJsons) {
    ApplicationJson oldApplication = findApplicationById(applicationId);
    applicationService.replaceDistributionList(applicationId, distributionEntryJsons);
    ApplicationJson updatedApplication = findApplicationById(applicationId);
    applicationHistoryService.addFieldChanges(applicationId, oldApplication, updatedApplication);
    return updatedApplication;
  }

}
