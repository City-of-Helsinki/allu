package fi.hel.allu.supervision.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.supervision.api.domain.CableReportApplication;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v1/cablereports")
@Api(tags = "Applications")
public class CableReportController extends BaseApplicationDetailsController<CableReportApplication> {

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.CABLE_REPORT;
  }

  @Override
  protected CableReportApplication mapApplication(ApplicationJson application) {
    return new CableReportApplication(application);
  }
}
