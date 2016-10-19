package fi.hel.allu.ui.service;

import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.mapper.ApplicationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class SearchService {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private ApplicationMapper applicationMapper;

  @Autowired
  public SearchService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      ApplicationMapper applicationMapper) {
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
   * Find applications by given fields.
   *
   * @param queryParameters list of query parameters
   * @return List of ids of found applications
   */
  public List<Integer> search(QueryParameters queryParameters) {
    ResponseEntity<Integer[]> applicationResult = restTemplate.postForEntity(applicationProperties
       .getSearchServiceUrl(ApplicationProperties.PATH_SEARCH_APPLICATION_FIND_BY_FIELDS), queryParameters, Integer[].class);

    return Arrays.asList(applicationResult.getBody());
  }
}
