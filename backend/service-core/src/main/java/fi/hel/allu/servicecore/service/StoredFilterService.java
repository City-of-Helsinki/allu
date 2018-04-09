package fi.hel.allu.servicecore.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import fi.hel.allu.model.domain.StoredFilter;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.StoredFilterJson;
import fi.hel.allu.servicecore.mapper.StoredFilterMapper;

@Service
public class StoredFilterService {
  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private UserService userService;

  @Autowired
  public StoredFilterService(ApplicationProperties applicationProperties,
                             RestTemplate restTemplate, UserService userService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.userService = userService;
  }

  public StoredFilterJson findById(int id) {
    ResponseEntity<StoredFilter> result = restTemplate.getForEntity(
      applicationProperties.getStoredFilterUrl(), StoredFilter.class, id);
    return StoredFilterMapper.toJson(result.getBody());
  }

  public List<StoredFilterJson> findByUser(int userId) {
    ResponseEntity<StoredFilter[]> result = restTemplate.getForEntity(
      applicationProperties.getStoredFilterFindByUserUrl(),
      StoredFilter[].class, userId);

    return Stream.of(result.getBody())
      .map(StoredFilterMapper::toJson)
      .collect(Collectors.toList());
  }

  public StoredFilterJson insert(StoredFilterJson json) {
    StoredFilter filter = StoredFilterMapper.toModel(json);
    filter.setUserId(userService.getCurrentUser().getId());

    ResponseEntity<StoredFilter> result = restTemplate.postForEntity(
      applicationProperties.getStoredFilterCreateUrl(), filter, StoredFilter.class);

    return StoredFilterMapper.toJson(result.getBody());
  }

  public StoredFilterJson update(StoredFilterJson json) {
    HttpEntity<StoredFilter> filter = new HttpEntity<>(StoredFilterMapper.toModel(json));
    ResponseEntity<StoredFilter> result = restTemplate.exchange(
      applicationProperties.getStoredFilterUrl(),
      HttpMethod.PUT,
      filter,
      StoredFilter.class,
      json.getId());
    return StoredFilterMapper.toJson(result.getBody());
  }

  public void delete(int id) {
    restTemplate.delete(applicationProperties.getStoredFilterUrl(), id);
  }

  public void setAsDefault(int filterId) {
    restTemplate.put(applicationProperties.getStoredFilterSetAsDefaultUrl(), null, filterId);
  }
}
