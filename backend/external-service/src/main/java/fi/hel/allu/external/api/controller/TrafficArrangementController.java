package fi.hel.allu.external.api.controller;

import fi.hel.allu.external.domain.TrafficArrangementExt;
import fi.hel.allu.external.mapper.TrafficArrangementExtMapper;
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
@RequestMapping({"/v1/trafficarrangements", "/v2/trafficarrangements"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Traffic arrangements")
public class TrafficArrangementController extends BaseApplicationController<TrafficArrangementExt, TrafficArrangementExtMapper> {


  private final TrafficArrangementExtMapper trafficArrangementMapper;

  public TrafficArrangementController(ApplicationServiceExt applicationService,
                                      ApplicationExtGeometryValidator geometryValidator,
                                      DefaultImageValidator defaultImageValidator,
                                      DecisionService decisionService,
                                      TerminationService terminationService,
                                      TrafficArrangementExtMapper trafficArrangementMapper) {
    super(applicationService, geometryValidator, defaultImageValidator, decisionService, terminationService);
    this.trafficArrangementMapper = trafficArrangementMapper;
  }

  @Override
  protected TrafficArrangementExtMapper getMapper() {
    return trafficArrangementMapper;
  }
}
