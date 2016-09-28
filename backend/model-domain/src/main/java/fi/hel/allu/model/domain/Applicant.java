package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.ApplicantType;

/**
 * in Finnish: Hakija
 */
public class Applicant {
  private Integer id;
  private ApplicantType type;
  private Integer personId;
  private Integer organizationId;

  /**
   * in Finnish: Hakijan tunniste
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Is applicant person or organization?
   */
  public ApplicantType getType() {
    return type;
  }

  public void setType(ApplicantType type) {
    this.type = type;
  }

  /**
   * in Finnish: Hakijaan liittyvän henkilön tunniste
   */
  public Integer getPersonId() {
    return personId;
  }

  public void setPersonId(Integer personId) {
    this.personId = personId;
  }

  /**
   * in Finnish: Hakijaan liittyvän organisaation tunniste
   */
  public Integer getOrganizationId() {
    return organizationId;
  }

  public void setOrganizationId(Integer organizationId) {
    this.organizationId = organizationId;
  }
}
