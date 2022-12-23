package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.ContractInfo;
import fi.hel.allu.model.dao.ContractDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/applications")
public class ContractController {

  @Autowired
  private ContractDao contractDao;

  @GetMapping(value = "/{id}/contract")
  public ResponseEntity<byte[]> getContract(@PathVariable Integer id) {
    return new ResponseEntity<>(contractDao.getContract(id), HttpStatus.OK);
  }

  /**
   * Updates contract info and proposal PDF data.
   */
  @PutMapping(value = "/{id}/contract")
  public ResponseEntity<Void> updateContract(@PathVariable Integer id,
                                             @RequestPart(value = "info") ContractInfo contractInfo,
                                             @RequestPart(value = "file") MultipartFile file) throws IOException {
    contractDao.updateContract(id, contractInfo, file.getBytes());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping(value = "/{id}/contract/proposal")
  public ResponseEntity<Void> insertContractProposal(@PathVariable int id, @RequestParam("data") MultipartFile file)
      throws IOException {
    contractDao.insertContractProposal(id, file.getBytes());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping(value = "/{id}/contract/approved")
  public ResponseEntity<Void> insertApprovedContract(@PathVariable int id, @RequestPart("contractinfo") ContractInfo contractInfo, @RequestPart("data") MultipartFile file)
      throws IOException {
    contractDao.insertApprovedContract(id, contractInfo, file.getBytes());
    return new ResponseEntity<>(HttpStatus.OK);
  }


  @PostMapping(value = "/{id}/contract/final")
  public ResponseEntity<Void> insertFinalContract(@PathVariable int id, @RequestParam("data") MultipartFile file)
      throws IOException {
    contractDao.insertFinalContract(id, file.getBytes());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping(value = "/{id}/contract/final")
  public ResponseEntity<byte[]> getFinalContract(@PathVariable int id) {
    return new ResponseEntity<>(contractDao.getFinalContract(id), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/contract/info")
  public ResponseEntity<Void> updateContractInfo(@PathVariable Integer id, @RequestBody ContractInfo contractInfo) {
    contractDao.updateContractInfo(id, contractInfo);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping(value = "/{id}/contract/info")
  public ResponseEntity<ContractInfo> getContractInfo(@PathVariable Integer id) {
    return new ResponseEntity<>(contractDao.getContractInfo(id), HttpStatus.OK);
  }

}