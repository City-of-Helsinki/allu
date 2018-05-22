package fi.hel.allu.pdfcreator.config;

import fi.hel.allu.common.controller.handler.ControllerExceptionHandlerConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableAutoConfiguration
public class AppConfig {

  @Bean
  public ControllerExceptionHandlerConfig controllerExceptionHandlerConfig() {
    ControllerExceptionHandlerConfig config = new ControllerExceptionHandlerConfig();
    config.setTranslateErrorMessages(false);
    return config;
  }
}
