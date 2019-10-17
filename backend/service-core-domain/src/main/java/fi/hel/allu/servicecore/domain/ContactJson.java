package fi.hel.allu.servicecore.domain;

import org.hibernate.validator.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Contact person")
public class ContactJson {
  private Integer id;
  private Integer customerId;
  @NotBlank(message = "{contact.name.notblank}")
  private String name;
  // TODO: refactor as PostalAddressJson
  private String streetAddress;
  private String postalCode;
  private String city;
  private String email;
  private String phone;
  private boolean active;
  private Boolean orderer;

  public ContactJson() {
  }

  public ContactJson(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value = "Id of the contact")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value = "Id of the customer whose contact this is")
  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  @ApiModelProperty(value = "Name of the contact")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @ApiModelProperty(value = "Street address of the contact")
  public String getStreetAddress() {
    return streetAddress;
  }

  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }

  @ApiModelProperty(value = "Postal code of the contact")
  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  @ApiModelProperty(value = "City of the contact")
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  @ApiModelProperty(value = "Email of the contact")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @ApiModelProperty(value = "Phone number of the contact")
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  @ApiModelProperty(value = "True, if the user is active i.e. has not been marked as deleted.")
  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  @ApiModelProperty(hidden = true)
  public Boolean getOrderer() {
    return orderer;
  }

  public void setOrderer(Boolean orderer) {
    this.orderer = orderer;
  }
}
