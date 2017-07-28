package fi.hel.allu.ui.config;

import fi.hel.allu.common.controller.handler.ServiceResponseErrorHandler;
import fi.hel.allu.servicecore.security.PreAuthorizeEnforcerInterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableAutoConfiguration
public class AppConfig extends WebMvcConfigurerAdapter {

  private PreAuthorizeEnforcerInterceptor preAuthorizeEnforcerInterceptor;

  @Autowired
  public AppConfig(PreAuthorizeEnforcerInterceptor preAuthorizeEnforcerInterceptor) {
    this.preAuthorizeEnforcerInterceptor = preAuthorizeEnforcerInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(preAuthorizeEnforcerInterceptor);
  }

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(new ServiceResponseErrorHandler());
    return restTemplate;
  }

}
