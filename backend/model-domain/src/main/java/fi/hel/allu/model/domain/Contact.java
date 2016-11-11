package fi.hel.allu.model.domain;

import org.hibernate.validator.constraints.NotBlank;

/**
 * in Finnish: Yhteyshenkilö
 */
public class Contact {

  private Integer id;
  private Integer applicantId;
  @NotBlank
  private String name;
  private String streetAddress;
  private String postalCode;
  private String city;
  private String email;
  private String phone;

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
   * In Finnish: katuosoite
   */
  public String getStreetAddress() {
    return streetAddress;
  }

  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }

  /**
   * In Finnish: postinumero
   */
  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  /**
   * In Finnish: kaupunki
   */
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
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

}
