package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.model.dao.TerminationDao;
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
public class TerminationController {

  @Autowired
  private TerminationDao terminationDao;


  @RequestMapping(value = "/{id}/termination", method = RequestMethod.GET)
  public ResponseEntity<byte[]> getTerminationDocument(@PathVariable Integer id) {
    return ResponseEntity.ok(terminationDao.getTerminationDocument(id));
  }

  @RequestMapping(value = "/{id}/termination", method = RequestMethod.POST)
  public ResponseEntity<Void> storeTerminationDocument(@PathVariable Integer id, @RequestParam("data") MultipartFile file)
      throws IOException {
    terminationDao.storeTerminationDocument(id, file.getBytes());
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri());
    return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/{id}/termination/info", method = RequestMethod.GET)
  public ResponseEntity<TerminationInfo> getTerminationInfo(@PathVariable Integer id) {
    return ResponseEntity.ok(terminationDao.getTerminationInfo(id));
  }

  @RequestMapping(value = "/{id}/termination/info", method = RequestMethod.POST)
  public ResponseEntity<TerminationInfo> insertTerminationInfo(@PathVariable Integer id, @RequestBody TerminationInfo terminationInfo) {
    return ResponseEntity.ok(terminationDao.insertTerminationInfo(id, terminationInfo));
  }

  @RequestMapping(value = "/{id}/termination/info", method = RequestMethod.PUT)
  public ResponseEntity<TerminationInfo> updateTerminationInfo(@PathVariable Integer id, @RequestBody TerminationInfo terminationInfo) {
    return ResponseEntity.ok(terminationDao.updateTerminationInfo(id, terminationInfo));
  }

  @RequestMapping(value = "/termination/pending", method = RequestMethod.GET)
  public ResponseEntity<List<Integer>> getApplicationsPendingForTermination() {
    return ResponseEntity.ok(terminationDao.getApplicationsPendingForTermination());
  }
}