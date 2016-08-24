package fi.hel.allu.ui.domain;

import org.geolatte.geom.Geometry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.model.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.model.domain.serialization.GeometrySerializerProxy;

/**
 * Container for the parameters in /application/search_location query.
 *
 */
public class LocationQueryJson {

  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry intersectingGeometry;

  /**
   * Get the geometry to use in intersection search.
   *
   * @return the intesectingGeometry
   */
  public Geometry getIntersectingGeometry() {
    return intersectingGeometry;
  }

  /**
   * Get the geometry to use in the intersection search. Should be a simple
   * geometry, i.e. not a GeometryCollection.
   *
   * @param intesectingGeometry
   *          the intesectingGeometry to set
   */
  public void setIntersectingGeometry(Geometry intesectingGeometry) {
    this.intersectingGeometry = intesectingGeometry;
  }

}
