package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import fi.hel.allu.common.domain.types.StatusType;
import org.geolatte.geom.Geometry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;

public class LocationSearchCriteria {

  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry intersects;

  private ZonedDateTime after;
  private ZonedDateTime before;
  private List<StatusType> statusTypes;

  /**
   * If non-null, the search should find locations that intersect with this
   * geometry.
   */
  public Geometry getIntersects() {
    return intersects;
  }

  public void setIntersects(Geometry intersects) {
    this.intersects = intersects;
  }

  /**
   * If non-null, events must be active after this time (i.e., events that end
   * before this are excluded)
   */
  public ZonedDateTime getAfter() {
    return after;
  }

  public void setAfter(ZonedDateTime after) {
    this.after = after;
  }

  /**
   * If non-null, events must be active before this time (i.e, events that start
   * after this time are excluded)
   */
  public ZonedDateTime getBefore() {
    return before;
  }

  public void setBefore(ZonedDateTime before) {
    this.before = before;
  }

  /**
   * Application status types which are used in search.
   */
  public List<StatusType> getStatusTypes() {
    return statusTypes;
  }

  public void setStatusTypes(List<StatusType> statusTypes) {
    this.statusTypes = statusTypes;
  }
}
