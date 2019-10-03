package fi.hel.allu.supervision.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.supervision.api.domain.EventApplication;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v1/events")
@Api(tags = "Applications")
public class EventController extends BaseApplicationDetailsController<EventApplication> {

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.EVENT;
  }

  @Override
  protected EventApplication mapApplication(ApplicationJson application) {
    return new EventApplication(application);
  }
}
