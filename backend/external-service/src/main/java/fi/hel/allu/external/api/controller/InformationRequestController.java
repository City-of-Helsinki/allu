package fi.hel.allu.external.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.external.domain.InformationRequestExt;
import fi.hel.allu.external.domain.InformationRequestFieldExt;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.servicecore.domain.InformationRequestFieldJson;
import fi.hel.allu.servicecore.domain.InformationRequestJson;
import fi.hel.allu.servicecore.service.InformationRequestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping({"/v1", "/v2"})
@Api(tags = "Information requests")
public class InformationRequestController {

  @Autowired
  private InformationRequestService informationRequestService;

  @Autowired
  private ApplicationServiceExt applicationService;

  @ApiOperation(value = "Fetch open information request for given application.",
      produces = "application/json",
      response = InformationRequestExt.class,
      authorizations=@Authorization(value ="api_key"))
  @RequestMapping(value = "/applications/{id}/informationrequests", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<InformationRequestExt> findByApplicationId(@ApiParam(value = "Application ID to get information request for") @PathVariable Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    InformationRequestJson request = informationRequestService.findOpenByApplicationId(applicationId);
    return new ResponseEntity<>(toInformationRequestExt(request, id), HttpStatus.OK);
  }

  private InformationRequestExt toInformationRequestExt(InformationRequestJson request, Integer externalApplicationId) {
    if (request == null) {
      return null;
    }
    return new InformationRequestExt(request.getId(), externalApplicationId, toInformationRequestExtFields(request.getFields()));
  }

  private List<InformationRequestFieldExt> toInformationRequestExtFields(List<InformationRequestFieldJson> fields) {
    return fields.stream().map(f -> new InformationRequestFieldExt(f.getFieldKey(), f.getDescription())).collect(Collectors.toList());
  }

}
