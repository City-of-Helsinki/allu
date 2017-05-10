package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

/**
 * Recurring period for an application. Each recurring application has one or two recurrence periods, which are mapped to year 1972. There's
 * one period only, if recurrence is between January and December. Two periods are used, if recurrence overlaps two years i.e. period is
 * for example between December and January. In this case, first period would cover December and second period would cover January.
 */
public class RecurringPeriod {
  private Integer id;
  private Integer applicationId;
  private ZonedDateTime periodStartTime;
  private ZonedDateTime periodEndTime;

  public RecurringPeriod(Integer applicationId, ZonedDateTime periodStartTime, ZonedDateTime periodEndTime) {
    this.applicationId = applicationId;
    this.periodStartTime = periodStartTime;
    this.periodEndTime = periodEndTime;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * @return Id of the application linked to this recurring period.
   */
  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  /**
   * @return Period start time in year 1972.
   */
  public ZonedDateTime getPeriodStartTime() {
    return periodStartTime;
  }

  public void setPeriodStartTime(ZonedDateTime periodStartTime) {
    this.periodStartTime = periodStartTime;
  }

  /**
   * @return Period end time in year 1972.
   */
  public ZonedDateTime getPeriodEndTime() {
    return periodEndTime;
  }

  public void setPeriodEndTime(ZonedDateTime periodEndTime) {
    this.periodEndTime = periodEndTime;
  }
}
