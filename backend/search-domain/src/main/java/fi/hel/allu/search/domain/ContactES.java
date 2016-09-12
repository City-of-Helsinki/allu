package fi.hel.allu.search.domain;

/**
 * ElasticSearch mapping for contact.
 */
public class ContactES {
  private String name;

  public ContactES() {
    // for JSON serialization
  }

  public ContactES(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
