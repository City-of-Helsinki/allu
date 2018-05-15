package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.InformationRequestFieldKey;

public class InformationRequestResponseField {

  private int informationRequestId;
  private InformationRequestFieldKey fieldKey;

  public InformationRequestResponseField() {
  }

  public InformationRequestResponseField(Integer requestId, InformationRequestFieldKey fieldKey) {
    this.informationRequestId = requestId;
    this.fieldKey = fieldKey;
  }

  public int getInformationRequestId() {
    return informationRequestId;
  }

  public void setInformationRequestId(int informationRequestId) {
    this.informationRequestId = informationRequestId;
  }

  public InformationRequestFieldKey getFieldKey() {
    return fieldKey;
  }

  public void setFieldKey(InformationRequestFieldKey fieldKey) {
    this.fieldKey = fieldKey;
  }

}
