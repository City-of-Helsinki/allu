package fi.hel.allu.servicecore.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.servicecore.config.ApplicationProperties;

@Service
public class InvoicingPeriodService {

  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;

  @Autowired
  public InvoicingPeriodService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  public List<InvoicingPeriod> createInvoicingPeriods(Integer applicationId, Integer periodLength) {
    InvoicingPeriod[] periods = restTemplate.postForObject(applicationProperties.getInvoicingPeriodUpdateUrl(), null,
        InvoicingPeriod[].class, applicationId, periodLength);
    return Arrays.asList(periods);
  }

  public List<InvoicingPeriod> updateInvoicingPeriods(Integer applicationId, int periodLength) {
    InvoicingPeriod[] periods = restTemplate.exchange(applicationProperties.getInvoicingPeriodUpdateUrl(), HttpMethod.PUT,
        null, InvoicingPeriod[].class, applicationId, periodLength).getBody();
    return Arrays.asList(periods);
  }

  public List<InvoicingPeriod> getInvoicingPeriods(Integer applicationId) {
    InvoicingPeriod[] periods = restTemplate.getForObject(applicationProperties.getInvoicingPeriodsUrl(),
        InvoicingPeriod[].class, applicationId);
    return Arrays.asList(periods);
  }

  public void deleteInvoicingPeriods(Integer id) {
    restTemplate.delete(applicationProperties.getInvoicingPeriodsUrl(), id);
  }

  public List<InvoicingPeriod> createPeriodsForRecurringApplication(Integer applicationId) {
    InvoicingPeriod[] periods = restTemplate.postForObject(applicationProperties.getRecurringApplicationPeriodsUrl(),
        null, InvoicingPeriod[].class, applicationId);
    return Arrays.asList(periods);

  }

  public void setPeriodsForExcavationAnnouncement(Integer applicationId) {
    restTemplate.exchange(applicationProperties.getExcavationAnnouncementPeriodsUrl(), HttpMethod.PUT,
        null, Void.class, applicationId);
  }
}
