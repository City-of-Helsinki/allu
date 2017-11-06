package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.util.ResourceUtil;
import fi.hel.allu.mail.model.MailMessage.Attachment;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.DecisionDetailsJson;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
  private static final String TEMPLATE_GENERIC = "yleinen.txt";

  private static final String TEMPLATE_CABLE_REPORT = "johtoselvitys.txt";

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
    Stream<Attachment> attachments = applicationJson.getAttachmentList().stream()
        .map(ai -> new Attachment(ai.getName(), attachmentService.getAttachmentData(ai.getId())));
    String messageBody = messageBodyFor(applicationJson, decisionDetailsJson.getMessageBody());
    alluMailService.sendDecision(applicationJson.getId(), emailRecipients, subject,
        String.format("%s.pdf", applicationJson.getApplicationId()), messageBody, attachments);
  }

  private String messageBodyFor(ApplicationJson applicationJson, String accompanyingMessage) {
    String templateFile = TEMPLATE_PATH + templateFor(applicationJson.getType());
    try {
      String mailTemplate = ResourceUtil.readClassPathResource(templateFile);
      return StrSubstitutor.replace(mailTemplate, mailVariables(applicationJson, accompanyingMessage));
    } catch (IOException e) {
      logger.error("Error reading mail template: " + e);
      return accompanyingMessage;
    }
  }

  private Map<String, String> mailVariables(ApplicationJson applicationJson, String accompanyingMessage) {
    Map<String, String> result = new HashMap<>();
    result.put("applicationId", applicationJson.getApplicationId());
    result.put("accompanyingMessage", accompanyingMessage);
    return result;
  }

  private String templateFor(ApplicationType type) {
    switch (type) {
      case CABLE_REPORT:
        return TEMPLATE_CABLE_REPORT;
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
