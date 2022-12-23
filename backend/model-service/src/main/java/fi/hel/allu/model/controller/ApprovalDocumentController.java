package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.DocumentSearchCriteria;
import fi.hel.allu.common.domain.DocumentSearchResult;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.ApprovalDocumentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApprovalDocumentController {

  private final ApprovalDocumentDao approvalDocumentDao;

  @Autowired
  public ApprovalDocumentController(ApprovalDocumentDao approvalDocumentDao) {
    this.approvalDocumentDao = approvalDocumentDao;
  }

  @GetMapping(value = "/{applicationId}/approvalDocument/{type}")
  public ResponseEntity<byte[]> getApprovalDocument(@PathVariable Integer applicationId, @PathVariable ApprovalDocumentType type) {
    final byte[] bytes = approvalDocumentDao.getApprovalDocument(applicationId, type)
        .orElseThrow(() -> new NoSuchEntityException("approvalDocument.notFound", applicationId));
    return ResponseEntity.ok(bytes);
  }

  @GetMapping(value = "/{applicationId}/approvalDocument/{type}/anonymized")
  public ResponseEntity<byte[]> getAnonymizedDocument(@PathVariable Integer applicationId, @PathVariable ApprovalDocumentType type) {
    final byte[] bytes = approvalDocumentDao.getAnonymizedDocument(applicationId, type)
        .orElseThrow(() -> new NoSuchEntityException("approvalDocument.notFound", applicationId));
    return ResponseEntity.ok(bytes);
  }


  @PostMapping(value = "/{applicationId}/approvalDocument/{type}")
  public ResponseEntity<Void> storeApprovalDocument(
      @PathVariable Integer applicationId,
      @PathVariable ApprovalDocumentType type,
      @RequestParam("file") MultipartFile file) throws IOException {
    approvalDocumentDao.storeApprovalDocument(applicationId, type, file.getBytes());
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri());
    return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
  }

  @PostMapping(value = "/{applicationId}/approvalDocument/{type}/anonymized")
  public ResponseEntity<Void> storeAnonymizedDocument(
      @PathVariable Integer applicationId,
      @PathVariable ApprovalDocumentType type,
      @RequestParam("file") MultipartFile file) throws IOException {
    approvalDocumentDao.storeAnonymizedDocument(applicationId, type, file.getBytes());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Search approval documents of given type.
   */
  @PostMapping(value = "/approvalDocument/{type}/search")
  public ResponseEntity<List<DocumentSearchResult>> searchApprovalDocuments(@PathVariable ApprovalDocumentType type,
      @RequestBody DocumentSearchCriteria searchCriteria) {
    return ResponseEntity.ok(approvalDocumentDao.searchApprovalDocuments(searchCriteria, type));
  }
}