package fi.hel.allu.model.domain;

import org.hibernate.validator.constraints.NotBlank;

/**
 * in Finnish: Yhteyshenkilö
 */
public class Contact implements PostalAddressItem {

  private Integer id;
  private Integer applicantId;
  @NotBlank
  private String name;
  private PostalAddress postalAddress;
  private String email;
  private String phone;
  private boolean isActive = true;

  /**
   * in Finnish: Yhteyshenkilön tunniste
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * in Finnish: Yhteyshenkilön hakijan tunniste
   */
  public Integer getApplicantId() {
    return applicantId;
  }

  public void setApplicantId(Integer applicantId) {
    this.applicantId = applicantId;
  }

  /**
   * In Finnish: nimi
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the postal address of the contant.
   *
   * @return  the postal address of the contact.
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
   * In Finnish: sähköpostiosoite
   */
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * In Finnish: puhelinnumero
   */
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  /**
   * @return  True, if the user is active i.e. has not been marked as deleted.
   */
  public boolean isActive() {
    return isActive;
  }

  public void setIsActive(boolean active) {
    isActive = active;
  }

  public void setActive(boolean active) {
    // JSON deserialization expects setActive() whereas QueryDSL expects setIsActive(). Nice!
    setIsActive(active);
  }
}
