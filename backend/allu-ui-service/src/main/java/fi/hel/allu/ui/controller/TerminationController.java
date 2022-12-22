package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.TerminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/applications")
public class TerminationController {

  private final TerminationService terminationService;
  private final ApplicationServiceComposer applicationServiceComposer;

  @Autowired
  public TerminationController(TerminationService terminationService, ApplicationServiceComposer applicationServiceComposer) {
    this.terminationService = terminationService;
    this.applicationServiceComposer = applicationServiceComposer;
  }

  @GetMapping(value = "/{applicationId}/termination/info")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<TerminationInfo> getTerminationInfo(@PathVariable int applicationId) {
    return ResponseEntity.ok(terminationService.getTerminationInfo(applicationId));
  }

  @PostMapping(value = "/{applicationId}/termination/info")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<TerminationInfo> insertTerminationInfo(@PathVariable int applicationId,
       @Valid @RequestBody TerminationInfo terminationInfo) {
    return ResponseEntity.ok(terminationService.insertTerminationInfo(applicationId, terminationInfo));
  }

  @PutMapping(value = "/{applicationId}/termination/info")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<TerminationInfo> updateTerminationInfo(@PathVariable int applicationId,
       @Valid @RequestBody TerminationInfo terminationInfo) {
    return ResponseEntity.ok(terminationService.updateTerminationInfo(applicationId, terminationInfo));
  }

  @DeleteMapping(value = "/{applicationId}/termination/info")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<Boolean> removeTerminationInfo(@PathVariable int applicationId) {
    terminationService.removeTerminationInfo(applicationId);
    return ResponseEntity.ok(true);
  }

  @GetMapping(value = "/{applicationId}/termination")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<byte[]> getTermination(@PathVariable Integer applicationId) {
    ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
    return pdfResult(terminationService.getTermination(application));
  }

  private ResponseEntity<byte[]> pdfResult(byte[] data) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_PDF);
    return new ResponseEntity<>(data, httpHeaders, HttpStatus.OK);
  }
}