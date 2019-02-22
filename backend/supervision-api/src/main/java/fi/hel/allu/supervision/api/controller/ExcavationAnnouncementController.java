package fi.hel.allu.supervision.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.supervision.api.domain.ExcavationAnnouncementApplication;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v1/excavationannouncements")
@Api(tags = "Applications")
public class ExcavationAnnouncementController extends BaseApplicationDetailsController<ExcavationAnnouncementApplication> {

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.EXCAVATION_ANNOUNCEMENT;
  }

  @Override
  protected ExcavationAnnouncementApplication mapApplication(ApplicationJson application) {
    return new ExcavationAnnouncementApplication(application);
  }
}
