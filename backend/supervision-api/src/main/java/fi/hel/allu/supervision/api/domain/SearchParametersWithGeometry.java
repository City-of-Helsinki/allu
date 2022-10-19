package fi.hel.allu.supervision.api.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;
import io.swagger.v3.oas.annotations.media.Schema;
import org.geolatte.geom.Geometry;

public class SearchParametersWithGeometry<T extends SearchField> extends SearchParameters<T> {
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry intersectingGeometry;

  @Schema(description =
    "Geometry intersecting application geometry in <a href=\"https://tools.ietf.org/html/rfc7946\">GeoJSON</a> with following limitations:"
      + "<ul>"
      + "<li>Feature / FeatureCollection is currently not supported, geometry should be given as <a href=\"https://tools.ietf.org/html/rfc7946#section-3.1.8\">GeometryCollection</a>.</li>"
      + "<li>Only named CRS is supported, the given name must either be of the form: urn:ogc:def:crs:EPSG:x.y:4326 (x.y: the version of the EPSG) or of the form EPSG:4326</li>"
      + "</ul>"
  )
  public Geometry getIntersectingGeometry() {
    return intersectingGeometry;
  }

  public void setIntersectingGeometry(Geometry intersectingGeometry) {
    this.intersectingGeometry = intersectingGeometry;
  }
}
