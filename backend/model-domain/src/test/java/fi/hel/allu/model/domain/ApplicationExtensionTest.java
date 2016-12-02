package fi.hel.allu.model.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ApplicationExtensionTest {

  ObjectMapper objectMapper;

  @Before
  public void setUp() throws Exception {
    objectMapper = new ObjectMapper();
  }

  @Test
  public void testOutdoorEvent() throws IOException {
    Event original = new Event();
    original.setDescription("Test Event");
    original.setEntryFee(12340);
    String serialized = objectMapper.writer().writeValueAsString(original);
    ApplicationExtension deserialized = objectMapper.readerFor(ApplicationExtension.class).readValue(serialized);
    assertEquals(Event.class, deserialized.getClass());
    assertEquals(original.getDescription(), ((Event) deserialized).getDescription());
    assertEquals(original.getEntryFee(), ((Event) deserialized).getEntryFee());
  }

}
