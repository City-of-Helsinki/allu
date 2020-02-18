package fi.hel.allu.supervision.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Contact search result")
public class ContactSearchResult {

  private Integer id;
  private String name;
  private String email;
  private String customerName;
  private Integer customerId;

  @ApiModelProperty(value = "Id of the contact")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value = "Name of the contact")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @ApiModelProperty(value = "Email address of the contact")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @ApiModelProperty(value = "Name of the customer whose contact this is")
  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  @ApiModelProperty(value = "Id of the customer whose contact this is")
  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

}
