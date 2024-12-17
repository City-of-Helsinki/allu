package fi.hel.allu.ui.controller;


import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.AnonymizableApplication;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.AttachmentService;
import fi.hel.allu.servicecore.service.InvoiceService;
import fi.hel.allu.servicecore.validation.ApplicationGeometryValidator;
import fi.hel.allu.ui.security.ApplicationSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
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
  private InvoiceService invoiceService;

  @Autowired
  private ApplicationGeometryValidator geometryValidator;

  @Autowired
  private ApplicationSecurityService applicationSecurityService;


  @InitBinder("applicationJson")
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(geometryValidator);
  }

  @PostMapping
  @PreAuthorize("@applicationSecurityService.canCreate(#applicationJson.getType())")
  public ResponseEntity<ApplicationJson> create(@Valid @RequestBody ApplicationJson applicationJson) {
    return new ResponseEntity<>(applicationServiceComposer.createApplication(applicationJson), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> update(@PathVariable int id, @Valid @RequestBody(required = true) ApplicationJson
      applicationJson) {
    return new ResponseEntity<>(applicationServiceComposer.updateApplication(id, applicationJson), HttpStatus.OK);
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<ApplicationJson>> fetch(@RequestParam("ids") final List<Integer> ids) {
    return new ResponseEntity<>(applicationServiceComposer.findApplicationsByIds(ids), HttpStatus.OK);
  }

  /**
   * Anonymize applications by ids
   * @param ids of applications
   * @return no content
   */
  @PatchMapping(value = "/anonymize")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<Void> anonymizeApplicationsByIds(@RequestBody List<Integer> ids) {
    return ResponseEntity.noContent().build();
  }

  /**
   * Get anonymizable/"deletable" applications
   * @return list of anonymizable/"deletable" applications
   */
  @GetMapping(value = "/anonymizable")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  public ResponseEntity<List<AnonymizableApplication>> getAnonymizableApplications() {
    return ResponseEntity.ok(applicationServiceComposer.getAnonymizableApplications());
  }

  /**
   * Delete a note from database.
   *
   * @param id note application's database ID
   */
  @DeleteMapping(value = "/note/{id}")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> deleteNote(@PathVariable int id) {
    applicationServiceComposer.deleteNote(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/owner/{id}")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> updateApplicationOwner(@PathVariable int id, @RequestBody(required = true) List<Integer> applicationsIds) {
    applicationServiceComposer.updateApplicationOwner(id, applicationsIds, true);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/owner/remove")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> removeApplicationOwner(@RequestBody(required = true) List<Integer> applicationsIds) {
    applicationServiceComposer.removeApplicationOwner(applicationsIds, true);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping(value = "/{id}")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<ApplicationJson> findByIdentifier(@PathVariable int id) {
    return new ResponseEntity<>(applicationServiceComposer.findApplicationById(id), HttpStatus.OK);
  }

  @PostMapping(value = "/search")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<Page<ApplicationES>> search(@Valid @RequestBody ApplicationQueryParameters queryParameters,
      @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.DEFAULT_PAGE_SIZE, sort = "creationTime", direction = Direction.DESC)
      Pageable pageRequest,
      @RequestParam(defaultValue = "false") Boolean matchAny) {
    return new ResponseEntity<>(applicationServiceComposer.search(queryParameters, pageRequest, matchAny), HttpStatus.OK);
  }

  /**
   * Get change items for an application
   *
   * @param applicationId
   *          application ID
   * @return list of changes ordered from oldest to newest
   */
  @GetMapping(value = "/{applicationId}/history")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ChangeHistoryItemJson>> getChanges(@PathVariable Integer applicationId) {
    return new ResponseEntity<>(applicationServiceComposer.getChanges(applicationId), HttpStatus.OK);
  }

  /**
   * Get status application has been (ignoring replaced applications)
   *
   * @param applicationId application ID
   * @return list of status application has been
   */
  @GetMapping(value = "/{applicationId}/statushistory")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<StatusType>> getStatusHistory(@PathVariable int applicationId) {
    return ResponseEntity.ok(applicationServiceComposer.getStatusChanges(applicationId));
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
  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION', 'ROLE_PROCESS_APPLICATION', 'ROLE_DECLARANT', 'ROLE_MANAGE_SURVEY')")
  public ResponseEntity<List<AttachmentInfoJson>> addAttachments(
      @PathVariable int id,
      @RequestPart("meta") @Valid AttachmentInfoJson[] infos, @RequestPart("file") MultipartFile[] files)
      throws IllegalArgumentException, IOException {
    return new ResponseEntity<>(attachmentService.addAttachments(id, infos, files), HttpStatus.CREATED);
  }

  /**
   * Fetches attachments for application
   * @param id application's id
   * @return List of attachments for specified application
   */
  @RequestMapping(value = "/{id}/attachments", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<AttachmentInfoJson>> getAttachments(@PathVariable int id) {
    return new ResponseEntity<>(attachmentService.findAttachmentsForApplication(id), HttpStatus.OK);
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
  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION', 'ROLE_PROCESS_APPLICATION', 'ROLE_DECLARANT', 'ROLE_MANAGE_SURVEY')")
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
  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION', 'ROLE_PROCESS_APPLICATION', 'ROLE_DECLARANT', 'ROLE_MANAGE_SURVEY')")
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
    AttachmentInfoJson info = attachmentService.getAttachment(attachmentId);
    byte[] bytes = attachmentService.getAttachmentData(attachmentId);
    HttpHeaders httpHeaders = new HttpHeaders();
    try {
      httpHeaders.setContentType(MediaType.parseMediaType(info.getMimeType()));
    } catch (InvalidMediaTypeException e) {
      httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    }
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

  @RequestMapping(value = "/{id}/tags", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<ApplicationTagJson>> updateTags(@PathVariable int id,
     @Valid @RequestBody List<ApplicationTagJson> tags) {
    return new ResponseEntity<>(applicationServiceComposer.updateTags(id, tags), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/tags", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<ApplicationTagJson>> getTags(@PathVariable int id) {
    return new ResponseEntity<>(applicationServiceComposer.findTags(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/tags", method = RequestMethod.POST)
  @PreAuthorize("@applicationSecurityService.canModifyTag(#tag.getType())")
  public ResponseEntity<ApplicationTagJson> addTag(@PathVariable int id, @Valid @RequestBody ApplicationTagJson tag) {
    return new ResponseEntity<>(applicationServiceComposer.addTag(id, tag), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/tags/{tagType}", method = RequestMethod.DELETE)
  @PreAuthorize("@applicationSecurityService.canModifyTag(#tagType)")
  public ResponseEntity<ApplicationTagJson> removeTag(@PathVariable int id, @PathVariable ApplicationTagType tagType) {
    applicationServiceComposer.removeTag(id, tagType);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Get the invoice data for application
   *
   * @param id The appication's database ID
   * @return all invoices for given application ID
   */
  @RequestMapping(value = "/{id}/invoices", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW', 'ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<InvoiceJson>> getInvoices(@PathVariable int id) {
    return new ResponseEntity<>(invoiceService.findByApplication(id), HttpStatus.OK);
  }

  /**
   * Replaces (creates a copy) application with given application ID.
   */
  @RequestMapping(value = "/{id}/replace",  method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> replace(@PathVariable int id) {
    return new ResponseEntity<>(applicationServiceComposer.replaceApplication(id), HttpStatus.OK);
  }

  /**
   * Loads given application's replacement history.
   * History include applications which were replaced and those which given
   * application replaces
   */
  @RequestMapping(value = "/{id}/replacementHistory",  method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ApplicationIdentifierJson>> replacementHistory(@PathVariable int id) {
    return new ResponseEntity<>(applicationServiceComposer.replacementHistory(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/invoicerecipient", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> setInvoiceRecipient(@PathVariable int id, @RequestParam(value = "invoicerecipientid", required = false) final Integer invoiceRecipientId) {
    applicationServiceComposer.setInvoiceRecipient(id, invoiceRecipientId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/invoicerecipient", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<CustomerJson> getInvoiceRecipient(@PathVariable int id) {
    return ResponseEntity.ok(applicationServiceComposer.findInvoiceRecipientJson(id));
  }

  @RequestMapping(value = "/{id}/clientapplicationdata", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> removeClientApplicationData(@PathVariable Integer id) {
    return ResponseEntity.ok(applicationServiceComposer.removeClientApplicationData(id));
  }

  @RequestMapping(value = "/{id}/ownernotification", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> addOwnerNotification(@PathVariable Integer id) {
    applicationServiceComposer.addOwnerNotification(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/ownernotification", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> removeOwnerNotification(@PathVariable Integer id) {
    applicationServiceComposer.removeOwnerNotification(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
