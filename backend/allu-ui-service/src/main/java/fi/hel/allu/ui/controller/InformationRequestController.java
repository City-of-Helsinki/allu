package fi.hel.allu.ui.controller;

import fi.hel.allu.servicecore.domain.InformationRequestJson;
import fi.hel.allu.servicecore.domain.informationrequest.InformationRequestResponseJson;
import fi.hel.allu.servicecore.domain.informationrequest.InformationRequestSummaryJson;
import fi.hel.allu.servicecore.service.InformationRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
public class InformationRequestController {

  @Autowired
  private InformationRequestService informationRequestService;

  @PostMapping(value = "/applications/{id}/informationrequests")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<InformationRequestJson> create(@PathVariable int id, @Valid @RequestBody(required = true) InformationRequestJson
      informationRequest) {
    return new ResponseEntity<>(informationRequestService.createForApplication(id, informationRequest), HttpStatus.OK);
  }

  @PutMapping(value = "/informationrequests/{requestid}")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<InformationRequestJson> update(@PathVariable("requestid") int id,
      @PathVariable("requestid") int informationRequestId, @Valid @RequestBody(required = true) InformationRequestJson informationRequest) {
    return new ResponseEntity<>(informationRequestService.update(id, informationRequest), HttpStatus.OK);
  }

  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  @PutMapping(value = "/informationrequests/{id}/close")
  public ResponseEntity<InformationRequestJson> closeInformationRequest(@PathVariable Integer id) {
    return new ResponseEntity<>(informationRequestService.closeInformationRequest(id), HttpStatus.OK);
  }

  @DeleteMapping(value = "/informationrequests/{id}")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> delete(@PathVariable("id") int id) {
    informationRequestService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping(value = "/informationrequests/{id}")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<InformationRequestJson> findById(@PathVariable("id") int id) {
    return ResponseEntity.ok(informationRequestService.findRequestById(id));
  }

  @GetMapping(value = "/informationrequests/{id}/response")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<InformationRequestResponseJson> findResponseForRequest(@PathVariable("id") int id) throws IOException {
    return ResponseEntity.ok(informationRequestService.findResponseForRequest(id));
  }

  @GetMapping(value = "/applications/{id}/informationrequests")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<InformationRequestJson> findByApplicationId(@PathVariable("id") int id) {
    return new ResponseEntity<>(informationRequestService.findByApplicationId(id), HttpStatus.OK);
  }

  @GetMapping(value = "/applications/{id}/informationrequests/response")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<InformationRequestResponseJson> findResponseForApplication(@PathVariable Integer id) throws IOException {
    return new ResponseEntity<>(informationRequestService.findResponseForApplication(id), HttpStatus.OK);
  }

  @GetMapping(value = "/applications/{id}/informationrequests/summaries")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<InformationRequestSummaryJson>> findSummariesForApplication(@PathVariable Integer id) throws IOException {
    return new ResponseEntity<>(informationRequestService.findSummariesByApplicationId(id), HttpStatus.OK);
  }
}