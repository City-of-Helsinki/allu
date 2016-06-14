package fi.hel.allu.ui.service;

import fi.hel.allu.ui.domain.OrganizationJson;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class OrganizationServiceTest  extends MockServices {
  @InjectMocks
  protected OrganizationService organizationService;
  private static Validator validator;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    initSaveMocks();
  }

  @BeforeClass
  public static void setUpBeforeClass() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  public void testValidationWithValidOrganization() {
    Set<ConstraintViolation<OrganizationJson>> constraintViolations =
        validator.validate( createOrganizationJson(1) );
    assertEquals(0, constraintViolations.size() );
  }

  @Test
  public void testValidationWithMissingName() {
    OrganizationJson organizationJson = createOrganizationJson(1);
    organizationJson.setName(null);
    Set<ConstraintViolation<OrganizationJson>> constraintViolations =
        validator.validate( organizationJson );
    assertEquals(1, constraintViolations.size() );
    assertEquals("Organization name is required", constraintViolations.iterator().next().getMessage());
  }

  @Test
  public void testValidationWithMissingBusinessId() {
    OrganizationJson organizationJson = createOrganizationJson(1);
    organizationJson.setBusinessId(null);
    Set<ConstraintViolation<OrganizationJson>> constraintViolations =
        validator.validate( organizationJson );
    assertEquals(1, constraintViolations.size() );
    assertEquals("Organization business identifier is required", constraintViolations.iterator().next().getMessage());
  }

  @Test
  public void createValidOrganization() {
    OrganizationJson organizationJson = organizationService.createOrganization(createOrganizationJson(null));
    assertNotNull(organizationJson);
    assertNotNull(organizationJson.getId());
    assertEquals(201, organizationJson.getId().intValue());
    assertNotNull(organizationJson.getPostalAddress());
    assertEquals("Kaupunki, Model", organizationJson.getPostalAddress().getCity());
    assertEquals("00211, Model", organizationJson.getPostalAddress().getPostalCode());
    assertEquals("Osoite 21, Model", organizationJson.getPostalAddress().getStreetAddress());
    assertEquals("32342342, Model", organizationJson.getPhone());
    assertEquals("Organisaatio 1, Model", organizationJson.getName());
    assertEquals("organization email, Model", organizationJson.getEmail());
    assertEquals("3333333, Model", organizationJson.getBusinessId());
  }


  @Test
  public void createOrganizationWithId() {
    OrganizationJson organizationJson = organizationService.createOrganization(createOrganizationJson(1));
    assertNotNull(organizationJson);
    assertNotNull(organizationJson.getId());
    assertEquals(1, organizationJson.getId().intValue());
    assertNotNull(organizationJson.getPostalAddress());
    assertEquals("Kaupunki2, Json", organizationJson.getPostalAddress().getCity());
    assertEquals("002113, Json", organizationJson.getPostalAddress().getPostalCode());
    assertEquals("Osoite 213, Json", organizationJson.getPostalAddress().getStreetAddress());
    assertEquals("323423421, Json", organizationJson.getPhone());
    assertEquals("Organisaatio 2, Json", organizationJson.getName());
    assertEquals("organization2 email, Json", organizationJson.getEmail());
    assertEquals("444444, Json", organizationJson.getBusinessId());
  }

  @Test
  public void updateValidOrganization() {
    OrganizationJson organizationJson = createOrganizationJson(1);
    organizationService.updateOrganization(organizationJson);
    assertNotNull(organizationJson);
    assertNotNull(organizationJson.getId());
    assertEquals(1, organizationJson.getId().intValue());
    assertNotNull(organizationJson.getPostalAddress());
    assertEquals("Kaupunki2, Json", organizationJson.getPostalAddress().getCity());
    assertEquals("002113, Json", organizationJson.getPostalAddress().getPostalCode());
    assertEquals("Osoite 213, Json", organizationJson.getPostalAddress().getStreetAddress());
    assertEquals("323423421, Json", organizationJson.getPhone());
    assertEquals("Organisaatio 2, Json", organizationJson.getName());
    assertEquals("organization2 email, Json", organizationJson.getEmail());
    assertEquals("444444, Json", organizationJson.getBusinessId());
  }

  @Test
  public void updateOrganizationWithoutId() {
    OrganizationJson organizationJson = createOrganizationJson(null);
    organizationService.updateOrganization(organizationJson);
    assertNotNull(organizationJson);
    assertNull(organizationJson.getId());
    assertNotNull(organizationJson.getPostalAddress());
    assertEquals("Kaupunki2, Json", organizationJson.getPostalAddress().getCity());
    assertEquals("002113, Json", organizationJson.getPostalAddress().getPostalCode());
    assertEquals("Osoite 213, Json", organizationJson.getPostalAddress().getStreetAddress());
    assertEquals("323423421, Json", organizationJson.getPhone());
    assertEquals("Organisaatio 2, Json", organizationJson.getName());
    assertEquals("organization2 email, Json", organizationJson.getEmail());
    assertEquals("444444, Json", organizationJson.getBusinessId());
  }

}
