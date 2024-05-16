package fi.hel.allu.servicecore.service;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.mapper.ProjectMapper;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.beans.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@SuppressWarnings("unchecked")
@RunWith(Spectrum.class)
public class SearchSyncServiceSpec {

  @Mock
  private RestTemplate restTemplate;
  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private ApplicationMapper applicationMapper;
  @Mock
  private ProjectMapper projectMapper;
  @Mock
  private ApplicationServiceComposer applicationServiceComposer;

  private SearchSyncService searchSyncService;

  {
    describe("Search sync service", () -> {
      beforeEach(() -> {
        MockitoAnnotations.initMocks(this);
        searchSyncService = new SearchSyncService(restTemplate, applicationProperties,
            projectMapper, applicationServiceComposer);
        setupApplicationProperties();

        Mockito.when(applicationMapper.mapApplicationToJson(any(Application.class))).thenReturn(new ApplicationJson());
      });
      it("Successfully syncs", () -> {
        setupRestTemplate(2, 2, 2, 2, 2);
        searchSyncService.sync();
        Mockito.verify(restTemplate).postForEntity(START_SEARCH_SYNC_URL, null, Void.class);
        Mockito.verify(restTemplate, times(2)).exchange(Mockito.eq(ALL_APPLICATIONS_URL), Mockito.eq(HttpMethod.GET),
            any(), any(ParameterizedTypeReference.class), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(restTemplate, times(2)).exchange(Mockito.eq(ALL_PROJECTS_URL), Mockito.eq(HttpMethod.GET),
            any(), any(ParameterizedTypeReference.class), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(restTemplate, times(2)).exchange(Mockito.eq(ALL_CUSTOMERS_URL), Mockito.eq(HttpMethod.GET),
            any(), any(ParameterizedTypeReference.class), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(restTemplate, times(2)).exchange(Mockito.eq(ALL_CONTACTS_URL), Mockito.eq(HttpMethod.GET),
            any(), any(ParameterizedTypeReference.class), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(restTemplate, times(2)).exchange(Mockito.eq(ALL_SUPERVISION_TASKS_URL), Mockito.eq(HttpMethod.GET),
                                                        any(), any(ParameterizedTypeReference.class), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(restTemplate).postForEntity(COMMIT_SEARCH_SYNC_URL, null, Void.class);
      });
      it("Cancels on failure", () -> {
        setupRestTemplate(2, 2, 0, 2, 2);
        searchSyncService.sync();
        Mockito.verify(restTemplate).postForEntity(START_SEARCH_SYNC_URL, null, Void.class);
        Mockito.verify(restTemplate).postForEntity(CANCEL_SEARCH_SYNC_URL, null, Void.class);
      });
    });
  }

  private static final String START_SEARCH_SYNC_URL = "startSearch";
  private static final String COMMIT_SEARCH_SYNC_URL = "commitSearch";
  private static final String CANCEL_SEARCH_SYNC_URL = "cancelSearch";
  private static final String ALL_APPLICATIONS_URL = "allApplications";
  private static final String ALL_PROJECTS_URL = "allProjects";
  private static final String ALL_CUSTOMERS_URL = "allCustomers";
  private static final String ALL_CONTACTS_URL = "allContacts";
  private static final String ALL_SUPERVISION_TASKS_URL = "allTasks";
  private static final String SYNC_APPLICATIONS_URL = "syncApplications";
  private static final String SYNC_PROJECTS_URL = "syncProjects";
  private static final String SYNC_CUSTOMERS_URL = "syncCustomers";
  private static final String SYNC_CONTACTS_URL = "syncContacts";
  private static final String SYNC_SUPERVISION_TASKS_URL = "syncTasks";

  private void setupApplicationProperties() {
    Mockito.when(applicationProperties.getStartSearchSyncUrl()).thenReturn(START_SEARCH_SYNC_URL);
    Mockito.when(applicationProperties.getCommitSearchSyncUrl()).thenReturn(COMMIT_SEARCH_SYNC_URL);
    Mockito.when(applicationProperties.getCancelSearchSyncUrl()).thenReturn(CANCEL_SEARCH_SYNC_URL);
    Mockito.when(applicationProperties.getAllApplicationsUrl()).thenReturn(ALL_APPLICATIONS_URL);
    Mockito.when(applicationProperties.getAllProjectsUrl()).thenReturn(ALL_PROJECTS_URL);
    Mockito.when(applicationProperties.getAllCustomersUrl()).thenReturn(ALL_CUSTOMERS_URL);
    Mockito.when(applicationProperties.getAllContactsUrl()).thenReturn(ALL_CONTACTS_URL);
    Mockito.when(applicationProperties.getAllSupervisionTasksUrl()).thenReturn(ALL_SUPERVISION_TASKS_URL);
    Mockito.when(applicationProperties.getSyncApplicationsUrl()).thenReturn(SYNC_APPLICATIONS_URL);
    Mockito.when(applicationProperties.getSyncProjectsUrl()).thenReturn(SYNC_PROJECTS_URL);
    Mockito.when(applicationProperties.getSyncCustomersUrl()).thenReturn(SYNC_CUSTOMERS_URL);
    Mockito.when(applicationProperties.getSyncContactsUrl()).thenReturn(SYNC_CONTACTS_URL);
    Mockito.when(applicationProperties.getSyncSupervisionTaskUrl()).thenReturn(SYNC_SUPERVISION_TASKS_URL);
  }


  private void setupRestTemplate(int numApplicationPages, int numProjectPages, int numCustomerPages,
      int numContactPages, int numSupervisionPages) {
    Mockito.when(restTemplate.postForEntity(START_SEARCH_SYNC_URL, null, Void.class))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));
    Mockito.when(restTemplate.postForEntity(COMMIT_SEARCH_SYNC_URL, null, Void.class))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));
    Mockito.when(restTemplate.postForEntity(CANCEL_SEARCH_SYNC_URL, null, Void.class))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));
    Mockito.when(restTemplate.postForEntity(Mockito.eq(SYNC_APPLICATIONS_URL), any(), Mockito.eq(Void.class)))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));
    Mockito.when(restTemplate.postForEntity(Mockito.eq(SYNC_PROJECTS_URL), any(), Mockito.eq(Void.class)))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));
    Mockito.when(restTemplate.postForEntity(Mockito.eq(SYNC_CUSTOMERS_URL), any(), Mockito.eq(Void.class)))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));
    Mockito.when(restTemplate.postForEntity(Mockito.eq(SYNC_CONTACTS_URL), any(), Mockito.eq(Void.class)))
        .thenReturn(new ResponseEntity<>(HttpStatus.OK));
    Mockito.when(restTemplate.postForEntity(Mockito.eq(SYNC_SUPERVISION_TASKS_URL), any(), Mockito.eq(Void.class)))
            .thenReturn(new ResponseEntity<>(HttpStatus.OK));
    // Setup mocking for data fetchers
    Mockito.when(restTemplate.exchange(Mockito.eq(ALL_APPLICATIONS_URL), Mockito.eq(HttpMethod.GET), any(),
                                       any(ParameterizedTypeReference.class), Mockito.anyInt(), Mockito.anyInt()))
            .then(new Answer<ResponseEntity<Page<Application>>>() {

              @Override
              public ResponseEntity<Page<Application>> answer(InvocationOnMock invocation) throws Throwable {
                return generatePage(invocation, numApplicationPages, Application.class);
              }
            });
    Mockito.when(restTemplate.exchange(Mockito.eq(ALL_PROJECTS_URL), Mockito.eq(HttpMethod.GET), any(),
                                       any(ParameterizedTypeReference.class), Mockito.anyInt(), Mockito.anyInt()))
            .then(new Answer<ResponseEntity<Page<Project>>>() {

              @Override
              public ResponseEntity<Page<Project>> answer(InvocationOnMock invocation) throws Throwable {
                return generatePage(invocation, numProjectPages, Project.class);
              }
            });
    Mockito.when(restTemplate.exchange(Mockito.eq(ALL_CUSTOMERS_URL), Mockito.eq(HttpMethod.GET), any(),
                                       any(ParameterizedTypeReference.class), Mockito.anyInt(), Mockito.anyInt()))
            .then(new Answer<ResponseEntity<Page<Customer>>>() {

              @Override
              public ResponseEntity<Page<Customer>> answer(InvocationOnMock invocation) throws Throwable {
                return generatePage(invocation, numCustomerPages, Customer.class);
              }
            });
    Mockito.when(restTemplate.exchange(Mockito.eq(ALL_CONTACTS_URL), Mockito.eq(HttpMethod.GET), any(),
                                       any(ParameterizedTypeReference.class), Mockito.anyInt(), Mockito.anyInt()))
            .then(new Answer<ResponseEntity<Page<Contact>>>() {

              @Override
              public ResponseEntity<Page<Contact>> answer(InvocationOnMock invocation) throws Throwable {
                return generatePage(invocation, numContactPages, Contact.class);
              }
            });

    Mockito.when(restTemplate.exchange(Mockito.eq(ALL_SUPERVISION_TASKS_URL), Mockito.eq(HttpMethod.GET), any(),
                                       any(ParameterizedTypeReference.class), Mockito.anyInt(), Mockito.anyInt()))
            .then(new Answer<ResponseEntity<Page<SupervisionWorkItem>>>() {

              @Override
              public ResponseEntity<Page<SupervisionWorkItem>> answer(InvocationOnMock invocation) throws Throwable {
                return generatePage(invocation, numSupervisionPages, SupervisionWorkItem.class);
              }
            });
  }

  // Mock code to emulate model-service's paging "get all" api. The requested
  // page number & page size are in invocation. Total number of pages to supply
  // is in numPages. If the request is for non-existing page, return
  // HttpStatus.NOT_FOUND, otherwise generate a page and return it.
  private <T> ResponseEntity<Page<T>> generatePage(InvocationOnMock invocation, int numPages, Class<T> clazz)
      throws InstantiationException, IllegalAccessException, Exception {
    int pageNum = invocation.getArgument(4);
    int pageSize = invocation.getArgument(5);
    if (pageNum >= numPages) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    List<T> elems = new ArrayList<>();
    for (int i = 0; i < pageSize; ++i) {
      T instance = clazz.newInstance();
      new Statement(instance, "setId", new Integer[] {i}).execute();
      elems.add(instance);
    }
    PageImpl<T> page = new PageImpl<>(elems, PageRequest.of(pageNum, pageSize), pageSize * numPages);
    return new ResponseEntity<>(page, HttpStatus.OK);
  }
}