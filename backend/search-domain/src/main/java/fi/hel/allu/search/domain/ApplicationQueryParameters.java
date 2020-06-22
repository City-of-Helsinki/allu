package fi.hel.allu.search.domain;

import org.geolatte.geom.Geometry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;

public class ApplicationQueryParameters extends QueryParameters {

  /**
   * intersectingGeometry is added to query as AND
   */
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry intersectingGeometry;

  /**
   * hasProject is added to query as AND
   */
  private Boolean hasProject;

  /**
   * By default archived applications are not included in search results.
   */
  private boolean includeArchived = false;

  /**
   * surveyRequired is added to query as OR
   */
  private Boolean surveyRequired;

  /**
   * zoom is added to query as SHOULD
   */
  private Integer zoom;

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

  public Boolean getSurveyRequired() {
    return surveyRequired;
  }

  public void setSurveyRequired(Boolean surveyRequired) {
    this.surveyRequired = surveyRequired;
  }

  public boolean isIncludeArchived() {
    return includeArchived;
  }

  public void setIncludeArchived(boolean includeArchived) {
    this.includeArchived = includeArchived;
  }

  public Integer getZoom() {
    return zoom;
  }

  public void setZoom(Integer zoom) {
    this.zoom = zoom;
  }
}
