package fi.hel.allu.external.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import fi.hel.allu.common.domain.types.CustomerType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * in Finnish: Asiakas (esimerkiksi Hakija, Rakennuttaja, Työn suorittaja ja Asiamies).
 *
 * <p>A customer is either person, organization or a company.
 */
@ApiModel(value="Application customer information")
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
  private String invoicingOperator;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value="Customer type", allowableValues = "PERSON, COMPANY, ASSOCIATION, OTHER")
  public CustomerType getType() {
    return type;
  }

  public void setType(CustomerType type) {
    this.type = type;
  }

  @ApiModelProperty(value = "The name of the customer person, company or organization.")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @ApiModelProperty(value = "The postal address of the customer")
  public PostalAddressExt getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddressExt postalAddress) {
    this.postalAddress = postalAddress;
  }

   @ApiModelProperty(value = "Email of the customer person, company or organization.")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @ApiModelProperty(value = "Phone number of the customer person, company or organization.")
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  @ApiModelProperty(value = "The registry key (social security number or business id i.e. Y-tunnus) of the customer person, company or organization.")
  public String getRegistryKey() {
    return registryKey;
  }

  public void setRegistryKey(String registryKey) {
    this.registryKey = registryKey;
  }

  @ApiModelProperty(value = "E-invoice identifier of the customer (OVT-tunnus).")
  public String getOvt() {
    return ovt;
  }

  public void setOvt(String ovt) {
    this.ovt = ovt;
  }

  @ApiModelProperty(value = "E-invoicing operator code.")
  public String getInvoicingOperator() {
    return invoicingOperator;
  }

  public void setInvoicingOperator(String invoicingOperator) {
    this.invoicingOperator = invoicingOperator;
  }
}
