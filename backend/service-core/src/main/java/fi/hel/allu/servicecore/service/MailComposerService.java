package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.mail.model.MailMessage.Attachment;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.DecisionDetailsJson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service that's responsible for creating the outgoing emails and passing them
 * to AlluMailService for sending
 */
@Service
public class MailComposerService {

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
    alluMailService.sendDecision(applicationJson.getId(), emailRecipients, subject,
        String.format("%s.pdf", applicationJson.getApplicationId()), decisionDetailsJson.getMessageBody(), attachments);
  }

  private String subjectFor(ApplicationType type) {
    switch (type) {
      case AREA_RENTAL:
        return "Aluevarauspäätös %s";
      case CABLE_REPORT:
        return "Johtoselvitys %s";
      case EVENT:
        return "Tapahtumapäätös %s";
      case EXCAVATION_ANNOUNCEMENT:
        return "Kaivuilmoitus %s";
      case PLACEMENT_CONTRACT:
        return "Sijoitussopimus %s";
      default:
        return "Päätös %s";
    }
  }

}
