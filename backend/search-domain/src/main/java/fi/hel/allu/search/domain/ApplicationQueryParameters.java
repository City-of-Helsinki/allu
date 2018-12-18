package fi.hel.allu.search.domain;

import org.geolatte.geom.Geometry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;

public class ApplicationQueryParameters extends QueryParameters {

  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry intersectingGeometry;

  private Boolean hasProject;

  public Geometry getIntersectingGeometry() {
    return intersectingGeometry;
  }

  public void setIntersectingGeometry(Geometry intersectingGeometry) {
    this.intersectingGeometry = intersectingGeometry;
  }

  public Boolean getHasProject() {
    return hasProject;
  }

  public void setHasProject(Boolean hasProject) {
    this.hasProject = hasProject;
  }

}
