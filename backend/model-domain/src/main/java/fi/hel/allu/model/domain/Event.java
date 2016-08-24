package fi.hel.allu.model.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = OutdoorEvent.class, name = "OUTDOOREVENT")
})
public abstract class Event {

  // The earliest and latest times possible:
  private static final ZonedDateTime MIN_TIME = LocalDateTime.MIN.atZone(ZoneId.of("Z"));
  private static final ZonedDateTime MAX_TIME = LocalDateTime.MAX.atZone(ZoneId.of("Z"));
  /**
   * Get the start time of the event. For repeating event, returns the start
   * time of the first occurrence.
   *
   * @return
   */
  public ZonedDateTime getStartTime() {
    return MIN_TIME;
  }

  /**
   * Get the end time of the event. For repeating events, returns the end time
   * of the last occurrence.
   */
  public ZonedDateTime getEndTime() {
    return MAX_TIME;
  }
}

