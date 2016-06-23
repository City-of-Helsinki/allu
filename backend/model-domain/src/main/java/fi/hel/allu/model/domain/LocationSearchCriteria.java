package fi.hel.allu.model.domain;

import org.geolatte.geom.Geometry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.model.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.model.domain.serialization.GeometrySerializerProxy;

public class LocationSearchCriteria {

  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry intersects;

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

}
