package fi.hel.allu.model.domain.meta;

/**
 * Metadata of a single application attribute like street address or application name. Notice that a single structural attribute might
 * consist of several attributes.
 */
public class AttributeMeta {

  private String name;
  private String uiName;
  private AttributeDataType dataType;
  private AttributeDataType listType;
  private Integer structureAttribute;
  private StructureMeta structureMeta;
  private String validationRule;

  /**
   * Returns the name of the attribute, for example streetAddress.
   *
   * @return  the name of the attribute.
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the UI name of the attribute, for example "Street address".
   *
   * @return  the UI name of the attribute.
   */
  public String getUiName() {
    return uiName;
  }

  public void setUiName(String uiName) {
    this.uiName = uiName;
  }

  /**
   * Returns the data type of the attribute.
   *
   * @return  the data type of the attribute.
   */
  public AttributeDataType getDataType() {
    return dataType;
  }

  public void setDataType(AttributeDataType dataType) {
    this.dataType = dataType;
  }

  /**
   * Returns the data type of a list item.
   *
   * @return  the data type of a list item or <code>null</code> if attribute is not of a list type.
   */
  public AttributeDataType getListType() {
    return listType;
  }

  public void setListType(AttributeDataType listType) {
    this.listType = listType;
  }

  /**
   * Returns the id of the structure attribute i.e. the structure this attribute contains.
   *
   * @return  the id of the structure attribute or <code>null</code>.
   */
  public Integer getStructureAttribute() {
    return structureAttribute;
  }

  public void setStructureAttribute(Integer structureAttribute) {
    this.structureAttribute = structureAttribute;
  }

  /**
   * Returns the structure type of attribute.
   *
   * @return  the structure type of attribute or <code>null</code> if attribute is not a structure.
   */
  public StructureMeta getStructureMeta() {
    return structureMeta;
  }

  public void setStructureMeta(StructureMeta structureMeta) {
    this.structureMeta = structureMeta;
  }

  /**
   * Returns the validation rule of the attribute.
   *
   * @return  the validation rule of the attribute.
   */
  public String getValidationRule() {
    return validationRule;
  }

  public void setValidationRule(String validationRule) {
    this.validationRule = validationRule;
  }

  @Override
  public String toString() {
    return "AttributeMeta{" +
        "name='" + name + '\'' +
        ", uiName='" + uiName + '\'' +
        ", dataType=" + dataType +
        ", listType=" + listType +
        ", structureAttribute=" + structureAttribute +
        ", structureMeta=" + structureMeta +
        ", validationRule='" + validationRule + '\'' +
        '}';
  }
}
