package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.ContractInfo;
import fi.hel.allu.common.types.CommentType;
import fi.hel.allu.servicecore.domain.CommentJson;
import fi.hel.allu.servicecore.domain.ContractApprovalInfo;
import fi.hel.allu.servicecore.service.CommentService;
import fi.hel.allu.servicecore.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
public class ContractController {

  @Autowired
  private ContractService contractService;

  @Autowired
  private CommentService commentService;

  /**
   * Gets preview of contract pdf.
   */
  @GetMapping(value = "/{id}/contract/preview")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<byte[]> getContractPreview(@PathVariable Integer id) {
    byte[] data = contractService.getContractPreview(id);
    return pdfResult(data);
  }

  /**
   * Gets previously created contract from database.
   */
  @GetMapping(value = "/{id}/contract")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<byte[]> getContract(@PathVariable Integer id) {
    byte[] data = contractService.getContract(id);
    return pdfResult(data);
  }

  /**
   * Generates contract proposal PDF and moves application to waiting for contract approval state.
   */
  @PostMapping(value = "/{id}/contract/proposal")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<byte[]> createContractProposal(@PathVariable int id) {
    return pdfResult(contractService.createContractProposal(id));
  }

  /**
   * Generates contract not requiring signing and moves application directly to waiting for decision state.
   * (Signed contract  must be as attachment or frame agreement should exist).
   */
  @PostMapping(value = "/{id}/contract/approved")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<byte[]> createApprovedContract(@PathVariable int id, @RequestBody ContractApprovalInfo contractApprovalInfo) {
    return pdfResult(contractService.createApprovedContract(id, contractApprovalInfo));
  }

  /**
   * Gets contract information for given application ID
   */
  @GetMapping(value = "/{id}/contract/info")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<ContractInfo> getContractInfo(@PathVariable int id) {
    return ResponseEntity.ok(contractService.getContractInfo(id));
  }

  @PostMapping(value = "/{id}/contract/rejected")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> rejectContract(@PathVariable Integer id, @RequestBody String rejectReason) {
    CommentJson comment = new CommentJson(CommentType.INTERNAL, rejectReason);
    commentService.addApplicationComment(id, comment);
    contractService.rejectContractProposal(id, rejectReason);
    return new ResponseEntity<Void>(HttpStatus.OK);
  }

  protected ResponseEntity<byte[]> pdfResult(byte[] data) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.parseMediaType("application/pdf"));
    return new ResponseEntity<>(data, httpHeaders, HttpStatus.OK);
  }



}