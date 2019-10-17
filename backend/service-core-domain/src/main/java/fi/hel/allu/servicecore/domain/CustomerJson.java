package fi.hel.allu.servicecore.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import fi.hel.allu.common.domain.types.CustomerType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Customer")
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
  private String invoicingOperator;
  private boolean invoicingOnly;
  @NotBlank(message = "{customer.country}")
  private String country;
  private String projectIdentifierPrefix;

  public CustomerJson() {
  }

  public CustomerJson(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value = "Id of the customer")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value = "Customer type")
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

  @ApiModelProperty(value = "Email of the customer")
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

  @ApiModelProperty(value = "Key of the customer (SSN or business identifier depending on customer type)")
  public String getRegistryKey() {
    return registryKey;
  }

  public void setRegistryKey(String registryKey) {
    this.registryKey = registryKey;
  }

  @ApiModelProperty(value = "E-invoice identifier (OVT-tunnus)")
  public String getOvt() {
    return ovt;
  }

  public void setOvt(String ovt) {
    this.ovt = ovt;
  }

  @ApiModelProperty(value = "True, if the customer is active i.e. has not been marked as deleted")
  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  @ApiModelProperty(value = "SAP customer number")
  public String getSapCustomerNumber() {
    return sapCustomerNumber;
  }

  public void setSapCustomerNumber(String sapCustomerNumber) {
    this.sapCustomerNumber = sapCustomerNumber;
  }

  @ApiModelProperty(value = "SAP invoicing prohibited (laskutuskielto SAP:ssa)")
  public boolean isInvoicingProhibited() {
    return invoicingProhibited;
  }

  public void setInvoicingProhibited(boolean invoicingProhibited) {
    this.invoicingProhibited = invoicingProhibited;
  }

  @ApiModelProperty(value = "E-invoicing operator code")
  public String getInvoicingOperator() {
    return invoicingOperator;
  }

  public void setInvoicingOperator(String invoicingOperator) {
    this.invoicingOperator = invoicingOperator;
  }

  @ApiModelProperty(value = "Customer used only as invoice recipient")
  public boolean isInvoicingOnly() {
    return invoicingOnly;
  }

  public void setInvoicingOnly(boolean invoicingOnly) {
    this.invoicingOnly = invoicingOnly;
  }

  @ApiModelProperty(value = "Customer's country (ISO 3166-1 alpha-2 country code).")
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  @ApiModelProperty(value = "Identifier prefix for customer's projects")
  public String getProjectIdentifierPrefix() {
    return projectIdentifierPrefix;
  }

  public void setProjectIdentifierPrefix(String projectIdentifierPrefix) {
    this.projectIdentifierPrefix = projectIdentifierPrefix;
  }
}
