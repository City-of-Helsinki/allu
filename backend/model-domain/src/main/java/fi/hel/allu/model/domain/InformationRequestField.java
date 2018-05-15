package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.InformationRequestFieldKey;

public class InformationRequestField {

  private Integer informationRequestId;
  private InformationRequestFieldKey fieldKey;
  private String description;

  public InformationRequestField() {
  }

  public InformationRequestField(Integer informationRequestId, InformationRequestFieldKey fieldKey,
      String description) {
    this.informationRequestId = informationRequestId;
    this.fieldKey = fieldKey;
    this.description = description;
  }

  public Integer getInformationRequestId() {
    return informationRequestId;
  }

  public void setInformationRequestId(Integer informationRequestId) {
    this.informationRequestId = informationRequestId;
  }

  public InformationRequestFieldKey getFieldKey() {
    return fieldKey;
  }

  public void setFieldKey(InformationRequestFieldKey fieldKey) {
    this.fieldKey = fieldKey;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
