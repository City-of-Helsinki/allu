package fi.hel.allu.scheduler.service;

import fi.hel.allu.common.domain.MailSenderLog;
import fi.hel.allu.mail.model.MailMessage;
import fi.hel.allu.mail.service.MailService;
import fi.hel.allu.scheduler.config.ApplicationProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service for sending mail from allu
 */
@Service
public class AlluMailService {

  private static final Logger logger = LoggerFactory.getLogger(AlluMailService.class);

  private final ApplicationProperties applicationProperties;
  private final MailService mailService;
  private final LogService logService;
  private Pattern emailAcceptPattern = null;


  @Autowired
  public AlluMailService(ApplicationProperties applicationProperties,
      JavaMailSender javaMailSender, LogService logService) {
    this.applicationProperties = applicationProperties;
    this.logService = logService;
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
    MailSenderLog log;
    List<String> forbiddenAddresses = getForbiddenEmailAddresses(recipients);
    if (!forbiddenAddresses.isEmpty() ) {
      String errorMessage = "Forbidden recipient addresses: " + String.join(", ", forbiddenAddresses);
      logger.warn(errorMessage);
      log = new MailSenderLog(subject, ZonedDateTime.now(), recipients, true, errorMessage);
    } else {
      MailMessage message = new MailMessage();
      message.setBody(body);
      message.setSubject(subject);
      message.setTo(recipients);
      message.setFrom(applicationProperties.getEmailSenderAddress());
      log =  mailService.send(message);
    }
    logService.addMailSenderLog(log);
  }

  public List<String> getForbiddenEmailAddresses(List<String> recipients) {
    List<String> forbidden;
    if (emailAcceptPattern != null) {
      forbidden = recipients.stream().filter(r -> emailAcceptPattern.matcher(r).matches() == false)
          .collect(Collectors.toList());
      return forbidden;
    } else {
     forbidden = Collections.emptyList();
    }
    return forbidden;
  }

}
