package fi.hel.allu.supervision.api.domain;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.NoteJson;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Note application")
public class NoteApplication extends BaseApplication<NoteJson> {

  public NoteApplication() {
  }

  public NoteApplication(ApplicationJson application) {
    super(application, (NoteJson) application.getExtension());
  }
}
