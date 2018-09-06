package fi.hel.allu.servicecore.domain;

import java.util.Optional;

import fi.hel.allu.common.domain.types.CustomerRoleType;

/**
 * Temporary application data received from client application.
 *
 */
public class ClientApplicationDataJson {

  private CustomerWithContactsJson customer;
  private CustomerJson invoicingCustomer;
  private String clientApplicationKind;
  private CustomerWithContactsJson representative;
  private CustomerWithContactsJson propertyDeveloper;
  private CustomerWithContactsJson contractor;

  public ClientApplicationDataJson() {
  }

  public ClientApplicationDataJson(CustomerWithContactsJson customer, CustomerJson invoicingCustomer,
      CustomerWithContactsJson representative, String clientApplicationKind) {
    this.customer = customer;
    this.invoicingCustomer = invoicingCustomer;
    this.representative = representative;
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

  public CustomerWithContactsJson getRepresentative() {
    return representative;
  }

  public void setRepresentative(CustomerWithContactsJson representative) {
    this.representative = representative;
  }

  public CustomerWithContactsJson getPropertyDeveloper() {
    return propertyDeveloper;
  }

  public void setPropertyDeveloper(CustomerWithContactsJson propertyDeveloper) {
    this.propertyDeveloper = propertyDeveloper;
  }

  public CustomerWithContactsJson getContractor() {
    return contractor;
  }

  public void setContractor(CustomerWithContactsJson contractor) {
    this.contractor = contractor;
  }

}
