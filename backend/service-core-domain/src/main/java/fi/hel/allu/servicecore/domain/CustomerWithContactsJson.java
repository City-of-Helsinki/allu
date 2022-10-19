package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Application customer and related contacts")
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

  @Schema(description = "Id of the customer (if present)")
  public Integer getId() {
    return (customer == null) ? null : customer.getId();
  }

  @Schema(description = "Customer role type")
  public CustomerRoleType getRoleType() {
    return roleType;
  }

  public void setRoleType(CustomerRoleType roleType) {
    this.roleType = roleType;
  }

  @Schema(description = "Application customer")
  public CustomerJson getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerJson customer) {
    this.customer = customer;
  }

  @Schema(description = "Contacts of the application customer")
  public List<ContactJson> getContacts() {
    return contacts;
  }

  public void setContacts(List<ContactJson> contacts) {
    this.contacts = contacts;
  }
}
