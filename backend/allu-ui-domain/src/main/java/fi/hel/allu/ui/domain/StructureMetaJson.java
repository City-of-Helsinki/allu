package fi.hel.allu.ui.domain;

import java.util.List;

/**
 * JSON for describing metadata structures related to application.
 */
public class StructureMetaJson {
  private String typeName;
  private int version;
  private List<AttributeMetaJson> attributes;

  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public List<AttributeMetaJson> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<AttributeMetaJson> attributes) {
    this.attributes = attributes;
  }
}
