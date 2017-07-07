package fi.hel.allu.scheduler.service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.ResourceUtil;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.DeadlineCheckParams;
import fi.hel.allu.scheduler.config.ApplicationProperties;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApplicantReminderService {

  private static final Logger logger = LoggerFactory.getLogger(ApplicantReminderService.class);

  /* Application types for which a reminder is sent: */
  private static final List<ApplicationType> APPLICATION_TYPES = Arrays.asList(ApplicationType.EXCAVATION_ANNOUNCEMENT,
      ApplicationType.AREA_RENTAL, ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS);
  /* Application statuses that we care of: */
  private static final List<StatusType> STATUS_TYPES = Arrays.asList(StatusType.DECISION);
  /* How many times before application's end should the notification be sent? */
  private static final int DAYS_BEFORE = 14;

  private static final String MAIL_SUBJECT = "Muistutus luvasta %s";
  private static final String MAIL_TEMPLATE = "/templates/mail-template.txt";
  private RestTemplate restTemplate;
  private ApplicationProperties applicationProperties;
  private AlluMailService alluMailService;

  @Autowired()
  public ApplicantReminderService(RestTemplate restTemplate, ApplicationProperties applicationProperties,
      AlluMailService alluMailService) {
    this.restTemplate = restTemplate;
    this.applicationProperties = applicationProperties;
    this.alluMailService = alluMailService;
  }

  /**
   * Send reminders about applications about to end to their applicants
   */
  public void sendReminders() {
    logger.info("ApplicantReminder: sending reminders");
    DeadlineCheckParams checkParams = new DeadlineCheckParams(APPLICATION_TYPES, STATUS_TYPES,
        ZonedDateTime.now(), ZonedDateTime.now().plusDays(DAYS_BEFORE));
    List<Application> apps = Arrays.asList(restTemplate
        .postForObject(applicationProperties.getDeadlineCheckUrl(), checkParams, Application[].class));
    if (apps.isEmpty()) {
      logger.debug("No applications about to end");
    } else {
      apps.forEach(a -> sendReminder(a));
      List<Integer> appIds = apps.stream().map(a -> a.getId()).collect(Collectors.toList());
      restTemplate.postForObject(applicationProperties.getMarkReminderSentUrl(), appIds, Void.class);
    }
  }

  private void sendReminder(Application application) {
    String email = application.getCustomersWithContacts().stream()
        .filter(cwc -> cwc.getRoleType() == CustomerRoleType.APPLICANT)
        .map(cwc -> cwc.getCustomer().getEmail()).findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No email found for customer in APPLICANT role!"));
    String subject = String.format(MAIL_SUBJECT, application.getApplicationId());
    String mailTemplate = null;
    try {
      mailTemplate = ResourceUtil.readClassPathResource(MAIL_TEMPLATE);
      String body = StrSubstitutor.replace(mailTemplate, mailVariables(application));
      alluMailService.sendEmail(Collections.singletonList(email), subject, body);
    } catch (IOException e) {
      logger.error("Error reading mail template: " + e);
    }
  }

  private Map<String, String> mailVariables(Application application) {
    Map<String, String> result = new HashMap<>();
    result.put("applicationId", application.getApplicationId());
    result.put("endDate", application.getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    return result;
  }
}
