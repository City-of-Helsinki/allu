package fi.hel.allu.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.ContractInfo;
import fi.hel.allu.servicecore.domain.ContractApprovalInfo;
import fi.hel.allu.servicecore.domain.StatusChangeInfoJson;
import fi.hel.allu.servicecore.service.ContractService;

@RestController
@RequestMapping("/applications")
public class ContractController {

  @Autowired
  private ContractService contractService;


  /**
   * Gets preview of contract pdf.
   */
  @RequestMapping(value = "/{id}/contract/preview", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<byte[]> getContractPreview(@PathVariable Integer id) {
    byte[] data = contractService.getContractPreview(id);
    return pdfResult(data);
  }

  /**
   * Gets previously created contract from database.
   */
  @RequestMapping(value = "/{id}/contract", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<byte[]> getContract(@PathVariable Integer id) {
    byte[] data = contractService.getContract(id);
    return pdfResult(data);
  }

  /**
   * Generates contract proposal PDF and moves application to waiting for contract approval state.
   */
  @RequestMapping(value = "/{id}/contract/proposal", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<byte[]> createContractProposal(@PathVariable int id) {
    return pdfResult(contractService.createContractProposal(id));
  }

  /**
   * Generates contract not requiring signing and moves application directly to waiting for decision state.
   * (Signed contract  must be as attachment or frame agreement should exist).
   */
  @RequestMapping(value = "/{id}/contract/approved", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<byte[]> createApprovedContract(@PathVariable int id, @RequestBody ContractApprovalInfo contractApprovalInfo) {
    return pdfResult(contractService.createApprovedContract(id, contractApprovalInfo));
  }

  /**
   * Gets contract information for given application ID
   */
  @RequestMapping(value = "/{id}/contract/info", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<ContractInfo> getContractInfo(@PathVariable int id) {
    return ResponseEntity.ok(contractService.getContractInfo(id));
  }

  protected ResponseEntity<byte[]> pdfResult(byte[] data) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.parseMediaType("application/pdf"));
    return new ResponseEntity<>(data, httpHeaders, HttpStatus.OK);
  }



}
