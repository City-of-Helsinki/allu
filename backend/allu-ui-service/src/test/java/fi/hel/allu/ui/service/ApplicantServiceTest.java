package fi.hel.allu.ui.service;

import fi.hel.allu.ui.domain.ApplicantJson;
import fi.hel.allu.ui.domain.CustomerJson;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ApplicantServiceTest extends MockServices {
  @Mock
  protected PersonService personService;
  @Mock
  protected OrganizationService organizationService;
  @InjectMocks
  protected ApplicantService applicantService;
  private static Validator validator;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    initSaveMocks();
    initSearchMocks();
  }

  @BeforeClass
  public static void setUpBeforeClass() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  public void testValidationWithValidApplicant() {
    Set<ConstraintViolation<ApplicantJson>> constraintViolations =
        validator.validate(createApplicantJson(1, 1));
    assertEquals(0, constraintViolations.size());
  }

  @Test
  public void createValidApplicant() {
    ApplicantJson applicantJson = applicantService.createApplicant(createApplicantJson(null, null));
    assertNotNull(applicantJson);
    assertNotNull(applicantJson.getId());
    assertEquals(103, applicantJson.getId().intValue());
  }

  @Test
  public void createValidApplicantWithId() {
    ApplicantJson applicantJson = applicantService.createApplicant(createApplicantJson(1, null));
    assertNotNull(applicantJson);
    assertNotNull(applicantJson.getId());
    assertEquals(1, applicantJson.getId().intValue());
  }

  @Test
  public void updateValidApplicant() {
    ApplicantJson applicantJson = createApplicantJson(1, 1);
    applicantService.updateApplicant(applicantJson);
    assertNotNull(applicantJson);
    assertNotNull(applicantJson.getId());
    assertEquals(1, applicantJson.getId().intValue());
  }

  @Test
  public void updateApplicantWithoutId() {
    ApplicantJson applicantJson = createApplicantJson(null, 1);
    applicantService.updateApplicant(applicantJson);
    assertNotNull(applicantJson);
    assertNull(applicantJson.getId());
  }

  @Test
  public void testFindById() {
    ApplicantJson applicantJson = applicantService.findApplicantById(103);
    assertNotNull(applicantJson);
    assertNotNull(applicantJson.getId());
    assertEquals(103, applicantJson.getId().intValue());
  }
}
