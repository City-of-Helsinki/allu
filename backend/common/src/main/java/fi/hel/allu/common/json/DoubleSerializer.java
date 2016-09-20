package fi.hel.allu.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Specific serializer for Doubles, to avoid serialization of doubles into scientific format i.e. 1E22.
 * <p>
 * Annotate using method with <code>@JsonSerialize(using = DoubleSerializer.class)</code>.
 */
public class DoubleSerializer extends JsonSerializer<Double> {
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
