package fi.hel.allu.servicecore.domain;

/**
 * Temporary application data received from client application.
 *
 */
public class ClientApplicationDataJson {

  private CustomerWithContactsJson customer;
  private CustomerJson invoicingCustomer;
  private String clientApplicationKind;

  public ClientApplicationDataJson() {
  }

  public ClientApplicationDataJson(CustomerWithContactsJson customer, CustomerJson invoicingCustomer,
      String clientApplicationKind) {
    this.customer = customer;
    this.invoicingCustomer = invoicingCustomer;
    this.clientApplicationKind = clientApplicationKind;
  }

  public CustomerWithContactsJson getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerWithContactsJson customer) {
    this.customer = customer;
  }

  public CustomerJson getInvoicingCustomer() {
    return invoicingCustomer;
  }

  public void setInvoicingCustomer(CustomerJson invoicingCustomer) {
    this.invoicingCustomer = invoicingCustomer;
  }

  public String getClientApplicationKind() {
    return clientApplicationKind;
  }

  public void setClientApplicationKind(String clientApplicationKind) {
    this.clientApplicationKind = clientApplicationKind;
  }

}
