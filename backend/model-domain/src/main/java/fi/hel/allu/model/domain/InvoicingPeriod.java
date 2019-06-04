package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Objects;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.TimeUtil;


public class InvoicingPeriod implements Comparable<InvoicingPeriod>{

  private Integer id;

  private Integer applicationId;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private boolean closed;
  private StatusType invoicableStatus;

  public InvoicingPeriod() {
  }

  public InvoicingPeriod(int applicationId, ZonedDateTime startTime, ZonedDateTime endTime) {
    this.applicationId = applicationId;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public InvoicingPeriod(Integer applicationId, StatusType invoicableStatus) {
    this.applicationId = applicationId;
    this.invoicableStatus = invoicableStatus;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  public boolean isClosed() {
    return closed;
  }

  public void setClosed(boolean closed) {
    this.closed = closed;
  }

  public StatusType getInvoicableStatus() {
    return invoicableStatus;
  }

  public void setInvoicableStatus(StatusType invoicableStatus) {
    this.invoicableStatus = invoicableStatus;
  }

  @Override
  public int hashCode() {
    return Objects.hash(applicationId, endTime, closed, startTime, invoicableStatus);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    InvoicingPeriod other = (InvoicingPeriod) obj;
    return Objects.equals(applicationId, other.applicationId) && TimeUtil.isSameDate(endTime, other.endTime)
        && closed == other.closed && TimeUtil.isSameDate(startTime, other.startTime)
        && Objects.equals(invoicableStatus, other.invoicableStatus);
  }

  @Override
  public int compareTo(InvoicingPeriod other) {
    if (startTime != null && other.startTime != null) {
      return startTime.compareTo(other.startTime);
    }
    if (invoicableStatus != null && other.getInvoicableStatus() != null) {
      return Integer.compare(invoicableStatus.getOrderNumber(), other.getInvoicableStatus().getOrderNumber());
    }
    return 0;
  }
}
