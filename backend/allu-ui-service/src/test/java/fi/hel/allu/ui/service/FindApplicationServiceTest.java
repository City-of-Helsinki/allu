package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.Person;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.Application;
import fi.hel.allu.ui.domain.ApplicationDTO;
import fi.hel.allu.ui.domain.Customer;
import fi.hel.allu.ui.domain.Project;
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

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FindApplicationServiceTest {
    @Mock
    private ApplicationProperties props;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private ApplicationService applicationService;
    private ApplicationDTO applicationDTO;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        applicationDTO = createMockApplication();
    }

    @Test
    public void testFindApplicationById() {
        Mockito.when(restTemplate.getForObject(Mockito.any(String.class), Mockito.eq(fi.hel.allu.model.domain.Application.class), Mockito.any
                (String.class)))
                .thenAnswer((Answer<fi.hel.allu.model.domain.Application>) invocation -> createMockModelDomainApplication());

        Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Person.class), Mockito.anyInt()))
                .thenAnswer((Answer<ResponseEntity<fi.hel.allu.model.domain.Person>>) invocation -> createMockModelPerson());

        Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(fi.hel.allu.model.domain.Project.class), Mockito.anyInt()))
                .thenAnswer((Answer<ResponseEntity<fi.hel.allu.model.domain.Project>>) invocation -> createMockModelProject());

        Mockito.when(props.getUrl(Mockito.any(String.class))).thenAnswer((Answer<String>) invocationOnMock -> "http://localhost:85/testing");

        ApplicationDTO response = applicationService.findApplicationById("123");

        assertNotNull(response);
        assertNotNull(response.getApplicationList());
        assertEquals(1, response.getApplicationList().size());
        assertNotNull(response.getApplicationList().get(0));
        assertNotNull(response.getApplicationList().get(0).getCustomer());
        assertNotNull(response.getApplicationList().get(0).getProject());
        assertEquals(222, response.getApplicationList().get(0).getCustomer().getId());
        assertEquals(555, response.getApplicationList().get(0).getProject().getId());
        assertEquals("MockName", response.getApplicationList().get(0).getName());
        assertEquals(123, response.getApplicationList().get(0).getId());
    }

    @Test
    public void testFindApplicationByHandler() {
        Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(fi.hel.allu.model.domain.Application[].class), Mockito.any
                (String.class)))
                .thenAnswer((Answer<ResponseEntity<fi.hel.allu.model.domain.Application[]>>) invocation ->
                        createMockModelDomainApplicationResponse());

        Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(Person.class), Mockito.anyInt()))
                .thenAnswer((Answer<ResponseEntity<fi.hel.allu.model.domain.Person>>) invocation -> createMockModelPerson());

        Mockito.when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(fi.hel.allu.model.domain.Project.class), Mockito.anyInt()))
                .thenAnswer((Answer<ResponseEntity<fi.hel.allu.model.domain.Project>>) invocation -> createMockModelProject());

        Mockito.when(props.getUrl(Mockito.any(String.class))).thenAnswer((Answer<String>) invocationOnMock -> "http://localhost:85/testing");

        ApplicationDTO response = applicationService.findApplicationByHandler("222");

        assertNotNull(response);
        assertNotNull(response.getApplicationList());
        assertEquals(2, response.getApplicationList().size());
        assertNotNull(response.getApplicationList().get(0));
        assertNotNull(response.getApplicationList().get(0).getCustomer());
        assertNotNull(response.getApplicationList().get(0).getProject());
        assertEquals(222, response.getApplicationList().get(0).getCustomer().getId());
        assertEquals(555, response.getApplicationList().get(0).getProject().getId());
        assertEquals("MockName", response.getApplicationList().get(0).getName());
        assertEquals(123, response.getApplicationList().get(0).getId());

        assertNotNull(response.getApplicationList().get(1));
        assertNotNull(response.getApplicationList().get(1).getCustomer());
        assertNotNull(response.getApplicationList().get(1).getProject());
        assertEquals(222, response.getApplicationList().get(1).getCustomer().getId());
        assertEquals(555, response.getApplicationList().get(1).getProject().getId());
        assertEquals("MockName2", response.getApplicationList().get(1).getName());
        assertEquals(1234, response.getApplicationList().get(1).getId());
    }


    private ResponseEntity<fi.hel.allu.model.domain.Application[]> createMockModelDomainApplicationResponse() {
        fi.hel.allu.model.domain.Application applicationModelArray[] = new fi.hel.allu.model.domain.Application[2];
        fi.hel.allu.model.domain.Application applicationModel = new fi.hel.allu.model.domain.Application();
        applicationModel.setId(123);
        applicationModel.setType("MockType");
        applicationModel.setHandler("MockHandler");
        applicationModel.setStatus("MockStatus");
        applicationModel.setProjectId(321);
        applicationModel.setName("MockName");
        applicationModel.setCustomerId(345);
        applicationModelArray[0] = applicationModel;

        applicationModel = new fi.hel.allu.model.domain.Application();
        applicationModel.setId(1234);
        applicationModel.setType("MockType2");
        applicationModel.setHandler("MockHandler2");
        applicationModel.setStatus("MockStatus2");
        applicationModel.setProjectId(4321);
        applicationModel.setName("MockName2");
        applicationModel.setCustomerId(3456);
        applicationModelArray[1] = applicationModel;

        return new ResponseEntity<>(applicationModelArray, HttpStatus.OK);
    }

    private fi.hel.allu.model.domain.Application createMockModelDomainApplication() {
        fi.hel.allu.model.domain.Application applicationModel = new fi.hel.allu.model.domain.Application();
        applicationModel.setId(123);
        applicationModel.setType("MockType");
        applicationModel.setHandler("MockHandler");
        applicationModel.setStatus("MockStatus");
        applicationModel.setProjectId(321);
        applicationModel.setName("MockName");
        applicationModel.setCustomerId(345);
        return applicationModel;
    }

    private ResponseEntity<Person> createMockModelPerson() {
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

    private ResponseEntity<fi.hel.allu.model.domain.Project> createMockModelProject() {
        fi.hel.allu.model.domain.Project projectModel = new fi.hel.allu.model.domain.Project();
        projectModel.setOwnerId(111);
        projectModel.setId(555);
        projectModel.setName("MockName");
        projectModel.setContactId(666);
        return new ResponseEntity<>(projectModel, HttpStatus.OK);
    }

    private ApplicationDTO createMockApplication() {
        ApplicationDTO appDTO = new ApplicationDTO();
        Application app = new Application();
        app.setName("Tapahtuma 1");
        app.setType("Ulkoilmatapahtuma");
        app.setInformation("Suspendisse quis arcu dolor. Donec fringilla nunc mollis aliquet mollis. Donec commodo tempus erat. " +
                "Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Duis convallis sem tincidunt " +
                "enim mattis eleifend eget eu ante");
        app.setCreateDate(ZonedDateTime.now());
        app.setStatus("Vireillä");
        app.setId(123);

        Customer customer = new Customer();
        customer.setId(23433);
        customer.setName("Asiakas");
        customer.setType("Henkilöasiakas");
        customer.setAddress("Jokutie");
        customer.setEmail("mail@mail.com");
        customer.setZipCode("00100");
        customer.setPostOffice("HELSINKI");
        app.setCustomer(customer);

        Project project = new Project();
        project.setId(398);
        project.setName("Hanke1");
        project.setType("Sähkötyö");
        project.setInformation("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec quis congue erat. Aenean eget suscipit " +
                "neque. Quisque et tincidunt dui. Donec dictum tellus lectus, ut lobortis nulla mollis nec. Morbi ante est, tristique eu " +
                "eros ut, cursus consectetur justo. Donec varius sodales arcu, a posuere velit porta quis. Aliquam erat volutpat. Aliquam" +
                " bibendum in lectus ac ornare. Aenean lacus massa, maximus et metus eu, rutrum bibendum massa.");
        app.setProject(project);

        appDTO.getApplicationList().add(app);

        app = new Application();
        app.setName("Tapahtuma 2");
        app.setType("Ulkoilmatapahtuma2");
        app.setCreateDate(ZonedDateTime.now());
        app.setStatus("Käsittelyssä");
        app.setId(456);

        customer = new Customer();
        customer.setId(321);
        customer.setName("Asiakas2");
        customer.setType("Henkilöasiakas2");
        customer.setAddress("Jokutie2");
        customer.setEmail("mail@mail.com2");
        customer.setZipCode("00200");
        customer.setPostOffice("HELSINKI");
        app.setCustomer(customer);

        project = new Project();
        project.setId(789);
        project.setName("Hanke2");
        project.setType("Sähkötyö2");
        app.setProject(project);

        appDTO.getApplicationList().add(app);
        return appDTO;
    }
}
