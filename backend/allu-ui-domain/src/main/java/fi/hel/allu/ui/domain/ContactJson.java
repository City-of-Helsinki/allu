package fi.hel.allu.ui.domain;

import org.hibernate.validator.constraints.NotBlank;

/**
 * in Finnish: Yhteyshenkilö
 */
public class ContactJson {
  private Integer id;
  private Integer applicantId;
  @NotBlank(message = "{contact.name.notblank}")
  private String name;
  // TODO: refactor as PostalAddressJson
  private String streetAddress;
  private String postalCode;
  private String city;
  private String email;
  private String phone;
  private boolean isActive;

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
   * in Finnish: Yhteyshenkilön nimi
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * in Finnish: Katuosoite
   */
  public String getStreetAddress() {
    return streetAddress;
  }

  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }

  /**
   * in Finnish: Postinumer
   */
  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  /**
   * in Finnish: Kaupunki
   */
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  /**
   * in Finnish: Sähköpostiosoite
   */
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * in Finnish: Puhelinnumero
   */
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
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
