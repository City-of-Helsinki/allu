package fi.hel.allu.supervision.api.domain;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.CableReportJson;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cable report application")
public class CableReportApplication extends BaseApplication<CableReportJson> {

  public CableReportApplication() {
  }

  public CableReportApplication(ApplicationJson application) {
    super(application, (CableReportJson) application.getExtension());
  }
}
