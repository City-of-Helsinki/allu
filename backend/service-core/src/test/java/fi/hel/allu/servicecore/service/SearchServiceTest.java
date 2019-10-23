package fi.hel.allu.servicecore.service;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.mapper.CustomerMapper;
import fi.hel.allu.servicecore.mapper.ProjectMapper;
import fi.hel.allu.servicecore.util.RestResponsePage;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SearchServiceTest {
  @Autowired
  private AsyncRestTemplate restTemplate;
  @Autowired
  private ApplicationProperties applicationProperties;
  @Autowired
  private ApplicationMapper applicationMapper;
  @Autowired
  private CustomerMapper customerMapper;
  @Autowired
  private ProjectMapper projectMapper;
  @Autowired
  private LocationService locationService;
  @Autowired
  private SearchService searchService;

  private RestTemplate syncRestTemplate;

  private static final String BASE_URL = "http://test:9000";
  private static final String APPLICATION_SEARCH = "/applications";

  @Before
  public void setup() {
    syncRestTemplate = Mockito.mock(RestTemplate.class);
    Mockito.when(restTemplate.getRestOperations()).thenReturn(syncRestTemplate);
  }

  @Test
  public void shouldFindApplications() {
    Mockito.when(applicationProperties.getApplicationSearchUrl()).thenReturn(APPLICATION_SEARCH);
    ApplicationQueryParameters queryParameters = new ApplicationQueryParameters();
    queryParameters.setQueryParameters(Arrays.asList(new QueryParameter()));
    RestResponsePage<Integer> response = new RestResponsePage<>(Arrays.asList(1, 2, 3), 0, 3, 50);

    Mockito.when(syncRestTemplate.exchange(
      any(URI.class), any(),
      any(),
      Mockito.<ParameterizedTypeReference<RestResponsePage<Integer>>> any()))
      .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

    Page<ApplicationES> applications= searchService.searchApplication(queryParameters,
      new PageRequest(0, 100),
      false);
    assertEquals(3, applications.getNumberOfElements());
  }

  @Test
  public void shouldRetryFailedOperations() {
    List<ApplicationJson> applications = Arrays.asList(new ApplicationJson());
    Mockito.when(applicationProperties.getApplicationsSearchUpdateUrl()).thenReturn(BASE_URL + APPLICATION_SEARCH);
    Mockito.when(restTemplate.put(anyString(), any(HttpEntity.class))).thenThrow(new RestClientException("Fail"));
    searchService.updateApplications(applications);
    Mockito.verify(restTemplate, times(3)).put(anyString(), any(HttpEntity.class));
  }

  @Test
  public void shouldCallSynchronousPutWhenWaitRefreshSet() {
    List<ApplicationJson> applications = Arrays.asList(new ApplicationJson());
    Mockito.when(applicationProperties.getApplicationsSearchUpdateUrl()).thenReturn(BASE_URL + APPLICATION_SEARCH);
    searchService.updateApplications(applications, true);
    Mockito.verify(syncRestTemplate, times(1)).put(anyString(), any(HttpEntity.class));
  }

  @Configuration
  @EnableRetry
  static class AppConfig {
    @Bean
    public AsyncRestTemplate restTemplate() {
      return Mockito.mock(AsyncRestTemplate.class);
    }

    @Bean
    public ApplicationProperties applicationProperties() {
      return Mockito.mock(ApplicationProperties.class);
    }

    @Bean
    public ApplicationMapper applicationMapper() {
      return Mockito.mock(ApplicationMapper.class);
    }

    @Bean
    public CustomerMapper customerMapper() {
      return Mockito.mock(CustomerMapper.class);
    }

    @Bean
    public ProjectMapper projectMapper() {
      return Mockito.mock(ProjectMapper.class);
    }

    @Bean
    public LocationService locationService() {
      return Mockito.mock(LocationService.class);
    }

    @Bean
    public SearchService searchService() {
      return new SearchService(applicationProperties(), restTemplate(), applicationMapper(), customerMapper(),
        projectMapper(), locationService());
    }
  }
}
