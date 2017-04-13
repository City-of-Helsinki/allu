package fi.hel.allu.search.domain;

import fi.hel.allu.common.types.ApplicantType;

/**
 * ElasticSearch mapping for applicant.
 */
public class ApplicantES {
  private Integer id;
  private String name;
  private String registryKey;
  private ApplicantType type;
  private boolean isActive;

  public ApplicantES() {
    // JSON serialization
  }

  public ApplicantES(Integer id, String name, String registryKey, ApplicantType type, boolean isActive) {
    this.id = id;
    this.name = name;
    this.registryKey = registryKey;
    this.type = type;
    this.isActive = isActive;
  }

  /**
   * @return  ElasticSearch id of the applicant. The same as database id.
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * @return  Name of the applicant.
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Registry key i.e. business id (Y-tunnus) or social security id (henkilötunnus) of the applicant.
   *
   * @return Registry key.
   */
  public String getRegistryKey() {
    return registryKey;
  }

  public void setRegistryKey(String registryKey) {
    this.registryKey = registryKey;
  }

  /**
   * Type of the applicant (on of person, company, association, property, other)
   */
  public ApplicantType getType() {
    return type;
  }

  public void setType(ApplicantType type) {
    this.type = type;
  }

  /*
   * @return  True, if the user is active i.e. has not been marked as deleted.
   */
  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }
}
