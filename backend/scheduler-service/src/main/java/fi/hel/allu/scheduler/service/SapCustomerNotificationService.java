package fi.hel.allu.scheduler.service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.util.ResourceUtil;
import fi.hel.allu.scheduler.config.ApplicationProperties;

@Service
public class SapCustomerNotificationService {

  private static final Logger logger = LoggerFactory.getLogger(SapCustomerNotificationService.class);

  private RestTemplate restTemplate;
  private ApplicationProperties applicationProperties;
  private AlluMailService alluMailService;
  private AuthenticationService authenticationService;

  private static final String MAIL_TEMPLATE = "/templates/customer-notification-mail-template.txt";

  @Autowired()
  public SapCustomerNotificationService(RestTemplate restTemplate, ApplicationProperties applicationProperties,
      AlluMailService alluMailService, AuthenticationService authenticationService) {
    this.restTemplate = restTemplate;
    this.applicationProperties = applicationProperties;
    this.alluMailService = alluMailService;
    this.authenticationService = authenticationService;
  }

  /**
   * Sends notification emails about customers waiting SAP customer number
   */
  public void sendSapCustomerNotificationEmails() {
    if (!StringUtil.isBlank(applicationProperties.getCustomerNotificationReceiverEmail())) {
      Integer numberOfCustomersWaitingSapNumber = getNumberOfCustomersWaitingSapNumber();
      if (numberOfCustomersWaitingSapNumber != null && numberOfCustomersWaitingSapNumber.intValue() > 0) {
        sendMail(numberOfCustomersWaitingSapNumber);
      }
    }
  }

  private Integer getNumberOfCustomersWaitingSapNumber() {
    return restTemplate.exchange(applicationProperties.getNrOfInvoiceRecipientsWithoutSapNumberUrl(), HttpMethod.GET,
        new HttpEntity<String>(createAuthenticationHeader()), Integer.class).getBody();
  }

  private void sendMail(Integer numberOfCustomersWaitingSapNumber) {
    String subject = String.format(applicationProperties.getCustomerNotificationMailSubject());
    String mailTemplate = null;
    try {
      mailTemplate = ResourceUtil.readClassPathResource(MAIL_TEMPLATE);
      String body = StrSubstitutor.replace(mailTemplate, mailVariables(numberOfCustomersWaitingSapNumber));
      alluMailService.sendEmail(Collections.singletonList(applicationProperties.getCustomerNotificationReceiverEmail()), subject, body);
    } catch (IOException e) {
      logger.error("Error reading mail template: " + e);
    }
  }

  private Map<String, String> mailVariables(Integer nrOfCustomers) {
    Map<String, String> result = new HashMap<>();
    result.put("nrOfCustomers", nrOfCustomers.toString());
    result.put("customerOrderUrl",  applicationProperties.getCustomerDownloadUrl());
    return result;
  }

  private HttpHeaders createAuthenticationHeader() {
    authenticationService.requestToken();
    return new HttpHeaders() {{
      setContentType(MediaType.APPLICATION_JSON);
        set(AUTHORIZATION, "Bearer " + authenticationService.getBearerToken());
    }};
  }

}
