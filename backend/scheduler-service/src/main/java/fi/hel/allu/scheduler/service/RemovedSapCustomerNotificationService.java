package fi.hel.allu.scheduler.service;

import fi.hel.allu.common.util.ResourceUtil;
import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.model.domain.CustomerSapInfo;
import fi.hel.allu.scheduler.config.ApplicationProperties;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for sending notifications about SAP customers that have been removed (marked as inactive).
 * This service fetches the removed customers from a remote source, gathers a list
 * of notification recipients, and sends an email with the details of the removed customers.
 * After successful notification, customers are marked as notified.
 *
 * This service is responsible for:
 *
 *   - Fetching removed customer details using a REST API.
 *   - Retrieving the list of email recipients for notifications.
 *   - Composing and sending notification emails in both plain text and HTML formats.
 *   - Marking customers as notified after emails are sent.
 *
 * Notification recipient logic
 * Recipients are not hardcoded — they are read at runtime from the model-service
 * {@code configuration} database table via the endpoint
 * {@code GET /configurations/CUSTOMER_NOTIFICATION_RECEIVER_EMAIL}.
 *
 * The table stores generic system-wide settings as {@code Configuration} objects with the
 * following fields:
 *
 *   - {@code id}      – surrogate primary key (auto-generated)
 *   - {@code type}    – {@code ConfigurationType.EMAIL} for email-address entries
 *   - {@code key}     – {@code ConfigurationKey.CUSTOMER_NOTIFICATION_RECEIVER_EMAIL},
 *                         the logical name that identifies this group of settings
 *   - {@code value}   – the actual email address string (e.g. {@code "officer@hel.fi"})
 *   - {@code readonly}– whether the entry may be edited through the API
 *
 * Multiple rows may share the same {@code key}, so the system supports any number of
 * recipients. Each row holds exactly one email address in its {@code value} field.
 * Rows with a {@code null} or blank {@code value} are silently ignored when the
 * recipient list is assembled (see {@link #getNotificationRecipients()}).
 *
 * Recipients are managed (inserted / updated) via the Allu administration UI or
 * directly in the database; this service only reads them.
 *
 * Dependencies
 *
 *   - {@code RestTemplate}          – HTTP communication with model-service.
 *   - {@code ApplicationProperties} – Provides URLs and email subject configuration.
 *   - {@code AlluMailService}        – Sends the actual email.
 *   - {@code AuthenticationService} – Creates the Bearer-token header for model-service calls.
 *
 */
@Service
public class RemovedSapCustomerNotificationService {

  private static final Logger logger = LoggerFactory.getLogger(RemovedSapCustomerNotificationService.class);

  private static final String TEMPLATE_PATH = "/templates/removed-customers-notification";

  private final RestTemplate restTemplate;
  private final ApplicationProperties applicationProperties;
  private final AlluMailService alluMailService;
  private final AuthenticationService authenticationService;

  @Autowired
  public RemovedSapCustomerNotificationService(
    RestTemplate restTemplate,
    ApplicationProperties applicationProperties,
    AlluMailService alluMailService,
    AuthenticationService authenticationService
  ) {
    this.restTemplate = restTemplate;
    this.applicationProperties = applicationProperties;
    this.alluMailService = alluMailService;
    this.authenticationService = authenticationService;
  }

  public void sendRemovedSapCustomerNotifications() {
    logger.info("Removed SAP customer notification job started");

    List<String> recipients = getNotificationRecipients();
    if (recipients.isEmpty()) {
      logger.warn("No recipients configured for removed SAP customer notifications!");
      return;
    }

    List<CustomerSapInfo> customers = fetchRemovedCustomers();
    if (customers.isEmpty()) {
      logger.info("No removed SAP customers to notify about");
      return;
    }

    try {
      sendMail(customers, recipients);
      markCustomersNotified(customers);
      logger.info("Removed SAP customer notification job finished. {} SAP customers processed.", customers.size());
    } catch (Exception e) {
      logger.error("Removed SAP customer notification job failed. Customers NOT marked as notified.", e);
    }
  }

  private List<CustomerSapInfo> fetchRemovedCustomers() {
    CustomerSapInfo[] response = restTemplate.exchange(
      applicationProperties.getRemovedSapCustomersUrl(),
      HttpMethod.GET,
      new HttpEntity<>(authenticationService.createAuthenticationHeader()),
      CustomerSapInfo[].class
    ).getBody();

    return response != null ? Arrays.asList(response) : Collections.emptyList();
  }

  private List<String> getNotificationRecipients() {
    final List<Configuration> emails = restTemplate.exchange(
      applicationProperties.getCustomerNotificationReceiverEmailsUrl(),
      HttpMethod.GET,
      null,
      new ParameterizedTypeReference<List<Configuration>>() {}
    ).getBody();

    return Optional.ofNullable(emails).orElseGet(Collections::emptyList).stream()
      .filter(Objects::nonNull)
      .map(Configuration::getValue)
      .filter(Objects::nonNull)
      .filter(v -> !v.isBlank())
      .collect(Collectors.toList());
  }

  private void sendMail(List<CustomerSapInfo> customers, List<String> recipients) throws IOException {
    String subject = applicationProperties.getRemovedSapCustomersSubject();

    logger.debug("Building removed SAP customer notification email for {} customers", customers.size());

    // Muuttujat molempiin templateihin
    Map<String, String> varsTxt = new HashMap<>();
    Map<String, String> varsHtml = new HashMap<>();

    varsTxt.put("count", String.valueOf(customers.size()));
    varsHtml.put("count", String.valueOf(customers.size()));

    varsTxt.put("rows", buildRowsTxt(customers));
    varsHtml.put("rows", buildRowsHtml(customers));

    // TXT
    String txtTemplate = ResourceUtil.readClassPathResource(TEMPLATE_PATH + ".txt");
    String body = StringSubstitutor.replace(txtTemplate, varsTxt);

    // HTML
    String htmlTemplate = ResourceUtil.readClassPathResource(TEMPLATE_PATH + ".html");
    String htmlBody = StringSubstitutor.replace(htmlTemplate, varsHtml);

    logger.info("Sending removed SAP customer notification email to {} recipients", recipients.size());
    boolean success = alluMailService.sendEmail(recipients, subject, body, htmlBody, null);

    if (!success) {
      throw new IllegalStateException("Removed SAP customer notification email sending failed");
    }
  }

  private String buildRowsHtml(List<CustomerSapInfo> customers) {
    return customers.stream()
      .map(c -> "<tr><td>" + c.id() + "</td><td>" + c.sapCustomerNumber() + "</td></tr>")
      .collect(Collectors.joining());
  }

  private String buildRowsTxt(List<CustomerSapInfo> customers) {
    return customers.stream()
      .map(c -> c.id() + " | " + c.sapCustomerNumber())
      .collect(Collectors.joining("\n"));
  }

  private void markCustomersNotified(List<CustomerSapInfo> customers) {
    List<Integer> ids = customers.stream().map(CustomerSapInfo::id).collect(Collectors.toList());

    restTemplate.postForObject(
      applicationProperties.getMarkRemovedSapCustomersNotifiedUrl(),
      ids,
      Void.class);
  }
}
