package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.TerminationInfo;
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

  @Autowired
  public TerminationController(TerminationService terminationService) {
    this.terminationService = terminationService;
  }

  @RequestMapping(value = "/{applicationId}/termination/info", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<TerminationInfo> getTerminationInfo(@PathVariable int applicationId) {
    return ResponseEntity.ok(terminationService.getTerminationInfo(applicationId));
  }

  @RequestMapping(value = "/{applicationId}/termination/info", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<TerminationInfo> insertTerminationInfo(@PathVariable int applicationId,
       @Valid @RequestBody TerminationInfo terminationInfo) {
    return ResponseEntity.ok(terminationService.insertTerminationInfo(applicationId, terminationInfo));
  }

  @RequestMapping(value = "/{applicationId}/termination/info", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<TerminationInfo> updateTerminationInfo(@PathVariable int applicationId,
       @Valid @RequestBody TerminationInfo terminationInfo) {
    return ResponseEntity.ok(terminationService.updateTerminationInfo(applicationId, terminationInfo));
  }

  @RequestMapping(value = "/{applicationId}/termination", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<byte[]> getTermination(@PathVariable Integer applicationId) {
    return pdfResult(terminationService.getTermination(applicationId));
  }

  private ResponseEntity<byte[]> pdfResult(byte[] data) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_PDF);
    return new ResponseEntity<>(data, httpHeaders, HttpStatus.OK);
  }
}