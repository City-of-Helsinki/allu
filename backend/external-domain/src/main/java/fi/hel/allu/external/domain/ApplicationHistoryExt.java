package fi.hel.allu.external.domain;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Application history. Contains events (status changes) of one application sorted on event time.")
public class ApplicationHistoryExt {

  private Integer applicationId;
  private Set<ApplicationStatusEventExt> events = new TreeSet<>();
  private Set<SupervisionEventExt> supervisionEvents = new TreeSet<>();


  public ApplicationHistoryExt() {
  }

  public ApplicationHistoryExt(Integer applicationId, Collection<ApplicationStatusEventExt> events,
      Collection<SupervisionEventExt> supervisionEvents) {
    this.applicationId = applicationId;
    this.events = new TreeSet<>(events);
    this.supervisionEvents = new TreeSet<>(supervisionEvents);
  }

  @Schema(description = "ID of the application")
  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  @Schema(description = "Status change events of the application sorted on event time.")
  public Set<ApplicationStatusEventExt> getEvents() {
    return events;
  }

  public void setEvents(Set<ApplicationStatusEventExt> events) {
    this.events = events;
  }

  public Set<SupervisionEventExt> getSupervisionEvents() {
    return supervisionEvents;
  }

  public void setSupervisionEvents(Set<SupervisionEventExt> supervisionEvents) {
    this.supervisionEvents = supervisionEvents;
  }

}
