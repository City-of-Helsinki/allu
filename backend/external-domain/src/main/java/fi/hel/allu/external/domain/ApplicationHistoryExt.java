package fi.hel.allu.external.domain;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Application history. Contains events (status changes) of one application sorted on event time.")
public class ApplicationHistoryExt {

  private Integer applicationId;
  private Set<ApplicationHistoryEventExt> events = new TreeSet<>();

  public ApplicationHistoryExt() {
  }

  public ApplicationHistoryExt(Integer applicationId, Collection<ApplicationHistoryEventExt> events) {
    this.applicationId = applicationId;
    this.events = new TreeSet<>(events);
  }

  @ApiModelProperty(value = "ID of the application")
  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  @ApiModelProperty(value = "Events of the application sorted on event time.")
  public Set<ApplicationHistoryEventExt> getEvents() {
    return events;
  }

  public void setEvents(Set<ApplicationHistoryEventExt> events) {
    this.events = events;
  }

}
