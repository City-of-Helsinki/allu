package fi.hel.allu.external.api.controller.event;

import fi.hel.allu.external.api.controller.BaseApplicationController;
import fi.hel.allu.external.domain.BigEventExt;
import fi.hel.allu.external.mapper.event.BigEventExtMapper;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.external.validation.ApplicationExtGeometryValidator;
import fi.hel.allu.external.validation.DefaultImageValidator;
import fi.hel.allu.servicecore.service.DecisionService;
import fi.hel.allu.servicecore.service.TerminationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/events/bigevents")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Events")
public class BigEventController extends BaseApplicationController<BigEventExt, BigEventExtMapper> {


  private BigEventExtMapper eventMapper;

  public BigEventController(ApplicationServiceExt applicationService,
                            ApplicationExtGeometryValidator geometryValidator,
                            DefaultImageValidator defaultImageValidator,
                            DecisionService decisionService,
                            TerminationService terminationService,
                            BigEventExtMapper eventMapper) {
    super(applicationService, geometryValidator, defaultImageValidator, decisionService, terminationService);
    this.eventMapper = eventMapper;
  }

  @Override
  protected BigEventExtMapper getMapper() {
    return eventMapper;
  }
}
