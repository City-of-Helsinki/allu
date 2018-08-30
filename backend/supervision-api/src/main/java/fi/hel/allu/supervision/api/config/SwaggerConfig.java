package fi.hel.allu.supervision.api.config;

import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import org.geolatte.geom.Geometry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.fasterxml.jackson.databind.SerializationFeature;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configuration of Swagger i.e. the REST documentation tool.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport {

  @Value("${supervision.api.basepath}")
  private String apiBasePath;

  @Bean
  public Docket api(ServletContext servletContext) {
      List<SecurityScheme> schemeList = Collections.singletonList(new ApiKey("api_key", "Authorization", "Bearer"));

      return new Docket(DocumentationType.SWAGGER_2)
         .directModelSubstitute(Geometry.class, String.class) // Prevent Swagger from generating documentation for third party geometry classes
        .select()
        .apis(RequestHandlerSelectors.basePackage("fi.hel.allu.supervision.api"))
        .paths(PathSelectors.any())
        .build()
        .pathProvider(
            new RelativePathProvider(servletContext) {
              @Override
              public String getApplicationBasePath() {
                return apiBasePath + super.getApplicationBasePath();
              }
            }
        )
        .securitySchemes(schemeList)
        .apiInfo(apiInfo());
  }

  private ApiInfo apiInfo() {
    return new ApiInfo(
        "Allu Supervision REST API",
        "Allu public interface.",
        "1.0.0",
        "TODO",
        new Contact("TODO", "TODO", "TODO"),
        "License (MIT)", "https://bitbucket.org/vincit/allu/raw/HEAD/LICENSE",
        Collections.emptyList());
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
    builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
    addDefaultHttpMessageConverters(converters);
  }

  @Override
  protected void addResourceHandlers(ResourceHandlerRegistry registry) {
      registry.addResourceHandler("/swagger-ui.html")
              .addResourceLocations("classpath:/META-INF/resources/");

      registry.addResourceHandler("/webjars/**")
              .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }
}