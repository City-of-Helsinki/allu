package fi.hel.allu.search.domain;

/**
 * ElasticSearch mapping for contact.
 */
public class ContactES {
  private Integer id;
  private String name;
  private boolean isActive;

  public ContactES() {
    // for JSON serialization
  }

  public ContactES(Integer id, String name, boolean isActive) {
    this.id = id;
    this.name = name;
    this.isActive = isActive;
  }

  /**
   * The id of the contact in ElasticSearch is the same as id in database.
   *
   * @return id of the Contact.
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * @return  name of the contact.
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return  True, if the user is active i.e. has not been marked as deleted.
   */
  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }
}
