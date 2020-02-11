package fi.hel.allu.model.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.service.PricingService;

@RestController
@RequestMapping("/prices")
public class PriceController {

  @Autowired
  private PricingService pricingService;

  /**
   * Returns all payment classes from prices with given application type and kind.
   */
  @RequestMapping(value = "/paymentclasses", method = RequestMethod.GET)
  public ResponseEntity<List<String>> getPaymentClasses(@RequestParam("type") ApplicationType type, @RequestParam("kind") ApplicationKind kind) {
    return ResponseEntity.ok(pricingService.getPaymentClasses(type, kind));
  }
}
