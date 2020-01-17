package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.MailSenderLog;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.mail.model.MailMessage;
import fi.hel.allu.mail.model.MailMessage.Attachment;
import fi.hel.allu.mail.model.MailMessage.InlineResource;
import fi.hel.allu.mail.service.MailService;
import fi.hel.allu.servicecore.config.ApplicationProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service for sending mail from allu
 */
@Service
public class AlluMailService {

  private final ApplicationProperties applicationProperties;
  private final MailService mailService;
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

  public class MailBuilder {
    private final MailMessage mailMessage = new MailMessage();
    private Map<String, Object> model = null;
    private List<Attachment> attachments = new ArrayList<>();

    MailBuilder(List<String> recipients) {
      if (emailAcceptPattern != null) {
        List<String> forbidden = recipients.stream().filter(r -> emailAcceptPattern.matcher(r).matches() == false)
            .collect(Collectors.toList());
        if (!forbidden.isEmpty()) {
          throw new IllegalArgumentException("Forbidden recipient addresses: " + String.join(", ", forbidden));
        }
      }
      mailMessage.setTo(recipients);
      mailMessage.setFrom(applicationProperties.getEmailSenderAddress());
    }

    public MailBuilder withBody(String body) {
      mailMessage.setBody(body);
      return this;
    }

    public MailBuilder withHtmlBody(String htmlBody) {
      mailMessage.setHtmlBody(htmlBody);
      return this;
    }

    public MailBuilder withSubject(String subject) {
      mailMessage.setSubject(subject);
      return this;
    }

    public MailBuilder withAttachment(Attachment attachment) {
      attachments.add(attachment);
      return this;
    }

    public MailBuilder withAttachments(List<Attachment> attachments) {
      this.attachments.addAll(attachments);
      return this;
    }

    public MailBuilder withInlineResources(List<InlineResource> inlineResources) {
      mailMessage.setInlineResources(inlineResources);
      return this;
    }

    public MailBuilder withModel(Map<String, Object> model) {
      this.model = model;
      return this;
    }

    public MailSenderLog send() {
      mailMessage.setAttachments(attachments);
      MailSenderLog log;
      if (model != null) {
        log = mailService.send(mailMessage, model);
      } else {
        log = mailService.send(mailMessage);
      }
      return log;
    }
  }

  public MailBuilder newMailTo(List<String> recipients) {
    return new MailBuilder(recipients);
  }
}
