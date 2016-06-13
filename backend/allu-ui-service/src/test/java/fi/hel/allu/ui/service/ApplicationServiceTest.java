package fi.hel.allu.ui.service;


import fi.hel.allu.ui.domain.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.*;

public class ApplicationServiceTest extends MockServices {
    @Mock
    protected LocationService locationService;
    @Mock
    protected PersonService personService;
    @InjectMocks
    protected ApplicationService applicationService;

    private static Validator validator;
    private ApplicationListJson applicationJsonList;

    public ApplicationServiceTest() {
        applicationJsonList = createMockApplicationListJson();
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        initMocks();
        Mockito.when(locationService.createLocation(Mockito.anyObject())).thenAnswer((Answer<LocationJson>) invocation ->
            createLocationJson(102));
        Mockito.when(personService.createPerson(Mockito.anyObject())).thenAnswer((Answer<PersonJson>) invocation ->
            createPersonJson(200));
    }

    @BeforeClass
    public static void setUpBeforeClass() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    @Test
    public void testCreateWithNullApplicationName() {
        applicationJsonList.getApplicationList().get(0).setName(null);
        Set<ConstraintViolation<ApplicationJson>> constraintViolations =
                validator.validate( applicationJsonList.getApplicationList().get(0) );
        assertEquals(1, constraintViolations.size() );
        assertEquals("Application name is required", constraintViolations.iterator().next().getMessage());
    }


    @Test
    public void testCreateWithEmptyApplicationType() {
        applicationJsonList.getApplicationList().get(0).setType("");
        Set<ConstraintViolation<ApplicationJson>> constraintViolations =
                validator.validate( applicationJsonList.getApplicationList().get(0) );
        assertEquals(1, constraintViolations.size() );
        assertEquals("Application type is required", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testCreateWithEmptyCustomerPersonName() {
        applicationJsonList.getApplicationList().get(0).getCustomer().getPerson().setName("");
        Set<ConstraintViolation<ApplicationJson>> constraintViolations =
                validator.validate( applicationJsonList.getApplicationList().get(0) );
        assertEquals(1, constraintViolations.size() );
        assertEquals("Person name is required", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testCreateWithEmptyCustomerOrganizationAndPerson() {
        applicationJsonList.getApplicationList().get(0).getCustomer().setOrganization(null);
        applicationJsonList.getApplicationList().get(0).getCustomer().setPerson(null);
        Set<ConstraintViolation<ApplicationJson>> constraintViolations =
                validator.validate( applicationJsonList.getApplicationList().get(0) );
        assertEquals(1, constraintViolations.size() );
        assertEquals("person is required, type is Person", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testCreateWithNotEmptyCustomerOrganization() {
        OrganizationJson organizationJson = new OrganizationJson();
        organizationJson.setBusinessId("444444");
        organizationJson.setName("Organisaatio 2");;
        organizationJson.setEmail("organization2 email");
        applicationJsonList.getApplicationList().get(0).getCustomer().setOrganization(organizationJson);

        Set<ConstraintViolation<ApplicationJson>> constraintViolations =
                validator.validate( applicationJsonList.getApplicationList().get(0) );
        assertEquals(1, constraintViolations.size() );
        assertEquals("organization must be null, type is Person", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testCreateWithEmptyApplicantOrganization() {
        applicationJsonList.getApplicationList().get(0).getApplicant().setOrganization(null);

        Set<ConstraintViolation<ApplicationJson>> constraintViolations =
                validator.validate( applicationJsonList.getApplicationList().get(0) );
        assertEquals(1, constraintViolations.size() );
        assertEquals("organization is required, type is Organization", constraintViolations.iterator().next().getMessage());
    }


    @Test
    public void testCreateWithValidApplication() {
        ApplicationListJson response = applicationService.createApplication(applicationJsonList);

        assertNotNull(response);
        assertNotNull(response.getApplicationList());
        assertEquals(1, response.getApplicationList().size());
        assertEquals(1, response.getApplicationList().get(0).getId().intValue());
        assertNotNull(response.getApplicationList().get(0).getApplicant());
        assertNotNull(response.getApplicationList().get(0).getCustomer());
        assertNotNull(response.getApplicationList().get(0).getProject());
        assertNotNull(response.getApplicationList().get(0).getLocation());
        assertEquals(100, response.getApplicationList().get(0).getProject().getId().intValue());
        assertEquals(101, response.getApplicationList().get(0).getCustomer().getId().intValue());
        assertEquals(102, response.getApplicationList().get(0).getLocation().getId().intValue());
        assertEquals(103, response.getApplicationList().get(0).getApplicant().getId().intValue());
        assertEquals("Kalle käsittelijä, Json", response.getApplicationList().get(0).getHandler());
        assertNull(response.getApplicationList().get(0).getApplicant().getPerson());
        assertNotNull(response.getApplicationList().get(0).getApplicant().getOrganization());
        assertEquals(201, response.getApplicationList().get(0).getApplicant().getOrganization().getId().intValue());
        assertNull(response.getApplicationList().get(0).getCustomer().getOrganization());
        assertNotNull(response.getApplicationList().get(0).getCustomer().getPerson());
        assertEquals(200, response.getApplicationList().get(0).getCustomer().getPerson().getId().intValue());
        assertNotNull(response.getApplicationList().get(0).getLocation().getGeometry());
    }



}
