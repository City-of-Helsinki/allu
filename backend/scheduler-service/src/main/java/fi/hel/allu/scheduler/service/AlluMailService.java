package fi.hel.allu.scheduler.service;

import fi.hel.allu.mail.model.MailMessage;
import fi.hel.allu.mail.service.MailService;
import fi.hel.allu.scheduler.config.ApplicationProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service for sending mail from allu
 */
@Service
public class AlluMailService {

  private ApplicationProperties applicationProperties;
  private MailService mailService;
  private Pattern emailAcceptPattern = null;

  @Autowired
  public AlluMailService(ApplicationProperties applicationProperties,
      JavaMailSender javaMailSender) {
    this.applicationProperties = applicationProperties;
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
   * Send email to given recipients
   *
   * @param recipients list of e-mail addresses
   * @param subjet the e-mails subject
   * @param body the e-mails body
   * @return
   */
  public void sendEmail(List<String> recipients, String subject, String body) {
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
    try {
      mailService.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

}
