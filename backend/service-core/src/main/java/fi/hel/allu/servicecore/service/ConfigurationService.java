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

import javax.annotation.PostConstruct;

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

  @PostConstruct
  public void test() {
    logger.info("PLACEMENT_CONTRACT_DECISION_MAKER={}", getSingleValue(ConfigurationKey.PLACEMENT_CONTRACT_DECISION_MAKER));
  }

  public String getSingleValue(ConfigurationKey key) {
    return getConfigurations(key).stream().findFirst().map(Configuration::getValue).orElse(null);
  }

  public List<String> getValues(ConfigurationKey key) {
    return getConfigurations(key).stream().map(Configuration::getValue).collect(Collectors.toList());
  }

  private List<Configuration> getConfigurations(ConfigurationKey key) {
    final List<Configuration> configurationRows = restTemplate.exchange(
        applicationProperties.getConfigurationUrl(key),
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Configuration>>() {}).getBody();
    return configurationRows;
  }

}
