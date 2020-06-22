package fi.hel.allu.servicecore.domain;

import org.geolatte.geom.Geometry;

public class GeometryWithMinZoomLevel {
  private Geometry geometry;
  private int minZoomLevel;

  public GeometryWithMinZoomLevel(Geometry geometry, int minZoomLevel) {
    this.geometry = geometry;
    this.minZoomLevel = minZoomLevel;
  }

  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  public int getMinZoomLevel() {
    return minZoomLevel;
  }

  public void setMinZoomLevel(int minZoomLevel) {
    this.minZoomLevel = minZoomLevel;
  }
}
