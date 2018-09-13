package fi.hel.allu.mail.service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import fi.hel.allu.common.domain.MailSenderLog;
import fi.hel.allu.mail.model.MailMessage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Service for sending emails.
 */
public class MailService {

  private static final Logger logger = LoggerFactory.getLogger(MailService.class);

  private JavaMailSender mailSender;
  private MessageHelperMaker messageHelperMaker;

  /*
   * In order to mock the message helper in tests, a user-provided creator class
   * is needed:
   */
  static class MessageHelperMaker {
    public MimeMessageHelper createMimeMessageHelper(MimeMessage mimeMessage, boolean multipart, String encoding)
        throws MessagingException {
      return new AlluMimeMessageHelper(mimeMessage, multipart, encoding);
    }
  }

  MailService(JavaMailSender mailSender, MessageHelperMaker messageHelperMaker) {
    this.mailSender = mailSender;
    this.messageHelperMaker = messageHelperMaker;
  }

  public MailService(JavaMailSender mailSender) {
    this(mailSender, new MessageHelperMaker());
  }

  /**
   * Sends email.
   *
   * @param mailMessage Email to be sent.
   * @throws MessagingException in case email sending fails for some reason.
   */
  public MailSenderLog send(MailMessage mailMessage) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper mimeMessageHelper = messageHelperMaker.createMimeMessageHelper(mimeMessage, true, "UTF-8");
      mimeMessageHelper.setSubject(mailMessage.getSubject());
      mimeMessageHelper.setFrom(mailMessage.getFrom());
      mimeMessageHelper.setTo(mailMessage.getTo().toArray(new String[0]));
      if (mailMessage.getHtmlBody() == null) {
        mimeMessageHelper.setText(mailMessage.getBody());
      } else {
        mimeMessageHelper.setText(mailMessage.getBody(), mailMessage.getHtmlBody());
      }
      for (MailMessage.InlineResource inline : mailMessage.getInlineResources()) {
        mimeMessageHelper.addInline(inline.getContentId(), inline);
      }
      for (MailMessage.Attachment attachment : mailMessage.getAttachments()) {
        mimeMessageHelper.addAttachment(
            MimeUtility.encodeText(attachment.getFilename()),
            new ByteArrayResource(attachment.getBytes()),
            attachment.getMimeType());
      }
      mailSender.send(mimeMessage);
      return createMailSenderLog(mailMessage, false, null);
    } catch (UnsupportedEncodingException | MessagingException e) {
      logger.warn("Failed to send email", e);
      return createMailSenderLog(mailMessage, true, e.getMessage());
    }
  }

  public MailSenderLog createMailSenderLog(MailMessage mailMessage, boolean sentFailed, String errorMessage) {
    MailSenderLog log = new MailSenderLog();
    log.setSentTime(ZonedDateTime.now());
    log.setSentFailed(sentFailed);
    log.setSubject(mailMessage.getSubject());
    log.setReceivers(mailMessage.getTo().toArray(new String[mailMessage.getTo().size()]));
    return log;
  }

  /**
   * Sends email after transforming email body with FreeMarker using the given model object.
   *
   * @param mailMessage   Email to be sent.
   * @param model         Model consisting of the FreeMarker template parameters
   * @throws MessagingException in case email sending fails for some reason.
   */
  public MailSenderLog send(MailMessage mailMessage, Map<String, Object> model) {
    MailMessage mailMessageCopy = new MailMessage(mailMessage);
    Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    try {
      mailMessageCopy.setBody(expandTemplate(mailMessage.getBody(), model, cfg));
      if (mailMessage.getHtmlBody() != null) {
        mailMessageCopy.setHtmlBody(expandTemplate(mailMessage.getHtmlBody(), model, cfg));
      }
    } catch (IOException | TemplateException e) {
      // should never happen
      logger.warn("Failed to send email", e);
      return createMailSenderLog(mailMessage, true, e.getMessage());
    }
    return send(mailMessageCopy);
  }

  /*
   * Expand given FreeMaker template text given a model and configuration
   */
  private String expandTemplate(String templateText, Map<String, Object> model, Configuration cfg)
      throws IOException, TemplateException {
    Template template = new Template("noname", templateText, cfg);
    StringWriter stringWriter = new StringWriter();
    template.process(model, stringWriter);
    return stringWriter.toString();
  }
}
