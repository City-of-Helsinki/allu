package fi.hel.allu.supervision.api.controller;

import fi.hel.allu.servicecore.domain.CreateCustomerWithContactsJson;
import fi.hel.allu.servicecore.domain.CreateEventApplicationJson;
import fi.hel.allu.servicecore.domain.CustomerWithContactsJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.ApprovalDocumentService;
import fi.hel.allu.servicecore.service.ChargeBasisService;
import fi.hel.allu.servicecore.service.LocationService;
import fi.hel.allu.supervision.api.mapper.ApplicationMapperCollector;
import fi.hel.allu.supervision.api.service.ApplicationUpdateService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.supervision.api.domain.EventApplication;

@RestController
@RequestMapping("/v1/events")
@Tag(name = "Applications")
public class EventController extends BaseApplicationDetailsController<EventApplication, CreateEventApplicationJson> {

  public EventController(ApprovalDocumentService approvalDocumentService,
                         ChargeBasisService chargeBasisService,
                         ApplicationServiceComposer applicationServiceComposer,
                         ApplicationUpdateService applicationUpdateService,
                         LocationService locationService,
                         ApplicationMapperCollector applicationMapperCollector) {
    super(approvalDocumentService, chargeBasisService, applicationServiceComposer, applicationUpdateService,
          locationService, applicationMapperCollector);
  }

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.EVENT;
  }

  @Override
  protected EventApplication mapApplication(ApplicationJson application) {
    return new EventApplication(application);
  }

  @Override
  @PutMapping(value = "/{applicationId}/applicant", produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerApplicant(@PathVariable Integer applicationId,
                                                                          @RequestBody @Parameter(description = "The new customer with contacts") CreateCustomerWithContactsJson customer) {
    return super.updateCustomerApplicant(applicationId, customer);
  }

  @Override
  @PutMapping(value = "/{applicationId}/representative", produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerRepresentative(@PathVariable Integer applicationId,
                                                                               @RequestBody @Parameter(description = "The new customer with contacts") CreateCustomerWithContactsJson customer) {
    return super.updateCustomerRepresentative(applicationId, customer);
  }

  @Override
  @DeleteMapping(value = "/{applicationId}/representative", produces = "application/json")
  public ResponseEntity<Void> removeRepresentative(@PathVariable Integer applicationId) {
    return super.removeRepresentative(applicationId);
  }

}
