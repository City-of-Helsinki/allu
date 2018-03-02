package fi.hel.allu.ui.config;

import org.junit.Assert;
import org.junit.Test;

public class ApplicationPropertiesTest {

  @Test
  public void testCreateValidUrl() throws Exception {
    ApplicationProperties props = new ApplicationProperties(
        "DEV",
        "1.1",
        "localhost/geocode",
        "localhost/geocode",
        "9090",
        "localhost",
        "9090",
        "jwtsecret",
        123,
        "jwtsecretexternalservice",
        "localhost",
        "9090",
        null,
        null,
        null,
        null);
    String result = props.getStreetGeocodeUrl();
    Assert.assertEquals("localhost/geocode", result);
  }
}
