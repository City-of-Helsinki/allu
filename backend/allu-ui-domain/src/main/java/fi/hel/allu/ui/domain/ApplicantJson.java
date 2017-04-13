package fi.hel.allu.ui.domain;

import fi.hel.allu.common.types.ApplicantType;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ApplicantJson {

  private Integer id;
  @NotNull(message = "{applicant.type.notnull}")
  private ApplicantType type;
  @NotBlank(message = "{applicant.name}")
  private String name;
  @Valid
  private PostalAddressJson postalAddress;
  private String email;
  private String phone;
  private String registryKey;
  private boolean isActive;

  /**
   * in Finnish: Tietokantatunniste
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * in Finnish: Hakijan tyyppi: yksityinen ihminen, yritys tai yhteisö.
   */
  public ApplicantType getType() {
    return type;
  }

  public void setType(ApplicantType type) {
    this.type = type;
  }

  /**
   * in Finnish: Henkilön/yrityksen/yhteisön nimi
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * in Finnish: Henkilön/yrityksen/yhteisön osoitetiedot
   */
  public PostalAddressJson getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddressJson postalAddress) {
    this.postalAddress = postalAddress;
  }

  /**
   * in Finnish: Henkilön/yrityksen/yhteisön sähköpostiosoite
   */
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * in Finnish: Henkilön/yrityksen/yhteisön puhelinnumero
   */
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  /**
   * in Finnish: Henkilön henkilötunnus tai yrityksen/yhteisön Y-tunnus.
   */
  public String getRegistryKey() {
    return registryKey;
  }

  public void setRegistryKey(String registryKey) {
    this.registryKey = registryKey;
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
