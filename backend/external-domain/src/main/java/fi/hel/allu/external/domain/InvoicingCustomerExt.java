package fi.hel.allu.external.domain;

/**
 * Used in customer updates (SAP customer update).
 *
 */
public class InvoicingCustomerExt extends CustomerExt {

  private String sapCustomerNumber;
  private Boolean invoicingProhibited;

  public String getSapCustomerNumber() {
    return sapCustomerNumber;
  }

  public void setSapCustomerNumber(String sapCustomerNumber) {
    this.sapCustomerNumber = sapCustomerNumber;
  }

  public Boolean getInvoicingProhibited() {
    return invoicingProhibited;
  }

  public void setInvoicingProhibited(Boolean invoicingProhibited) {
    this.invoicingProhibited = invoicingProhibited;
  }

}
