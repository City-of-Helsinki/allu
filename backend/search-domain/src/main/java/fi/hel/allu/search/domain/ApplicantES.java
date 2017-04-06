package fi.hel.allu.search.domain;

/**
 * ElasticSearch mapping for applicant.
 */
public class ApplicantES {
  private Integer id;
  private String name;
  private String registryKey;

  // TODO: add ApplicantType as indexed value too

  public ApplicantES() {
    // JSON serialization
  }

  public ApplicantES(Integer id, String name, String registryKey) {
    this.id = id;
    this.name = name;
    this.registryKey = registryKey;
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
}
