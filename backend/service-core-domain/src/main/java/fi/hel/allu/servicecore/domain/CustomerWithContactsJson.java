package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "Application customer and related contacts")
public class CustomerWithContactsJson {

  private CustomerRoleType roleType;
  private CustomerJson customer;
  private List<ContactJson> contacts = new ArrayList<>();

  public CustomerWithContactsJson() {
  }

  public CustomerWithContactsJson(CustomerRoleType roleType, CustomerJson customer) {
    this.roleType = roleType;
    this.customer = customer;
  }

  public CustomerWithContactsJson(CustomerRoleType roleType, CustomerJson customer, List<ContactJson> contacts) {
    this.roleType = roleType;
    this.customer = customer;
    this.contacts = contacts;
  }

  @ApiModelProperty(value = "Id of the customer (if present)")
  public Integer getId() {
    return (customer == null) ? null : customer.getId();
  }

  @ApiModelProperty(value = "Customer role type")
  public CustomerRoleType getRoleType() {
    return roleType;
  }

  public void setRoleType(CustomerRoleType roleType) {
    this.roleType = roleType;
  }

  @ApiModelProperty(value = "Application customer")
  public CustomerJson getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerJson customer) {
    this.customer = customer;
  }

  @ApiModelProperty(value = "Contacts of the application customer")
  public List<ContactJson> getContacts() {
    return contacts;
  }

  public void setContacts(List<ContactJson> contacts) {
    this.contacts = contacts;
  }
}
