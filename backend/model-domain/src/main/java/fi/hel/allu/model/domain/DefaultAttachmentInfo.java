package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.AttachmentType;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Default attachment info adds default attachment specific information to the regular attachment.
 */
public class DefaultAttachmentInfo extends AttachmentInfo {
  private Integer defaultAttachmentId;
  private List<ApplicationType> applicationTypes;
  private Integer fixedLocationAreaId;

  public DefaultAttachmentInfo() {
  }

  public DefaultAttachmentInfo(
      Integer id,
      Integer userId,
      AttachmentType type,
      String name,
      String description,
      Long size,
      ZonedDateTime creationTime,
      Integer defaultAttachmentId,
      List<ApplicationType> applicationTypes,
      Integer fixedLocationAreaId) {
    super(id, userId, type, name, description, size, creationTime);
    this.defaultAttachmentId = defaultAttachmentId;
    this.applicationTypes = applicationTypes;
    this.fixedLocationAreaId = fixedLocationAreaId;
  }

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
   * Returns id of the fixed location area related to the default attachment.
   *
   * @return  id of the fixed location area related to the default attachment. May be <code>null</code>.
   */
  public Integer getFixedLocationAreaId() {
    return fixedLocationAreaId;
  }

  public void setFixedLocationAreaId(Integer fixedLocationAreaId) {
    this.fixedLocationAreaId = fixedLocationAreaId;
  }
}
