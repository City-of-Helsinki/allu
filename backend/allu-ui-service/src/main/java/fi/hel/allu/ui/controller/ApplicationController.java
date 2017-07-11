package fi.hel.allu.ui.controller;


import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.AttachmentService;
import fi.hel.allu.servicecore.service.DecisionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApplicationController {
  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;

  @Autowired
  private AttachmentService attachmentService;

  @Autowired
  private DecisionService decisionService;

  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION')")
  public ResponseEntity<ApplicationJson> create(@Valid @RequestBody ApplicationJson applicationJson) {
    return new ResponseEntity<>(applicationServiceComposer.createApplication(applicationJson), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> update(@PathVariable int id, @Valid @RequestBody(required = true) ApplicationJson
      applicationJson) {
    return new ResponseEntity<>(applicationServiceComposer.updateApplication(id, applicationJson), HttpStatus.OK);
  }

  /**
   * Delete a note from database.
   *
   * @param id note application's database ID
   */
  @RequestMapping(value = "/note/{id}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> deleteNote(@PathVariable int id) {
    applicationServiceComposer.deleteNote(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/handler/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> updateApplicationHandler(@PathVariable int id, @RequestBody(required = true) List<Integer> applicationsIds) {
    applicationServiceComposer.updateApplicationHandler(id, applicationsIds);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/handler/remove", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> removeApplicationHandler(@RequestBody(required = true) List<Integer> applicationsIds) {
    applicationServiceComposer.removeApplicationHandler(applicationsIds);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<ApplicationJson> findByIdentifier(@PathVariable int id) {
    return new ResponseEntity<>(applicationServiceComposer.findApplicationById(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/search_location", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ApplicationJson>> findByLocation(@RequestBody final LocationQueryJson query) {
    return new ResponseEntity<>(applicationServiceComposer.findApplicationByLocation(query), HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ApplicationJson>> search(@Valid @RequestBody QueryParametersJson queryParameters) {
    return new ResponseEntity<>(applicationServiceComposer.search(queryParameters), HttpStatus.OK);
  }

  /**
   * Get change items for an application
   *
   * @param applicationId
   *          application ID
   * @return list of changes ordered from oldest to newest
   */
  @RequestMapping(value = "/{applicationId}/history", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ApplicationChangeJson>> getChanges(@PathVariable Integer applicationId) {
    return new ResponseEntity<>(applicationServiceComposer.getChanges(applicationId), HttpStatus.OK);
  }

  // Attachment API
  /**
   * Add attachments to application
   *
   * @param id
   *          Application ID
   * @param infos
   *          Array of attachment's infos
   * @param files
   *          Matching files (files[0] for infos[0] etc.)
   * @return Updated result infos
   * @throws IOException
   * @throws IllegalArgumentException
   */
  @RequestMapping(value = "/{id}/attachments", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<AttachmentInfoJson>> addAttachments(
      @PathVariable int id,
      @RequestPart("meta") @Valid AttachmentInfoJson[] infos, @RequestPart("file") MultipartFile[] files)
      throws IllegalArgumentException, IOException {
    return new ResponseEntity<>(attachmentService.addAttachments(id, infos, files), HttpStatus.CREATED);
  }

  /**
   * Read attachment info by ID
   *
   * @param id
   *          attachment ID
   * @return attachment info for the ID
   */
  @RequestMapping(value = "/attachments/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<AttachmentInfoJson> readAttachmentInfo(@PathVariable int id) {
    return new ResponseEntity<>(attachmentService.getAttachment(id), HttpStatus.OK);
  }

  /**
   * Update existing attachment info
   *
   * @param id
   *          attachment ID
   * @param attachmentInfoJson
   * @return
   */
  @RequestMapping(value = "/attachments/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<AttachmentInfoJson> updateAttachmentInfo(@PathVariable int id,
      @Valid @RequestBody AttachmentInfoJson attachmentInfoJson) {
    return new ResponseEntity<>(attachmentService.updateAttachment(id, attachmentInfoJson), HttpStatus.OK);
  }

  /**
   * Delete attachment
   *
   * @param applicationId   Id of the application whose attachment will be deleted.
   * @param attachmentId    Id of the attachment to be deleted.
   * @return
   */
  @RequestMapping(value = "/{applicationId}/attachments/{attachmentId}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<Void> deleteAttachment(
      @PathVariable int applicationId,
      @PathVariable int attachmentId) {
    attachmentService.deleteAttachment(applicationId, attachmentId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Get the attachment's data
   *
   * @param attachmentId
   *          attachment's ID
   * @return The attachment's data
   */
  @RequestMapping(value = "/attachments/{attachmentId}/data", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<byte[]> getAttachmentData(@PathVariable int attachmentId) {
    byte[] bytes = attachmentService.getAttachmentData(attachmentId);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.parseMediaType("application/octet-stream"));
    return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
  }

  /**
   * Read default attachment info by ID.
   *
   * @param id          attachment ID
   * @return attachment info for the ID.
   */
  @RequestMapping(value = "/default-attachments/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<DefaultAttachmentInfoJson> readDefaultAttachmentInfo(@PathVariable int id) {
    return new ResponseEntity<>(attachmentService.getDefaultAttachment(id), HttpStatus.OK);
  }

  /**
   * Read all default attachments.
   *
   * @return attachment info of all default attachments.
   */
  @RequestMapping(value = "/default-attachments", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<DefaultAttachmentInfoJson>> readDefaultAttachmentInfos() {
    return new ResponseEntity<>(attachmentService.getDefaultAttachments(), HttpStatus.OK);
  }

  /**
   * Read default attachments for given application type.
   *
   * @return attachment info of all default attachments.
   */
  @RequestMapping(value = "/default-attachments/applicationType/{applicationType}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<DefaultAttachmentInfoJson>> readDefaultAttachmentInfos(@PathVariable ApplicationType applicationType) {
    return new ResponseEntity<>(attachmentService.getDefaultAttachmentsByApplicationType(applicationType), HttpStatus.OK);
  }

  /**
   * Get the decision PDF for application
   *
   * @param applicationId
   *          the application's Id
   * @return The PDF data
   */
  @RequestMapping(value = "/{applicationId}/decision", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<byte[]> getDecision(@PathVariable int applicationId) {
    byte[] bytes = decisionService.getDecision(applicationId);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.parseMediaType("application/pdf"));
    return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
  }

  /**
   * Send the decision PDF for application as email to an updated distribution list.
   *
   * @param applicationId       the application's Id.
   * @param decisionDetailsJson Details of the decision.
   */
  @RequestMapping(value = "/{applicationId}/decision/send", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<Void> sendDecision(
      @PathVariable int applicationId,
      @RequestBody DecisionDetailsJson decisionDetailsJson) {
    applicationServiceComposer.sendDecision(applicationId, decisionDetailsJson);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Get the decision preview PDF for application
   *
   * @param applicationId
   *          the application's Id
   * @return The PDF data
   */
  @RequestMapping(value = "/{applicationId}/decision-preview", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<byte[]> getDecisionPreview(@PathVariable int applicationId) {
    ApplicationJson applicationJson = applicationServiceComposer.findApplicationById(applicationId);
    byte[] bytes = decisionService.getDecisionPreview(applicationJson);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.parseMediaType("application/pdf"));
    return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
  }

  /**
   * Get the invoice rows for an application
   *
   * @param id
   *          the application ID
   * @return the invoice rows for the application
   */
  @RequestMapping(value = "/{id}/invoice-rows", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<InvoiceRow>> getInvoiceRows(@PathVariable int id) {
    return new ResponseEntity<>(applicationServiceComposer.getInvoiceRows(id), HttpStatus.OK);
  }

  /**
   * Set the manual invoice rows for an application
   *
   * @param id the application ID
   * @param invoiceRows the invoice rows to store. Only rows that are marked as
   *          manually set will be used
   * @return the new invoice rows for the application
   */
  @RequestMapping(value = "/{id}/invoice-rows", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<InvoiceRow>> setInvoiceRows(@PathVariable int id,
      @Valid @RequestBody List<InvoiceRow> invoiceRows) {
    return new ResponseEntity<>(applicationServiceComposer.setInvoiceRows(id, invoiceRows), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/tags", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<ApplicationTagJson>> updateTags(@PathVariable int id,
     @Valid @RequestBody List<ApplicationTagJson> tags) {
    return new ResponseEntity<>(applicationServiceComposer.updateTags(id, tags), HttpStatus.OK);
  }
}
