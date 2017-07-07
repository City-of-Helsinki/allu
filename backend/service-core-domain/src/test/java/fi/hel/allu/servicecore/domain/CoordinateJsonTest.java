
package fi.hel.allu.servicecore.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.hel.allu.servicecore.domain.CoordinateJson;
import org.junit.Assert;
import org.junit.Test;

public class CoordinateJsonTest {
  @Test
  public void testDoubleSerialization() throws Exception {

    String expectedJson1 = "{\"x\":1.0,\"y\":2.1}";
    String expectedJson2 = "{\"x\":123456789.0,\"y\":123456789.0}";
    String expectedJson3 = "{\"x\":123456789.01,\"y\":123456789.0}";

    double x1 = 1.0;
    double y1 = 2.1;
    double x2 = 123456789.0;
    double y2 = 123456789.0;
    double x3 = 123456789.01;
    double y3 = 123456789;

    ObjectMapper mapper = new ObjectMapper();
    CoordinateJson coordinateJson = new CoordinateJson(x1, y1);
    Assert.assertEquals(expectedJson1, mapper.writeValueAsString(coordinateJson));
    coordinateJson = new CoordinateJson(x2, y2);
    Assert.assertEquals(expectedJson2, mapper.writeValueAsString(coordinateJson));
    coordinateJson = new CoordinateJson(x3, y3);
    Assert.assertEquals(expectedJson3, mapper.writeValueAsString(coordinateJson));
  }
}