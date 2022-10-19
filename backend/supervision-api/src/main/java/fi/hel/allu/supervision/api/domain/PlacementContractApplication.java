package fi.hel.allu.supervision.api.domain;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.PlacementContractJson;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Placement contract application")
public class PlacementContractApplication extends BaseApplication<PlacementContractJson> {

  public PlacementContractApplication() {
  }

  public PlacementContractApplication(ApplicationJson application) {
    super(application, (PlacementContractJson) application.getExtension());
  }
}
