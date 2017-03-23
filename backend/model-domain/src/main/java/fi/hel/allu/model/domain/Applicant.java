package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.ApplicantType;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * in Finnish: Hakija
 *
 * <p>An applicant is either person, organization or a company.
 */
public class Applicant implements PostalAddressItem {
  private Integer id;
  @NotNull
  private ApplicantType type;
  @NotBlank
  private String name;
  private PostalAddress postalAddress;
  private String email;
  private String phone;
  private String registryKey;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Type of the application.
   *
   * @return  Type of the application.
   */
  public ApplicantType getType() {
    return type;
  }

  public void setType(ApplicantType type) {
    this.type = type;
  }

  /**
   * The name of the applicant person, company or organization.
   *
   * @return  The name of the applicant person, company or organization.
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the postal address of the applicant.
   *
   * @return  the postal address of the applicant.
   */
  @Override
  public PostalAddress getPostalAddress() {
    return postalAddress;
  }

  @Override
  public void setPostalAddress(PostalAddress postalAddress) {
    this.postalAddress = postalAddress;
  }

  /**
   * Email of the applicant person, company or organization.
   *
   * @return Email of the applicant person, company or organization.
   */
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Phone number of the applicant person, company or organization.
   *
   * @return  Phone number of the applicant person, company or organization.
   */
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  /**
   * The registry key (social security number or business id i.e. Y-tunnus) of the applicant person, company or organization.
   *
   * @return  The registry key (social security number or business id i.e. Y-tunnus) of the applicant person, company or organization.
   */
  public String getRegistryKey() {
    return registryKey;
  }

  public void setRegistryKey(String registryKey) {
    this.registryKey = registryKey;
  }
}
