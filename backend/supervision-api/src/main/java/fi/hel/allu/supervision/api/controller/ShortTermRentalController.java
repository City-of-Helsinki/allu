package fi.hel.allu.supervision.api.controller;

import fi.hel.allu.servicecore.domain.CreateCustomerWithContactsJson;
import fi.hel.allu.servicecore.domain.CreateShortTermRentalApplicationJson;
import fi.hel.allu.servicecore.domain.CustomerWithContactsJson;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.supervision.api.domain.ShortTermRentalApplication;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v1/shorttermrentals")
@Api(tags = "Applications")
public class ShortTermRentalController extends BaseApplicationDetailsController<ShortTermRentalApplication, CreateShortTermRentalApplicationJson> {

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.SHORT_TERM_RENTAL;
  }

  @Override
  protected ShortTermRentalApplication mapApplication(ApplicationJson application) {
    return new ShortTermRentalApplication(application);
  }

  @RequestMapping(value = "/{applicationId}/applicant", method = RequestMethod.PUT, produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerApplicant(@PathVariable Integer applicationId,
                                                                          @RequestBody @ApiParam("The new customer with contacts") CreateCustomerWithContactsJson customer) {
    return super.updateCustomerApplicant(applicationId, customer);
  }

  @RequestMapping(value = "/{applicationId}/representative", method = RequestMethod.PUT, produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerRepresentative(@PathVariable Integer applicationId,
                                                                               @RequestBody @ApiParam("The new customer with contacts") CreateCustomerWithContactsJson customer) {
    return super.updateCustomerRepresentative(applicationId, customer);
  }
}
