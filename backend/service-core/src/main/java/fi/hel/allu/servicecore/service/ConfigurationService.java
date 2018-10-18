package fi.hel.allu.servicecore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.model.domain.ConfigurationKey;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

@Service
public class ConfigurationService {

  private static final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);
  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;

  @Autowired
  public ConfigurationService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  public List<Configuration> getAllConfigurations() {
    return restTemplate.exchange(
        applicationProperties.getConfigurationUrl(),
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Configuration>>() {}).getBody();
  }

  public String getSingleValue(ConfigurationKey key) {
    return getConfigurations(key).stream().findFirst().map(Configuration::getValue).orElse(null);
  }

  public List<String> getValues(ConfigurationKey key) {
    return getConfigurations(key).stream().map(Configuration::getValue).collect(Collectors.toList());
  }

  public Configuration updateConfiguration(int id, Configuration configuration) {
    final HttpEntity<Configuration> requestEntity = new HttpEntity<>(configuration);
    final ResponseEntity<Configuration> responseEntity = restTemplate.exchange(
        applicationProperties.getConfigurationUrl(),
        HttpMethod.PUT,
        requestEntity,
        Configuration.class,
        id);
    return responseEntity.getBody();
  }

  private List<Configuration> getConfigurations(ConfigurationKey key) {
    final List<Configuration> configurationRows = restTemplate.exchange(
        applicationProperties.getConfigurationUrlForKey(key),
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Configuration>>() {}).getBody();
    return configurationRows;
  }

}
