package fi.hel.allu.supervision.api.controller;

import fi.hel.allu.servicecore.domain.CreateNoteApplicationJson;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.supervision.api.domain.NoteApplication;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v1/notes")
@Api(tags = "Applications")
public class NoteController extends BaseApplicationDetailsController<NoteApplication, CreateNoteApplicationJson> {

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.NOTE;
  }

  @Override
  protected NoteApplication mapApplication(ApplicationJson application) {
    return new NoteApplication(application);
  }
}
