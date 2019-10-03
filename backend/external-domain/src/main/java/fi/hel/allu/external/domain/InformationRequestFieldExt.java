package fi.hel.allu.external.domain;

import fi.hel.allu.common.domain.types.InformationRequestFieldKey;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Field of information request.")
public class InformationRequestFieldExt {

  private String requestDescription;
  private InformationRequestFieldKey fieldKey;

  public InformationRequestFieldExt() {
  }

  public InformationRequestFieldExt(InformationRequestFieldKey fieldKey, String requestDescription) {
    this.fieldKey = fieldKey;
    this.requestDescription = requestDescription;
  }

  @ApiModelProperty(value = "Handler's description for information request.")
  public String getRequestDescription() {
    return requestDescription;
  }

  public void setRequestDescription(String requestDescription) {
    this.requestDescription = requestDescription;
  }

  @ApiModelProperty(value = "Key of the information request field")
  public InformationRequestFieldKey getFieldKey() {
    return fieldKey;
  }

  public void setFieldKey(InformationRequestFieldKey fieldKey) {
    this.fieldKey = fieldKey;
  }

}
