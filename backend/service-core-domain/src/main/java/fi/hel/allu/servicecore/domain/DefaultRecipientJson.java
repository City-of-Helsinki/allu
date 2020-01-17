package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import javax.validation.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Default recipient for different application types
 */
public class DefaultRecipientJson {
  private Integer id;
  @NotBlank(message = "{defaultRecipient.email}")
  private String email;
  @NotNull(message = "{defaultRecipient.applicationType}")
  private ApplicationType applicationType;

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
