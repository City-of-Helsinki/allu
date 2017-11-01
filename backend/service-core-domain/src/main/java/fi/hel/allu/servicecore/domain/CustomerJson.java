package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.CustomerType;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class CustomerJson {

  private Integer id;
  @NotNull(message = "{customer.type.notnull}")
  private CustomerType type;
  @NotBlank(message = "{customer.name}")
  private String name;
  @Valid
  private PostalAddressJson postalAddress;
  private String email;
  private String phone;
  private String registryKey;
  private String ovt;
  private boolean active;
  private String sapCustomerNumber;
  private boolean invoicingProhibited;

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
  public CustomerType getType() {
    return type;
  }

  public void setType(CustomerType type) {
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

  /**
   * E-invoice identifier of the customer (OVT-tunnus).
   */
  public String getOvt() {
    return ovt;
  }

  public void setOvt(String ovt) {
    this.ovt = ovt;
  }

  /*
   * @return  True, if the user is active i.e. has not been marked as deleted.
   */
  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  /**
   * SAP Customer Number (KUNNR)
   *
   */
  public String getSapCustomerNumber() {
    return sapCustomerNumber;
  }

  public void setSapCustomerNumber(String sapCustomerNumber) {
    this.sapCustomerNumber = sapCustomerNumber;
  }

  /**
   * SAP invoicing prohibited (SAP laskutuskielto)
   * @return
   */
  public boolean isInvoicingProhibited() {
    return invoicingProhibited;
  }

  public void setInvoicingProhibited(boolean invoicingProhibited) {
    this.invoicingProhibited = invoicingProhibited;
  }

}
