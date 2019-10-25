package fi.hel.allu.supervision.api.controller;

import fi.hel.allu.servicecore.domain.CreateCustomerWithContactsJson;
import fi.hel.allu.servicecore.domain.CreateEventApplicationJson;
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
import fi.hel.allu.supervision.api.domain.EventApplication;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v1/events")
@Api(tags = "Applications")
public class EventController extends BaseApplicationDetailsController<EventApplication, CreateEventApplicationJson> {

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.EVENT;
  }

  @Override
  protected EventApplication mapApplication(ApplicationJson application) {
    return new EventApplication(application);
  }

  @RequestMapping(value = "/{applicationId}/customerApplicant", method = RequestMethod.PUT, produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerApplicant(@PathVariable Integer applicationId,
                                                                          @RequestBody @ApiParam("The new customer with contacts") CreateCustomerWithContactsJson customer) {
    return super.updateCustomerApplicant(applicationId, customer);
  }

  @RequestMapping(value = "/{applicationId}/customerRepresentative", method = RequestMethod.PUT, produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerRepresentative(@PathVariable Integer applicationId,
                                                                               @RequestBody @ApiParam("The new customer with contacts") CreateCustomerWithContactsJson customer) {
    return super.updateCustomerRepresentative(applicationId, customer);
  }
}
