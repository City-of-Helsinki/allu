package fi.hel.allu.common.domain.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.geolatte.common.dataformats.json.to.GeoJsonTo;
import org.geolatte.common.dataformats.json.to.GeoJsonToAssembler;
import org.geolatte.geom.Geometry;

import java.io.IOException;

public class GeometrySerializerProxy extends JsonSerializer<Geometry> {

  private final ObjectMapper mapper = new ObjectMapper();
  private final GeoJsonToAssembler assembler = new GeoJsonToAssembler();

  @Override
  public void serialize(Geometry value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    GeoJsonTo to = assembler.toTransferObject(value);
    String serialized = mapper.writeValueAsString(to);
    gen.writeRawValue(serialized);
  }

}