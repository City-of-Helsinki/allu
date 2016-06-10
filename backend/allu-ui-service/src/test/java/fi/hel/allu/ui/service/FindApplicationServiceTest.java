package fi.hel.allu.ui.service;

import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.geometrycollection;
import static org.geolatte.geom.builder.DSL.ring;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.types.CustomerType;
import fi.hel.allu.model.domain.Applicant;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.Person;
import fi.hel.allu.model.domain.Project;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicationJson;

public class FindApplicationServiceTest {
    @Mock
    private ApplicationProperties props;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private ApplicationService applicationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindApplicationById() {
        Mockito.when(restTemplate.getForObject(Mockito.any(String.class), Mockito.eq(Application.class), Mockito.any
                (String.class)))
                .thenAnswer((Answer<Application>) invocation -> createMockApplicationResponse());

        Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Person.class), Mockito.anyInt()))
                .thenAnswer((Answer<ResponseEntity<Person>>) invocation -> createMockPersonResponse());

        Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Customer.class), Mockito.anyInt()))
                .thenAnswer((Answer<ResponseEntity<Customer>>) invocation -> createMockCustomerResponse());

        Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Project.class), Mockito.anyInt()))
                .thenAnswer((Answer<ResponseEntity<Project>>) invocation -> createMockProjectResponse());

        Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Applicant.class), Mockito.anyInt()))
                .thenAnswer((Answer<ResponseEntity<Applicant>>) invocation -> createMockApplicantResponse());

        Mockito.when(props.getUrl(Mockito.any(String.class))).thenAnswer((Answer<String>) invocationOnMock -> "http://localhost:85/testing");

        ApplicationJson response = applicationService.findApplicationById("123");

        assertNotNull(response);
        assertNotNull(response.getCustomer());
        assertNotNull(response.getProject());
        assertNotNull(response.getApplicant());
        assertEquals(1, response.getCustomer().getId());
        assertEquals(555, response.getProject().getId());
        assertEquals(222, response.getApplicant().getId());
        assertNull(response.getCustomer().getOrganization());
        assertNull(response.getApplicant().getOrganization());
        assertNotNull(response.getApplicant().getPerson());
        assertNotNull(response.getCustomer().getPerson());
        assertEquals(222, response.getApplicant().getPerson().getId());
        assertEquals(222, response.getCustomer().getPerson().getId());
    }

    @Test
    public void testFindApplicationByHandler() {
        Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Application[].class), Mockito.any
                (String.class)))
                .thenAnswer((Answer<ResponseEntity<Application[]>>) invocation ->
                        createMockApplicationListResponse());

        Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Person.class), Mockito.anyInt()))
                .thenAnswer((Answer<ResponseEntity<Person>>) invocation -> createMockPersonResponse());

        Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Customer.class), Mockito.anyInt()))
                .thenAnswer((Answer<ResponseEntity<Customer>>) invocation -> createMockCustomerResponse());

        Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Project.class), Mockito.anyInt()))
                .thenAnswer((Answer<ResponseEntity<Project>>) invocation -> createMockProjectResponse());

        Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Applicant.class), Mockito.anyInt()))
                .thenAnswer((Answer<ResponseEntity<Applicant>>) invocation -> createMockApplicantResponse());

        Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Location.class), Mockito.anyInt()))
                .thenAnswer((Answer<ResponseEntity<Location>>) invocation -> createMockLocationResponse());

        Mockito.when(props.getUrl(Mockito.any(String.class))).thenAnswer((Answer<String>) invocationOnMock -> "http://localhost:85/testing");

        List<ApplicationJson> response = applicationService.findApplicationByHandler("222");

        assertNotNull(response);;
        assertEquals(2, response.size());
        assertNotNull(response.get(0));
        assertNotNull(response.get(0).getCustomer());
        assertNotNull(response.get(0).getProject());
        assertEquals(1, response.get(0).getCustomer().getId());
        assertEquals(555, response.get(0).getProject().getId());
        assertEquals("MockName", response.get(0).getName());
        assertEquals(123, response.get(0).getId());

        assertNotNull(response.get(1));
        assertNotNull(response.get(1).getCustomer());
        assertNotNull(response.get(1).getProject());
        assertEquals(1, response.get(1).getCustomer().getId());
        assertEquals(555, response.get(1).getProject().getId());
        assertEquals("MockName2", response.get(1).getName());
        assertEquals(1234, response.get(1).getId());
        assertNotNull(response.get(1).getLocation());
        assertNotNull(response.get(1).getLocation().getGeometry());
        assertEquals(777, response.get(1).getLocation().getId());
    }

    private Application createMockApplicationResponse() {
        Application applicationModel = new Application();
        applicationModel.setId(123);
        applicationModel.setType("MockType");
        applicationModel.setHandler("MockHandler");
        applicationModel.setStatus("MockStatus");
        applicationModel.setProjectId(321);
        applicationModel.setName("MockName");
        applicationModel.setCustomerId(345);
        applicationModel.setApplicantId(555);
        return applicationModel;
    }


    private ResponseEntity<Application[]> createMockApplicationListResponse() {
        Application applicationModelArray[] = new Application[2];
        Application applicationModel = new Application();
        applicationModel.setId(123);
        applicationModel.setType("MockType");
        applicationModel.setHandler("MockHandler");
        applicationModel.setStatus("MockStatus");
        applicationModel.setProjectId(321);
        applicationModel.setName("MockName");
        applicationModel.setCustomerId(345);
        applicationModel.setApplicantId(555);
        applicationModelArray[0] = applicationModel;

        applicationModel = new fi.hel.allu.model.domain.Application();
        applicationModel.setId(1234);
        applicationModel.setType("MockType2");
        applicationModel.setHandler("MockHandler2");
        applicationModel.setStatus("MockStatus2");
        applicationModel.setProjectId(4321);
        applicationModel.setName("MockName2");
        applicationModel.setCustomerId(3456);
        applicationModel.setApplicantId(655);
        applicationModel.setLocationId(345);
        applicationModelArray[1] = applicationModel;

        return new ResponseEntity<>(applicationModelArray, HttpStatus.OK);
    }

    private ResponseEntity<Person> createMockPersonResponse() {
        Person personModel = new Person();
        personModel.setStreetAddress("Mock address");
        personModel.setPostalCode("123");
        personModel.setPhone("33333");
        personModel.setName("Last");
        personModel.setId(222);
        personModel.setSsn("3242423");
        personModel.setEmail("email");
        return new ResponseEntity<>(personModel, HttpStatus.OK);
    }

    private ResponseEntity<Project> createMockProjectResponse() {
        Project projectModel = new Project();
        projectModel.setOwnerId(111);
        projectModel.setId(555);
        projectModel.setName("MockName");
        projectModel.setContactId(666);
        return new ResponseEntity<>(projectModel, HttpStatus.OK);
    }

    private ResponseEntity<Customer> createMockCustomerResponse() {
        Customer customer = new Customer();
        customer.setId(1);
        customer.setType(CustomerType.Person);
        customer.setSapId("333");
        customer.setPersonId(222);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    private ResponseEntity<Applicant> createMockApplicantResponse() {
        Applicant applicant = new Applicant();
        applicant.setId(222);
        applicant.setPersonId(1);
        return new ResponseEntity<>(applicant, HttpStatus.OK);
    }

    private ResponseEntity<Location> createMockLocationResponse() {
        Location location = new Location();
        location.setCity("City1");
        location.setPostalCode("33333");
        location.setId(777);
        location.setGeometry(geometrycollection(3879, ring(c(0, 0), c(0, 1), c(1, 1), c(1, 0), c(0, 0))));
        return new ResponseEntity<>(location, HttpStatus.OK);
    }
}
