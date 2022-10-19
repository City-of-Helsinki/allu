package fi.hel.allu.external.domain;

import fi.hel.allu.common.domain.types.InformationRequestFieldKey;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description ="Field of information request.")
public class InformationRequestFieldExt {

  private String requestDescription;
  private InformationRequestFieldKey fieldKey;

  public InformationRequestFieldExt() {
  }

  public InformationRequestFieldExt(InformationRequestFieldKey fieldKey, String requestDescription) {
    this.fieldKey = fieldKey;
    this.requestDescription = requestDescription;
  }

  @Schema(description = "Handler's description for information request.")
  public String getRequestDescription() {
    return requestDescription;
  }

  public void setRequestDescription(String requestDescription) {
    this.requestDescription = requestDescription;
  }

  @Schema(description = "Key of the information request field")
  public InformationRequestFieldKey getFieldKey() {
    return fieldKey;
  }

  public void setFieldKey(InformationRequestFieldKey fieldKey) {
    this.fieldKey = fieldKey;
  }

}
