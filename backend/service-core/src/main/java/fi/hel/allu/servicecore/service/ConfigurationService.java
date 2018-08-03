package fi.hel.allu.servicecore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.model.domain.ConfigurationType;
import fi.hel.allu.servicecore.config.ApplicationProperties;

@Service
public class ConfigurationService {

  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;

  @Autowired
  public ConfigurationService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  public String getSingleValue(ConfigurationType configuration) {
    return getConfigurations(configuration).stream().findFirst().map(Configuration::getValue).orElse(null);
  }

  public List<String> getValues(ConfigurationType configuration) {
    return getConfigurations(configuration).stream().map(Configuration::getValue).collect(Collectors.toList());
  }

  private List<Configuration> getConfigurations(ConfigurationType configuration) {
    final List<Configuration> configurationRows = restTemplate.exchange(
        applicationProperties.getConfigurationUrl(configuration),
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Configuration>>() {}).getBody();
    return configurationRows;
  }

}
