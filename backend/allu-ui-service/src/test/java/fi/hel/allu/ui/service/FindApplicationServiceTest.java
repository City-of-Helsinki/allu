package fi.hel.allu.ui.service;

import fi.hel.allu.common.types.CustomerType;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.LocationJson;
import fi.hel.allu.ui.domain.PersonJson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.geolatte.geom.builder.DSL.*;
import static org.junit.Assert.*;

public class FindApplicationServiceTest extends MockServices {
    @Mock
    protected LocationService locationService;
    @Mock
    protected PersonService personService;
    @InjectMocks
    private ApplicationService applicationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        initMocks();
        Mockito.when(locationService.findLocationById(Mockito.anyInt())).thenAnswer((Answer<LocationJson>) invocation ->
            createLocationJson(102));
        Mockito.when(personService.findPersonById(Mockito.anyInt())).thenAnswer((Answer<PersonJson>) invocation ->
            createPersonJson(200));
    }

    @Test
    public void testFindApplicationById() {
        ApplicationJson response = applicationService.findApplicationById("123");

        assertNotNull(response);
        assertNotNull(response.getCustomer());
        assertNotNull(response.getProject());
        assertNotNull(response.getApplicant());
        assertEquals(100, response.getProject().getId().intValue());
        assertEquals(101, response.getCustomer().getId().intValue());
        assertEquals(102, response.getLocation().getId().intValue());
        assertEquals(103, response.getApplicant().getId().intValue());
        assertNull(response.getCustomer().getOrganization());
        assertNull(response.getApplicant().getPerson());
        assertNotNull(response.getApplicant().getOrganization());
        assertNotNull(response.getCustomer().getPerson());
        assertEquals(201, response.getApplicant().getOrganization().getId().intValue());
        assertEquals(200, response.getCustomer().getPerson().getId().intValue());
    }

    @Test
    public void testFindApplicationByHandler() {

        List<ApplicationJson> response = applicationService.findApplicationByHandler("222");

        assertNotNull(response);
        assertEquals(2, response.size());

        assertNotNull(response.get(0).getCustomer());
        assertNotNull(response.get(0).getProject());
        assertNotNull(response.get(0).getApplicant());
        assertNotNull(response.get(0).getLocation());
        assertEquals(100, response.get(0).getProject().getId().intValue());
        assertEquals(101, response.get(0).getCustomer().getId().intValue());
        assertEquals(102, response.get(0).getLocation().getId().intValue());
        assertEquals(103, response.get(0).getApplicant().getId().intValue());
        assertNull(response.get(0).getCustomer().getOrganization());
        assertNull(response.get(0).getApplicant().getPerson());
        assertNotNull(response.get(0).getApplicant().getOrganization());
        assertNotNull(response.get(0).getCustomer().getPerson());
        assertEquals(201, response.get(0).getApplicant().getOrganization().getId().intValue());
        assertEquals(200, response.get(0).getCustomer().getPerson().getId().intValue());

        assertNotNull(response.get(1));
        assertNotNull(response.get(1).getCustomer());
        assertNotNull(response.get(1).getProject());
        assertNotNull(response.get(1).getApplicant());
        assertNotNull(response.get(1).getLocation());
        assertEquals("MockName2", response.get(1).getName());
    }
}
