package fi.hel.allu.supervision.api.domain;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.AreaRentalJson;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Area rental application")
public class AreaRentalApplication extends BaseApplication<AreaRentalJson> {

  public AreaRentalApplication() {
  }

  public AreaRentalApplication(ApplicationJson application) {
    super(application, (AreaRentalJson) application.getExtension());
  }
}
