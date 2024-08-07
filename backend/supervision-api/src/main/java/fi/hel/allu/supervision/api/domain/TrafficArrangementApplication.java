package fi.hel.allu.supervision.api.domain;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.TrafficArrangementJson;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Temporary traffic arrangement application")
public class TrafficArrangementApplication extends BaseApplication<TrafficArrangementJson> {

  public TrafficArrangementApplication() {
  }

  public TrafficArrangementApplication(ApplicationJson application) {
    super(application, (TrafficArrangementJson) application.getExtension());
  }
}
