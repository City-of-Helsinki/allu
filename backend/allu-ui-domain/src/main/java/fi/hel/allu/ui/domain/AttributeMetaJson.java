package fi.hel.allu.ui.domain;

import fi.hel.allu.model.domain.meta.AttributeDataType;

/**
 * JSON for describing metadata related to attributes of applications.
 */
public class AttributeMetaJson {
  private String name;
  private String uiName;
  private AttributeDataType dataType;
  private AttributeDataType listType;
  private StructureMetaJson structureMeta;
  private String validationRule;

  /**
   * @return  the technical name of the attribute.
   */
  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return  the name of the attribute shown in UI.
   */
  public String getUiName() {
    return uiName;
  }

  public void setUiName(String uiName) {
    this.uiName = uiName;
  }

  /**
   * @return  the data type of the attribute.
   */
  public AttributeDataType getDataType() {
    return dataType;
  }

  public void setDataType(AttributeDataType dataType) {
    this.dataType = dataType;
  }

  /**
   * @return  the data type of attributes in a list or <code>null</code>, if not a list.
   */
  public AttributeDataType getListType() {
    return listType;
  }

  public void setListType(AttributeDataType listType) {
    this.listType = listType;
  }

  /**
   * @return  the structure metadata of the attribute or <code>null</code>, if attribute is not a structure.
   */
  public StructureMetaJson getStructureMeta() {
    return structureMeta;
  }

  public void setStructureMeta(StructureMetaJson structureMeta) {
    this.structureMeta = structureMeta;
  }

  /**
   * @return  the validation rule related to the attribute.
   */
  public String getValidationRule() {
    return validationRule;
  }

  public void setValidationRule(String validationRule) {
    this.validationRule = validationRule;
  }
}
