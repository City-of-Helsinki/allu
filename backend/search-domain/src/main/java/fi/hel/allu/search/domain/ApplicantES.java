package fi.hel.allu.search.domain;

/**
 * ElasticSearch mapping for applicant.
 */
public class ApplicantES {
  private String name;

  public ApplicantES() {
    // JSON serialization
  }

  public ApplicantES(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
