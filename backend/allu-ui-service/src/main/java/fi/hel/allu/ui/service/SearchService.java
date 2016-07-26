package fi.hel.allu.ui.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.mapper.ApplicationMapper;

@Service
public class SearchService {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private ApplicationMapper applicationMapper;

  @Autowired
  public SearchService(ApplicationProperties applicationProperties, RestTemplate restTemplate, ApplicationMapper applicationMapper) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.applicationMapper = applicationMapper;
  }

  public void insertApplicationToES(ApplicationJson applicationJson) {
    restTemplate.postForObject(applicationProperties
            .getSearchServiceUrl(ApplicationProperties.PATH_SEARCH_APPLICATION_CREATE), applicationMapper.createApplicationESModel
        (applicationJson), ApplicationES.class);
  }

  /**
   * Update given application search index. Application id is needed to update.
   *
   * @param applicationJson Application that is going to be updated
   */
  public void updateApplication(ApplicationJson applicationJson) {
    if (applicationJson != null && applicationJson.getId() != null && applicationJson.getId() > 0) {
      restTemplate.put(applicationProperties.getSearchServiceUrl(ApplicationProperties.PATH_SEARCH_APPLICATION_UPDATE), applicationMapper
          .createApplicationESModel(applicationJson), applicationJson.getId().intValue());
    }
  }

  /**
   * Find applications by given query string.
   *
   * @param queryString handler identifier that is used to find details
   * @return List of found application with details
   */
  public List<ApplicationJson> searchAll(String queryString) {
    ResponseEntity<ApplicationES[]> applicationResult = restTemplate.getForEntity(applicationProperties
        .getSearchServiceUrl(ApplicationProperties.PATH_SEARCH_APPLICATION_FIND_BY_QUERYSTRING), ApplicationES[].class, queryString);

    return Arrays.stream(applicationResult.getBody()).map(applicationES -> applicationMapper.mapApplicationESToJson(new
        ApplicationJson(), applicationES)).collect(Collectors.toList());
  }

  /**
   * Find applications by given fields.
   *
   * @param queryParameters list of query parameters
   * @return List of found application with details
   */
  public List<ApplicationJson> search(QueryParameters queryParameters) {
    ResponseEntity<ApplicationES[]> applicationResult = restTemplate.postForEntity(applicationProperties
       .getSearchServiceUrl(ApplicationProperties.PATH_SEARCH_APPLICATION_FIND_BY_FIELDS), queryParameters, ApplicationES[].class);

    return Arrays.stream(applicationResult.getBody()).map(applicationES -> applicationMapper.mapApplicationESToJson(new ApplicationJson
        (), applicationES)).collect(Collectors.toList());
  }
}
