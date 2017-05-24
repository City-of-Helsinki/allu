package fi.hel.allu.search.domain;

import java.util.List;

/**
 * ElasticSearch mapping for customer with contacts.
 */
public class CustomerWithContactsES {
  private CustomerES customer;
  private List<ContactES> contacts;

  public CustomerES getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerES customer) {
    this.customer = customer;
  }

  public List<ContactES> getContacts() {
    return contacts;
  }

  public void setContacts(List<ContactES> contacts) {
    this.contacts = contacts;
  }
}
