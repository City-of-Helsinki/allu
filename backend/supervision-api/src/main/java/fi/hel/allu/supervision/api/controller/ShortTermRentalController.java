package fi.hel.allu.supervision.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.supervision.api.domain.ShortTermRentalApplication;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v1/shorttermrentals")
@Api(tags = "Applications")
public class ShortTermRentalController extends BaseApplicationDetailsController<ShortTermRentalApplication> {

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.SHORT_TERM_RENTAL;
  }

  @Override
  protected ShortTermRentalApplication mapApplication(ApplicationJson application) {
    return new ShortTermRentalApplication(application);
  }
}
