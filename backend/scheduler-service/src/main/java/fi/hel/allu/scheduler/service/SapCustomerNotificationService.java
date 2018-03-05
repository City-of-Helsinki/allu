package fi.hel.allu.scheduler.service;

import fi.hel.allu.common.util.ResourceUtil;
import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.scheduler.config.ApplicationProperties;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SapCustomerNotificationService {

  private static final Logger logger = LoggerFactory.getLogger(SapCustomerNotificationService.class);

  private final RestTemplate restTemplate;
  private final ApplicationProperties applicationProperties;
  private final AlluMailService alluMailService;
  private final AuthenticationService authenticationService;

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
    final List<String> customerNotificationReceiverEmails = getCustomerNotificationReceiverEmails();
    if (!customerNotificationReceiverEmails.isEmpty()) {
      Integer numberOfCustomersWaitingSapNumber = getNumberOfCustomersWaitingSapNumber();
      if (numberOfCustomersWaitingSapNumber != null && numberOfCustomersWaitingSapNumber > 0) {
        sendMail(numberOfCustomersWaitingSapNumber, customerNotificationReceiverEmails);
      }
    }
  }

  private Integer getNumberOfCustomersWaitingSapNumber() {
    return restTemplate.exchange(applicationProperties.getNrOfInvoiceRecipientsWithoutSapNumberUrl(), HttpMethod.GET,
        new HttpEntity<>(authenticationService.createAuthenticationHeader()), Integer.class).getBody();
  }

  private void sendMail(Integer numberOfCustomersWaitingSapNumber, List<String> receiverEmails) {
    String subject = String.format(applicationProperties.getCustomerNotificationMailSubject());
    try {
      String mailTemplate = ResourceUtil.readClassPathResource(MAIL_TEMPLATE);
      String body = StrSubstitutor.replace(mailTemplate, mailVariables(numberOfCustomersWaitingSapNumber));
      alluMailService.sendEmail(receiverEmails, subject, body);
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

  private List<String> getCustomerNotificationReceiverEmails() {
    final List<Configuration> emails = restTemplate.exchange(
        applicationProperties.getCustomerNotificationReceiverEmailsUrl(),
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Configuration>>() {}).getBody();
    return emails.stream().map(c -> c.getValue()).collect(Collectors.toList());
  }
}
