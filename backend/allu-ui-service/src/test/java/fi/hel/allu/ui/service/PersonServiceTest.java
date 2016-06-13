package fi.hel.allu.ui.service;

import fi.hel.allu.ui.domain.LocationJson;
import fi.hel.allu.ui.domain.PersonJson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class PersonServiceTest extends MockServices {
  @InjectMocks
  protected PersonService personService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    initMocks();
  }

  @Test
  public void createValidPerson() {
    PersonJson personJson = personService.createPerson(createPersonJson(null));
    assertNotNull(personJson);
    assertNotNull(personJson.getId());
    assertEquals(200, personJson.getId().intValue());
    assertNotNull(personJson.getPostalAddress());
    assertEquals("Person city, Model", personJson.getPostalAddress().getCity());
    assertEquals("postalcode, Model", personJson.getPostalAddress().getPostalCode());
    assertEquals("street address 2, Model", personJson.getPostalAddress().getStreetAddress());
    assertEquals("343232, Model", personJson.getSsn());
    assertEquals("43244323, Model", personJson.getPhone());
    assertEquals("Mock person, Model", personJson.getName());
    assertEquals("Mock email, Model", personJson.getEmail());
  }

  @Test
  public void createLocationWithId() {
    PersonJson personJson = personService.createPerson(createPersonJson(1));
    assertNotNull(personJson);
    assertNotNull(personJson.getId());
    assertEquals(1, personJson.getId().intValue());
    assertNotNull(personJson.getPostalAddress());
    assertEquals("Person city, Json", personJson.getPostalAddress().getCity());
    assertEquals("postalcode, Json", personJson.getPostalAddress().getPostalCode());
    assertEquals("street address 2, Json", personJson.getPostalAddress().getStreetAddress());
    assertEquals("343232, Json", personJson.getSsn());
    assertEquals("43244323, Json", personJson.getPhone());
    assertEquals("Mock person, Json", personJson.getName());
    assertEquals("Mock email, Json", personJson.getEmail());
  }

  @Test
  public void updateValidPerson() {
    PersonJson personJson = createPersonJson(1);
    personService.updatePerson(personJson);
    assertNotNull(personJson);
    assertNotNull(personJson.getId());
    assertEquals(1, personJson.getId().intValue());
    assertNotNull(personJson.getPostalAddress());
    assertEquals("Person city, Json", personJson.getPostalAddress().getCity());
    assertEquals("postalcode, Json", personJson.getPostalAddress().getPostalCode());
    assertEquals("street address 2, Json", personJson.getPostalAddress().getStreetAddress());
    assertEquals("343232, Json", personJson.getSsn());
    assertEquals("43244323, Json", personJson.getPhone());
    assertEquals("Mock person, Json", personJson.getName());
    assertEquals("Mock email, Json", personJson.getEmail());
  }

  @Test
  public void updatePersonWithoutId() {
    PersonJson personJson = createPersonJson(null);
    personService.updatePerson(personJson);
    assertNotNull(personJson);
    assertNull(personJson.getId());
    assertNotNull(personJson.getPostalAddress());
    assertEquals("Person city, Json", personJson.getPostalAddress().getCity());
    assertEquals("postalcode, Json", personJson.getPostalAddress().getPostalCode());
    assertEquals("street address 2, Json", personJson.getPostalAddress().getStreetAddress());
    assertEquals("343232, Json", personJson.getSsn());
    assertEquals("43244323, Json", personJson.getPhone());
    assertEquals("Mock person, Json", personJson.getName());
    assertEquals("Mock email, Json", personJson.getEmail());
  }

}
