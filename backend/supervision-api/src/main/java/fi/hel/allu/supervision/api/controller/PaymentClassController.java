package fi.hel.allu.supervision.api.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@RestController
@RequestMapping("/v1")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payment classes")
public class PaymentClassController {

  @Autowired
  private ApplicationService applicationService;

  @Operation(summary = "Gets payment classes available for given application type and kind.")
  @RequestMapping(value = "/paymentclasses", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<List<String>> getPaymentClasses(
      @Parameter(description = "Application type") @RequestParam(value = "type") ApplicationType type,
      @Parameter(description = "Application kind") @RequestParam(value = "kind") ApplicationKind kind) {
    return ResponseEntity.ok(applicationService.getPricelistPaymentClasses(type, kind));
  }
}
