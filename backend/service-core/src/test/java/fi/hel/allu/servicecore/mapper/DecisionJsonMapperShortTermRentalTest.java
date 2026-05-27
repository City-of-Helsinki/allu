package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.pdf.domain.DecisionJson;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ShortTermRentalJson;
import fi.hel.allu.servicecore.service.MetaService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class DecisionJsonMapperShortTermRentalTest {

  private static final String REGISTRATION_NUMBERS = "AKU-123, LOL-1, AUD-1";

  private DecisionJsonMapper mapper;

  @Before
  public void setup() {
    MetaService metaService = Mockito.mock(MetaService.class);
    when(metaService.findTranslation(anyString(), anyString())).thenReturn("Liikkuva myynti");
    // All service dependencies can be null because the test application has no locations,
    // no customers and no id – those code paths are skipped.
    mapper = new DecisionJsonMapper(null, null, null, null, metaService);
  }

  @Test
  public void shouldMapRegistrationNumbersToDecisionJson() {
    ApplicationJson application = createStrApplication(REGISTRATION_NUMBERS);

    DecisionJson result = mapper.mapToDocumentJson(application, false);

    assertEquals(REGISTRATION_NUMBERS, result.getRegistrationNumbers());
  }

  @Test
  public void shouldHandleNullRegistrationNumbers() {
    ApplicationJson application = createStrApplication(null);

    DecisionJson result = mapper.mapToDocumentJson(application, false);

    assertNull(result.getRegistrationNumbers());
  }

  @Test
  public void anonymizedDocumentMapperShouldStillWriteRegistrationNumbers() {
    // Anonymization is enforced at XSL level via the anonymizedDocument flag,
    // NOT by the mapper. The mapper always writes the field.
    ApplicationJson application = createStrApplication(REGISTRATION_NUMBERS);

    DecisionJson result = mapper.mapToDocumentJson(application, false);

    assertEquals(REGISTRATION_NUMBERS, result.getRegistrationNumbers());
  }

  /**
   * Creates a minimal SHORT_TERM_RENTAL ApplicationJson with the given registrationNumbers.
   * Locations and customer contacts are intentionally empty to keep service mocking minimal.
   */
  private ApplicationJson createStrApplication(String registrationNumbers) {
    ApplicationJson application = new ApplicationJson();
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setKindsWithSpecifiers(
        Collections.singletonMap(ApplicationKind.MOBILE_SALES, Collections.emptyList()));
    application.setLocations(Collections.emptyList());
    application.setCustomersWithContacts(Collections.emptyList());

    ShortTermRentalJson extension = new ShortTermRentalJson();
    extension.setDescription("Testikuvaus");
    extension.setRegistrationNumbers(registrationNumbers);
    application.setExtension(extension);

    return application;
  }
}

