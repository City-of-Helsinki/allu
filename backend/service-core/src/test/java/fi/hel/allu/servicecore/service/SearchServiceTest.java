package fi.hel.allu.servicecore.service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.mapper.CustomerMapper;
import fi.hel.allu.servicecore.mapper.ProjectMapper;
import fi.hel.allu.servicecore.util.RestResponsePage;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SearchServiceTest {

  private static final String TEST_HOST = "localhost";
  private static final int TEST_PORT = 9021;
  private static final int APPLICATION_ID = 992;
  private static final int PROJECT_ID = 789;
  private static MockWebServer mockBackEnd;

  @Autowired
  private SearchService searchService;
  private static ObjectMapper objectMapper;

  @BeforeClass
  public static void setupClass() throws IOException {
    objectMapper = new ObjectMapper();
    mockBackEnd = new MockWebServer();
    mockBackEnd.start(TEST_PORT);
  }

  @AfterClass
  public static void tearDownClass() throws IOException {
      mockBackEnd.shutdown();
  }

  @Test
  public void shouldFindApplications() throws JsonProcessingException, InterruptedException {
    RestResponsePage<ApplicationES> response = new RestResponsePage<>(
        Arrays.asList(new ApplicationES(), new ApplicationES(), new ApplicationES()), PageRequest.of(0, 3), 50);
      mockBackEnd.enqueue(new MockResponse()
          .setBody(objectMapper.writeValueAsString(response))
          .addHeader("Content-Type", "application/json"));
    Page<ApplicationES> applications = searchService.searchApplication(new ApplicationQueryParameters(), PageRequest.of(0, 10), false);
    assertEquals(3, applications.getNumberOfElements());
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.POST.name(), request.getMethod());
    assertTrue(request.getPath().contains("/applications/search"));
  }

  @Test
  public void shouldFindProjects() throws JsonProcessingException, InterruptedException {
    RestResponsePage<Integer> response = new RestResponsePage<>(Arrays.asList(1, 2, 3), PageRequest.of(0, 3), 50);
      mockBackEnd.enqueue(new MockResponse()
          .setBody(objectMapper.writeValueAsString(response))
          .addHeader("Content-Type", "application/json"));
    Page<ProjectJson> projects = searchService.searchProject(new QueryParameters(), PageRequest.of(0, 10),
        ids -> { return ids.stream().map(id -> new ProjectJson()).collect(Collectors.toList());});
    assertEquals(3, projects.getNumberOfElements());
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.POST.name(), request.getMethod());
    assertTrue(request.getPath().contains("/projects/search"));
  }

  @Test
  public void shouldFindCustomers() throws JsonProcessingException, InterruptedException {
    RestResponsePage<Integer> response = new RestResponsePage<>(Arrays.asList(1, 2, 3), PageRequest.of(0, 3), 50);
      mockBackEnd.enqueue(new MockResponse()
          .setBody(objectMapper.writeValueAsString(response))
          .addHeader("Content-Type", "application/json"));
    Page<CustomerJson> customers = searchService.searchCustomer(new QueryParameters(), PageRequest.of(0, 10),
        ids -> { return ids.stream().map(id -> new CustomerJson()).collect(Collectors.toList());});
    assertEquals(3, customers.getNumberOfElements());
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.POST.name(), request.getMethod());
    assertTrue(request.getPath().contains("/customers/search"));
  }

  @Test
  public void shouldFindCustomersByType() throws JsonProcessingException, InterruptedException {
    RestResponsePage<Integer> response = new RestResponsePage<>(Arrays.asList(1, 2, 3), PageRequest.of(0, 3), 50);
      mockBackEnd.enqueue(new MockResponse()
          .setBody(objectMapper.writeValueAsString(response))
          .addHeader("Content-Type", "application/json"));
    Page<CustomerJson> customers = searchService.searchCustomerByType(CustomerType.COMPANY, new QueryParameters(), PageRequest.of(0, 10),
        false, ids -> { return ids.stream().map(id -> new CustomerJson()).collect(Collectors.toList());});
    assertEquals(3, customers.getNumberOfElements());
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.POST.name(), request.getMethod());
    assertTrue(request.getPath().contains("/customers/search/" + CustomerType.COMPANY.name()));
  }

  @Test
  public void shouldFindContacts() throws JsonProcessingException, InterruptedException {
    RestResponsePage<Integer> response = new RestResponsePage<>(Arrays.asList(1, 2, 3), PageRequest.of(0, 3), 50);
      mockBackEnd.enqueue(new MockResponse()
          .setBody(objectMapper.writeValueAsString(response))
          .addHeader("Content-Type", "application/json"));
    Page<ContactJson> contacts = searchService.searchContact(new QueryParameters(), PageRequest.of(0, 10),
        ids -> { return ids.stream().map(id -> new ContactJson()).collect(Collectors.toList());});
    assertEquals(3, contacts.getNumberOfElements());
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.POST.name(), request.getMethod());
    assertTrue(request.getPath().contains("/contacts/search"));
  }

  @Test
  public void shouldInsertApplication() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    searchService.insertApplication(new ApplicationJson());
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.POST.name(), request.getMethod());
    assertTrue(request.getPath().endsWith("/applications"));
  }

  @Test
  public void shouldUpdateApplicationAsync() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    searchService.updateApplications(Collections.singletonList(new ApplicationJson()));
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.PUT.name(), request.getMethod());
    assertTrue(request.getPath().contains("/applications/update"));
    assertFalse(request.getPath().contains("waitRefresh=true"));
  }

  @Test
  public void shouldUpdateApplicationSync() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    searchService.updateApplications(Collections.singletonList(new ApplicationJson()), true);
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.PUT.name(), request.getMethod());
    assertTrue(request.getPath().contains("/applications/update"));
    assertTrue(request.getPath().contains("waitRefresh=true"));
  }

  @Test
  public void shouldUpdateApplicationTagsAsync() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    searchService.updateTags(APPLICATION_ID, Collections.singletonList(new ApplicationTagJson()));
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.PUT.name(), request.getMethod());
    assertTrue(request.getPath().contains("/applications/partialupdate"));
    assertFalse(request.getPath().contains("waitRefresh=true"));
  }

  @Test
  public void shouldUpdateApplicationFieldAsync() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    searchService.updateApplicationField(APPLICATION_ID, "name", "foo", false);
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.PUT.name(), request.getMethod());
    assertTrue(request.getPath().contains("/applications/partialupdate"));
    assertFalse(request.getPath().contains("waitRefresh=true"));
  }

  @Test
  public void shouldUpdateApplicationFieldSync() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    searchService.updateApplicationField(APPLICATION_ID, "name", "foo", true);
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.PUT.name(), request.getMethod());
    assertTrue(request.getPath().contains("/applications/partialupdate"));
    assertTrue(request.getPath().contains("waitRefresh=true"));
  }

  @Test
  public void shouldUpdateApplicationCustomer() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    CustomerWithContactsJson customer = new CustomerWithContactsJson();
    customer.setRoleType(CustomerRoleType.APPLICANT);
    searchService.updateApplicationCustomerWithContacts(APPLICATION_ID, customer);
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.PUT.name(), request.getMethod());
    assertTrue(request.getPath().contains("/applications/" + APPLICATION_ID + "/customersWithContacts"));
    assertFalse(request.getPath().contains("waitRefresh=true"));
  }

  @Test
  public void shouldDeleteNote() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    searchService.deleteNote(APPLICATION_ID);
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.DELETE.name(), request.getMethod());
    assertTrue(request.getPath().endsWith("/applications/" + APPLICATION_ID));
    assertFalse(request.getPath().contains("waitRefresh=true"));
  }

  @Test
  public void shouldDeleteDraft() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    searchService.deleteDraft(APPLICATION_ID);
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.DELETE.name(), request.getMethod());
    assertTrue(request.getPath().endsWith("/applications/" + APPLICATION_ID));
    assertFalse(request.getPath().contains("waitRefresh=true"));
  }

  @Test
  public void shouldInsertProjoect() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    searchService.insertProject(new ProjectJson());
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.POST.name(), request.getMethod());
    assertTrue(request.getPath().endsWith("/projects"));
  }

  @Test
  public void shouldUpdateProject() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    ProjectJson project = new ProjectJson();
    project.setId(PROJECT_ID);
    searchService.updateProject(project);
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.PUT.name(), request.getMethod());
    assertTrue(request.getPath().contains("/projects/" + PROJECT_ID));
    assertFalse(request.getPath().contains("waitRefresh=true"));
  }

  @Test
  public void shouldUpdateProjects() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    searchService.updateProjects(Collections.singletonList(new ProjectJson()));
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.PUT.name(), request.getMethod());
    assertTrue(request.getPath().contains("/projects/update"));
    assertFalse(request.getPath().contains("waitRefresh=true"));
  }

  @Test
  public void shouldDeleteProject() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    searchService.deleteProject(PROJECT_ID);
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.DELETE.name(), request.getMethod());
    assertTrue(request.getPath().endsWith("/projects/" + PROJECT_ID));
  }

  @Test
  public void shouldInsertCustomer() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    searchService.insertCustomer(new CustomerJson());
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.POST.name(), request.getMethod());
    assertTrue(request.getPath().endsWith("/customers"));
  }

  @Test
  public void shouldUpdateCustomer() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    searchService.updateCustomers(Collections.singletonList(new CustomerJson()));
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.PUT.name(), request.getMethod());
    assertTrue(request.getPath().contains("/customers/update"));
    assertFalse(request.getPath().contains("waitRefresh=true"));
  }

  @Test
  public void shouldInsertContacts() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    searchService.insertContacts(Collections.singletonList(new ContactJson()));
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.POST.name(), request.getMethod());
    assertTrue(request.getPath().endsWith("/contacts"));
  }

  @Test
  public void shouldUpdateContacts() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    searchService.updateContacts(Collections.singletonList(new ContactJson()));
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.PUT.name(), request.getMethod());
    assertTrue(request.getPath().contains("/contacts/update"));
    assertFalse(request.getPath().contains("waitRefresh=true"));
  }

  @Test
  public void shouldUpdateCustomerOfApplications() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    CustomerJson customer = new CustomerJson(123);
    Map<Integer, List<CustomerRoleType>> applicationIdToCustomerRoleType = new HashMap<>();
    applicationIdToCustomerRoleType.put(APPLICATION_ID, Collections.singletonList(CustomerRoleType.APPLICANT));
    searchService.updateCustomerOfApplications(customer, applicationIdToCustomerRoleType);
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.PUT.name(), request.getMethod());
    assertTrue(request.getPath().contains("/customers/" + customer.getId() + "/applications"));
    assertFalse(request.getPath().contains("waitRefresh=true"));
  }

  @Test
  public void shouldUpdateContactOfApplications() throws InterruptedException {
    mockBackEnd.enqueue(new MockResponse());
    searchService.updateContactsOfApplications(Collections.singletonList(new ApplicationWithContactsES()));
    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals(HttpMethod.PUT.name(), request.getMethod());
    assertTrue(request.getPath().contains("/contacts/applications"));
    assertFalse(request.getPath().contains("waitRefresh=true"));
  }

  @Configuration
  static class AppConfig {
    @Bean
    public ApplicationProperties applicationProperties() {
      return new ApplicationProperties(null, null, TEST_HOST, String.valueOf(TEST_PORT), null, null, null, null, null, null, null,
          null, null);
    }

    @Bean
    public ApplicationMapper applicationMapper() {
      ApplicationMapper mapper = Mockito.mock(ApplicationMapper.class);
      Mockito.when(mapper.createApplicationESModel(Mockito.any(ApplicationJson.class))).thenReturn(new ApplicationES());
      return mapper;
    }

    @Bean
    public CustomerMapper customerMapper() {
      CustomerMapper mapper = Mockito.mock(CustomerMapper.class);
      Mockito.when(mapper.createCustomerES(Mockito.any(CustomerJson.class))).thenReturn(new CustomerES());
      Mockito.when(mapper.createContactES(Mockito.anyList())).thenReturn(Collections.singletonList(new ContactES()));
      return mapper;
    }

    @Bean
    public ProjectMapper projectMapper() {
      ProjectMapper mapper = Mockito.mock(ProjectMapper.class);
      Mockito.when(mapper.createProjectESModel(Mockito.any(ProjectJson.class))).thenReturn(new ProjectES());
      return mapper;
    }

    @Bean
    public LocationService locationService() {
      return Mockito.mock(LocationService.class);
    }

    @Bean
    public SearchService searchService() {
      return new SearchService(applicationProperties(), applicationMapper(), customerMapper(),
        projectMapper(), locationService(),
        WebClient.builder().exchangeStrategies(
            builder -> builder.codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
        ));
    }
  }
}
