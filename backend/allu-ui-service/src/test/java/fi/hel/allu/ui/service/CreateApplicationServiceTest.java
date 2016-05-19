package fi.hel.allu.ui.service;


import fi.hel.allu.ui.domain.Application;
import fi.hel.allu.ui.domain.ApplicationDTO;
import fi.hel.allu.ui.domain.Customer;
import fi.hel.allu.ui.domain.Project;
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
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CreateApplicationServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private ApplicationService applicationService;
    private static Validator validator;
    private ApplicationDTO applicationDTO;

    @BeforeClass
    public static void setUpBeforeClass() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        applicationDTO = createMockApplication();
    }

    @Test
    public void testCreateWithEmptyApplicationList() {
        ApplicationDTO appDTO = new ApplicationDTO();

        Set<ConstraintViolation<ApplicationDTO>> constraintViolations =
                validator.validate( appDTO );

        assertEquals( 1, constraintViolations.size() );
        assertEquals("At least one application is required", constraintViolations.iterator().next().getMessage());
    }
    @Test
    public void testCreateWithNullApplicationName() {
        applicationDTO.getApplicationList().get(0).setName(null);
        Set<ConstraintViolation<ApplicationDTO>> constraintViolations =
                validator.validate( applicationDTO );
        assertEquals( 1, constraintViolations.size() );
        assertEquals("Application name is required", constraintViolations.iterator().next().getMessage());
    }


    @Test
    public void testCreateWithEmptyApplicationType() {
        applicationDTO.getApplicationList().get(0).setType("");
        Set<ConstraintViolation<ApplicationDTO>> constraintViolations =
                validator.validate( applicationDTO );
        assertEquals( 1, constraintViolations.size() );
        assertEquals("Application type is required", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testCreateWithEmptyCustomerName() {
        applicationDTO.getApplicationList().get(0).getCustomer().setName("");
        Set<ConstraintViolation<ApplicationDTO>> constraintViolations =
                validator.validate( applicationDTO );
        assertEquals( 1, constraintViolations.size() );
        assertEquals("Customer name is required", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void testCreateWithValidApplication() {
        Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.any(ApplicationDTO.class),
                Mockito.eq(ApplicationDTO.class)))
                .thenAnswer((Answer<ApplicationDTO>) invocation -> createMockApplication());

        ApplicationDTO response = applicationService.createApplication(applicationDTO);

        Mockito.verify(restTemplate, Mockito.times(1)).postForObject(Mockito.any(String.class), Mockito.any(ApplicationDTO.class),
                Mockito.eq(ApplicationDTO.class));
        assertNotNull(response);
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

        Customer customer = new Customer();
        customer.setId("23433452");
        customer.setName("Asiakas");
        customer.setType("Henkilöasiakas");
        customer.setAddress("Jokutie");
        customer.setEmail("mail@mail.com");
        customer.setZipCode("00100");
        customer.setPostOffice("HELSINKI");
        app.setCustomer(customer);

        Project project = new Project();
        project.setId("8754937398");
        project.setName("Hanke1");
        project.setType("Sähkötyö");
        project.setInformation("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec quis congue erat. Aenean eget suscipit " +
                "neque. Quisque et tincidunt dui. Donec dictum tellus lectus, ut lobortis nulla mollis nec. Morbi ante est, tristique eu " +
                "eros ut, cursus consectetur justo. Donec varius sodales arcu, a posuere velit porta quis. Aliquam erat volutpat. Aliquam" +
                " bibendum in lectus ac ornare. Aenean lacus massa, maximus et metus eu, rutrum bibendum massa.");
        app.setProject(project);

        appDTO.getApplicationList().add(app);
        return appDTO;
    }

}
