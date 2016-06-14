package fi.hel.allu.ui.config;

import org.junit.Assert;
import org.junit.Test;

public class ApplicationPropertiesTest {

  @Test
  public void testCreateValidUrl() throws Exception {
    ApplicationProperties props = new ApplicationProperties("localhost", "9090");
    String result = props.getUrl("/test/joo");
    Assert.assertEquals("http://localhost:9090/test/joo", result);
  }
}
