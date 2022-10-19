package fi.hel.allu.external.domain;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import fi.hel.allu.common.domain.types.InformationRequestFieldKey;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Reported application changes")
public class InformationRequestResponseExt<T extends BaseApplicationExt> {

  private T applicationData;
  @NotEmpty(message = "{informationRequest.fields}")
  private List<InformationRequestFieldKey> updatedFields;

  @Schema(description = "Application data. Can be empty (e.g. if only attachment update requested)")
  public T getApplicationData() {
    return applicationData;
  }

  public void setApplicationData(T applicationData) {
    this.applicationData = applicationData;
  }

  @Schema(description = "Keys of updated application fields", required = true)
  public List<InformationRequestFieldKey> getUpdatedFields() {
    return updatedFields;
  }

  public void setUpdatedFields(List<InformationRequestFieldKey> updatedFields) {
    this.updatedFields = updatedFields;
  }

}
