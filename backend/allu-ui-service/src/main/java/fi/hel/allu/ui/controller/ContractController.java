package fi.hel.allu.ui.controller;

import java.io.IOException;

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

import fi.hel.allu.common.domain.ContractInfo;
import fi.hel.allu.servicecore.service.ContractService;

@RestController
@RequestMapping("/applications")
public class ContractController {

  @Autowired
  private ContractService contractService;

  @RequestMapping(value = "/{id}/contract/proposal", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<byte[]> getContractProposal(@PathVariable Integer id) {
    byte[] data = contractService.getContractProposal(id);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.parseMediaType("application/pdf"));
    return new ResponseEntity<>(data, httpHeaders, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/contract/proposal", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<byte[]> createContractProposal(@PathVariable int id)
      throws IOException {
    return ResponseEntity.ok(contractService.createContractProposal(id));
  }

  @RequestMapping(value = "/{id}/contract/info", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<ContractInfo> getContractInfo(@PathVariable int id) {
    return ResponseEntity.ok(contractService.getContractInfo(id));
  }

  @RequestMapping(value = "/{id}/contract/approved", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<byte[]> getApprovedContract(@PathVariable Integer id) {
    byte[] data = contractService.getApprovedContract(id);
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.parseMediaType("application/pdf"));
    return new ResponseEntity<>(data, httpHeaders, HttpStatus.OK);

  }


}
