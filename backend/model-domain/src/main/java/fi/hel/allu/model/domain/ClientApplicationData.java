package fi.hel.allu.model.domain;

/**
 * Temporary application data received from client application.
 *
 */
public class ClientApplicationData {

  CustomerWithContacts customer;
  Customer invoicingCustomer;
  CustomerWithContacts representative;
  CustomerWithContacts contractor;
  CustomerWithContacts propertyDeveloper;
  String clientApplicationKind;


  public ClientApplicationData() {
  }

  public ClientApplicationData(CustomerWithContacts customer, Customer invoicingCustomer,
      CustomerWithContacts representative, CustomerWithContacts contractor, CustomerWithContacts propertyDeveloper,
      String clientApplicationKind) {
    this.customer = customer;
    this.invoicingCustomer = invoicingCustomer;
    this.representative = representative;
    this.contractor = contractor;
    this.propertyDeveloper = propertyDeveloper;
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

  public CustomerWithContacts getRepresentative() {
    return representative;
  }

  public void setRepresentative(CustomerWithContacts representative) {
    this.representative = representative;
  }

  public CustomerWithContacts getContractor() {
    return contractor;
  }

  public void setContractor(CustomerWithContacts contractor) {
    this.contractor = contractor;
  }

  public CustomerWithContacts getPropertyDeveloper() {
    return propertyDeveloper;
  }

  public void setPropertyDeveloper(CustomerWithContacts propertyDeveloper) {
    this.propertyDeveloper = propertyDeveloper;
  }
}
