package fi.hel.allu.servicecore.service;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.mapper.CustomerMapper;
import fi.hel.allu.servicecore.mapper.ProjectMapper;
import fi.hel.allu.servicecore.util.RestResponsePage;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class SearchServiceSpec {
  @Mock
  private RestTemplate restTemplate;
  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private ApplicationMapper applicationMapper;
  @Mock
  private CustomerMapper customerMapper;
  @Mock
  private ProjectMapper projectMapper;

  private SearchService searchService;

  {
    describe("Search service", () -> {
      beforeEach(() -> {
        MockitoAnnotations.initMocks(this);
        searchService = new SearchService(applicationProperties, restTemplate, applicationMapper, customerMapper,
            projectMapper);
      });
      it("Finds applications", () -> {
        final String APPLICATION_SEARCH = "APPLICATION_SEARCH";
        Mockito.when(applicationProperties.getApplicationSearchUrl()).thenReturn(APPLICATION_SEARCH);
        QueryParameters queryParameters = new QueryParameters();
        queryParameters.setQueryParameters(Arrays.asList(new QueryParameter()));
        RestResponsePage<Integer> response = new RestResponsePage<>();
        response.setContent(Arrays.asList(1, 2, 3));
        response.setNumberOfElements(3);
        response.setTotalElements(50);
        Mockito.when(restTemplate.exchange(
            Mockito.any(URI.class), Mockito.any(),
            Mockito.any(),
            Mockito.<ParameterizedTypeReference<RestResponsePage<Integer>>> any()))
            .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        Page<ApplicationJson> applicationJsons = searchService.searchApplication(queryParameters,
            new PageRequest(0, 100),
            false,
            ids -> mapApplications(ids));
        assertEquals(3, applicationJsons.getNumberOfElements());
      });
    });
  }

  private List<ApplicationJson> mapApplications(List<Integer> ids) {
    return ids.stream().map(i -> {
      ApplicationJson a = new ApplicationJson();
      a.setId(i);
      return a;
    }).collect(Collectors.toList());
  }

}
