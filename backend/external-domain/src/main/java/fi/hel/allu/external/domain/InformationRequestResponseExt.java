package fi.hel.allu.external.domain;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import fi.hel.allu.common.domain.types.InformationRequestFieldKey;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Response to information request")
public class InformationRequestResponseExt<T extends BaseApplicationExt> {

  private T applicationData;
  @NotEmpty(message = "{informationRequest.fields}")
  private List<InformationRequestFieldKey> updatedFields;

  @ApiModelProperty(value = "Application data. Can be empty (e.g. if only attachment update requested)")
  public T getApplicationData() {
    return applicationData;
  }

  public void setApplicationData(T applicationData) {
    this.applicationData = applicationData;
  }

  @ApiModelProperty(value = "Keys of updated application fields", required = true)
  public List<InformationRequestFieldKey> getUpdatedFields() {
    return updatedFields;
  }

  public void setUpdatedFields(List<InformationRequestFieldKey> updatedFields) {
    this.updatedFields = updatedFields;
  }

}
