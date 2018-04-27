package fi.hel.allu.model.controller;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.AttachmentDao;
import fi.hel.allu.model.dao.DecisionDao;
import fi.hel.allu.model.dao.DistributionEntryDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ApplicationIdentifier;
import fi.hel.allu.model.domain.ApplicationTag;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.DeadlineCheckParams;
import fi.hel.allu.model.domain.DistributionEntry;
import fi.hel.allu.model.domain.Invoice;
import fi.hel.allu.model.domain.LocationSearchCriteria;
import fi.hel.allu.model.service.ApplicationReplacementService;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.InvoiceService;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

  private final ApplicationService applicationService;
  private final AttachmentDao attachmentDao;
  private final DecisionDao decisionDao;
  private final DistributionEntryDao distributionEntryDao;
  private final ApplicationReplacementService applicationReplacementService;
  private final InvoiceService invoiceService;

  @Autowired
  public ApplicationController(
      ApplicationService applicationService,
      AttachmentDao attachmentDao,
      DecisionDao decisionDao,
      DistributionEntryDao distributionEntryDao,
      InvoiceService invoiceService,
      ApplicationReplacementService applicationReplacementService) {
    this.applicationService = applicationService;
    this.attachmentDao = attachmentDao;
    this.decisionDao = decisionDao;
    this.distributionEntryDao = distributionEntryDao;
    this.invoiceService = invoiceService;
    this.applicationReplacementService = applicationReplacementService;
  }

  /**
   * Find application by application ID
   *
   * @param id
   * @return the application
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Application> findById(@PathVariable int id) {
    return new ResponseEntity<>(applicationService.findById(id), HttpStatus.OK);
  }

  /**
   * Find applications by application IDs
   *
   * @param   ids to be searched.
   * @return  found applications
   */
  @RequestMapping(value = "/find", method = RequestMethod.POST)
  public ResponseEntity<List<Application>> findByIds(@RequestBody List<Integer> ids) {
    return new ResponseEntity<>(applicationService.findByIds(ids, true), HttpStatus.OK);
  }

  /**
   * Find applications within an area
   *
   * @param lsc
   *          the location search criteria
   * @return All intersecting applications
   */
  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<List<Application>> findByLocation(@Valid @RequestBody LocationSearchCriteria lsc) {
    return new ResponseEntity<>(applicationService.findByLocation(lsc), HttpStatus.OK);
  }

  /**
   * Find applications by applicationId starting with given text
   *
   * @param applicationIdStartsWith string containing part of applicationId used in search
   * @return list of application identifier which start with given text
   */
  @RequestMapping(value = "/identifiers", method = RequestMethod.GET)
  public ResponseEntity<List<ApplicationIdentifier>> findByApplicationIdStartingWith(@RequestParam String applicationIdStartsWith) {
    return new ResponseEntity<>(applicationService.findByApplicationIdStartingWith(applicationIdStartsWith), HttpStatus.OK);
  }

  /**
   * Find all applications, with paging support
   *
   * @param pageRequest page request for the search
   */
  @RequestMapping()
  public ResponseEntity<Page<Application>> findAll(
      @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.DEFAULT_PAGE_SIZE)
      Pageable pageRequest) {
    return new ResponseEntity<>(applicationService.findAll(pageRequest), HttpStatus.OK);
  }

  /**
   * Update existing application
   *
   * @param id
   * @param application
   * @return the updated application
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Application> update(@PathVariable int id,
      @Valid @RequestBody(required = true) Application application) {
    return new ResponseEntity<>(applicationService.update(id, application), HttpStatus.OK);
  }

  /**
   * Updates owner of given applications.
   *
   * @param   ownerId     New owner set to the applications.
   * @param   applications  Applications whose owner is updated.
   */
  @RequestMapping(value = "/owner/{ownerId}", method = RequestMethod.PUT)
  public ResponseEntity<Void> updateOwner(@PathVariable int ownerId, @RequestBody List<Integer> applications) {
    applicationService.updateOwner(ownerId, applications);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Removes owner of given applications.
   *
   * @param   applications  Applications whose owner is removed.
   */
  @RequestMapping(value = "/owner/remove", method = RequestMethod.PUT)
  public ResponseEntity<Void> removeOwner(@RequestBody List<Integer> applications) {
    applicationService.removeOwner(applications);
    return new ResponseEntity<>(HttpStatus.OK);
  }


  /**
   * Create new application
   *
   * @param application
   *          The application data
   * @return The created application
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Application> insert(@Valid @RequestBody(required = true) Application application) {
    return new ResponseEntity<>(applicationService.insert(application), HttpStatus.OK);
  }

  /**
   * Replace (create a copy from) application
   *
   * @param applicationId
   *          Id of the application to replace
   * @return Id of the replacing application
   */
  @RequestMapping(value = "/{id}/replace", method = RequestMethod.POST)
  public ResponseEntity<Integer> replace(@PathVariable int id, @RequestParam(required = true) int userId) {
    return new ResponseEntity<>(applicationReplacementService.replaceApplication(id, userId), HttpStatus.OK);
  }




  /**
   * Delete note and its related data
   *
   * @param id application's database ID.
   */
  @RequestMapping(value = "/note/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteNote(@PathVariable int id) {
    applicationService.deleteNote(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Delete draft and its related data
   *
   * @param id application's database ID.
   */
  @RequestMapping(value = "/drafts/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteDraft(@PathVariable int id) {
    applicationService.deleteDraft(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Add single tag to application
   *
   * @param id Application's database ID
   * @param tag Tag to add
   */
  @RequestMapping(value = "/{id}/tag", method = RequestMethod.POST)
  public ResponseEntity<ApplicationTag> addTag(@PathVariable int id, @RequestBody ApplicationTag tag) {
    return new ResponseEntity<>(applicationService.addTag(id, tag), HttpStatus.OK);
  }

  /**
   * Update (replace) applications tags with new ones
   * @param id Id of the application to be changed.
   * @param tags New tags
   * @return stored tags
   */
  @RequestMapping(value = "/{id}/tags", method = RequestMethod.PUT)
  public ResponseEntity<List<ApplicationTag>> updateTags(@PathVariable int id, @RequestBody List<ApplicationTag> tags) {
    return new ResponseEntity<>(applicationService.updateTags(id, tags), HttpStatus.OK);
  }

  /**
   * Fetches tags for specified application
   *
   * @param id id of application which tags are fetched for
   * @return tags for specified application
   */
  @RequestMapping(value = "/{id}/tags", method = RequestMethod.GET)
  public ResponseEntity<List<ApplicationTag>> findTagsByApplicationId(@PathVariable int id) {
    return new ResponseEntity<>(applicationService.findTagsByApplicationId(id), HttpStatus.OK);
  }

  /**
   * Find attachments for an application
   *
   * @param id The application id
   * @return list of attachments
   */
  @RequestMapping(value = "/{id}/attachments", method = RequestMethod.GET)
  public ResponseEntity<List<AttachmentInfo>> findAttachments(@PathVariable int id) {
    return new ResponseEntity<>(attachmentDao.findByApplication(id), HttpStatus.OK);
  }

  /**
   * Store the decision PDF for application
   *
   * @param id
   *          application ID
   * @param file
   *          decision PDF data
   * @return
   * @throws IOException
   */
  @RequestMapping(value = "/{id}/decision", method = RequestMethod.POST)
  public ResponseEntity<Void> storeDecision(@PathVariable int id, @RequestParam("file") MultipartFile file)
      throws IOException {
    decisionDao.storeDecision(id, file.getBytes());
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri());
    return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
  }

  /**
   * Get the decision PDF for application
   *
   * @param id
   *          application ID
   * @return Decision PDF data
   *
   */
  @RequestMapping(value = "/{id}/decision", method = RequestMethod.GET)
  public ResponseEntity<byte[]> getDecision(@PathVariable int id) {
    byte[] bytes = decisionDao.getDecision(id)
        .orElseThrow(() -> new NoSuchEntityException("Decision not found", Integer.toString(id)));
    return new ResponseEntity<>(bytes, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/decision-distribution-list", method = RequestMethod.POST)
  public ResponseEntity<Void> replaceDecisionDistributionList(
      @PathVariable int id,
      @RequestBody List<DistributionEntry> distributionEntries) {
    distributionEntryDao.replaceEntries(id, distributionEntries);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Get list of applications about to end
   *
   * @param specifiers List of application specifiers
   */
  @RequestMapping(value = "/deadline-check", method = RequestMethod.POST)
  public ResponseEntity<List<Application>> deadlineCheck(@RequestBody @Valid DeadlineCheckParams checkParams) {
    return new ResponseEntity<>(applicationService.deadLineCheck(checkParams), HttpStatus.OK);
  }

  /**
   * Mark the given applications having a reminder sent
   *
   * @param applicationIds list of application IDs
   */
  @RequestMapping(value = "/reminder-sent", method = RequestMethod.POST)
  public ResponseEntity<Void> markReminderSent(@RequestBody List<Integer> applicationIds) {
    applicationService.markReminderSent(applicationIds);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Get the invoice data for application
   *
   * @param id The appication's database ID
   * @return all invoices for given application ID
   */
  @RequestMapping(value = "/{id}/invoices", method = RequestMethod.GET)
  public ResponseEntity<List<Invoice>> getInvoices(@PathVariable int id) {
    return new ResponseEntity<>(invoiceService.findByApplication(id), HttpStatus.OK);
  }

  /**
   * Get list of invoices that are ready to be sent to SAP
   *
   * @return list of invoices
   */
  @RequestMapping(value = "/invoices/ready-to-send", method = RequestMethod.GET)
  public ResponseEntity<List<Invoice>> getPendingInvoices() {
    return new ResponseEntity<>(invoiceService.findPending(), HttpStatus.OK);
  }

  /**
   * Mark given invoices as sent.
   *
   * @param invoiceIds list of invoice IDs
   */
  @RequestMapping(value = "/invoices/mark-as-sent", method = RequestMethod.POST)
  public ResponseEntity<Void> markInvoicesSent(@RequestBody List<Integer> invoiceIds) {
    invoiceService.markSent(invoiceIds);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/invoices/{id}/release-pending",  method = RequestMethod.PUT)
  public ResponseEntity<Void> releasePendingInvoice(@PathVariable Integer id) {
    invoiceService.releasePending(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Finds finished applications having one of the given statuses.
   */
  @RequestMapping(value = "/finished", method = RequestMethod.POST)
  public ResponseEntity<List<Integer>> findFinishedApplications(@RequestBody List<StatusType> statuses) {
    return new ResponseEntity<>(applicationService.findFinishedApplications(statuses), HttpStatus.OK);
  }

  /**
   * Finds id of the external owner (user) of the application.
   */
  @RequestMapping(value = "/{id}/externalowner", method = RequestMethod.GET)
  public ResponseEntity<Integer> getApplicationExternalOwner(@PathVariable Integer id) {
    return new ResponseEntity<>(applicationService.getApplicationExternalOwner(id), HttpStatus.OK);
  }


}
