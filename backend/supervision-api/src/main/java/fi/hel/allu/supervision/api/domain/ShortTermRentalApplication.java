package fi.hel.allu.supervision.api.domain;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ShortTermRentalJson;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Short term rental application")
public class ShortTermRentalApplication extends BaseApplication<ShortTermRentalJson> {

  public ShortTermRentalApplication() {
  }

  public ShortTermRentalApplication(ApplicationJson application) {
    super(application, (ShortTermRentalJson) application.getExtension());
  }
}
