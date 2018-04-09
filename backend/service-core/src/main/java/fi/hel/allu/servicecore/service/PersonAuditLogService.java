package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.CustomerWithContacts;
import fi.hel.allu.model.domain.PersonAuditLogLog;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ContactJson;
import fi.hel.allu.servicecore.domain.CustomerJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;

/**
 * Writes a log entry to db when someone views customer (when type==PERSON) data or contact data.
 */
@Service
public class PersonAuditLogService {
  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;
  private final UserService userService;

  @Autowired
  public PersonAuditLogService(
        ApplicationProperties applicationProperties,
        RestTemplate restTemplate,
        UserService userService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.userService = userService;
  }

  public void log(Customer customer, String source) {
    if (customer.getType() == CustomerType.PERSON) {
      PersonAuditLogLog logEntry = new PersonAuditLogLog(customer.getId(), null, userService.getCurrentUser().getId(), source, ZonedDateTime.now());
      restTemplate.postForObject(applicationProperties.getPersonAuditLogUrl(), logEntry, PersonAuditLogLog.class);
    }
  }

  public void log(CustomerJson customer, String source) {
    if (customer.getType() == CustomerType.PERSON) {
      PersonAuditLogLog logEntry = new PersonAuditLogLog(customer.getId(), null, userService.getCurrentUser().getId(), source, ZonedDateTime.now());
      restTemplate.postForObject(applicationProperties.getPersonAuditLogUrl(), logEntry, PersonAuditLogLog.class);
    }
  }

  public void log(Contact contact, String source) {
    PersonAuditLogLog logEntry = new PersonAuditLogLog(null, contact.getId(), userService.getCurrentUser().getId(), source, ZonedDateTime.now());
    restTemplate.postForObject(applicationProperties.getPersonAuditLogUrl(), logEntry, PersonAuditLogLog.class);
  }

  public void log(ContactJson contact, String source) {
    PersonAuditLogLog logEntry = new PersonAuditLogLog(null, contact.getId(), userService.getCurrentUser().getId(), source, ZonedDateTime.now());
    restTemplate.postForObject(applicationProperties.getPersonAuditLogUrl(), logEntry, PersonAuditLogLog.class);
  }

  public void log(CustomerWithContacts cwc, String source) {
    if (cwc.getCustomer().getId() != null) {
      log(cwc.getCustomer(), source);
      cwc.getContacts().forEach(c -> log(c, source));
    }
  }
}
