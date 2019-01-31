package fi.hel.allu.model.domain;

import java.util.List;

public class InvoiceRecipientCustomer {

  private Customer customer;
  private List<String> applicationIdentifiers;

  public InvoiceRecipientCustomer() {
  }

  public InvoiceRecipientCustomer(Customer customer, List<String> applicationIdentifiers) {
    this.customer = customer;
    this.applicationIdentifiers = applicationIdentifiers;
  }
  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public List<String> getApplicationIdentifiers() {
    return applicationIdentifiers;
  }

  public void setApplicationIdentifiers(List<String> applicationIdentifiers) {
    this.applicationIdentifiers = applicationIdentifiers;
  }
}
