package fi.hel.allu.ui.service;


import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.geometrycollection;
import static org.geolatte.geom.builder.DSL.ring;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.types.CustomerType;
import fi.hel.allu.model.domain.Applicant;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.Organization;
import fi.hel.allu.model.domain.Person;
import fi.hel.allu.model.domain.Project;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicantJson;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.ApplicationListJson;
import fi.hel.allu.ui.domain.CustomerJson;
import fi.hel.allu.ui.domain.LocationJson;
import fi.hel.allu.ui.domain.OrganizationJson;
import fi.hel.allu.ui.domain.PersonJson;
import fi.hel.allu.ui.domain.PostalAddressJson;
import fi.hel.allu.ui.domain.ProjectJson;

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

        ApplicationListJson response = applicationService.createApplication(applicationJsonList);

        assertNotNull(response);
        assertNotNull(response.getApplicationList());
        assertEquals(1, response.getApplicationList().size());
        assertEquals(4321, response.getApplicationList().get(0).getId().intValue());
        assertNotNull(response.getApplicationList().get(0).getApplicant());
        assertNotNull(response.getApplicationList().get(0).getCustomer());
        assertNotNull(response.getApplicationList().get(0).getProject());
        assertNotNull(response.getApplicationList().get(0).getLocation());
        assertEquals(555, response.getApplicationList().get(0).getApplicant().getId().intValue());
        assertEquals(1234, response.getApplicationList().get(0).getProject().getId().intValue());
        assertEquals(3, response.getApplicationList().get(0).getCustomer().getId().intValue());
        assertEquals("Kalle käsittelijä", response.getApplicationList().get(0).getHandler());
        assertNull(response.getApplicationList().get(0).getApplicant().getPerson());
        assertNotNull(response.getApplicationList().get(0).getApplicant().getOrganization());
        assertEquals(2, response.getApplicationList().get(0).getApplicant().getOrganization().getId().intValue());
        assertNull(response.getApplicationList().get(0).getCustomer().getOrganization());
        assertNotNull(response.getApplicationList().get(0).getCustomer().getPerson());
        assertEquals(1, response.getApplicationList().get(0).getCustomer().getPerson().getId().intValue());
        assertNotNull(response.getApplicationList().get(0).getLocation().getGeometry());
        assertEquals(777, response.getApplicationList().get(0).getLocation().getId().intValue());
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

        PostalAddressJson postalAddressJson = new PostalAddressJson();
        postalAddressJson.setCity("Person city");
        postalAddressJson.setPostalCode("postalcode");
        postalAddressJson.setStreetAddress("street address 2");
        personJson.setPostalAddress(postalAddressJson);
        personJson.setSsn("343232");
        personJson.setPhone("43244323");
        personJson.setName("Mock person");
        personJson.setEmail("Mock email");

        CustomerJson customer = new CustomerJson();
        customer.setPerson(personJson);
        customer.setSapId("444-1");
        customer.setType(CustomerType.Person);

        ProjectJson project = new ProjectJson();
        project.setName("Hanke1");
        project.setType("Sähkötyö");

        OrganizationJson organizationJson2 = new OrganizationJson();
        organizationJson2.setBusinessId("444444");
        PostalAddressJson postalAddressJsonOrgnization = new PostalAddressJson();
        postalAddressJsonOrgnization.setCity("Kaupunki2");
        postalAddressJsonOrgnization.setStreetAddress("Osoite 213");
        postalAddressJsonOrgnization.setPostalCode("002113");
        organizationJson2.setPostalAddress(postalAddressJsonOrgnization);
        organizationJson2.setPhone("323423421");
        organizationJson2.setName("Organisaatio 2");;
        organizationJson2.setEmail("organization2 email");

        ApplicantJson applicantJson = new ApplicantJson();
        applicantJson.setOrganization(organizationJson2);

        LocationJson locationJson = new LocationJson();
        PostalAddressJson postalAddressJsonLocation = new PostalAddressJson();
        postalAddressJsonLocation.setStreetAddress("address");
        postalAddressJsonLocation.setPostalCode("33333");
        postalAddressJsonLocation.setCity("city");
        locationJson.setPostalAddress(postalAddressJsonLocation);
        locationJson.setGeometry(geometrycollection(3879, ring(c(0, 0), c(0, 1), c(1, 1), c(1, 0), c(0, 0))));


        applicantJson.setType(CustomerType.Company);
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
        customer.setType(CustomerType.Person);
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
