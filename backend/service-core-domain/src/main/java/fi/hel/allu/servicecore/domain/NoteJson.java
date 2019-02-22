package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value = "Note specific fields")
public class NoteJson extends ApplicationExtensionJson {

  private String description;

  @ApiModelProperty(value = "Application type (always NOTE).", allowableValues="NOTE", required = true)
  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.NOTE;
  }

  @ApiModelProperty(value = "Description of the note")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
