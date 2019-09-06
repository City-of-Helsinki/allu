package fi.hel.allu.servicecore.service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import fi.hel.allu.common.domain.types.StatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import fi.hel.allu.common.domain.MailSenderLog;
import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.util.ResourceUtil;
import fi.hel.allu.mail.model.MailMessage.Attachment;
import fi.hel.allu.mail.model.MailMessage.InlineResource;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.service.AlluMailService.MailBuilder;

/**
 * Service that's responsible for creating the outgoing emails and passing them
 * to AlluMailService for sending
 */
@Service
public class MailComposerService {

  // E-mail subjects for various application types:
  private static final String SUBJECT_GENERIC = "Päätös %s";
  private static final String SUBJECT_PLACEMENT_CONTR = "Sijoitussopimus %s";
  private static final String SUBJECT_EXCAVATION_ANN = "Kaivuilmoitukseen liittyvä päätös %s";
  private static final String SUBJECT_EVENT = "Tapahtumapäätös %s";
  private static final String SUBJECT_CABLE_REPORT = "Johtoselvitys %s";
  private static final String SUBJECT_AREA_RENTAL = "Aluevuokraukseen liittyvä päätös %s";
  private static final String SUBJECT_TRAFFIC_ARRANGEMENT = "Liikennejärjestelypäätös %s";

  // E-mail templates for various application types:
  private static final String TEMPLATE_GENERIC = "GENERIC";
  private static final String TEMPLATE_CABLE_REPORT = "CABLE_REPORT";
  private static final String TEMPLATE_SHORT_TERM_RENTAL = "SHORT_TERM_RENTAL";
  private static final String TEMPLATE_EVENT = "EVENT";
  private static final String TEMPLATE_TEMPORARY_TRAFFIC_ARRANGEMENTS = "TEMPORARY_TRAFFIC_ARRANGEMENTS";
  private static final String TEMPLATE_PLACEMENT_CONTRACT = "PLACEMENT_CONTRACT";
  private static final String TEMPLATE_EXCAVATION_ANNOUNCEMENT = "EXCAVATION_ANNOUNCEMENT";
  private static final String TEMPLATE_AREA_RENTAL = "AREA_RENTAL";

  private static final String TEMPLATE_TERMINATION_SUFFIX = "-termination";

  private static final String DECISION_TYPE_TRAFFIC_ARRANGEMENT = "liikennejärjestelypäätös";
  private static final String DECISION_TYPE_AREA_RENTAL = "aluevuokrauspäätös";
  private static final String DECISION_TYPE_EXCAVATION_ANN = "kaivuilmoituspäätös";

  // File extensions:
  private static final String EXTENSION_TXT = ".txt";
  private static final String EXTENSION_HTML = ".html";

  // Inline image for logo in HTML:
  private static final String INLINE_LOGO_FILE = "logo-hki-color-fi.png";

  // Content-ID for the inline logo
  private static final String INLINE_LOGO_CID = "logoHkiColorFi";

  // Path to template files in resources:
  private static final String TEMPLATE_PATH = "/templates/";

  private static final List<ApplicationType> subjectAddressTypes = Arrays.asList(
      ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS,
      ApplicationType.AREA_RENTAL,
      ApplicationType.SHORT_TERM_RENTAL,
      ApplicationType.EXCAVATION_ANNOUNCEMENT,
      ApplicationType.EVENT,
      ApplicationType.PLACEMENT_CONTRACT);

  private static final List<ApplicationType> subjectNameTypes = Arrays.asList(
      ApplicationType.SHORT_TERM_RENTAL,
      ApplicationType.EVENT);

  private static final Logger logger = LoggerFactory.getLogger(MailComposerService.class);

  private final AlluMailService alluMailService;
  private final LogService logService;
  private final ApplicationService applicationService;
  private final MailAttachmentService mailAttachmentService;

  @Autowired
  public MailComposerService(AlluMailService alluMailService,
                             MailAttachmentService mailAttachmentService,
                             LogService logService, ApplicationService applicationService) {
    this.alluMailService = alluMailService;
    this.mailAttachmentService = mailAttachmentService;
    this.logService = logService;
    this.applicationService = applicationService;
  }

  public void sendDecision(ApplicationJson application, DecisionDetailsJson decisionDetailsJson, DecisionDocumentType type) {
    final String subject = subject(application, type);
    List<String> emailRecipients = decisionDetailsJson.getDecisionDistributionList().stream()
        .filter(entry -> entry.getEmail() != null).map(entry -> entry.getEmail()).collect(Collectors.toList());

    if (!emailRecipients.isEmpty()) {
      final List<InlineResource> inlineResources = inlineResources();

      MailSenderLog log;
      try {
        final String attachmentName = attachmentName(type, application.getApplicationId());
        final List<Attachment> attachments = mailAttachmentService.forApplication(application, type, attachmentName);
        final MailBuilder mailBuilder = alluMailService.newMailTo(emailRecipients)
          .withSubject(subject)
          .withBody(textBodyFor(application))
          .withHtmlBody(htmlBodyFor(application))
          .withInlineResources(inlineResources)
          .withModel(mailModel(application, decisionDetailsJson.getMessageBody(), decisionTypeFor(application.getType()), attachmentName))
          .withAttachments(attachments);

        log = mailBuilder.send();
      } catch (Exception e) {
        logger.warn("Failed to send message", e);
        log = new MailSenderLog(subject, ZonedDateTime.now(), emailRecipients, true, e.getMessage());
      }
      saveMailSenderLog(log);
      if (log.isSentFailed()) {
        handleDecisionEmailSentFailed(log, application.getId());
      }
    } else {
      logger.warn("No email recipients");
    }
  }

  private void handleDecisionEmailSentFailed(MailSenderLog log, Integer id) {
    applicationService.addTag(id, new ApplicationTagJson(null, ApplicationTagType.DECISION_NOT_SENT, ZonedDateTime.now()));
    throw new MailSendException("decision.email.failed");
  }

  private void saveMailSenderLog(MailSenderLog log) {
    logService.addMailSenderLog(log);
  }

  private List<InlineResource> inlineResources() {
    try {
      return Arrays.asList(new InlineResource(INLINE_LOGO_FILE, INLINE_LOGO_CID,
          ResourceUtil.readClassPathResourceAsBytes(TEMPLATE_PATH + INLINE_LOGO_FILE)));
    } catch (IOException e) {
      return Collections.emptyList();
    }
  }



  private String textBodyFor(ApplicationJson applicationJson) {
    String templateFile = TEMPLATE_PATH + templateFor(applicationJson.getType(), applicationJson.getStatus()) + EXTENSION_TXT;
    try {
      return ResourceUtil.readClassPathResource(templateFile);
    } catch (IOException e) {
      logger.error("Error reading mail template: " + e);
      return null;
    }
  }

  private String htmlBodyFor(ApplicationJson applicationJson) {
    String templateFile = TEMPLATE_PATH + templateFor(applicationJson.getType(), applicationJson.getStatus()) + EXTENSION_HTML;
    try {
      return ResourceUtil.readClassPathResource(templateFile);
    } catch (IOException e) {
      logger.info("Can't find HTML template: " + e);
      return null;
    }
  }

  private Map<String, Object> mailModel(ApplicationJson applicationJson, String accompanyingMessage, String decisionType, String attachmentName) {
    Map<String, Object> result = new HashMap<>();
    result.put("applicationId", applicationJson.getApplicationId());
    result.put("decisionType", decisionType);
    result.put("accompanyingMessage", accompanyingMessage);
    result.put("handlerName", Optional.ofNullable(applicationJson.getHandler()).map(h -> h.getRealName()).orElse(null));
    result.put("totalPrice", applicationJson.getCalculatedPrice());
    result.put("inlineImageName", "cid:" + INLINE_LOGO_CID);
    result.put("attachmentName", attachmentName);
    return result;
  }

  private String templateFor(ApplicationType type, StatusType status) {
    switch (type) {
      case CABLE_REPORT:
        return TEMPLATE_CABLE_REPORT;
      case SHORT_TERM_RENTAL:
        return templateForStatus(TEMPLATE_SHORT_TERM_RENTAL, status);
      case EVENT:
        return TEMPLATE_EVENT;
      case TEMPORARY_TRAFFIC_ARRANGEMENTS:
        return TEMPLATE_TEMPORARY_TRAFFIC_ARRANGEMENTS;
      case AREA_RENTAL:
        return TEMPLATE_AREA_RENTAL;
      case EXCAVATION_ANNOUNCEMENT:
        return TEMPLATE_EXCAVATION_ANNOUNCEMENT;
      case PLACEMENT_CONTRACT:
        return TEMPLATE_PLACEMENT_CONTRACT;
      default:
        return TEMPLATE_GENERIC;
    }
  }

  private String templateForStatus(String baseTemplate, StatusType status) {
    if (StatusType.TERMINATED == status) {
      return baseTemplate + TEMPLATE_TERMINATION_SUFFIX;
    } else {
      return baseTemplate;
    }
  }

  private String subject(ApplicationJson application, DecisionDocumentType type) {
    StringBuilder subject = new StringBuilder();
    subject.append(String.format(subjectFor(application.getType()), attachmentName(type, application.getApplicationId())));
    subject.append(getApplicationNameForSubject(application));
    subject.append(getAddressForSubject(application));
    return subject.toString();
  }

  private String subjectFor(ApplicationType type) {
    switch (type) {
      case AREA_RENTAL:
        return SUBJECT_AREA_RENTAL;
      case CABLE_REPORT:
        return SUBJECT_CABLE_REPORT;
      case EVENT:
        return SUBJECT_EVENT;
      case EXCAVATION_ANNOUNCEMENT:
        return SUBJECT_EXCAVATION_ANN;
      case PLACEMENT_CONTRACT:
        return SUBJECT_PLACEMENT_CONTR;
      case TEMPORARY_TRAFFIC_ARRANGEMENTS:
        return SUBJECT_TRAFFIC_ARRANGEMENT;
      default:
        return SUBJECT_GENERIC;
    }
  }

  private String decisionTypeFor(ApplicationType type) {
    switch (type) {
      case TEMPORARY_TRAFFIC_ARRANGEMENTS:
        return DECISION_TYPE_TRAFFIC_ARRANGEMENT;
      case AREA_RENTAL:
        return DECISION_TYPE_AREA_RENTAL;
      case EXCAVATION_ANNOUNCEMENT:
        return DECISION_TYPE_EXCAVATION_ANN;
      default:
        return "";
    }
  }

  private String getAddressForSubject(ApplicationJson application) {
    if (subjectAddressTypes.contains(application.getType())) {
      final String address = address(application);
      if (!StringUtils.isEmpty(address)) {
        return ", " + address;
      }
    }
    return "";
  }

  private String address(ApplicationJson application) {
    final List<LocationJson> locations = application.getLocations();
    if (locations == null || locations.isEmpty()) {
      return null;
    }
    final LocationJson location = locations.get(0);
    return location.getAddress();
  }

  private String getApplicationNameForSubject(ApplicationJson application) {
    if (subjectNameTypes.contains(application.getType())) {
      final String name = application.getName();
      if (!StringUtils.isEmpty(name)) {
        return ", " + name;
      }
    }
    return "";
  }

  private String attachmentName(DecisionDocumentType type, String applicationId) {
    switch (type) {
      case OPERATIONAL_CONDITION:
        return String.format("%s_toiminnallinen_kunto.pdf", applicationId);
      case WORK_FINISHED:
        return String.format("%s_valmis.pdf", applicationId);
      case TERMINATION:
        return String.format("%s_irtisanominen.pdf", applicationId);
      default:
        return String.format("%s.pdf", applicationId);
    }
  }
}
