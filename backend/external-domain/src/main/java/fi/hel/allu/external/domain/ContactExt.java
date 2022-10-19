package fi.hel.allu.external.domain;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Contact of application customer")
public class ContactExt {
  private Integer id;
  @NotBlank(message = "{contact.name.notblank}")
  private String name;
  private PostalAddressExt postalAddress;
  private String email;
  private String phone;
  private Boolean orderer;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(description = "Name of the contact", required = true)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Schema(description = "Postal address of the contact")
  public PostalAddressExt getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddressExt postalAddress) {
    this.postalAddress = postalAddress;
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

  @Schema(description = "Value indicating whether contact is orderer of the application")
  public Boolean getOrderer() {
    return orderer;
  }

  public void setOrderer(Boolean orderer) {
    this.orderer = orderer;
  }
}
