package fi.hel.allu.servicecore.service;

import fi.hel.allu.mail.model.MailMessage;
import fi.hel.allu.mail.model.MailMessage.Attachment;
import fi.hel.allu.mail.model.MailMessage.InlineResource;
import fi.hel.allu.mail.service.MailService;
import fi.hel.allu.servicecore.config.ApplicationProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;

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

  public class MailBuilder {
    private final MailMessage mailMessage = new MailMessage();
    private Map<String, Object> model = null;
    private Attachment decisionAttachment = null;
    private List<Attachment> otherAttachments = Collections.emptyList();

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

    public MailBuilder withDecision(String decisionPdfName, int applicationId) {
      decisionAttachment = new Attachment(decisionPdfName, "application/pdf", decisionService.getDecision(applicationId));
      return this;
    }

    public MailBuilder withAttachments(List<Attachment> attachments) {
      otherAttachments = attachments;
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

    public void send() {
      List<Attachment> attachments = new ArrayList<>();
      if (decisionAttachment != null) {
        attachments.add(decisionAttachment);
      }
      if (otherAttachments != null) {
        attachments.addAll(otherAttachments);
      }
      mailMessage.setAttachments(attachments);

      try {
        if (model != null) {
          mailService.send(mailMessage, model);
        } else {
          mailService.send(mailMessage);
        }
      } catch (MessagingException e) {
        throw new RuntimeException(e);
      }

    }
  }

  public MailBuilder newMailTo(List<String> recipients) {
    return new MailBuilder(recipients);
  }
}
