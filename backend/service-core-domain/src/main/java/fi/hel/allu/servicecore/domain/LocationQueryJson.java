package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;

import org.geolatte.geom.Geometry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;

/**
 * Container for the parameters in /application/search_location query.
 *
 */
public class LocationQueryJson {

  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry intersectingGeometry;

  private ZonedDateTime after;
  private ZonedDateTime before;

  /**
   * Get the geometry to use in intersection search.
   *
   * @return the intersectingGeometry
   */
  public Geometry getIntersectingGeometry() {
    return intersectingGeometry;
  }

  /**
   * Get the geometry to use in the intersection search. Should be a simple
   * geometry, i.e. not a GeometryCollection.
   *
   * @param intersectingGeometry
   *          the intersectingGeometry to set
   */
  public void setIntersectingGeometry(Geometry intersectingGeometry) {
    this.intersectingGeometry = intersectingGeometry;
  }

  /**
   * Get the search time period's start. If non-null, limits search to
   * applications that are active after this point of time.
   *
   * @return the after
   */
  public ZonedDateTime getAfter() {
    return after;
  }

  /**
   * Set the search period's start time. Set to non-null to limit search to
   * applications that are active after this point of time.
   *
   * @param after
   *          the after to set
   */
  public void setAfter(ZonedDateTime after) {
    this.after = after;
  }

  /**
   * Get the search period's end time. If this is non-null, only applications
   * that are active before given time are returned.
   *
   * @return the before
   */
  public ZonedDateTime getBefore() {
    return before;
  }

  /**
   * Set the search period's end time. Set to non-null to limit search to
   * applications that are active before this point of time.
   *
   * @param before
   *          the before to set
   */
  public void setBefore(ZonedDateTime before) {
    this.before = before;
  }

}
