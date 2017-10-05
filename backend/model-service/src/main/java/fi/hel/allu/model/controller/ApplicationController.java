package fi.hel.allu.model.controller;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.*;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.service.ApplicationService;

import org.springframework.beans.factory.annotation.Autowired;
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

  private ApplicationService applicationService;

  private AttachmentDao attachmentDao;

  private DecisionDao decisionDao;

  private ChargeBasisDao chargeBasisDao;

  private HistoryDao historyDao;

  private DistributionEntryDao distributionEntryDao;

  private InvoiceDao invoiceDao;
  @Autowired
  public ApplicationController(
      ApplicationService applicationService,
      AttachmentDao attachmentDao,
      DecisionDao decisionDao,
      ChargeBasisDao chargeBasisDao,
      HistoryDao historyDao,
      DistributionEntryDao distributionEntryDao,
      InvoiceDao invoiceDao) {
    this.applicationService = applicationService;
    this.attachmentDao = attachmentDao;
    this.decisionDao = decisionDao;
    this.chargeBasisDao = chargeBasisDao;
    this.historyDao = historyDao;
    this.distributionEntryDao = distributionEntryDao;
    this.invoiceDao = invoiceDao;
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
   * Updates handler of given applications.
   *
   * @param   handlerId     New handler set to the applications.
   * @param   applications  Applications whose handler is updated.
   */
  @RequestMapping(value = "/handler/{handlerId}", method = RequestMethod.PUT)
  public ResponseEntity<Void> updateHandler(@PathVariable int handlerId, @RequestBody List<Integer> applications) {
    applicationService.updateHandler(handlerId, applications);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Removes handler of given applications.
   *
   * @param   applications  Applications whose handler is removed.
   */
  @RequestMapping(value = "/handler/remove", method = RequestMethod.PUT)
  public ResponseEntity<Void> removeHandler(@RequestBody List<Integer> applications) {
    applicationService.removeHandler(applications);
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

  /**
   * Get the charge basis entries for an application
   *
   * @param id the application ID
   * @return the charge basis entries for the application
   */
  @RequestMapping(value = "/{id}/charge-basis", method = RequestMethod.GET)
  public ResponseEntity<List<ChargeBasisEntry>> getChargeBasis(@PathVariable int id) {
    return new ResponseEntity<>(chargeBasisDao.getChargeBasis(id), HttpStatus.OK);
  }

  /**
   * Set the charge basis entries for an application
   *
   * @param id the application ID
   * @param chargeBasisEntries the charge basis entries for the application.
   *          Only the entries that are marked as manually set will be stored.
   * @return the charge basis entries for the application
   */
  @RequestMapping(value = "/{id}/charge-basis", method = RequestMethod.PUT)
  public ResponseEntity<List<ChargeBasisEntry>> setManualChargeBasis(@PathVariable int id,
      @RequestBody List<ChargeBasisEntry> chargeBasisEntries) {
    return new ResponseEntity<>(applicationService.setManualChargeBasis(id, chargeBasisEntries), HttpStatus.OK);
  }

  /**
   * Get application history
   *
   * @param id the application's database ID
   * @return list of changes for the application
   */
  @RequestMapping(value = "/{id}/history", method = RequestMethod.GET)
  public ResponseEntity<List<ChangeHistoryItem>> getChanges(@PathVariable int id) {
    return new ResponseEntity<>(historyDao.getApplicationHistory(id), HttpStatus.OK);
  }

  /**
   * Add an application history entry
   * @param id The application's database ID
   * @param change the change item to add
   */
  @RequestMapping(value = "/{id}/history", method = RequestMethod.POST)
  public ResponseEntity<Void> addChange(@PathVariable int id, @RequestBody ChangeHistoryItem change) {
    historyDao.addApplicationChange(id, change);
    return new ResponseEntity<>(HttpStatus.OK);
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
    return new ResponseEntity<>(invoiceDao.findByApplication(id), HttpStatus.OK);
  }
}
