package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

public class PersonAuditLogLog {

  private Integer id;
  private Integer customerId;
  private Integer contactId;
  @NotNull
  private Integer userId;
  @NotNull
  private String source;
  @NotNull
  private ZonedDateTime creationTime;

  public PersonAuditLogLog(Integer customerId, Integer contactId, Integer userId, String source, ZonedDateTime creationTime) {
    this.customerId = customerId;
    this.contactId = contactId;
    this.userId = userId;
    this.source = source;
    this.creationTime = creationTime;
  }

  public PersonAuditLogLog() {

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

  public Integer getContactId() {
    return contactId;
  }

  public void setContactId(Integer contactId) {
    this.contactId = contactId;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }
}
