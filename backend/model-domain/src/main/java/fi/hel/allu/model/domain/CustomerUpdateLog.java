package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

public class CustomerUpdateLog {
  private Integer id;
  private Integer customerId;
  private ZonedDateTime updateTime;
  private ZonedDateTime processedTime;

  public CustomerUpdateLog() {
  }

  public CustomerUpdateLog(Integer customerId, ZonedDateTime updateTime) {
    this.customerId = customerId;
    this.updateTime = updateTime;
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  public ZonedDateTime getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(ZonedDateTime updateTime) {
    this.updateTime = updateTime;
  }


  public ZonedDateTime getProcessedTime() {
    return processedTime;
  }

  public void setProcessedTime(ZonedDateTime processedTime) {
    this.processedTime = processedTime;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

}
