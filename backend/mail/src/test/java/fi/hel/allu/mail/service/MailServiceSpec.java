package fi.hel.allu.mail.service;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.mail.model.MailMessage;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StreamUtils;

import javax.activation.FileTypeMap;

import java.nio.charset.Charset;
import java.util.*;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;

@RunWith(Spectrum.class)
public class MailServiceSpec {

  private JavaMailSenderImpl mailSender;
  private MailService mailService;
  private MailMessage mailMessage;

  private MailService.MessageHelperMaker mockMessageHelperMaker;
  private MimeMessageHelper mockMimeMessageHelper;

  {
    final String TEXT_BODY = "Test body";
    final String HTML_BODY = "<HTML><HEAD></HEAD><BODY>Boo</BODY></HTML>";

    context("MailService", () -> {

      context("With mocks", () -> {
        beforeEach(() -> {
          mailSender = Mockito.mock(JavaMailSenderImpl.class);
          mailMessage = new MailMessage();
          mailMessage.setSubject("Test subject");
          mailMessage.setFrom("test@from.fi");
          mailMessage.setTo(Collections.singletonList("test@to.fi"));
          mailMessage.setBody(TEXT_BODY);

          mockMimeMessageHelper = Mockito.mock(MimeMessageHelper.class);
          Mockito.when(mockMimeMessageHelper.getFileTypeMap()).thenReturn(FileTypeMap.getDefaultFileTypeMap());
          mockMessageHelperMaker = Mockito.mock(MailService.MessageHelperMaker.class);
          Mockito
              .when(mockMessageHelperMaker.createMimeMessageHelper(Mockito.any(), Mockito.anyBoolean(), Mockito.any()))
              .thenReturn(mockMimeMessageHelper);
          mailService = new MailService(mailSender, mockMessageHelperMaker);
        });

        describe("with HTML body", () -> {
          it("should set both HTML and text bodies correcly", () -> {
            mailMessage.setHtmlBody(HTML_BODY);
            mailService.send(mailMessage);
            ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> htmlCaptor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(mockMimeMessageHelper).setText(textCaptor.capture(), htmlCaptor.capture());
            Assert.assertEquals(TEXT_BODY, textCaptor.getValue());
            Assert.assertEquals(HTML_BODY, htmlCaptor.getValue());
          });
        });

        describe("with plain text body", () -> {
          final ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
          beforeEach(() -> {
            mailService.send(mailMessage);
          });
          it("should populate subject correctly", () -> {
            Mockito.verify(mockMimeMessageHelper).setSubject(stringCaptor.capture());
            Assert.assertEquals("Test subject", stringCaptor.getValue());
          });
          it("should populate from correctly", () -> {
            Mockito.verify(mockMimeMessageHelper).setFrom(stringCaptor.capture());
            Assert.assertEquals("test@from.fi", stringCaptor.getValue());
          });
          it("should populate to correctly", () -> {
            ArgumentCaptor<String[]> strArrayCaptor = ArgumentCaptor.forClass(String[].class);
            Mockito.verify(mockMimeMessageHelper).setTo(strArrayCaptor.capture());
            Assert.assertEquals(1, strArrayCaptor.getValue().length);
            Assert.assertEquals("test@to.fi", strArrayCaptor.getValue()[0]);
          });
          it("should populate body correctly", () -> {
            Mockito.verify(mockMimeMessageHelper).setText(stringCaptor.capture());
            Assert.assertEquals(TEXT_BODY, stringCaptor.getValue());
          });
        });
        describe("with FreeMarker template in the body", () -> {
          it("should populate FreeMarker template with values", () -> {
            mailMessage.setBody("This value comes from model: ${testkey}");
            Map<String, Object> model = Collections.singletonMap("testkey", "testvalue");
            mailService.send(mailMessage, model);
            ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(mockMimeMessageHelper).setText(stringCaptor.capture());
            String body = stringCaptor.getValue();
            Assert.assertEquals("This value comes from model: testvalue", body);
          });
          it("should work with missing FreeMarker template i.e. with plain text", () -> {
            mailMessage.setBody("No template");
            mailService.send(mailMessage, Collections.emptyMap());
            ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(mockMimeMessageHelper).setText(stringCaptor.capture());
            String body = stringCaptor.getValue();
            Assert.assertEquals("No template", body);
          });
          it("should also expand template in HTML body", () -> {
            final String TEXT_TEMPLATE = "Text template ${testKey}";
            final String HTML_TEMPLATE = "HTML template ${otherKey}";
            final String TEXT_EXPANDED = "Text template Expanded";
            final String HTML_EXPANDED = "HTML template Even More Expanded";
            final Map<String, Object> model = new HashMap<String, Object>() {
              {
                put("testKey", "Expanded");
                put("otherKey", "Even More Expanded");
              }
            };
            mailMessage.setBody(TEXT_TEMPLATE);
            mailMessage.setHtmlBody(HTML_TEMPLATE);
            mailService.send(mailMessage, model);
            ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> htmlCaptor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(mockMimeMessageHelper).setText(textCaptor.capture(), htmlCaptor.capture());
            Assert.assertEquals(TEXT_EXPANDED, textCaptor.getValue());
            Assert.assertEquals(HTML_EXPANDED, htmlCaptor.getValue());
          });
        });
        describe("with Attachments in email", () -> {
          it("should handle attachments correctly", () -> {
            mailMessage.setAttachments(
                Collections.singletonList(new MailMessage.Attachment("somename", "application/pdf", "somebytes".getBytes())));
            mailService.send(mailMessage);
            ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<InputStreamSource> issCaptor = ArgumentCaptor.forClass(InputStreamSource.class);
            ArgumentCaptor<String> mimeTypeCaptor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(mockMimeMessageHelper).addAttachment(stringCaptor.capture(), issCaptor.capture(), mimeTypeCaptor.capture());
            Assert.assertEquals("somename", stringCaptor.getValue());
            Assert.assertEquals("somebytes",
                StreamUtils.copyToString(issCaptor.getValue().getInputStream(), Charset.forName("UTF-8")));
            Assert.assertEquals("application/pdf", mimeTypeCaptor.getValue());
          });
        });
        describe("with inline resources in email", () -> {
          it("should handle inline resources correctly", () -> {
            mailMessage.setInlineResources(
                Collections
                    .singletonList(new MailMessage.InlineResource("somename.ext", "somename", "somebytes".getBytes())));
            mailService.send(mailMessage);
            ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<Resource> resourceCaptor = ArgumentCaptor.forClass(Resource.class);
            Mockito.verify(mockMimeMessageHelper).addInline(stringCaptor.capture(), resourceCaptor.capture());
            Assert.assertEquals("somename", stringCaptor.getValue());
            Assert.assertEquals("somebytes",
                StreamUtils.copyToString(resourceCaptor.getValue().getInputStream(), Charset.forName("UTF-8")));
          });
        });

      });

      xdescribe("Send to localhost", () -> {
        beforeAll(() -> {
          mailSender = new JavaMailSenderImpl();
          mailSender.setHost("localhost");
          mailSender.setPort(2525);
          mailSender.setUsername("alluprojekti@gmail.com");

          Properties javaMailProperties = new Properties();
          javaMailProperties.put("mail.transport.protocol", "smtp");
          // javaMailProperties.put("mail.debug", "true"); //Prints out
          // everything on screen
          mailSender.setJavaMailProperties(javaMailProperties);
        });

        sendTests();
      });

      xdescribe("Send using gmail", () -> {
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
          // javaMailProperties.put("mail.debug", "true"); //Prints out
          // everything on screen
          mailSender.setJavaMailProperties(javaMailProperties);
        });

        sendTests();
      });
    });
  }

  private void sendTests() {
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
      mailMessage.setAttachments(Collections
          .singletonList(new MailMessage.Attachment("liitetiedost.txt", "text/plain", "liitetiedoston sisältö".getBytes())));
      mailService.send(mailMessage);
    });
    it("should send HTML mail with inline resource", () -> {
      mailMessage.setHtmlBody("<HTML><HEAD></HEAD><BODY>Test email</BODY></HTML>");
      mailMessage.setInlineResources(Collections
          .singletonList(
              new MailMessage.InlineResource("liitetiedost.png", "liitetied", "liitetiedoston sisältö".getBytes())));
      mailService.send(mailMessage);
    });
    it("should send email to multiple recipients", () -> {
      mailMessage.setTo(Arrays.asList("alluprojekti+1@gofore.fi", "alluprojekti+2@gofore.fi"));
      mailService.send(mailMessage);
    });
  }
}
