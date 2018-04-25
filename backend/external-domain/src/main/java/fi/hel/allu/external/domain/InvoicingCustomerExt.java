package fi.hel.allu.external.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import fi.hel.allu.common.domain.types.CustomerType;
import io.swagger.annotations.ApiModel;

/**
 * Used in customer updates (SAP customer update).
 *
 * @author User
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
