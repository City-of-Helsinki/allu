package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

public interface OperationalConditionDates {
  public ZonedDateTime getWinterTimeOperation();
  public void setWinterTimeOperation(ZonedDateTime winterTimeOperation);
  public ZonedDateTime getCustomerWinterTimeOperation();
  public void setCustomerWinterTimeOperation(ZonedDateTime customerWinterTimeOperation);
  public ZonedDateTime getOperationalConditionReported();
  public void setOperationalConditionReported(ZonedDateTime operationalConditionReported);
}
