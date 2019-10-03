package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.InformationRequestFieldKey;

/**
 * Field of information request.
 *
 */
public class InformationRequestFieldJson {

  private InformationRequestFieldKey fieldKey;
  private String description;

  public InformationRequestFieldJson() {
  }

  public InformationRequestFieldJson(InformationRequestFieldKey fieldKey, String description) {
    this.fieldKey = fieldKey;
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public InformationRequestFieldKey getFieldKey() {
    return fieldKey;
  }

  public void setFieldKey(InformationRequestFieldKey fieldKey) {
    this.fieldKey = fieldKey;
  }

}
