package fi.hel.allu.external.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;

import java.util.List;

/**
 * Allu application customer with its related contacts exposed to external users.
 */
public class CustomerWithContactsExt {
  CustomerRoleType roleType;
  private Integer customer;
  List<Integer> contacts;

  public CustomerRoleType getRoleType() {
    return roleType;
  }

  public void setRoleType(CustomerRoleType roleType) {
    this.roleType = roleType;
  }

  public Integer getCustomer() {
    return customer;
  }

  public void setCustomer(Integer customer) {
    this.customer = customer;
  }

  public List<Integer> getContacts() {
    return contacts;
  }

  public void setContacts(List<Integer> contacts) {
    this.contacts = contacts;
  }
}
