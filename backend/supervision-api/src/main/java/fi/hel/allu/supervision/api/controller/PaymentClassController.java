package fi.hel.allu.supervision.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.service.ApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping("/v1")
@Api(tags = "Payment classes")
public class PaymentClassController {

  @Autowired
  private ApplicationService applicationService;

  @ApiOperation(value = "Gets payment classes available for given application type and kind.",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @RequestMapping(value = "/paymentclasses", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<List<String>> getPaymentClasses(
      @ApiParam(value = "Application type") @RequestParam(value = "type") ApplicationType type,
      @ApiParam(value = "Application kind") @RequestParam(value = "kind") ApplicationKind kind) {
    return ResponseEntity.ok(applicationService.getPricelistPaymentClasses(type, kind));
  }
}
