package fi.hel.allu.ui.domain;

import fi.hel.allu.model.domain.meta.AttributeMeta;

import java.util.List;

/**
 * JSON for describing metadata structures related to application.
 */
public class StructureMetaJson {
  private String applicationType;
  private int version;
  private List<AttributeMetaJson> attributes;

  public String getApplicationType() {
    return applicationType;
  }

  public void setApplicationType(String applicationType) {
    this.applicationType = applicationType;
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
