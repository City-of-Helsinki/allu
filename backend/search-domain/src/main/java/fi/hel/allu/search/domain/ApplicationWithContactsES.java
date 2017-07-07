package fi.hel.allu.search.domain;

import fi.hel.allu.common.domain.types.CustomerRoleType;

import java.util.List;

/**
 * Helper structure for updating ElasticSearch application data in case of contact information changes.
 */
public class ApplicationWithContactsES {
  int applicationId;
  List<ContactES> contacts;
  CustomerRoleType customerRoleType;

  public ApplicationWithContactsES() {
    // JSON deserialization
  }

  public ApplicationWithContactsES(int applicationId, CustomerRoleType customerRoleType, List<ContactES> contacts) {
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

  public List<ContactES> getContacts() {
    return contacts;
  }

  public void setContacts(List<ContactES> contacts) {
    this.contacts = contacts;
  }

  public CustomerRoleType getCustomerRoleType() {
    return customerRoleType;
  }

  public void setCustomerRoleType(CustomerRoleType customerRoleType) {
    this.customerRoleType = customerRoleType;
  }
}
