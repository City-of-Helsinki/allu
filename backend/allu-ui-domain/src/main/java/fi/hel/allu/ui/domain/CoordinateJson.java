package fi.hel.allu.ui.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fi.hel.allu.common.json.DoubleSerializer;

/**
 * EPSG3879 coordinate.
 */
public class CoordinateJson {
  private double x;
  private double y;

  public CoordinateJson(double x, double y) {
    this.x = x;
    this.y = y;
  }

  @JsonSerialize(using = DoubleSerializer.class)
  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  @JsonSerialize(using = DoubleSerializer.class)
  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }
}

