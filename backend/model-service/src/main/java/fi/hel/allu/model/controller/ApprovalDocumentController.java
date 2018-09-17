package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.ApprovalDocumentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@RestController
@RequestMapping("/applications")
public class ApprovalDocumentController {

  private final ApprovalDocumentDao approvalDocumentDao;

  @Autowired
  public ApprovalDocumentController(ApprovalDocumentDao approvalDocumentDao) {
    this.approvalDocumentDao = approvalDocumentDao;
  }

  @RequestMapping(value = "/{applicationId}/approvalDocument/{type}", method = RequestMethod.GET)
  public ResponseEntity<byte[]> getApprovalDocument(@PathVariable Integer applicationId, @PathVariable ApprovalDocumentType type) {
    final byte[] bytes = approvalDocumentDao.getApprovalDocument(applicationId, type)
        .orElseThrow(() -> new NoSuchEntityException("approvalDocument.notFound", applicationId));
    return ResponseEntity.ok(bytes);
  }

  @RequestMapping(value = "/{applicationId}/approvalDocument/{type}", method = RequestMethod.POST)
  public ResponseEntity<Void> storeApprovalDocument(
      @PathVariable Integer applicationId,
      @PathVariable ApprovalDocumentType type,
      @RequestParam("file") MultipartFile file) throws IOException {
    approvalDocumentDao.storeApprovalDocument(applicationId, type, file.getBytes());
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri());
    return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
  }
}
