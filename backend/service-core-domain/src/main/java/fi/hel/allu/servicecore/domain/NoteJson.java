package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.domain.mapper.UpdatableProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Note specific fields")
public class NoteJson extends ApplicationExtensionJson {

  private String description;

  @Schema(description = "Application type (always NOTE).", allowableValues="NOTE", required = true)
  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.NOTE;
  }

  @Schema(description = "Description of the note")
  public String getDescription() {
    return description;
  }

  @UpdatableProperty
  public void setDescription(String description) {
    this.description = description;
  }

}
