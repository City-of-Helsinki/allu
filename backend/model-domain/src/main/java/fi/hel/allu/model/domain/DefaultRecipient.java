package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.ApplicationType;

/**
 * Default recipient for different application types
 */
public class DefaultRecipient {
  private Integer id;
  private String email;
  private ApplicationType applicationType;

  public DefaultRecipient() {
    // for (de)serialization
  }

  public DefaultRecipient(Integer id, String email, ApplicationType applicationType) {
    this.id = id;
    this.email = email;
    this.applicationType = applicationType;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Email address of the recipient
   *
   * @return email as string
   */
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * The type of the application this recipient is bound to.
   *
   * @return  The type of the application this recipient is bound to.
   */
  public ApplicationType getApplicationType() {
    return applicationType;
  }

  public void setApplicationType(ApplicationType applicationType) {
    this.applicationType = applicationType;
  }
}
