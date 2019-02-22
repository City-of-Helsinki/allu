package fi.hel.allu.supervision.api.domain;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.NoteJson;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "Note application")
public class NoteApplication extends BaseApplication<NoteJson> {

  public NoteApplication() {
  }

  public NoteApplication(ApplicationJson application) {
    super(application, (NoteJson) application.getExtension());
  }
}
