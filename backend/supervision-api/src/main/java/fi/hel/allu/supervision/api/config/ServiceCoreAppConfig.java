package fi.hel.allu.supervision.api.config;

import fi.hel.allu.servicecore.config.ApplicationProperties;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;
import java.util.List;

@Configuration
public class ServiceCoreAppConfig {

  @Bean
  public fi.hel.allu.servicecore.config.ApplicationProperties serviceCoreApplicationProperties(
      @Value("${model.service.host}") @NotEmpty String modelServiceHost,
      @Value("${model.service.port}") @NotEmpty String modelServicePort,
      @Value("${search.service.host}") @NotEmpty String searchServiceHost,
      @Value("${search.service.port}") @NotEmpty String searchServicePort,
      @Value("${pdf.service.host}") @NotEmpty String pdfServiceHost,
      @Value("${pdf.service.port}") @NotEmpty String pdfServicePort,
      @Value("#{'${email.allowed.addresses:}'.split(',')}") List<String> emailAllowedAddresses,
      @Value("${email.sender.address}") @NotEmpty String emailSenderAddress,
      @Value("#{'${anonymous.access.paths:}'.split(',')}") @NotNull List<String> anonymousAccessPaths,
      @Value("${wfs.paymentclass.url}") @NotEmpty String paymentClassUrl,
      @Value("${wfs.username}") @NotEmpty String paymentClassUsername,
      @Value("${wfs.password}") @NotEmpty String paymentClassPassword) {
    return new ApplicationProperties(
        modelServiceHost,
        modelServicePort,
        searchServiceHost,
        searchServicePort,
        pdfServiceHost,
        pdfServicePort,
        emailAllowedAddresses,
        emailSenderAddress,
        anonymousAccessPaths,
        paymentClassUrl,
        paymentClassUsername,
        paymentClassPassword);
  }
}
