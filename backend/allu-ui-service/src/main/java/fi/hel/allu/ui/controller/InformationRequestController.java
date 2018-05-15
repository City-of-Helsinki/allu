package fi.hel.allu.ui.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.servicecore.domain.InformationRequestJson;
import fi.hel.allu.servicecore.service.InformationRequestService;

@RestController
public class InformationRequestController {

  @Autowired
  private InformationRequestService informationRequestService;

  @RequestMapping(value = "/applications/{id}/informationrequests", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<InformationRequestJson> create(@PathVariable int id, @Valid @RequestBody(required = true) InformationRequestJson
      informationRequest) {
    return new ResponseEntity<>(informationRequestService.createForApplication(id, informationRequest), HttpStatus.OK);
  }

  @RequestMapping(value = "/informationrequests/{requestid}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<InformationRequestJson> update(@PathVariable("id") int id,
      @PathVariable("requestid") int informationRequestId, @Valid @RequestBody(required = true) InformationRequestJson informationRequest) {
    return new ResponseEntity<>(informationRequestService.update(id, informationRequest), HttpStatus.OK);
  }

  @RequestMapping(value = "/informationrequests/{requestid}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> delete(@PathVariable("id") int id,
      @PathVariable("requestid") int informationRequestId) {
    informationRequestService.delete(informationRequestId);
    return new ResponseEntity<>(HttpStatus.OK);
  }


  @RequestMapping(value = "/applications/{id}/informationrequests", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<InformationRequestJson> findOpenByApplicationId(@PathVariable("id") int id) {
    return new ResponseEntity<>(informationRequestService.findOpenByApplicationId(id), HttpStatus.OK);
  }



}
