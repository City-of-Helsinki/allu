package fi.hel.allu.supervision.api.domain;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.CableReportJson;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "Cable report application")
public class CableReportApplication extends BaseApplication<CableReportJson> {

  public CableReportApplication() {
  }

  public CableReportApplication(ApplicationJson application) {
    super(application, (CableReportJson) application.getExtension());
  }
}
