package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;

import java.util.ArrayList;
import java.util.List;

/**
 * Customer with role and its related contacts in the context of an application.
 */
public class CustomerWithContacts implements CustomerWithContactsI {
  CustomerRoleType roleType;
  Customer customer;
  List<Contact> contacts = new ArrayList<>();

  public CustomerWithContacts() {
  }

  public CustomerWithContacts(CustomerRoleType roleType, Customer customer, List<Contact> contacts) {
    this.roleType = roleType;
    this.customer = customer;
    this.contacts = contacts;
  }


  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  /**
   * @return  The role of the customer in the context of an application.
   */
  public CustomerRoleType getRoleType() {
    return roleType;
  }

  public void setRoleType(CustomerRoleType roleType) {
    this.roleType = roleType;
  }

  /**
   * @return  Customer.
   */
  public Customer getCustomer() {
    return customer;
  }

  public void setCustomerId(Customer customer) {
    this.customer= customer;
  }

  /**
   * @return  The contacts of the customer used by application.
   */
  public List<Contact> getContacts() {
    return contacts;
  }

  public void setContacts(List<Contact> contacts) {
    this.contacts = contacts;
  }
}