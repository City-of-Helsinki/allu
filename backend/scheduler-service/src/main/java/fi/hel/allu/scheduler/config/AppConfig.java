package fi.hel.allu.scheduler.config;

import fi.hel.allu.common.controller.handler.ServiceResponseErrorHandler;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAutoConfiguration
@EnableScheduling
public class AppConfig {
  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(new ServiceResponseErrorHandler());
    return restTemplate;
  }
}
