package fi.hel.allu.supervision.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.supervision.api.domain.PlacementContractApplication;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v1/placementcontracts")
@Api(tags = "Applications")
public class PlacementContractController extends BaseApplicationDetailsController<PlacementContractApplication> {

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.PLACEMENT_CONTRACT;
  }

  @Override
  protected PlacementContractApplication mapApplication(ApplicationJson application) {
    return new PlacementContractApplication(application);
  }
}
