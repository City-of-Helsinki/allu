package fi.hel.allu.mail.service;

import fi.hel.allu.mail.model.MailMessage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * Service for sending emails.
 */
public class MailService {
  private JavaMailSender mailSender;

  public MailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  /**
   * Sends email.
   *
   * @param mailMessage Email to be sent.
   * @throws MessagingException in case email sending fails for some reason.
   */
  public void send(MailMessage mailMessage) throws MessagingException {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    mimeMessageHelper.setSubject(mailMessage.getSubject());
    mimeMessageHelper.setFrom(mailMessage.getFrom());
    mimeMessageHelper.setTo(mailMessage.getTo().toArray(new String[0]));
    mimeMessageHelper.setText(mailMessage.getBody());
    for (MailMessage.Attachment attachment : mailMessage.getAttachments()) {
      mimeMessageHelper.addAttachment(attachment.getFilename(), new ByteArrayResource(attachment.getBytes()));
    }

    mailSender.send(mimeMessage);
  }

  /**
   * Sends email after transforming email body with FreeMarker using the given model object.
   *
   * @param mailMessage   Email to be sent.
   * @param model         Model consisting of the FreeMarker template parameters
   * @throws MessagingException in case email sending fails for some reason.
   */
  public void send(MailMessage mailMessage, Map<String, Object> model) throws MessagingException {
    Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    try {
      Template template = new Template("noname", mailMessage.getBody(), cfg);
      StringWriter stringWriter = new StringWriter();
      template.process(model, stringWriter);
      MailMessage mailMessageCopy = new MailMessage(mailMessage);
      mailMessageCopy.setBody(stringWriter.toString());
      send(mailMessageCopy);
    } catch (IOException | TemplateException e) {
      // should never happen
      throw new RuntimeException(e);
    }

  }
}
