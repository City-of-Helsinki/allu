package fi.hel.allu.external.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Configuration of Swagger i.e. the REST documentation tool.
 */
@Configuration
public class SwaggerConfig {

  @Bean
  WebMvcConfigurer configurer () {
    return new WebMvcConfigurerAdapter() {
      @Override
      public void addResourceHandlers (ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**").addResourceLocations("classpath:/swagger-ui/");
      }
    };
  }
}