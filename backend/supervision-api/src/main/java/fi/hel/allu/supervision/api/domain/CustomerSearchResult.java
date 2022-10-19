package fi.hel.allu.supervision.api.domain;

import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.servicecore.domain.PostalAddressJson;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Customer search result")
public class CustomerSearchResult {

  private Integer id;
  private CustomerType type;
  private String name;
  private PostalAddressJson postalAddress;
  private String email;
  private String phone;
  private String registryKey;
  private String ovt;
  private boolean active;
  private String sapCustomerNumber;
  private boolean invoicingProhibited;
  private String invoicingOperator;
  private boolean invoicingOnly;
  private String country;
  private String projectIdentifierPrefix;

  @Schema(description = "Id of the customer")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(description = "Type of the customer")
  public CustomerType getType() {
    return type;
  }

  public void setType(CustomerType type) {
    this.type = type;
  }

  @Schema(description = "Name of the customer")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Schema(description = "Postal address of the customer")
  public PostalAddressJson getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddressJson postalAddress) {
    this.postalAddress = postalAddress;
  }

  @Schema(description = "Email address of the customer")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Schema(description = "Phone number of the customer")
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  @Schema(description = "Registry key of the customer")
  public String getRegistryKey() {
    return registryKey;
  }

  public void setRegistryKey(String registryKey) {
    this.registryKey = registryKey;
  }

  @Schema(description = "OVT number of the customer")
  public String getOvt() {
    return ovt;
  }

  public void setOvt(String ovt) {
    this.ovt = ovt;
  }

  @Schema(description = "Is the customer entity currently active? Inactive customers are ignored in most functionality")
  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  @Schema(description = "SAP number of the customer")
  public String getSapCustomerNumber() {
    return sapCustomerNumber;
  }

  public void setSapCustomerNumber(String sapCustomerNumber) {
    this.sapCustomerNumber = sapCustomerNumber;
  }

  @Schema(description = "Is invoicing of the customer prohibited")
  public boolean isInvoicingProhibited() {
    return invoicingProhibited;
  }

  public void setInvoicingProhibited(boolean invoicingProhibited) {
    this.invoicingProhibited = invoicingProhibited;
  }

  @Schema(description = "Invoicing operator of the customer")
  public String getInvoicingOperator() {
    return invoicingOperator;
  }

  public void setInvoicingOperator(String invoicingOperator) {
    this.invoicingOperator = invoicingOperator;
  }

  @Schema(description = "Is the customer for invoicing only")
  public boolean isInvoicingOnly() {
    return invoicingOnly;
  }

  public void setInvoicingOnly(boolean invoicingOnly) {
    this.invoicingOnly = invoicingOnly;
  }

  @Schema(description = "Country of the customer")
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  @Schema(description = "Project identifier prefix of the customer")
  public String getProjectIdentifierPrefix() {
    return projectIdentifierPrefix;
  }

  public void setProjectIdentifierPrefix(String projectIdentifierPrefix) {
    this.projectIdentifierPrefix = projectIdentifierPrefix;
  }
}
