package fi.hel.allu.model.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import fi.hel.allu.common.domain.ContractInfo;
import fi.hel.allu.model.dao.ContractDao;

@RestController
@RequestMapping("/applications")
public class ContractController {

  @Autowired
  private ContractDao contractDao;

  @RequestMapping(value = "/{id}/contract/proposal", method = RequestMethod.GET)
  public ResponseEntity<byte[]> getContractProposal(@PathVariable Integer id) {
    return new ResponseEntity<>(contractDao.getContractProposal(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/contract/proposal", method = RequestMethod.POST)
  public ResponseEntity<Void> insertContractProposal(@PathVariable int id, @RequestParam("data") MultipartFile file)
      throws IOException {
    contractDao.insertContractProposal(id, file.getBytes());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/contract/approved", method = RequestMethod.GET)
  public ResponseEntity<byte[]> getApprovedContract(@PathVariable Integer id) {
    return new ResponseEntity<>(contractDao.getApprovedContract(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/contract/approved", method = RequestMethod.POST)
  public ResponseEntity<Void> insertApprovedContract(@PathVariable int id, @RequestParam("data") MultipartFile file)
      throws IOException {
    contractDao.insertApprovedContract(id, file.getBytes());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/contract/rejected", method = RequestMethod.POST)
  public ResponseEntity<Void> rejectContract(@PathVariable int id, @RequestBody String rejectionReason) {
    contractDao.rejectContract(id, rejectionReason);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/contract/info", method = RequestMethod.GET)
  public ResponseEntity<ContractInfo> getContractInfo(@PathVariable Integer id) {
    return new ResponseEntity<>(contractDao.getContractInfo(id), HttpStatus.OK);
  }

}
