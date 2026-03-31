package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

/**
 * In Finnish: arkistoitu asiakas
 * Archived customer is a customer that has been permanently deleted from the system along with all history and log data.
 */
public class ArchivedCustomer {
  private Integer id;
  private Integer customerId;
  private String sapCustomerNumber;
  private ZonedDateTime deletedAt;

  public ArchivedCustomer() {}

  public ArchivedCustomer(Integer id, Integer customerId, String sapCustomerNumber, ZonedDateTime deletedAt) {
    this.id = id;
    this.customerId = customerId;
    this.sapCustomerNumber = sapCustomerNumber;
    this.deletedAt = deletedAt;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  public String getSapCustomerNumber() {
    return sapCustomerNumber;
  }

  public void setSapCustomerNumber(String sapCustomerNumber) {
    this.sapCustomerNumber = sapCustomerNumber;
  }

  public ZonedDateTime getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(ZonedDateTime deletedAt) {
    this.deletedAt = deletedAt;
  }
}
