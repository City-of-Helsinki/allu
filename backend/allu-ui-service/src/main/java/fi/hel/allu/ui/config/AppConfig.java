package fi.hel.allu.ui.config;

import fi.hel.allu.common.controller.handler.ServiceResponseErrorHandler;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.ui.security.PreAuthorizeEnforcerInterceptor;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
@EnableAutoConfiguration
public class AppConfig extends WebMvcConfigurerAdapter {
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new PreAuthorizeEnforcerInterceptor());
  }

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(new ServiceResponseErrorHandler());
    return restTemplate;
  }

  @Bean
  public fi.hel.allu.servicecore.config.ApplicationProperties serviceCoreApplicationProperties(
  @Value("${model.service.host}") @NotEmpty String modelServiceHost,
  @Value("${model.service.port}") @NotEmpty String modelServicePort,
  @Value("${search.service.host}") @NotEmpty String searchServiceHost,
  @Value("${search.service.port}") @NotEmpty String searchServicePort,
  @Value("${pdf.service.host}") @NotEmpty String pdfServiceHost,
  @Value("${pdf.service.port}") @NotEmpty String pdfServicePort,
  @Value("#{'${email.allowed.addresses:}'.split(',')}") List<String> emailAllowedAddresses,
  @Value("${email.sender.address}") @NotEmpty String emailSenderAddress) {
      return new ApplicationProperties(
          modelServiceHost,
          modelServicePort,
          searchServiceHost,
          searchServicePort,
          pdfServiceHost,
          pdfServicePort,
          emailAllowedAddresses,
          emailSenderAddress);
  }

}
