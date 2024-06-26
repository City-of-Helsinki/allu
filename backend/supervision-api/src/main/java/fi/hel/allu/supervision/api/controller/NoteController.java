package fi.hel.allu.supervision.api.controller;

import fi.hel.allu.servicecore.domain.CreateCustomerWithContactsJson;
import fi.hel.allu.servicecore.domain.CreateNoteApplicationJson;
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
import fi.hel.allu.supervision.api.domain.NoteApplication;

@RestController
@RequestMapping("/v1/notes")
@Tag(name = "Applications")
public class NoteController extends BaseApplicationDetailsController<NoteApplication, CreateNoteApplicationJson> {

  public NoteController(ApprovalDocumentService approvalDocumentService,
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
    return ApplicationType.NOTE;
  }

  @Override
  protected NoteApplication mapApplication(ApplicationJson application) {
    return new NoteApplication(application);
  }

  @Override
  @PutMapping(value = "/{applicationId}/applicant", produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerApplicant(@PathVariable Integer applicationId,
                                                                          @RequestBody @Parameter(description = "The new customer with contacts") CreateCustomerWithContactsJson customer) {
    return super.updateCustomerApplicant(applicationId, customer);
  }
}
