package fi.hel.allu.servicecore.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotBlank;

import fi.hel.allu.common.domain.types.CustomerType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Customer")
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

  @Schema(description = "Id of the customer")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(description = "Customer type")
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

  @Schema(description = "Email of the customer")
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

  @Schema(description = "Key of the customer (SSN or business identifier depending on customer type)")
  public String getRegistryKey() {
    return registryKey;
  }

  public void setRegistryKey(String registryKey) {
    this.registryKey = registryKey;
  }

  @Schema(description = "E-invoice identifier (OVT-tunnus)")
  public String getOvt() {
    return ovt;
  }

  public void setOvt(String ovt) {
    this.ovt = ovt;
  }

  @Schema(description = "True, if the customer is active i.e. has not been marked as deleted")
  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  @Schema(description = "SAP customer number")
  public String getSapCustomerNumber() {
    return sapCustomerNumber;
  }

  public void setSapCustomerNumber(String sapCustomerNumber) {
    this.sapCustomerNumber = sapCustomerNumber;
  }

  @Schema(description = "SAP invoicing prohibited (laskutuskielto SAP:ssa)")
  public boolean isInvoicingProhibited() {
    return invoicingProhibited;
  }

  public void setInvoicingProhibited(boolean invoicingProhibited) {
    this.invoicingProhibited = invoicingProhibited;
  }

  @Schema(description = "E-invoicing operator code")
  public String getInvoicingOperator() {
    return invoicingOperator;
  }

  public void setInvoicingOperator(String invoicingOperator) {
    this.invoicingOperator = invoicingOperator;
  }

  @Schema(description = "Customer used only as invoice recipient")
  public boolean isInvoicingOnly() {
    return invoicingOnly;
  }

  public void setInvoicingOnly(boolean invoicingOnly) {
    this.invoicingOnly = invoicingOnly;
  }

  @Schema(description = "Customer's country (ISO 3166-1 alpha-2 country code).")
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  @Schema(description = "Identifier prefix for customer's projects")
  public String getProjectIdentifierPrefix() {
    return projectIdentifierPrefix;
  }

  public void setProjectIdentifierPrefix(String projectIdentifierPrefix) {
    this.projectIdentifierPrefix = projectIdentifierPrefix;
  }
}
