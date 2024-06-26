package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.CustomerType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * in Finnish: Asiakas (esimerkiksi Hakija, Rakennuttaja, Työn suorittaja ja Asiamies).
 *
 * <p>A customer is either person, organization or a company.
 */
public class Customer implements PostalAddressItem, CustomerInterface {
  private Integer id;
  @NotNull
  private CustomerType type;
  @NotBlank
  private String name;
  private PostalAddress postalAddress;
  private String email;
  private String phone;
  private String registryKey;
  private String ovt;
  private boolean isActive = true;
  private String sapCustomerNumber;
  private boolean invoicingProhibited = false;
  private String invoicingOperator;
  private boolean invoicingOnly;
  @NotNull
  private Integer countryId;
  private String projectIdentifierPrefix;

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
  @Override
  public PostalAddress getPostalAddress() {
    return postalAddress;
  }

  @Override
  public void setPostalAddress(PostalAddress postalAddress) {
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

  /**
   * SAP Customer number (KUNNR)
   * @return
   */
  public String getSapCustomerNumber() {
    return sapCustomerNumber;
  }

  public void setSapCustomerNumber(String sapCustomerNumber) {
    this.sapCustomerNumber = sapCustomerNumber;
  }

  /**
   * SAP invoicing prohibited (SPERR)
   */
  public boolean isInvoicingProhibited() {
    return invoicingProhibited;
  }

  public void setInvoicingProhibited(boolean invoicingProhibited) {
    this.invoicingProhibited = invoicingProhibited;
  }

  /**
   * E-invoicing operator code
   */
  public String getInvoicingOperator() {
    return invoicingOperator;
  }

  public void setInvoicingOperator(String invoicingOperator) {
    this.invoicingOperator = invoicingOperator;
  }

  /**
   * Customer is only for invoicing, not to be shown on regular customer list.
   */
  public boolean isInvoicingOnly() {
    return invoicingOnly;
  }

  public void setInvoicingOnly(boolean invoicingOnly) {
    this.invoicingOnly = invoicingOnly;
  }

  public Integer getCountryId() {
    return countryId;
  }

  public void setCountryId(Integer countryId) {
    this.countryId = countryId;
  }

  public String getProjectIdentifierPrefix() {
    return projectIdentifierPrefix;
  }

  public void setProjectIdentifierPrefix(String projectIdentifierPrefix) {
    this.projectIdentifierPrefix = projectIdentifierPrefix;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Customer customer = (Customer) o;

    return id != null ? id.equals(customer.id) : customer.id == null;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}