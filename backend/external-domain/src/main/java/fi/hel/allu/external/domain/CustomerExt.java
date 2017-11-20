package fi.hel.allu.external.domain;

import fi.hel.allu.common.domain.types.CustomerType;
import org.hibernate.validator.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * in Finnish: Asiakas (esimerkiksi Hakija, Rakennuttaja, Työn suorittaja ja Asiamies).
 *
 * <p>A customer is either person, organization or a company.
 */
public class CustomerExt {
  private Integer id;
  @NotNull
  private CustomerType type;
  @NotBlank
  private String name;
  private PostalAddressExt postalAddress;
  private String email;
  private String phone;
  private String registryKey;
  private String ovt;
  private String sapCustomerNumber;
  private Boolean invoicingProhibited;

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
  public CustomerType getType() {
    return type;
  }

  public void setType(CustomerType type) {
    this.type = type;
  }

  /**
   * The name of the customer person, company or organization.
   *
   * @return  The name of the customer person, company or organization.
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the postal address of the customer.
   *
   * @return  the postal address of the customer.
   */
  public PostalAddressExt getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddressExt postalAddress) {
    this.postalAddress = postalAddress;
  }

  /**
   * Email of the customer person, company or organization.
   *
   * @return Email of the customer person, company or organization.
   */
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Phone number of the customer person, company or organization.
   *
   * @return  Phone number of the customer person, company or organization.
   */
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  /**
   * The registry key (social security number or business id i.e. Y-tunnus) of the customer person, company or organization.
   *
   * @return  The registry key (social security number or business id i.e. Y-tunnus) of the customer person, company or organization.
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

  /**
   * SAP Customer Number (KUNNR)
   */
  public String getSapCustomerNumber() {
    return sapCustomerNumber;
  }

  public void setSapCustomerNumber(String sapCustomerNumber) {
    this.sapCustomerNumber = sapCustomerNumber;
  }

  /**
   * SAP invoicing prohibited (SAP laskutuskielto)
   *
   * @return
   */
  public Boolean getInvoicingProhibited() {
    return invoicingProhibited;
  }

  public void setInvoicingProhibited(Boolean invoicingProhibited) {
    this.invoicingProhibited = invoicingProhibited;
  }
}
