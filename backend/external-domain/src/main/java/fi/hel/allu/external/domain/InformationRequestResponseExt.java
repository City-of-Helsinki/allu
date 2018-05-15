package fi.hel.allu.external.domain;

import java.util.List;

import fi.hel.allu.common.domain.types.InformationRequestFieldKey;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Response to information request")
public class InformationRequestResponseExt<T extends ApplicationExt> {

  private T applicationData;
  private List<InformationRequestFieldKey> updatedFields;

  @ApiModelProperty(value = "Application data")
  public T getApplicationData() {
    return applicationData;
  }

  public void setApplicationData(T applicationData) {
    this.applicationData = applicationData;
  }

  @ApiModelProperty(value = "Keys of updated application fields")
  public List<InformationRequestFieldKey> getUpdatedFields() {
    return updatedFields;
  }

  public void setUpdatedFields(List<InformationRequestFieldKey> updatedFields) {
    this.updatedFields = updatedFields;
  }

}
