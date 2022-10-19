package fi.hel.allu.supervision.api.domain;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ExcavationAnnouncementJson;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Excavation announcement application")
public class ExcavationAnnouncementApplication extends BaseApplication<ExcavationAnnouncementJson> {

  public ExcavationAnnouncementApplication() {
  }

  public ExcavationAnnouncementApplication(ApplicationJson application) {
    super(application, (ExcavationAnnouncementJson) application.getExtension());
  }
}
