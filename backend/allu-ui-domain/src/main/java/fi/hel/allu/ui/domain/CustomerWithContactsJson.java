package fi.hel.allu.ui.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class for communicating changes to customer and its related contacts to and back from frontend.
 */
public class CustomerWithContactsJson {
  CustomerRoleType roleType;
  CustomerJson customer;
  List<ContactJson> contacts = new ArrayList<>();

  /**
   * @return  The role type of the customer.
   */
  public CustomerRoleType getRoleType() {
    return roleType;
  }

  public void setRoleType(CustomerRoleType roleType) {
    this.roleType = roleType;
  }

  /**
   * Created or updated customer.
   *
   * @return  Created or updated customer. May be <code>null</code> in case customer is not changed, but related contacts are (the
   *          id of the customer must be communicated by other means than as instance variable).
   */
  public CustomerJson getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerJson customer) {
    this.customer = customer;
  }

  /**
   * Created or updated contacts. If a contact is neither created nor updated, it should not appear on the list!
   *
   * @return  List of created or updated contacts. If contact has an id, it's considered as an update.
   */
  public List<ContactJson> getContacts() {
    return contacts;
  }

  public void setContacts(List<ContactJson> contacts) {
    this.contacts = contacts;
  }
}
