package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.CustomerRoleType;

import java.util.List;

/**
 * Helper structure for updating ElasticSearch application data in case of contact information changes. This class should not be used for
 * other purposes.
 */
public class ApplicationWithContacts {
  int applicationId;
  List<Contact> contacts;
  CustomerRoleType customerRoleType;

  public ApplicationWithContacts() {
    // JSON deserialization
  }

  public ApplicationWithContacts(int applicationId, CustomerRoleType customerRoleType, List<Contact> contacts) {
    this.applicationId = applicationId;
    this.customerRoleType = customerRoleType;
    this.contacts = contacts;
  }

  public int getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(int applicationId) {
    this.applicationId = applicationId;
  }

  public List<Contact> getContacts() {
    return contacts;
  }

  public void setContacts(List<Contact> contacts) {
    this.contacts = contacts;
  }

  public CustomerRoleType getCustomerRoleType() {
    return customerRoleType;
  }

  public void setCustomerRoleType(CustomerRoleType customerRoleType) {
    this.customerRoleType = customerRoleType;
  }
}
