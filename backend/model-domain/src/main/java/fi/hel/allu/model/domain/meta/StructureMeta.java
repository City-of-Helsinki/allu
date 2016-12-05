package fi.hel.allu.model.domain.meta;

import java.util.List;

/**
 * Collection of attribute metadata modeling a single application type like Event.
 */
public class StructureMeta {
  private int id;
  private String applicationType;
  private int version;
  private List<AttributeMeta> attributes;

  /**
   * Returns the database id of the structure.
   *
   * @return  the database id of the structure.
   */
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  /**
   * Returns the type of the application. For example Event.
   *
   * @return  the type of the application.
   */
  public String getApplicationType() {
    return applicationType;
  }

  public void setApplicationType(String applicationType) {
    this.applicationType = applicationType;
  }

  /**
   * Returns the version of the metadata.
   *
   * @return
   */
  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  /**
   * Returns the attribute metadata related to this structure.
   *
   * @return  the attribute metadata related to this structure.
   */
  public List<AttributeMeta> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<AttributeMeta> attributes) {
    this.attributes = attributes;
  }

  @Override
  public String toString() {
    return "StructureMeta{" +
        "id=" + id +
        ", applicationType='" + applicationType + '\'' +
        ", version=" + version +
        ", attributes=" + attributes +
        '}';
  }
}
