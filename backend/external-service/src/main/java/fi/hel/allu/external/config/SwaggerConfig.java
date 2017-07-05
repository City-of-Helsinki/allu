package fi.hel.allu.external.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Configuration of Swagger i.e. the REST documentation tool.
 */
@Configuration
//@EnableSwagger2
//@Import({ springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration.class }) // enable some of the JSR-303 annotations
public class SwaggerConfig {

  @Bean
  WebMvcConfigurer configurer () {
    return new WebMvcConfigurerAdapter() {
      @Override
      public void addResourceHandlers (ResourceHandlerRegistry registry) {
        PathResourceResolver pathResourceResolver = new PathResourceResolver();
        pathResourceResolver.setAllowedLocations();
        registry.addResourceHandler("/swagger-ui/**").
            addResourceLocations("classpath:/swagger-ui/").resourceChain(true).addResolver(new PathResourceResolver());
      }
    };
  }
//  @Bean
//  public Docket api() {
//    return new Docket(DocumentationType.SWAGGER_2)
//        .select()
//        .apis(RequestHandlerSelectors.any())
//        .paths(PathSelectors.any())
//        .build();
//  }
}