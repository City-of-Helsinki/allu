package fi.hel.allu.supervision.api.domain;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.PlacementContractJson;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "Placement contract application")
public class PlacementContractApplication extends BaseApplication<PlacementContractJson> {

  public PlacementContractApplication() {
  }

  public PlacementContractApplication(ApplicationJson application) {
    super(application, (PlacementContractJson) application.getExtension());
  }
}
