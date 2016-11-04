package fi.hel.allu.model.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class EventTest {

  ObjectMapper objectMapper;

  @Before
  public void setUp() throws Exception {
    objectMapper = new ObjectMapper();
  }

  @Test
  public void testOutdoorEvent() throws IOException {
    OutdoorEvent original = new OutdoorEvent();
    original.setDescription("Test OutdoorEvent");
    original.setEntryFee(12340);
    String serialized = objectMapper.writer().writeValueAsString(original);
    Event deserialized = objectMapper.readerFor(Event.class).readValue(serialized);
    assertEquals(OutdoorEvent.class, deserialized.getClass());
    assertEquals(original.getDescription(), ((OutdoorEvent) deserialized).getDescription());
    assertEquals(original.getEntryFee(), ((OutdoorEvent) deserialized).getEntryFee());
  }

}
