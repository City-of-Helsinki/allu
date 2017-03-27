package fi.hel.allu.ui.service;

import fi.hel.allu.ui.domain.ApplicantJson;
import fi.hel.allu.ui.mapper.ApplicationMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ApplicantServiceTest extends MockServices {
  private static Validator validator;
  protected ApplicantService applicantService;

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
    applicantService = new ApplicantService(props, restTemplate, new ApplicationMapper());
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
    assertEquals(103, applicantJson.getId().intValue());
  }

  @Test
  public void testFindById() {
    ApplicantJson applicantJson = applicantService.findApplicantById(103);
    assertNotNull(applicantJson);
    assertNotNull(applicantJson.getId());
    assertEquals(103, applicantJson.getId().intValue());
  }
}
