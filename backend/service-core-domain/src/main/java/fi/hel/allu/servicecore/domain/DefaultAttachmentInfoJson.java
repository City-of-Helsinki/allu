package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.ApplicationType;

import java.util.List;

/**
 * Default attachment (vakioliite) metadata information.
 */
public class DefaultAttachmentInfoJson extends AttachmentInfoJson {
  private Integer defaultAttachmentId;
  private List<ApplicationType> applicationTypes;
  private Integer fixedLocationId;

  /**
   * Returns the database id of default attachment (different than the attachment id of super class).
   *
   * @return  the database id of default attachment (different than the attachment id of super class).
   */
  public Integer getDefaultAttachmentId() {
    return defaultAttachmentId;
  }

  public void setDefaultAttachmentId(Integer defaultAttachmentId) {
    this.defaultAttachmentId = defaultAttachmentId;
  }

  /**
   * Returns list of application types using the default attachment.
   *
   * @return  list of application types using the default attachment. May be <code>null</code>.
   */
  public List<ApplicationType> getApplicationTypes() {
    return applicationTypes;
  }

  public void setApplicationTypes(List<ApplicationType> applicationTypes) {
    this.applicationTypes = applicationTypes;
  }

  /**
   * Returns id of the fixed location related to the default attachment.
   *
   * @return  id of the fixed location related to the default attachment. May be <code>null</code>.
   */
  public Integer getFixedLocationId() {
    return fixedLocationId;
  }

  public void setFixedLocationId(Integer fixedLocationId) {
    this.fixedLocationId = fixedLocationId;
  }
}
