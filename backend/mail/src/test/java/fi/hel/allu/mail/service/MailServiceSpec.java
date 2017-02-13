package fi.hel.allu.mail.service;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.mail.model.MailMessage;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.function.Supplier;

import static com.greghaskins.spectrum.Spectrum.*;

@RunWith(Spectrum.class)
public class MailServiceSpec {

  private JavaMailSenderImpl mailSender;
  private MailService mailService;
  private MailMessage mailMessage;

  static String getEmailBody(MimeMessage msg) throws Exception {
    MimeMultipart content = (MimeMultipart) msg.getContent();
    MimeBodyPart bodyPart = (MimeBodyPart) content.getBodyPart(0);
    MimeMultipart content2 = (MimeMultipart) bodyPart.getContent();
    String body = (String) content2.getBodyPart(0).getContent();
    return body;
  }

  static byte[] getFirstAttachment(MimeMessage msg) throws Exception {
    MimeMultipart content = (MimeMultipart) msg.getContent();
    MimeBodyPart bodyPart = (MimeBodyPart) content.getBodyPart(1);
    ByteArrayInputStream bais = (ByteArrayInputStream) bodyPart.getContent();
    byte[] attachment = new byte[1024];
    int byteCount = bais.read(attachment, 0, 1024);
    return Arrays.copyOf(attachment, byteCount);
  }

  {
    describe("MailService using mocks", () -> {
      final ArgumentCaptor<MimeMessage> msgCaptor = ArgumentCaptor.forClass(MimeMessage.class);
      beforeEach(() -> {
        mailSender = Mockito.mock(JavaMailSenderImpl.class);
        MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(Mockito.mock(Properties.class)));
        Mockito.when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        mailService = new MailService(mailSender);
        mailMessage = new MailMessage();
        mailMessage.setSubject("Test subject");
        mailMessage.setFrom("test@from.fi");
        mailMessage.setTo(Collections.singletonList("test@to.fi"));
        mailMessage.setBody("Test body");
      });
      describe("Plain text body", () -> {
        beforeEach(() -> {
          mailService.send(mailMessage);
          Mockito.verify(mailSender).send(msgCaptor.capture());
        });
        final Supplier<MimeMessage> capturedMimeMessage = let(() -> {
          return msgCaptor.getValue();
        });
        it("should populate subject correctly", () -> {
          MimeMessage mimeMessage = capturedMimeMessage.get();
          Assert.assertEquals("Test subject", mimeMessage.getSubject());
        });
        it("should populate from correctly", () -> {
          MimeMessage mimeMessage = capturedMimeMessage.get();
          Assert.assertEquals("test@from.fi", mimeMessage.getFrom()[0].toString());
        });
        it("should populate to correctly", () -> {
          MimeMessage mimeMessage = capturedMimeMessage.get();
          Assert.assertEquals(1, mimeMessage.getRecipients(Message.RecipientType.TO).length);
          Assert.assertEquals("test@to.fi", mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString());
        });
        it("should populate body correctly", () -> {
          MimeMessage mimeMessage = capturedMimeMessage.get();
          Assert.assertEquals("Test body", getEmailBody(mimeMessage));
        });
      });
      describe("FreeMarker template in the body", () -> {
        it("should populate FreeMarker template with values", () -> {
          mailMessage.setBody("This value comes from model: ${testkey}");
          Map<String, Object> model = Collections.singletonMap("testkey", "testvalue");
          mailService.send(mailMessage, model);
          Mockito.verify(mailSender).send(msgCaptor.capture());
          String body = getEmailBody(msgCaptor.getValue());
          Assert.assertEquals("This value comes from model: testvalue", body);
        });
        it("should work with missing FreeMarker template i.e. with plain text", () -> {
          mailMessage.setBody("No template");
          mailService.send(mailMessage, Collections.emptyMap());
          Mockito.verify(mailSender).send(msgCaptor.capture());
          String body = getEmailBody(msgCaptor.getValue());
          Assert.assertEquals("No template", body);
        });
      });
      describe("Attachments in email", () -> {
        it("should handle attachments correctly", () -> {
          mailMessage.setAttachments(Collections.singletonList(new MailMessage.Attachment("somename", "somebytes".getBytes())));
          mailService.send(mailMessage);
          Mockito.verify(mailSender).send(msgCaptor.capture());
          MimeMessage mimeMessage = msgCaptor.getValue();
          Assert.assertEquals("somebytes", new String(getFirstAttachment(mimeMessage), "UTF-8"));
        });
      });
    });

    xdescribe("MailService using gmail", () -> {
      beforeAll(() -> {
        mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("alluprojekti@gmail.com");
        mailSender.setPassword("insertpasswordhere");

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.starttls.enable", "true");
        javaMailProperties.put("mail.smtp.auth", "true");
        javaMailProperties.put("mail.transport.protocol", "smtp");
        // javaMailProperties.put("mail.debug", "true"); //Prints out everything on screen
        mailSender.setJavaMailProperties(javaMailProperties);
      });
      beforeEach(() -> {
        mailService = new MailService(mailSender);
        mailMessage = new MailMessage();
        mailMessage.setSubject("Test email from allu");
        mailMessage.setFrom("alluprojekti@gmail.com");
        mailMessage.setTo(Collections.singletonList("alluprojekti@gmail.com"));
        mailMessage.setBody("leipäteksti");
      });
      it("should send email successfully", () -> {
        mailService.send(mailMessage);
      });
      it("should send email with attachment", () -> {
        mailMessage.setAttachments(Collections.singletonList(
            new MailMessage.Attachment("liitetiedost.txt", "liitetiedoston sisältö".getBytes())));
        mailService.send(mailMessage);
      });
      it("should send email to multiple recipients", () -> {
        mailMessage.setTo(Arrays.asList("alluprojekti+1@vincit.fi", "alluprojekti+2@vincit.fi"));
        mailService.send(mailMessage);
      });
    });
  }
}
