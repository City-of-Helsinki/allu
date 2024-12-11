package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.DocumentSearchCriteria;
import fi.hel.allu.common.domain.DocumentSearchResult;
import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.AttachmentDao;
import fi.hel.allu.model.dao.DecisionDao;
import fi.hel.allu.model.dao.DistributionEntryDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.service.ApplicationReplacementService;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

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
    return new ResponseEntity<>(applicationService.findByIds(ids), HttpStatus.OK);
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
  public ResponseEntity<Application> update(@PathVariable int id, @RequestParam(required = true) int userId,
      @Valid @RequestBody(required = true) Application application) {
    return new ResponseEntity<>(applicationService.update(id, application, userId), HttpStatus.OK);
  }

  /**
   * Replace the customer of the given type with their contacts for an application
   *
   * @param applicationId
   * @param customerWithContacts
   * @return the saved customer with their contacts
   */
  @RequestMapping(value = "/{applicationId}/customerWithContacts", method = RequestMethod.PUT)
  public ResponseEntity<CustomerWithContacts> replaceCustomerWithContacts(@PathVariable int applicationId,
                                                                          @Valid @RequestBody CustomerWithContacts customerWithContacts) {
    return ResponseEntity.ok(applicationService.replaceCustomerWithContacts(applicationId, customerWithContacts));
  }

  /**
   * Remove the customer of the given type with their contacts from an application
   */
  @RequestMapping(value = "/{applicationId}/customerWithContacts/{roleType}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> removeCustomerWithContacts(@PathVariable int applicationId, @PathVariable CustomerRoleType roleType) {
    applicationService.removeCustomerWithContacts(applicationId, roleType);
    return ResponseEntity.ok().build();
  }

  /**
   * Updates owner of given applications.
   *
   * @param   ownerId     New owner set to the applications.
   * @param   userId      Current user
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

  @RequestMapping(value = "/{id}/handler/{handlerId}", method = RequestMethod.PUT)
  public ResponseEntity<Void> updateHandler(@PathVariable("id") Integer id, @PathVariable("handlerId") Integer handlerId) {
    applicationService.updateHandler(id, handlerId);
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
  public ResponseEntity<Application> insert(@RequestParam(required = true) int userId, @Valid @RequestBody(required = true) Application application) {
    return new ResponseEntity<>(applicationService.insert(application, userId), HttpStatus.OK);
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
  @RequestMapping(value = "/{id}/tags", method = RequestMethod.POST)
  public ResponseEntity<ApplicationTag> addTag(@PathVariable int id, @RequestBody ApplicationTag tag) {
    return new ResponseEntity<>(applicationService.addTag(id, tag), HttpStatus.OK);
  }

  @RequestMapping(value ="/{id}/tags/{tagType}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> removeTag(@PathVariable int id, @PathVariable ApplicationTagType tagType) {
    applicationService.removeTag(id, tagType);
    return new ResponseEntity<>(HttpStatus.OK);
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

  @RequestMapping(value = "/{id}/decision/anonymized", method = RequestMethod.POST)
  public ResponseEntity<Void> storeAnonymizedDecision(@PathVariable int id, @RequestParam("file") MultipartFile file)
      throws IOException {
    decisionDao.storeAnonymizedDecision(id, file.getBytes());
    return new ResponseEntity<>(HttpStatus.OK);
  }


  /**
   * Get the decision PDF for application
   *
   * @param id
   *          application ID
   * @return Decision PDF data
   *
   */
  @GetMapping(value = "/{id}/decision")
  public ResponseEntity<byte[]> getDecision(@PathVariable int id) {
    byte[] bytes = decisionDao.getDecision(id)
        .orElseThrow(() -> new NoSuchEntityException("Decision not found", Integer.toString(id)));
    return new ResponseEntity<>(bytes, HttpStatus.OK);
  }

  @GetMapping(value = "/{id}/decision/anonymized")
  public ResponseEntity<byte[]> getAnonymizedDecision(@PathVariable int id) {
    byte[] bytes = decisionDao.getAnonymizedDecision(id)
        .orElseThrow(() -> new NoSuchEntityException("Decision not found", Integer.toString(id)));
    return new ResponseEntity<>(bytes, HttpStatus.OK);
  }

  @PostMapping(value = "/{id}/decision-distribution-list")
  public ResponseEntity<Void> replaceDecisionDistributionList(
      @PathVariable int id,
      @RequestBody List<DistributionEntry> distributionEntries) {
    distributionEntryDao.replaceEntries(id, distributionEntries);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping(value = "/{id}/decision-distribution-list")
  public ResponseEntity<List<DistributionEntry>> getDecisionDistributionList(@PathVariable int id) {
    return ResponseEntity.ok(distributionEntryDao.findByApplicationId(id));
  }


  /**
   * Get list of applications about to end
   *
   * @param specifiers List of application specifiers
   */
  @PostMapping(value = "/deadline-check")
  public ResponseEntity<List<Application>> deadlineCheck(@RequestBody @Valid DeadlineCheckParams checkParams) {
    return new ResponseEntity<>(applicationService.deadLineCheck(checkParams), HttpStatus.OK);
  }

  /**
   * Mark the given applications having a reminder sent
   *
   * @param applicationIds list of application IDs
   */
  @PostMapping(value = "/reminder-sent")
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
  @GetMapping(value = "/{id}/invoices")
  public ResponseEntity<List<Invoice>> getInvoices(@PathVariable int id) {
    return new ResponseEntity<>(invoiceService.findByApplication(id), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/targetstate")
  public ResponseEntity<Application> setTargetState(@PathVariable Integer id, @RequestBody StatusType targetState) {
    return ResponseEntity.ok(applicationService.setTargetState(id, targetState));
  }

  @PutMapping(value = "/{id}/targetstate/clear")
  public ResponseEntity<Application> clearTargetState(@PathVariable Integer id) {
    return ResponseEntity.ok(applicationService.setTargetState(id, null));
  }

  /**
   * Get list of invoices that are ready to be sent to SAP
   *
   * @return list of invoices
   */
  @GetMapping(value = "/invoices/ready-to-send")
  public ResponseEntity<List<Invoice>> getPendingInvoices() {
    return new ResponseEntity<>(invoiceService.findPending(), HttpStatus.OK);
  }

  /**
   * Mark given invoices as sent.
   *
   * @param invoiceIds list of invoice IDs
   */
  @PostMapping(value = "/invoices/mark-as-sent")
  public ResponseEntity<Void> markInvoicesSent(@RequestBody List<Integer> invoiceIds) {
    invoiceService.markSent(invoiceIds);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/invoices/{id}/release-pending")
  public ResponseEntity<Void> releasePendingInvoice(@PathVariable Integer id) {
    invoiceService.releasePending(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Finds finished applications having one of the given statuses.
   */
  @PostMapping(value = "/finished")
  public ResponseEntity<List<Integer>> findFinishedApplications(@RequestBody DeadlineCheckParams params) {
    return new ResponseEntity<>(applicationService.findFinishedApplications(params), HttpStatus.OK);
  }

  @GetMapping(value = "/activeexcavationannouncements")
  public ResponseEntity<List<Application>> findActiveExcavationAnnouncements() {
    return new ResponseEntity<>(applicationService.findActiveExcavationAnnouncements(), HttpStatus.OK);
  }

  /**
   * Finds finished notes
   */
  @GetMapping(value = "/notes/finished")
  public ResponseEntity<List<Integer>> findFinishedNotes() {
    return new ResponseEntity<>(applicationService.findFinishedNotes(), HttpStatus.OK);
  }

  /**
   * Finds id of the owner (user) of the application.
   */
  @GetMapping(value = "/{id}/owner")
  public ResponseEntity<Integer> getApplicationOwner(@PathVariable Integer id) {
    return new ResponseEntity<>(applicationService.getApplicationOwner(id), HttpStatus.OK);
  }

  /**
   * Finds id of the external owner (user) of the application.
   */
  @GetMapping(value = "/{id}/externalowner")
  public ResponseEntity<Integer> getApplicationExternalOwner(@PathVariable Integer id) {
    return new ResponseEntity<>(applicationService.getApplicationExternalOwner(id), HttpStatus.OK);
  }

  @GetMapping(value = "/external/{externalid}/applicationid")
  public ResponseEntity<Integer> getApplicationIdForExternalId(@PathVariable(value = "externalid") Integer externalId) {
    return new ResponseEntity<>(applicationService.getApplicationIdForExternalId(externalId), HttpStatus.OK);
  }


  @GetMapping(value = "/{id}/handler")
  public ResponseEntity<User> getApplicationHandler(@PathVariable Integer id) {
    return new ResponseEntity<>(applicationService.getApplicationHandler(id), HttpStatus.OK);
  }

  @GetMapping(value = "/{id}/decisionmaker")
  public ResponseEntity<User> getApplicationDecisionMaker(@PathVariable Integer id) {
    return new ResponseEntity<>(applicationService.getApplicationDecisionMaker(id), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/invoicerecipient")
  public ResponseEntity<Void> setInvoiceRecipient(@PathVariable int id, @RequestParam(value = "invoicerecipientid", required = false) final Integer invoiceRecipientId,
      @RequestParam("userid") final Integer userId) {
    applicationService.setInvoiceRecipient(id, invoiceRecipientId, userId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping(value = "/{id}/invoicerecipient")
  public ResponseEntity<Customer> getInvoiceRecipient(@PathVariable int id) {
    return ResponseEntity.ok(applicationService.getInvoiceRecipient(id));
  }

  @GetMapping(value = "/{id}/replacing")
  public ResponseEntity<Integer> getReplacingApplicationId(@PathVariable int id) {
    return new ResponseEntity<>(applicationService.getReplacingApplicationId(id), HttpStatus.OK);
  }

  @DeleteMapping(value = "/{id}/clientapplicationdata")
  public ResponseEntity<Void> removeClientApplicationData(@PathVariable Integer id) {
    applicationService.removeClientApplicationData(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping(value = "/{id}/customers")
  public ResponseEntity<List<CustomerWithContacts>> getApplicationCustomers(@PathVariable Integer id) {
    return new ResponseEntity<>(applicationService.getApplicationCustomers(id), HttpStatus.OK);
  }

  @GetMapping(value = "/{id}/version")
  public ResponseEntity<Integer> getVersion(@PathVariable int id) {
    return new ResponseEntity<>(applicationService.getVersion(id), HttpStatus.OK);
  }

  @PostMapping(value = "/{id}/ownernotification")
  public ResponseEntity<Void> addOwnerNotification(@PathVariable Integer id) {
    applicationService.addOwnerNotification(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping(value = "/{id}/ownernotification")
  public ResponseEntity<Void> removeOwnerNotification(@PathVariable Integer id) {
    applicationService.removeOwnerNotification(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Search decisions
   */
  @PostMapping(value = "/decisions/search")
  public ResponseEntity<List<DocumentSearchResult>> searchDecisions(@RequestBody DocumentSearchCriteria searchCriteria) {
    return ResponseEntity.ok(decisionDao.searchDecisions(searchCriteria));
  }

}
