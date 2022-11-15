package fi.hel.allu.supervision.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
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
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Optional;

/**
 * Configuration of Swagger i.e. the REST documentation tool.
 */
@Configuration
public class SwaggerConfig implements WebMvcConfigurer {


    private static final String[] FILTERED_APPLICATION_FIELDS = {"extension", "metadataVersion",
            "clientApplicationData", "externalOwnerId", "externalApplicationId", "invoicingChanged", "targetState"};


    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        final String apiTitle = String.format("%s API", StringUtils.capitalize("Allu REST"));
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                                    new SecurityScheme()
                                                            .name(securitySchemeName)
                                                            .type(SecurityScheme.Type.HTTP)
                                                            .scheme("bearer")
                                                            .bearerFormat("JWT")
                                )
                )
                .info(new Info().title(apiTitle).version("3"));
    }


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Find jackson message converter and add custom object mapper
        SimpleBeanPropertyFilter applicationFilter = SimpleBeanPropertyFilter
                .serializeAllExcept(FILTERED_APPLICATION_FIELDS);
        final FilterProvider filters = new SimpleFilterProvider().addFilter("applicationFilter", applicationFilter);

        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json()
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                                   SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS)
                .filters(filters)
                .build();
        Optional<HttpMessageConverter<?>> converter = converters.stream()
                .filter(m -> m.getClass().isAssignableFrom(MappingJackson2HttpMessageConverter.class))
                .findFirst();
        if (converter.isPresent()) {
            MappingJackson2HttpMessageConverter jacksonConverter = (MappingJackson2HttpMessageConverter) converter.get();
            jacksonConverter.setObjectMapper(mapper);
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


}