package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.servicecore.service.ApprovalDocumentService;
import fi.hel.allu.servicecore.service.ChargeBasisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/applications")
public class ApprovalDocumentController {

  private final ApprovalDocumentService approvalDocumentService;
  private final ChargeBasisService chargeBasisService;

  @Autowired
  public ApprovalDocumentController(ApprovalDocumentService approvalDocumentService,
      ChargeBasisService chargeBasisService) {
    this.approvalDocumentService = approvalDocumentService;
    this.chargeBasisService = chargeBasisService;
  }

  @RequestMapping(value = "/{applicationId}/approvalDocument/{type}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<byte[]> getApprovalDocument(@PathVariable Integer applicationId, @PathVariable ApprovalDocumentType type) {
    return pdfResult(approvalDocumentService.getApprovalDocument(applicationId, type, chargeBasisService.getUnlockedChargeBasis(applicationId)));
  }

  protected ResponseEntity<byte[]> pdfResult(byte[] data) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_PDF);
    return new ResponseEntity<>(data, httpHeaders, HttpStatus.OK);
  }
}
