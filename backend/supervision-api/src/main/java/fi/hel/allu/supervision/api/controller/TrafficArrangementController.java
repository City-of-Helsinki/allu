package fi.hel.allu.supervision.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.supervision.api.domain.TrafficArrangementApplication;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v1/trafficarrangements")
@Api(tags = "Applications")
public class TrafficArrangementController extends BaseApplicationDetailsController<TrafficArrangementApplication> {

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS;
  }

  @Override
  protected TrafficArrangementApplication mapApplication(ApplicationJson application) {
    return new TrafficArrangementApplication(application);
  }
}
