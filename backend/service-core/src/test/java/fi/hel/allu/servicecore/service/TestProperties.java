package fi.hel.allu.servicecore.service;

import fi.hel.allu.servicecore.config.ApplicationProperties;

public class TestProperties {

  public static ApplicationProperties getProperties() {
    return new ApplicationProperties("http://localhost", "85", null, null, null, null, null, null, null, null, null, null, null);
  }

}
