package fi.hel.allu.supervision.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.supervision.api.domain.AreaRentalApplication;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v1/arearentals")
@Api(tags = "Applications")
public class AreaRentalController extends BaseApplicationDetailsController<AreaRentalApplication> {

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.AREA_RENTAL;
  }

  @Override
  protected AreaRentalApplication mapApplication(ApplicationJson application) {
    return new AreaRentalApplication(application);
  }
}
