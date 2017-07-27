package fi.hel.allu.servicecore.service;

import fi.hel.allu.mail.model.MailMessage;
import fi.hel.allu.mail.model.MailMessage.Attachment;
import fi.hel.allu.mail.service.MailService;
import fi.hel.allu.servicecore.config.ApplicationProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service for sending mail from allu
 */
@Service
public class AlluMailService {

  private ApplicationProperties applicationProperties;
  private DecisionService decisionService;
  private MailService mailService;
  private Pattern emailAcceptPattern = null;

  @Autowired
  public AlluMailService(ApplicationProperties applicationProperties, DecisionService decisionService,
      JavaMailSender javaMailSender) {
    this.applicationProperties = applicationProperties;
    this.decisionService = decisionService;
    mailService = new MailService(javaMailSender);
  }

  @PostConstruct
  public void setupEmailPattern() {
    if (!applicationProperties.getEmailAllowedAddresses().isEmpty()) {
      String regex = String.join("|",
          applicationProperties.getEmailAllowedAddresses().stream().map(s -> s.replace(".", "\\.").replace("*", ".*"))
              .map(s -> String.format("(%s)", s)).collect(Collectors.toList()));
      emailAcceptPattern = Pattern.compile(regex);
    }
  }

  /**
   * Send the decision as an email to given recipients
   *
   * @param applicationId
   * @param recipients list of e-mail addresses
   * @param subject The subject of the e-mail
   * @param decisionPdfName What name to give to the decision PDF attachment
   * @param body The body of the email
   * @param attachments attachments that should be added to the mail
   * @return
   */
  public void sendDecision(int applicationId, List<String> recipients, String subject, String decisionPdfName,
      String body, Stream<Attachment> attachments) {
    if (emailAcceptPattern != null) {
      List<String> forbidden = recipients.stream().filter(r -> emailAcceptPattern.matcher(r).matches() == false)
          .collect(Collectors.toList());
      if (forbidden.size() != 0) {
        throw new IllegalArgumentException("Forbidden recipient addresses: " + String.join(", ", forbidden));
      }
    }
    MailMessage message = new MailMessage();
    message.setBody(body);
    message.setSubject(subject);
    message.setTo(recipients);
    message.setFrom(applicationProperties.getEmailSenderAddress());

    message.setAttachments(
        Stream.concat(Stream.of(new Attachment(decisionPdfName, decisionService.getDecision(applicationId))),
            attachments).collect(Collectors.toList()));
    try {
      mailService.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

}
