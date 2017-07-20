package fi.hel.allu.common.domain.serialization;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.geolatte.common.dataformats.json.to.GeoJsonTo;
import org.geolatte.common.dataformats.json.to.GeoJsonToAssembler;
import org.geolatte.geom.Geometry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class GeometrySerializerProxy extends JsonSerializer<Geometry> {

  private ObjectMapper mapper = new ObjectMapper();
  private GeoJsonToAssembler assembler = new GeoJsonToAssembler();

  @Override
  public void serialize(Geometry value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException, JsonProcessingException {
    GeoJsonTo to = assembler.toTransferObject(value);
    String serialized = mapper.writeValueAsString(to);
    gen.writeRawValue(serialized);
  }

}
