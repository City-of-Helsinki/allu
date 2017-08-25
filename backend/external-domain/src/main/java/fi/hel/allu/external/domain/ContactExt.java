package fi.hel.allu.external.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * in Finnish: Yhteyshenkilö
 */
public class ContactExt {
  private Integer id;
  @NotNull
  private Integer customerId;
  @NotBlank(message = "{contact.name.notblank}")
  private String name;
  private PostalAddressExt postalAddress;
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
  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
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

  public PostalAddressExt getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddressExt postalAddress) {
    this.postalAddress = postalAddress;
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
}
