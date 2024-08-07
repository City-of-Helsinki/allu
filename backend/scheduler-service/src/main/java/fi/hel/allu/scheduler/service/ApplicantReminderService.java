package fi.hel.allu.scheduler.service;


import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.ResourceUtil;
import fi.hel.allu.mail.model.MailMessage.InlineResource;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.scheduler.config.ApplicationProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ApplicantReminderService {

  private static final Logger logger = LoggerFactory.getLogger(ApplicantReminderService.class);

  /* Application types for which a reminder is sent: */
  private static final List<ApplicationType> APPLICATION_TYPES = Arrays.asList(ApplicationType.EXCAVATION_ANNOUNCEMENT,
      ApplicationType.AREA_RENTAL);
  /* Application statuses that we care of: */
  private static final List<StatusType> STATUS_TYPES = Arrays.asList(StatusType.DECISION, StatusType.OPERATIONAL_CONDITION);
  /* How many times before application's end should the notification be sent? */
  private static final int DAYS_BEFORE = 2;

  private static final String INLINE_LOGO_FILE = "logo-hki-color-fi.png";
  private static final String INLINE_LOGO_CID = "logoHkiColorFi";
  private static final String TEMPLATE_PATH = "/templates/";

  private static final String MAIL_SUBJECT = "Päätöksenne %s voimassaolo on päättymässä";
  private static final String MAIL_TEMPLATE = "/templates/applicant-reminder";

  private static final Predicate<Application> excludeApplicationsWithCustomerWorkFinished = (application) -> {
    ApplicationExtension extension = application.getExtension();
    if (WorkFinishedDates.class.isAssignableFrom(extension.getClass())) {
      return ((WorkFinishedDates) extension).getCustomerWorkFinished() == null;
    } else {
      return true;
    }
  };

  private final RestTemplate restTemplate;
  private final ApplicationProperties applicationProperties;
  private final AlluMailService alluMailService;

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
    List<Application> apps = getApplicationsNeedingReminder();
    if (apps.isEmpty()) {
      logger.debug("No applications about to end");
    } else {
      apps.forEach(a -> sendReminder(a));
      List<Integer> appIds = apps.stream().map(a -> a.getId()).collect(Collectors.toList());
      restTemplate.postForObject(applicationProperties.getMarkReminderSentUrl(), appIds, Void.class);
    }
  }

  private List<Application> getApplicationsNeedingReminder() {
    DeadlineCheckParams checkParams = new DeadlineCheckParams(APPLICATION_TYPES, STATUS_TYPES,
        ZonedDateTime.now(), ZonedDateTime.now().plusDays(DAYS_BEFORE));

    return Arrays.stream(
        restTemplate.postForObject(applicationProperties.getDeadlineCheckUrl(), checkParams, Application[].class))
        .filter(excludeApplicationsWithCustomerWorkFinished)
        .collect(Collectors.toList());
  }

  private void sendReminder(Application application) {
    List<String> recipients = application.getCustomersWithContacts().stream()
      .flatMap(cwc -> cwc.getContacts().stream())
      .map(c -> c.getEmail())
      .filter(e -> StringUtils.isNotBlank(e))
      .collect(Collectors.toList());
     if (!recipients.isEmpty()) {
       sendEmails(recipients, application);
     }
  }

  private void sendEmails(List<String> recipients, Application application) {
    final String subject = String.format(MAIL_SUBJECT, idAndAddress(application));
    try {
      final Map<String, String> mailVariables = mailVariables(application);
      final String mailTemplate = ResourceUtil.readClassPathResource(MAIL_TEMPLATE + ".txt");
      final String body = StringSubstitutor.replace(mailTemplate, mailVariables);
      final String htmlMailTemplate = ResourceUtil.readClassPathResource(MAIL_TEMPLATE + ".html");
      final String htmlBody = StringSubstitutor.replace(htmlMailTemplate, mailVariables);
      final List<InlineResource> inlineResources = inlineResources();
      alluMailService.sendEmail(recipients, subject, body, htmlBody, inlineResources);
    } catch (IOException e) {
      logger.error("Error reading mail template: " + e);
    }
  }

  private Map<String, String> mailVariables(Application application) {
    Map<String, String> result = new HashMap<>();
    result.put("idAndAddress", idAndAddress(application));
    result.put("endDate", application.getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    result.put("inlineImageName", "cid:" + INLINE_LOGO_CID);
    return result;
  }

  private String address(Application application) {
    final List<Location> locations = application.getLocations();
    if (locations != null && !locations.isEmpty()) {
      final Location location = locations.get(0);
      if (location.getPostalAddress() != null) {
        return location.getPostalAddress().getStreetAddress() != null ? location.getPostalAddress().getStreetAddress() : "";
      }
    }
    return null;
  }

  private String idAndAddress(Application application) {
    final String address = address(application);
    if (address != null) {
      return application.getApplicationId() + " " + address;
    } else {
      return application.getApplicationId();
    }
  }

  private List<InlineResource> inlineResources() {
    try {
      return Arrays.asList(new InlineResource(INLINE_LOGO_FILE, INLINE_LOGO_CID,
          ResourceUtil.readClassPathResourceAsBytes(TEMPLATE_PATH + INLINE_LOGO_FILE)));
    } catch (IOException e) {
      return Collections.emptyList();
    }
  }
}
