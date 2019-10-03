package fi.hel.allu.supervision.api.domain;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.EventJson;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "Event application")
public class EventApplication extends BaseApplication<EventJson> {

  public EventApplication() {
  }

  public EventApplication(ApplicationJson application) {
    super(application, (EventJson) application.getExtension());
  }
}
