package fi.hel.allu.supervision.api.domain;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ExcavationAnnouncementJson;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "Excavation announcement application")
public class ExcavationAnnouncementApplication extends BaseApplication<ExcavationAnnouncementJson> {
  public ExcavationAnnouncementApplication(ApplicationJson application) {
    super(application, (ExcavationAnnouncementJson) application.getExtension());
  }
}
