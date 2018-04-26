package fi.hel.allu.external.domain;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "Application customer and related contacts")
public class CustomerWithContactsExt {
  private CustomerExt customer;
  private List<ContactExt> contacts = new ArrayList<>();

  public CustomerExt getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerExt customer) {
    this.customer = customer;
  }

  public List<ContactExt> getContacts() {
    return contacts;
  }

  public void setContacts(List<ContactExt> contacts) {
    this.contacts = contacts;
  }

}
