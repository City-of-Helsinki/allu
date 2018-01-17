package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.util.ResourceUtil;
import fi.hel.allu.mail.model.MailMessage.Attachment;
import fi.hel.allu.mail.model.MailMessage.InlineResource;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.DecisionDetailsJson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service that's responsible for creating the outgoing emails and passing them
 * to AlluMailService for sending
 */
@Service
public class MailComposerService {

  // E-mail subjects for various application types:
  private static final String SUBJECT_GENERIC = "Päätös %s";

  private static final String SUBJECT_PLACEMENT_CONTR = "Sijoitussopimus %s";

  private static final String SUBJECT_EXCAVATION_ANN = "Kaivuilmoitus %s";

  private static final String SUBJECT_EVENT = "Tapahtumapäätös %s";

  private static final String SUBJECT_CABLE_REPORT = "Johtoselvitys %s";

  private static final String SUBJECT_AREA_RENTAL = "Aluevarauspäätös %s";

  // E-mail templates for various application types:
  private static final String TEMPLATE_GENERIC = "yleinen";

  private static final String TEMPLATE_CABLE_REPORT = "johtoselvitys";

  private static final String TEMPLATE_SHORT_TERM_RENTAL = "lyhytaikainen_vuokraus";

  private static final String TEMPLATE_EVENT = "tapahtuma";

  // File extensions:
  private static final String EXTENSION_TXT = ".txt";
  private static final String EXTENSION_HTML = ".html";

  // Inline image for logo in HTML:
  private static final String INLINE_LOGO_FILE = "logo-hki-color-fi.png";

  // Content-ID for the inline logo
  private static final String INLINE_LOGO_CID = "logoHkiColorFi";

  // Path to template files in resources:
  private static final String TEMPLATE_PATH = "/templates/";

  private static final Logger logger = LoggerFactory.getLogger(MailComposerService.class);

  private AlluMailService alluMailService;
  private AttachmentService attachmentService;

  @Autowired
  public MailComposerService(AlluMailService alluMailService, AttachmentService attachmentService) {
    this.alluMailService = alluMailService;
    this.attachmentService = attachmentService;
  }

  public void sendDecision(ApplicationJson applicationJson, DecisionDetailsJson decisionDetailsJson) {
    String subject = String.format(subjectFor(applicationJson.getType()), applicationJson.getApplicationId());
    List<String> emailRecipients = decisionDetailsJson.getDecisionDistributionList().stream()
        .filter(entry -> entry.getEmail() != null).map(entry -> entry.getEmail()).collect(Collectors.toList());

    if (!emailRecipients.isEmpty()) {
      List<Attachment> attachments = applicationJson.getAttachmentList().stream()
          .filter(ai -> ai.isDecisionAttachment())
          .map(ai -> new Attachment(ai.getName(), attachmentService.getAttachmentData(ai.getId())))
          .collect(Collectors.toList());

      List<InlineResource> inlineResources = inlineResources();

      alluMailService.newMailTo(emailRecipients)
        .withSubject(subject)
        .withDecision(String.format("%s.pdf", applicationJson.getApplicationId()), applicationJson.getId())
        .withBody(textBodyFor(applicationJson))
        .withHtmlBody(htmlBodyFor(applicationJson))
        .withAttachments(attachments)
        .withInlineResources(inlineResources)
        .withModel(mailModel(applicationJson, decisionDetailsJson.getMessageBody()))
        .send();
    } else {
      logger.warn("No email recipients");
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

  private String textBodyFor(ApplicationJson applicationJson) {
    String templateFile = TEMPLATE_PATH + templateFor(applicationJson.getType()) + EXTENSION_TXT;
    try {
      return ResourceUtil.readClassPathResource(templateFile);
    } catch (IOException e) {
      logger.error("Error reading mail template: " + e);
      return null;
    }
  }

  private String htmlBodyFor(ApplicationJson applicationJson) {
    String templateFile = TEMPLATE_PATH + templateFor(applicationJson.getType()) + EXTENSION_HTML;
    try {
      return ResourceUtil.readClassPathResource(templateFile);
    } catch (IOException e) {
      logger.info("Can't find HTML template: " + e);
      return null;
    }
  }

  private Map<String, Object> mailModel(ApplicationJson applicationJson, String accompanyingMessage) {
    Map<String, Object> result = new HashMap<>();
    result.put("applicationId", applicationJson.getApplicationId());
    result.put("accompanyingMessage", accompanyingMessage);
    result.put("handlerName", Optional.ofNullable(applicationJson.getHandler()).map(h -> h.getRealName()).orElse(null));
    result.put("totalPrice", applicationJson.getCalculatedPrice());
    result.put("inlineImageName", "cid:" + INLINE_LOGO_CID);
    return result;
  }

  private String templateFor(ApplicationType type) {
    switch (type) {
      case CABLE_REPORT:
        return TEMPLATE_CABLE_REPORT;
      case SHORT_TERM_RENTAL:
        return TEMPLATE_SHORT_TERM_RENTAL;
      case EVENT:
        return TEMPLATE_EVENT;
      default:
        return TEMPLATE_GENERIC;
    }
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
      default:
        return SUBJECT_GENERIC;
    }
  }

}
