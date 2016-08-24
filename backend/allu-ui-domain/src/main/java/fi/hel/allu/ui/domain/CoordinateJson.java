package fi.hel.allu.ui.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

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

  /**
   * Specific serializer for Doubles, to avoid serialization of doubles into scientific format i.e. 1E22.
   */
  public static class DoubleSerializer extends JsonSerializer<Double> {
    @Override
    public void serialize(Double value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
      if (null == value) {
        jgen.writeNull();
      } else {
        NumberFormat formatter = new DecimalFormat("#0.0#");
        final String serializedValue = formatter.format(value);
        jgen.writeNumber(serializedValue);
      }
    }
  }
}

