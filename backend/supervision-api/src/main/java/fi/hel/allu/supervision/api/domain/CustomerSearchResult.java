package fi.hel.allu.supervision.api.domain;

import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.servicecore.domain.PostalAddressJson;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Customer search result")
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

  @ApiModelProperty(value = "Id of the customer")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value = "Type of the customer")
  public CustomerType getType() {
    return type;
  }

  public void setType(CustomerType type) {
    this.type = type;
  }

  @ApiModelProperty(value = "Name of the customer")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @ApiModelProperty(value = "Postal address of the customer")
  public PostalAddressJson getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddressJson postalAddress) {
    this.postalAddress = postalAddress;
  }

  @ApiModelProperty(value = "Email address of the customer")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @ApiModelProperty(value = "Phone number of the customer")
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  @ApiModelProperty(value = "Registry key of the customer")
  public String getRegistryKey() {
    return registryKey;
  }

  public void setRegistryKey(String registryKey) {
    this.registryKey = registryKey;
  }

  @ApiModelProperty(value = "OVT number of the customer")
  public String getOvt() {
    return ovt;
  }

  public void setOvt(String ovt) {
    this.ovt = ovt;
  }

  @ApiModelProperty(value = "Is the customer entity currently active? Inactive customers are ignored in most functionality")
  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  @ApiModelProperty(value = "SAP number of the customer")
  public String getSapCustomerNumber() {
    return sapCustomerNumber;
  }

  public void setSapCustomerNumber(String sapCustomerNumber) {
    this.sapCustomerNumber = sapCustomerNumber;
  }

  @ApiModelProperty(value = "Is invoicing of the customer prohibited")
  public boolean isInvoicingProhibited() {
    return invoicingProhibited;
  }

  public void setInvoicingProhibited(boolean invoicingProhibited) {
    this.invoicingProhibited = invoicingProhibited;
  }

  @ApiModelProperty(value = "Invoicing operator of the customer")
  public String getInvoicingOperator() {
    return invoicingOperator;
  }

  public void setInvoicingOperator(String invoicingOperator) {
    this.invoicingOperator = invoicingOperator;
  }

  @ApiModelProperty(value = "Is the customer for invoicing only")
  public boolean isInvoicingOnly() {
    return invoicingOnly;
  }

  public void setInvoicingOnly(boolean invoicingOnly) {
    this.invoicingOnly = invoicingOnly;
  }

  @ApiModelProperty(value = "Country of the customer")
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  @ApiModelProperty(value = "Project identifier prefix of the customer")
  public String getProjectIdentifierPrefix() {
    return projectIdentifierPrefix;
  }

  public void setProjectIdentifierPrefix(String projectIdentifierPrefix) {
    this.projectIdentifierPrefix = projectIdentifierPrefix;
  }
}
