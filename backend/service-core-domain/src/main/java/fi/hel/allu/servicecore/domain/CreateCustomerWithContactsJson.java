package fi.hel.allu.servicecore.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "Application customer id and related contacts' ids")
public class CreateCustomerWithContactsJson {

  private Integer customerId;
  private List<Integer> contactIds = new ArrayList<>();

  @ApiModelProperty(value = "Customer id")
  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  @ApiModelProperty(value = "Contact ids of the customer for the application")
  public List<Integer> getContactIds() {
    return contactIds;
  }

  public void setContactIds(List<Integer> contacts) {
    this.contactIds = contacts;
  }
}
