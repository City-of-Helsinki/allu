package fi.hel.allu.ui.domain;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import fi.hel.allu.common.types.ApplicationType;

/**
 * DecisionJson is used to transfer all needed data to PDF service for decision
 * generation.
 */
public class DecisionJson {
  private ApplicationJson application;
  private Double applicationArea;

  // Days for the whole reservation (always set)
  private String reservationStartDate;
  private String reservationEndDate;
  private long numReservationDays;

  // If there are some build or teardown days, these are set too:
  private String buildStartDate;
  private String buildEndDate;
  private String eventStartDate;
  private String eventEndDate;
  private String teardownStartDate;
  private String teardownEndDate;
  private int numEventDays;
  private int numBuildAndTeardownDays;

  /**
   * @return the application
   */
  public ApplicationJson getApplication() {
    return application;
  }

  /**
   * @return the applicationArea
   */
  public Double getApplicationArea() {
    return applicationArea;
  }

  /**
   * @return the reservationStartDate
   */
  public String getReservationStartDate() {
    return reservationStartDate;
  }

  /**
   * @return the reservationEndDate
   */
  public String getReservationEndDate() {
    return reservationEndDate;
  }

  /**
   * @return the numReservationDays
   */
  public long getNumReservationDays() {
    return numReservationDays;
  }

  /**
   * @return the buildStartDate
   */
  public String getBuildStartDate() {
    return buildStartDate;
  }

  /**
   * @return the buildEndDate
   */
  public String getBuildEndDate() {
    return buildEndDate;
  }

  /**
   * @return the buildStartDate
   */
  public String getEventStartDate() {
    return eventStartDate;
  }

  /**
   * @return the buildEndDate
   */
  public String getEventEndDate() {
    return eventEndDate;
  }

  /**
   * @return the teardownStartDate
   */
  public String getTeardownStartDate() {
    return teardownStartDate;
  }

  /**
   * @return the teardownEndDate
   */
  public String getTeardownEndDate() {
    return teardownEndDate;
  }

  /**
   * @return the numBuildAndTeardownDays
   */
  public int getNumBuildAndTeardownDays() {
    return numBuildAndTeardownDays;
  }

  /**
   * @return the numEventDays
   */
  public int getNumEventDays() {
    return numEventDays;
  }

  /**
   * @param application
   *          the application to set
   */
  public void setApplication(ApplicationJson application) {
    this.application = application;
    setupTimeFields();
  }

  // Deduce decision's time fields. All too complex.
  private void setupTimeFields() {
    LocalDate reservationStart = checkedDate(application.getStartTime());
    LocalDate reservationEnd = checkedDate(application.getEndTime());
    if (reservationStart == null || reservationEnd == null) {
      return; // Infinite event, no point in additional calculations
    }
    numReservationDays = reservationStart.until(reservationEnd, ChronoUnit.DAYS) + 1;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    reservationStartDate = checkedFormat(reservationStart, formatter);
    reservationEndDate = checkedFormat(reservationEnd, formatter);

    if (application.getType() == ApplicationType.OUTDOOREVENT) {
      LocalDate buildStart = null;
      LocalDate buildEnd = null;
      LocalDate tearDownStart = null;
      LocalDate tearDownEnd = null;
      OutdoorEventJson outdoorEvent = (OutdoorEventJson) application.getEvent();
      // In outdoor events, the application's start time can actually mean start
      // time for build.
      // The actual event's start time is then in eventStartTime
      LocalDate eventStart = checkedDate(outdoorEvent.getEventStartTime());
      if (eventStart != null && eventStart.isAfter(reservationStart)) {
        buildStart = reservationStart;
        buildEnd = eventStart.minusDays(1);
        numBuildAndTeardownDays = (int) buildStart.until(eventStart, ChronoUnit.DAYS);
      }
      // Similar logic applies to teardown end time
      LocalDate eventEnd = checkedDate(outdoorEvent.getEventEndTime());
      if (eventEnd != null && eventEnd.isBefore(reservationEnd)) {
        tearDownEnd = reservationEnd;
        tearDownStart = eventEnd.plusDays(1);
        numBuildAndTeardownDays += (int) eventEnd.until(tearDownEnd, ChronoUnit.DAYS);
      }
      buildStartDate = checkedFormat(buildStart, formatter);
      buildEndDate = checkedFormat(buildEnd, formatter);
      eventStartDate = checkedFormat(eventStart, formatter);
      eventEndDate = checkedFormat(eventEnd, formatter);
      teardownStartDate = checkedFormat(tearDownStart, formatter);
      teardownEndDate = checkedFormat(tearDownEnd, formatter);

      // How many days for the event? (either eventStart or eventEnd can be
      // null)
      numEventDays = (int) ((eventStart != null) ? eventStart : reservationStart)
          .until((eventEnd != null) ? eventEnd : reservationEnd, ChronoUnit.DAYS) + 1;
    }
  }

  private LocalDate checkedDate(ZonedDateTime dateTime) {
    return dateTime == null ? null : dateTime.toLocalDate();
  }

  private String checkedFormat(LocalDate date, DateTimeFormatter formatter) {
    return date == null ? null : date.format(formatter);
  }
}
