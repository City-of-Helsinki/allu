package fi.hel.allu.servicecore.domain;

import fi.hel.allu.model.domain.ContactInterface;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@Schema(description = "Contact person")
public class ContactJson implements ContactInterface {
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

  public ContactJson(Integer id, Integer customerId, String name, String streetAddress, String postalCode, String city, String email, String phone, boolean active, Boolean orderer) {
    this.id = id;
    this.customerId = customerId;
    this.name = name;
    this.streetAddress = streetAddress;
    this.postalCode = postalCode;
    this.city = city;
    this.email = email;
    this.phone = phone;
    this.active = active;
    this.orderer = orderer;
  }

  @Schema(description = "Id of the contact")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(description = "Id of the customer whose contact this is")
  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  @Schema(description = "Name of the contact")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Schema(description = "Street address of the contact")
  public String getStreetAddress() {
    return streetAddress;
  }

  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }

  @Schema(description = "Postal code of the contact")
  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  @Schema(description = "City of the contact")
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  @Schema(description = "Email of the contact")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Schema(description = "Phone number of the contact")
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  @Schema(description = "True, if the user is active i.e. has not been marked as deleted.")
  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  @Schema(hidden = true)
  public Boolean getOrderer() {
    return orderer;
  }

  public void setOrderer(Boolean orderer) {
    this.orderer = orderer;
  }
}
