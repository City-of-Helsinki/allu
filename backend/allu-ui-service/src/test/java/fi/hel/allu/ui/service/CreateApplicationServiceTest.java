package fi.hel.allu.ui.service;


import fi.hel.allu.model.domain.*;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.*;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryType;
import org.geolatte.geom.GeometryVisitor;
import org.geolatte.geom.PointCollection;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.web.client.RestTemplate;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.geometrycollection;
import static org.geolatte.geom.builder.DSL.ring;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CreateApplicationServiceTest {
    @Mock
    private ApplicationProperties props;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private ApplicationService applicationService;
    private static Validator validator;
    private ApplicationListJson applicationJsonList;

    @BeforeClass
    public static void setUpBeforeClass() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        applicationJsonList = createMockApplicationListJson();
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

    @Test (expected = IllegalArgumentException.class)
    public void testCreateWithEmptyCustomerOrganizationAndPerson() {
        applicationJsonList.getApplicationList().get(0).getCustomer().setOrganization(null);
        applicationJsonList.getApplicationList().get(0).getCustomer().setPerson(null);
        List<ApplicationJson> response = applicationService.createApplication(applicationJsonList);
    }

    @Test
    public void testCreateWithValidApplication() {
        Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
                Mockito.eq(Application.class)))
                .thenAnswer((Answer<Application>) invocation -> createMockApplication());

        Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
                Mockito.eq(Person.class)))
                .thenAnswer((Answer<Person>) invocation -> createMockPerson());

        Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
                Mockito.eq(Organization.class)))
                .thenAnswer((Answer<Organization>) invocation -> createMockOrganization());

        Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
                Mockito.eq(Customer.class)))
                .thenAnswer((Answer<Customer>) invocation -> createMockCustomer());

        Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
                Mockito.eq(Project.class)))
                .thenAnswer((Answer<Project>) invocation -> createMockProject());

        Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
                Mockito.eq(Applicant.class)))
                .thenAnswer((Answer<Applicant>) invocation -> createMockApplicant());

        Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
                Mockito.eq(Location.class)))
                .thenAnswer((Answer<Location>) invocation -> createMockLocation());

        List<ApplicationJson> response = applicationService.createApplication(applicationJsonList);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(4321, response.get(0).getId());
        assertNotNull(response.get(0).getApplicant());
        assertNotNull(response.get(0).getCustomer());
        assertNotNull(response.get(0).getProject());
        assertNotNull(response.get(0).getLocation());
        assertEquals(555, response.get(0).getApplicant().getId());
        assertEquals(1234, response.get(0).getProject().getId());
        assertEquals(3, response.get(0).getCustomer().getId());
        assertEquals("Kalle käsittelijä", response.get(0).getHandler());
        assertNull(response.get(0).getApplicant().getPerson());
        assertNotNull(response.get(0).getApplicant().getOrganization());
        assertEquals(2, response.get(0).getApplicant().getOrganization().getId());
        assertNull(response.get(0).getCustomer().getOrganization());
        assertNotNull(response.get(0).getCustomer().getPerson());
        assertEquals(1, response.get(0).getCustomer().getPerson().getId());
        assertNotNull(response.get(0).getLocation().getGeometry());
        assertEquals(777, response.get(0).getLocation().getId());
    }


    private ApplicationListJson createMockApplicationListJson() {
        ApplicationListJson applicationListJson = new ApplicationListJson();

        List<ApplicationJson> applicationJsonList = new ArrayList<>();

        ApplicationJson applicationJson = new ApplicationJson();
        applicationJson.setName("Tapahtuma 1");
        applicationJson.setType("Ulkoilmatapahtuma");
        applicationJson.setCreationTime(ZonedDateTime.now());
        applicationJson.setStatus("Vireillä");
        applicationJson.setHandler("Kalle käsittelijä");

        PersonJson personJson = new PersonJson();
        personJson.setCity("Person city");
        personJson.setPostalCode("postalcode");
        personJson.setStreetAddress("street address 2");
        personJson.setSsn("343232");
        personJson.setPhone("43244323");
        personJson.setName("Mock person");
        personJson.setEmail("Mock email");

        CustomerJson customer = new CustomerJson();
        customer.setPerson(personJson);
        customer.setSapId("444-1");
        customer.setType("Person");

        ProjectJson project = new ProjectJson();
        project.setName("Hanke1");
        project.setType("Sähkötyö");

        OrganizationJson organizationJson2 = new OrganizationJson();
        organizationJson2.setBusinessId("444444");
        organizationJson2.setCity("Kaupunki2");
        organizationJson2.setStreetAddress("Osoite 213");
        organizationJson2.setPostalCode("002113");
        organizationJson2.setPhone("323423421");
        organizationJson2.setName("Organisaatio 2");;
        organizationJson2.setEmail("organization2 email");

        ApplicantJson applicantJson = new ApplicantJson();
        applicantJson.setOrganization(organizationJson2);

        LocationJson locationJson = new LocationJson();
        locationJson.setStreetAddress("address");
        locationJson.setPostalCode("33333");
        locationJson.setCity("city");
        locationJson.setGeometry(geometrycollection(3879, ring(c(0, 0), c(0, 1), c(1, 1), c(1, 0), c(0, 0))));


        applicationJson.setCustomer(customer);
        applicationJson.setApplicant(applicantJson);
        applicationJson.setProject(project);
        applicationJson.setLocation(locationJson);

        applicationJsonList.add(applicationJson);

        applicationListJson.setApplicationJsonList(applicationJsonList);
        return applicationListJson;
    }

    private Application createMockApplication() {
        Application application = new Application();
        application.setId(4321);
        application.setName("Mock name");
        application.setProjectId(12345);
        application.setCreationTime(ZonedDateTime.now());
        application.setCustomerId(111);
        application.setHandler("Mock handler");
        application.setType("Mock type");
        application.setLocationId(1);
        return application;
    }

    private Person createMockPerson() {
        Person person = new Person();
        person.setCity("Person city");
        person.setPostalCode("postalcode");
        person.setStreetAddress("street address 2");
        person.setSsn("343232");
        person.setPhone("43244323");
        person.setName("Mock person");
        person.setId(1);
        person.setEmail("Mock email");
        return person;
    }

    private Organization createMockOrganization() {
        Organization organization = new Organization();
        organization.setBusinessId("3333333");
        organization.setCity("Kaupunki");
        organization.setStreetAddress("Osoite 21");
        organization.setPostalCode("00211");
        organization.setPhone("32342342");
        organization.setName("Organisaatio 1");
        organization.setId(2);
        organization.setEmail("organization email");
        return organization;
    }

    private Customer createMockCustomer() {
        Customer customer = new Customer();
        customer.setId(3);
        //customer.setOrganizationId(2);
        customer.setPersonId(1);
        customer.setSapId("444-1");
        customer.setType("Tyyppi");
        return customer;
    }

    private Project createMockProject() {
        Project project = new Project();
        project.setId(1234);
        project.setName("Hanke1");
        return project;
    }

    private Applicant createMockApplicant() {
        Applicant applicant = new Applicant();
        applicant.setId(555);
        applicant.setOrganizationId(2);
        return applicant;
    }

    private Location createMockLocation() {
        Location location = new Location();
        location.setCity("City1");
        location.setPostalCode("33333");
        location.setId(777);
        location.setGeometry(geometrycollection(3879, ring(c(0, 0), c(0, 1), c(1, 1), c(1, 0), c(0, 0))));
        return location;
    }
}
