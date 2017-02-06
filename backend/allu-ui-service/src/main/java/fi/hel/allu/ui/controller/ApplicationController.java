package fi.hel.allu.ui.controller;


import fi.hel.allu.model.domain.CableInfoText;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.AttachmentInfoJson;
import fi.hel.allu.ui.domain.LocationQueryJson;
import fi.hel.allu.ui.domain.QueryParametersJson;
import fi.hel.allu.ui.service.ApplicationServiceComposer;
import fi.hel.allu.ui.service.AttachmentService;
import fi.hel.allu.ui.service.DecisionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
   * @param id  attachment ID
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
   * Generate decision PDF for application
   *
   * @param applicationId
   *          the application's Id
   * @return Response with Location header pointing to generated PDF
   * @throws IOException
   */
  @RequestMapping(value = "/{applicationId}/decision", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_DECISION')")
  public ResponseEntity<Void> generateDecision(@PathVariable int applicationId) throws IOException {
    ApplicationJson applicationJson = applicationServiceComposer.findApplicationById(applicationId);
    decisionService.generateDecision(applicationId, applicationJson);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri());
    return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
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
   * Get standard texts for cable info
   *
   * @return all CableIOnfoTexts in a list
   */
  @RequestMapping(value = "/cable-info/texts", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<CableInfoText>> getCableInfoTexts() {
    return new ResponseEntity<>(applicationServiceComposer.getCableInfoTexts(), HttpStatus.OK);
  }

  /**
   * Add a standard text for cable infos
   *
   * @param cableInfoText the new CableInfoText to add -- the ID field will be ignored.
   * @return the new CableInfoText, with ID
   */
  @RequestMapping(value = "/cable-info/texts", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<CableInfoText> addCableInfoText(@RequestBody CableInfoText cableInfoText) {
    return new ResponseEntity<>(
        applicationServiceComposer.createCableInfoText(cableInfoText.getCableInfoType(), cableInfoText.getTextValue()),
        HttpStatus.OK);
  }

  /**
   * Update a standard text for cable infos
   *
   * @param id ID of the text to update
   * @param cableInfoText the new contents for the info -- only the textValue field is used
   * @return the updated CableInfoText
   */
  @RequestMapping(value = "/cable-info/texts/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<CableInfoText> updateCableInfoText(@PathVariable int id,
      @RequestBody CableInfoText cableInfoText) {
    return new ResponseEntity<>(
        applicationServiceComposer.updateCableInfoText(id, cableInfoText.getTextValue()),
        HttpStatus.OK);
  }

  /**
   * Delete a cable info standard text
   *
   * @param id the ID of the text to remove
   * @return
   */
  @RequestMapping(value = "/cable-info/texts/{id}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> deleteCableInfoText(@PathVariable int id) {
    applicationServiceComposer.deleteCableInfoText(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Get the invoice rows for an application
   *
   * @param id the application ID
   * @return the invoice rows for the application
   */
  @RequestMapping(value = "{id}/invoice-rows", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<InvoiceRow>> getInvoiceRows(@PathVariable int id) {
    return new ResponseEntity<>(applicationServiceComposer.getInvoiceRows(id), HttpStatus.OK);
  }

}
