package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.InformationRequestResponse;
import fi.hel.allu.common.domain.types.InformationRequestFieldKey;
import fi.hel.allu.model.dao.InformationRequestDao;
import fi.hel.allu.model.domain.InformationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InformationRequestController {

  private final InformationRequestDao informationRequestDao;

  @Autowired
  public InformationRequestController(InformationRequestDao informationRequestDao) {
    this.informationRequestDao = informationRequestDao;
  }

  @GetMapping(value = "/informationrequests/{id}")
  public ResponseEntity<InformationRequest> findById(@PathVariable Integer id) {
    return new ResponseEntity<>(informationRequestDao.findById(id), HttpStatus.OK);
  }

  @PostMapping(value = "/applications/{id}/informationrequest")
  public ResponseEntity<InformationRequest> insertInformationRequest(@PathVariable Integer id,
      @RequestBody(required = true) InformationRequest informationRequest) {
    informationRequest.setApplicationId(id);
    return new ResponseEntity<>(informationRequestDao.insert(informationRequest), HttpStatus.OK);
  }

  @PutMapping(value = "/informationrequests/{id}")
  public ResponseEntity<InformationRequest> updateInformationRequest(@PathVariable Integer id,
      @RequestBody(required = true) InformationRequest informationRequest) {
    return new ResponseEntity<>(informationRequestDao.update(id, informationRequest), HttpStatus.OK);
  }

  @PutMapping(value = "/informationrequests/{id}/close")
  public ResponseEntity<InformationRequest> closeInformationRequest(@PathVariable Integer id) {
    return new ResponseEntity<>(informationRequestDao.closeInformationRequest(id), HttpStatus.OK);
  }

  @DeleteMapping(value = "/informationrequests/{id}")
  public ResponseEntity<Void> deleteInformationRequest(@PathVariable Integer id) {
    informationRequestDao.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping(value = "/informationrequests/{id}/response")
  public ResponseEntity<Void> insertResponse(@PathVariable Integer id, @RequestBody InformationRequestResponse response) {
    informationRequestDao.insertResponse(id, response);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping(value = "/informationrequests/{id}/response")
  public ResponseEntity<InformationRequestResponse> findResponse(@PathVariable Integer id) {
    return ResponseEntity.ok(informationRequestDao.findResponseForRequest(id));
  }

  @GetMapping(value = "/applications/{id}/informationrequests/response")
  public ResponseEntity<InformationRequestResponse> findResponseForApplication(@PathVariable Integer id) {
    return new ResponseEntity<>(informationRequestDao.findResponseForApplicationId(id), HttpStatus.OK);
  }

  /**
   * Finds open information request for given application.
   */
  @GetMapping(value = "/applications/{id}/informationrequest/open")
  public ResponseEntity<InformationRequest> findOpenByApplicationId(@PathVariable int id) {
    return new ResponseEntity<>(informationRequestDao.findOpenByApplicationId(id), HttpStatus.OK);
  }

  @GetMapping(value = "/applications/{id}/informationrequest/active")
  public ResponseEntity<InformationRequest> findActiveByApplicationId(@PathVariable int id) {
    return new ResponseEntity<>(informationRequestDao.findActiveByApplicationId(id), HttpStatus.OK);
  }

  @GetMapping(value = "/applications/{id}/informationrequest")
  public ResponseEntity<List<InformationRequest>> findAllByApplicationId(@PathVariable int id) {
    return new ResponseEntity<>(informationRequestDao.findAllByApplicationId(id), HttpStatus.OK);
  }

  @GetMapping(value = "/informationrequests/{id}/responsefields")
  public ResponseEntity<List<InformationRequestFieldKey>> findResponseFieldsForInformationRequest(@PathVariable int id) {
    return ResponseEntity.ok(informationRequestDao.getResponseFields(id));
  }
}