package fi.hel.allu.servicecore.domain;

public class ZoomLevelSizeBounds {
  private Integer minZoomLevel;
  private Integer maxZoomLevel;
  private Integer minSize;
  private Integer maxSize;
  private GeometryComplexity complexity;

  public ZoomLevelSizeBounds() {}

  public ZoomLevelSizeBounds(Integer minZoomLevel, Integer maxZoomLevel, Integer minSize, Integer maxSize, GeometryComplexity complexity) {
    this.minZoomLevel = minZoomLevel;
    this.maxZoomLevel = maxZoomLevel;
    this.minSize = minSize;
    this.maxSize = maxSize;
    this.complexity = complexity;
  }

  public Integer getMaxZoomLevel() {
    return maxZoomLevel;
  }

  public Integer getMinZoomLevel() {
    return minZoomLevel;
  }

  public Integer getMaxSize() {
    return maxSize;
  }

  public Integer getMinSize() {
    return minSize;
  }

  public GeometryComplexity getComplexity() {
    return complexity;
  }

  public boolean isWithinBounds(double boundingBoxSize) {
    if (Double.isNaN(boundingBoxSize)) return false;
    return (hasLowerBound() && minSize <= boundingBoxSize && hasUpperBound() && boundingBoxSize < maxSize) ||
      (!hasLowerBound() && hasUpperBound() && boundingBoxSize < maxSize) ||
      (hasLowerBound() && minSize <= boundingBoxSize && !hasUpperBound());
  }

  private boolean hasLowerBound() {
    return minSize != null;
  }

  private boolean hasUpperBound() {
    return maxSize != null;
  }
}
