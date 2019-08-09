package fi.hel.allu.model.domain.changehistory;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import fi.hel.allu.common.types.ChangeType;

public class HistorySearchCriteria {

  private List<Integer> applicationIds = new ArrayList<>();
  private List<ChangeType> changeTypes = new ArrayList<>();
  private ZonedDateTime eventsAfter;

  public HistorySearchCriteria() {
  }

  public HistorySearchCriteria(List<Integer> applicationIds, List<ChangeType> changeTypes,
      ZonedDateTime eventsAfter) {
    this.applicationIds = applicationIds;
    this.changeTypes = changeTypes;
    this.eventsAfter = eventsAfter;
  }

  public List<Integer> getApplicationIds() {
    return applicationIds;
  }

  public void setApplicationIds(List<Integer> applicationIds) {
    this.applicationIds = applicationIds;
  }

  public List<ChangeType> getChangeTypes() {
    return changeTypes;
  }

  public void setChangeTypes(List<ChangeType> changeTypes) {
    this.changeTypes = changeTypes;
  }

  public ZonedDateTime getEventsAfter() {
    return eventsAfter;
  }

  public void setEventsAfter(ZonedDateTime eventsAfter) {
    this.eventsAfter = eventsAfter;
  }

}
