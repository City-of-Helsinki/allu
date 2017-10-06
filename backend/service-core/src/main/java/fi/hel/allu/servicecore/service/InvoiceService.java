package fi.hel.allu.servicecore.service;

import fi.hel.allu.model.domain.Invoice;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.InvoiceJson;
import fi.hel.allu.servicecore.mapper.InvoiceMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;

  @Autowired
  public InvoiceService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  /**
   * Retrieve invoices for application
   *
   * @param applicationId
   * @return
   */
  public List<InvoiceJson> findByApplication(int applicationId) {
    Invoice[] foundInvoices = restTemplate.getForObject(applicationProperties.getFindApplicationInvoicesUrl(),
        Invoice[].class, applicationId);

    return Arrays.stream(foundInvoices).map(InvoiceMapper::mapToJson).collect(Collectors.toList());
  }
}
