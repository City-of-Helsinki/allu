package fi.hel.allu.model.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.InformationRequestResponse;
import fi.hel.allu.model.dao.InformationRequestDao;
import fi.hel.allu.model.domain.InformationRequest;

@RestController
public class InformationRequestController {

  private final InformationRequestDao informationRequestDao;

  @Autowired
  public InformationRequestController(InformationRequestDao informationRequestDao) {
    this.informationRequestDao = informationRequestDao;
  }

  @RequestMapping(value = "/informationrequests/{id}", method = RequestMethod.GET)
  public ResponseEntity<InformationRequest> findById(@PathVariable Integer id) {
    return new ResponseEntity<>(informationRequestDao.findById(id), HttpStatus.OK);
  }

  @RequestMapping(value = "applications/{id}/informationrequest", method = RequestMethod.POST)
  public ResponseEntity<InformationRequest> insertInformationRequest(@PathVariable Integer id,
      @RequestBody(required = true) InformationRequest informationRequest) {
    informationRequest.setApplicationId(id);
    return new ResponseEntity<>(informationRequestDao.insert(informationRequest), HttpStatus.OK);
  }

  @RequestMapping(value = "/informationrequests/{id}", method = RequestMethod.PUT)
  public ResponseEntity<InformationRequest> updateInformationRequest(@PathVariable Integer id,
      @RequestBody(required = true) InformationRequest informationRequest) {
    return new ResponseEntity<>(informationRequestDao.update(id, informationRequest), HttpStatus.OK);
  }

  @RequestMapping(value = "/informationrequests/{id}/close", method = RequestMethod.PUT)
  public ResponseEntity<InformationRequest> closeInformationRequest(@PathVariable Integer id) {
    return new ResponseEntity<>(informationRequestDao.closeInformationRequest(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/informationrequests/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteInformationRequest(@PathVariable Integer id) {
    informationRequestDao.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/informationrequests/{id}/response", method = RequestMethod.POST)
  public ResponseEntity<Void> insertResponse(@PathVariable Integer id, @RequestBody InformationRequestResponse response) {
    informationRequestDao.insertResponse(id, response);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/applications/{id}/informationrequests/response", method = RequestMethod.GET)
  public ResponseEntity<InformationRequestResponse> findResponseForApplication(@PathVariable Integer id) {
    return new ResponseEntity<>(informationRequestDao.findResponseForApplicationId(id), HttpStatus.OK);
  }

  /**
   * Finds open information request for given application.
   */
  @RequestMapping(value = "applications/{id}/informationrequest/open", method = RequestMethod.GET)
  public ResponseEntity<InformationRequest> findOpenByApplicationId(@PathVariable int id) {
    return new ResponseEntity<>(informationRequestDao.findOpenByApplicationId(id), HttpStatus.OK);
  }

  @RequestMapping(value = "applications/{id}/informationrequest", method = RequestMethod.GET)
  public ResponseEntity<InformationRequest> findByApplicationId(@PathVariable int id) {
    return new ResponseEntity<>(informationRequestDao.findByApplicationId(id), HttpStatus.OK);
  }


}
