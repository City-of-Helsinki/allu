package fi.hel.allu.external.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import java.util.List;


/**
 * Configuration of Swagger i.e. the REST documentation tool.
 */
@Configuration
@EnableWebMvc
public class SwaggerConfig extends WebMvcConfigurationSupport {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        final String apiTitle = String.format("%s API", StringUtils.capitalize("Allu REST"));
        return new OpenAPI().addSecurityItem(new SecurityRequirement().addList(securitySchemeName)).components(
                        new Components().addSecuritySchemes(securitySchemeName,
                                                            new SecurityScheme().name(securitySchemeName)
                                                                    .type(SecurityScheme.Type.HTTP).scheme("bearer")
                                                                    .bearerFormat("JWT")))
                .info(new Info().title(apiTitle).version("1"));
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MappingJackson2HttpMessageConverter jacksonConverter =
                new MappingJackson2HttpMessageConverter(builder.build());
        converters.replaceAll(converter -> converter.getClass()
                .isAssignableFrom(MappingJackson2HttpMessageConverter.class) ? jacksonConverter : converter);

    }

      @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
      registry.addResourceHandler("/swagger-ui.html")
              .addResourceLocations("classpath:/META-INF/resources/");

      registry.addResourceHandler("/webjars/**")
              .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }
}
