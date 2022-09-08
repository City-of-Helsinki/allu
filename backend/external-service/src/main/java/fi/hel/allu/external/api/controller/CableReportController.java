package fi.hel.allu.external.api.controller;

import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.external.validation.ApplicationExtGeometryValidator;
import fi.hel.allu.external.validation.DefaultImageValidator;
import fi.hel.allu.servicecore.service.DecisionService;
import fi.hel.allu.servicecore.service.TerminationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.external.domain.CableReportExt;
import fi.hel.allu.external.mapper.CableReportExtMapper;

@RestController
@RequestMapping({"/v1/cablereports", "/v2/cablereports"})
@Tag(name = "Cable reports")
public class CableReportController extends BaseApplicationController<CableReportExt, CableReportExtMapper>{

  private CableReportExtMapper mapper;

  public CableReportController(ApplicationServiceExt applicationService,
                               ApplicationExtGeometryValidator geometryValidator,
                               DefaultImageValidator defaultImageValidator,
                               DecisionService decisionService,
                               TerminationService terminationService,
                               CableReportExtMapper cableReportExtMapper) {
    super(applicationService, geometryValidator, defaultImageValidator, decisionService, terminationService);
    this.mapper = cableReportExtMapper;
  }

  @Override
  protected CableReportExtMapper getMapper() {
    return mapper;
  }

}
