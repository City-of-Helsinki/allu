package fi.hel.allu.external.config;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import javax.servlet.ServletContext;

import fi.hel.allu.external.mapper.event.PromotionExtMapper;
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

import fi.hel.allu.external.mapper.*;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
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

  private static final Class<?>[] IGNORED_CLASSES = {
      CableReportExtMapper.class,
      PromotionExtMapper.class,
      ExcavationAnnouncementExtMapper.class,
      PlacementContractExtMapper.class,
      ShortTermRentalExtMapper.class
  };

  @Value("${ext.api.basepath}")
  private String apiBasePath;

  @Bean
  public Docket api10(ServletContext servletContext) {
    return createApi(servletContext, "external-api-v1", "v1")
      .tags(
        new Tag("Authentication", "Authentication API", 1),
        new Tag("Applications", "API to read and update data common to all application types", 2),
        new Tag("Cable reports", "Cable report application API", 3),
        new Tag("Events", "Event application API", 4),
        new Tag("Excavation announcements", "Excavation announcement application API", 5),
        new Tag("Placement contracts", "Placement contracts application API", 6),
        new Tag("Short term rentals", "Short term rental application API", 7),
        new Tag("Traffic arrangements", "Traffic arrangements application API", 8),
        new Tag("Application attachments", "API to list, dowload and add application attachments", 9),
        new Tag("Application history", "API to list application events (status changes and supervision events)", 10),
        new Tag("Comments", "API to manage application comments", 11),
        new Tag("Information requests", "API to read application information requests", 12),
        new Tag("Application kinds", "API to list application kinds", 13),
        new Tag("Fixed locations", "API to list Allu fixed locations", 14),
        new Tag("Traffic arrangement images", "API to list and download Allu traffic arrangement images", 15),
        new Tag("Application documents", "API to list application decisions and approval documents and download them with private person data anonymized. Allowed only for Allu internal users.", 16)
      );
  }

  @Bean
  public Docket api20(ServletContext servletContext) {
    return createApi(servletContext, "external-api-v2", "v2")
      .tags(
        new Tag("Authentication", "Authentication API", 1),
        new Tag("Applications", "API to read and update data common to all application types", 2),
        new Tag("Cable reports", "Cable report application API", 3),
        new Tag("Events", "Event application API", 4),
        new Tag("Excavation announcements", "Excavation announcement application API", 5),
        new Tag("Placement contracts", "Placement contracts application API", 6),
        new Tag("Short term rentals", "Short term rental application API", 7),
        new Tag("Traffic arrangements", "Traffic arrangements application API", 8),
        new Tag("Application attachments", "API to list, dowload and add application attachments", 9),
        new Tag("Application history", "API to list application events (status changes and supervision events)", 10),
        new Tag("Comments", "API to manage application comments", 11),
        new Tag("Information requests", "API to read application information requests", 12),
        new Tag("Application kinds", "API to list application kinds", 13),
        new Tag("Fixed locations", "API to list Allu fixed locations", 14),
        new Tag("Traffic arrangement images", "API to list and download Allu traffic arrangement images", 15),
        new Tag("Application documents", "API to list application decisions and approval documents and download them with private person data anonymized. Allowed only for Allu internal users.", 16)
      );
  }

  private Docket createApi(ServletContext servletContext, String groupName, String version) {
    List<SecurityScheme> schemeList = Collections.singletonList(new ApiKey("api_key", "Authorization", "Bearer"));

    return new Docket(DocumentationType.SWAGGER_2)
      .groupName(groupName)
      .directModelSubstitute(Geometry.class, String.class) // Prevent Swagger from generating documentation for third party geometry classes
      .select()
        .apis(RequestHandlerSelectors.basePackage("fi.hel.allu.external.api"))
        .paths(PathSelectors.regex(String.format("/%s.*", version)))
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
      .apiInfo(apiInfo(version))
      .ignoredParameterTypes(IGNORED_CLASSES);
  }

  private ApiInfo apiInfo(String version) {
    return new ApiInfo(
        "Allu REST API",
        "Allu public interface.",
        version,
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
