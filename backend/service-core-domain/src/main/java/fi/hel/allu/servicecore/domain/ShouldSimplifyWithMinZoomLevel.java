package fi.hel.allu.servicecore.domain;

import org.geolatte.geom.Geometry;

public class ShouldSimplifyWithMinZoomLevel {
  private boolean shouldSimplify;
  private int minZoomLevel;
  private Geometry geometry;

  public ShouldSimplifyWithMinZoomLevel(boolean shouldSimplify, int minZoomLevel, Geometry geometry) {
    this.shouldSimplify = shouldSimplify;
    this.minZoomLevel = minZoomLevel;
    this.geometry = geometry;
  }

  public boolean shouldSimplify() {
    return shouldSimplify;
  }

  public void setShouldSimplify(boolean shouldSimplify) {
    this.shouldSimplify = shouldSimplify;
  }

  public int getMinZoomLevel() {
    return minZoomLevel;
  }

  public void setMinZoomLevel(int minZoomLevel) {
    this.minZoomLevel = minZoomLevel;
  }

  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }
}
