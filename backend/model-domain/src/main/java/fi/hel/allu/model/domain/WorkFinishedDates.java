package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

public interface WorkFinishedDates {
  public ZonedDateTime getWorkFinished();
  public void setWorkFinished(ZonedDateTime workFinished);
  public ZonedDateTime getCustomerWorkFinished();
  public void setCustomerWorkFinished(ZonedDateTime customerWorkFinished);
  public ZonedDateTime getWorkFinishedReported();
  public void setWorkFinishedReported(ZonedDateTime workFinishedReported);
}
