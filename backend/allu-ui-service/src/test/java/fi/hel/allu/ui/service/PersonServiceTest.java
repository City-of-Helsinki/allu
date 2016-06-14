package fi.hel.allu.ui.service;

import fi.hel.allu.ui.domain.PersonJson;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.*;

public class PersonServiceTest extends MockServices {
  private static Validator validator;
  @InjectMocks
  protected PersonService personService;

  @BeforeClass
  public static void setUpBeforeClass() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    initSaveMocks();
    initSearchMocks();
  }

  @Test
  public void testValidationWithValidPerson() {
    Set<ConstraintViolation<PersonJson>> constraintViolations =
        validator.validate(createPersonJson(1));
    assertEquals(0, constraintViolations.size());
  }

  @Test
  public void testValidationWithMissingName() {
    PersonJson personJson = createPersonJson(1);
    personJson.setName(null);
    Set<ConstraintViolation<PersonJson>> constraintViolations =
        validator.validate(personJson);
    assertEquals(1, constraintViolations.size());
    assertEquals("Person name is required", constraintViolations.iterator().next().getMessage());
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
  public void createPersonWithId() {
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

  @Test
  public void testFindById() {
    PersonJson personJson = personService.findPersonById(200);
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
}
