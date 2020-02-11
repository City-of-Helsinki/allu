package fi.hel.allu.supervision.api.config;

import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import org.geolatte.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.supervision.api.domain.BaseApplication;
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
      ApplicationExtensionJson.class,
      Page.class,
      ClientApplicationDataJson.class,
      Sort.class
  };

  private static final String[] FILTERED_APPLICATION_FIELDS = { "extension", "metadataVersion",
      "clientApplicationData", "externalOwnerId", "externalApplicationId", "invoicingChanged", "targetState" };

  @Value("${supervision.api.basepath}")
  private String apiBasePath;

  @Autowired
  private TypeResolver typeResolver;

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
        .apiInfo(apiInfo())
        .tags(
              new Tag("Applications", "API to search and manage applications"),
              new Tag("Supervision tasks", "API to search and manage supervision tasks"),
              new Tag("Projects", "Project API"),
              new Tag("Authentication", "Authentication API"),
              new Tag("Charge basis entries", "API to list and manage charge basis entries"),
              new Tag("Invoices", "API to list application's invoices"),
              new Tag("Users", "User API"),
              new Tag("Codes", "API providing translations for Allu codes"),
              new Tag("Application tags", "API to add and remove application tags"),
              new Tag("Comments", "API to list and add/remove application comments"),
              new Tag("Application attachments", "API to list and add application attachments"),
              new Tag("City districts", "API to list city districts"),
              new Tag("Customers", "API to search and manage customers"),
              new Tag("Locations", "API to manage locations"),
              new Tag("Payment classes", "API to fetch payment classes")
              )

        .additionalModels(
            typeResolver.resolve(BaseApplication.class),
            typeResolver.resolve(ExcavationAnnouncementJson.class),
            typeResolver.resolve(AreaRentalJson.class),
            typeResolver.resolve(CableReportJson.class),
            typeResolver.resolve(PlacementContractJson.class),
            typeResolver.resolve(ShortTermRentalJson.class),
            typeResolver.resolve(TrafficArrangementJson.class),
            typeResolver.resolve(EventJson.class),
            typeResolver.resolve(NoteJson.class))
        .ignoredParameterTypes(IGNORED_CLASSES);
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
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    // Find jackson message converter and add custom object mapper
    SimpleBeanPropertyFilter applicationFilter = SimpleBeanPropertyFilter
        .serializeAllExcept(FILTERED_APPLICATION_FIELDS);
    ObjectMapper mapper = Jackson2ObjectMapperBuilder.json()
      .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS)
      .filters(new SimpleFilterProvider().addFilter("applicationFilter", applicationFilter))
      .build();
    getMessageConverters().stream()
        .filter(m -> m.getClass().isAssignableFrom(MappingJackson2HttpMessageConverter.class))
        .findFirst()
        .ifPresent(j -> ((MappingJackson2HttpMessageConverter)j).setObjectMapper(mapper));
  }

  @Override
  protected void addResourceHandlers(ResourceHandlerRegistry registry) {
      registry.addResourceHandler("/swagger-ui.html")
              .addResourceLocations("classpath:/META-INF/resources/");

      registry.addResourceHandler("/webjars/**")
              .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }

}