package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

public interface ValidityDates {
  public ZonedDateTime getCustomerStartTime();
  public void setCustomerStartTime(ZonedDateTime customerStartTime);
  public ZonedDateTime getCustomerEndTime();
  public void setCustomerEndTime(ZonedDateTime customerEndTime);
  public ZonedDateTime getValidityReported();
  public void setValidityReported(ZonedDateTime validityReported);
}
