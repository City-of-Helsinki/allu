package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Application history search parameters")
public class ApplicationHistorySearchExt {

  private List<Integer> applicationIds = new ArrayList<>();
  private ZonedDateTime eventsAfter;

  @Schema(description = "IDs of the applications included in search. If empty, all applications created by calling client system are included.")
  public List<Integer> getApplicationIds() {
    return applicationIds;
  }

  public void setApplicationIds(List<Integer> applicationIds) {
    this.applicationIds = applicationIds;
  }

  @Schema(description = "Time limit for events - only events after given time are returned. If null, complete history is returned.")
  public ZonedDateTime getEventsAfter() {
    return eventsAfter;
  }

  public void setEventsAfter(ZonedDateTime eventsAfter) {
    this.eventsAfter = eventsAfter;
  }
}
