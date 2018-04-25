package fi.hel.allu.model.domain;

/**
 * Temporary application data received from client application.
 *
 */
public class ClientApplicationData {

  CustomerWithContacts customer;
  Customer invoicingCustomer;
  String clientApplicationKind;

  public ClientApplicationData() {
  }

  public ClientApplicationData(CustomerWithContacts customer, Customer invoicingCustomer,
      String clientApplicationKind) {
    this.customer = customer;
    this.invoicingCustomer = invoicingCustomer;
    this.clientApplicationKind = clientApplicationKind;
  }

  public CustomerWithContacts getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerWithContacts customer) {
    this.customer = customer;
  }

  public Customer getInvoicingCustomer() {
    return invoicingCustomer;
  }

  public void setInvoicingCustomer(Customer invoicingCustomer) {
    this.invoicingCustomer = invoicingCustomer;
  }

  public String getClientApplicationKind() {
    return clientApplicationKind;
  }

  public void setClientApplicationKind(String clientApplicationKind) {
    this.clientApplicationKind = clientApplicationKind;
  }
}
