package fi.hel.allu.external.config;

import fi.hel.allu.common.controller.handler.ControllerExceptionHandlerConfig;
import fi.hel.allu.common.controller.handler.ServiceResponseErrorHandler;
import fi.hel.allu.servicecore.security.PreAuthorizeEnforcerInterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAutoConfiguration
@EnableAsync
public class AppConfig  implements WebMvcConfigurer {

  @Autowired
  private PreAuthorizeEnforcerInterceptor preAuthorizeEnforcerInterceptor;

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

  @Bean
  public MessageSource errorMessageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("ErrorMessages");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

  @Bean
  public MessageSource validationMessageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("ValidationMessages");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

  @Bean
  public MessageSource translationMessageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setBasename("TranslationMessages");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

  @Bean
  public ControllerExceptionHandlerConfig controllerExceptionHandlerConfig() {
    ControllerExceptionHandlerConfig config = new ControllerExceptionHandlerConfig();
    config.setTranslateErrorMessages(true);
    return config;
  }
}
