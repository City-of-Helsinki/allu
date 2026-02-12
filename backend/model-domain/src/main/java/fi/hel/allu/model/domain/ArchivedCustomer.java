package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

public class ArchivedCustomer {
  private Integer id;
  private Integer customerId;
  private String sapCustomerNumber;
  private ZonedDateTime deletedAt;
  private ZonedDateTime notificationSentAt;

  public ArchivedCustomer() {}

  public ArchivedCustomer(Integer id, Integer customerId, String sapCustomerNumber, ZonedDateTime deletedAt, ZonedDateTime notificationSentAt) {
    this.id = id;
    this.customerId = customerId;
    this.sapCustomerNumber = sapCustomerNumber;
    this.deletedAt = deletedAt;
    this.notificationSentAt = notificationSentAt;
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

  public ZonedDateTime getNotificationSentAt() {
    return notificationSentAt;
  }

  public void setNotificationSentAt(ZonedDateTime notificationSentAt) {
    this.notificationSentAt = notificationSentAt;
  }
}
