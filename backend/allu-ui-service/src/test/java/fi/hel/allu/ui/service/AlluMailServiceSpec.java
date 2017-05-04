package fi.hel.allu.ui.service;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.mail.model.MailMessage.Attachment;
import fi.hel.allu.ui.config.ApplicationProperties;

import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Spectrum.class)
public class AlluMailServiceSpec {

  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private DecisionService decisionService;
  @Mock
  private JavaMailSender javaMailSender;

  private AlluMailService alluMailService;

  {
    describe("Decision service", () -> {
      beforeEach(() -> {
        MockitoAnnotations.initMocks(this);
        alluMailService = new AlluMailService(applicationProperties, decisionService, javaMailSender);
      });

      final Supplier<MimeMessage> mockMimeMessage = let(()->Mockito.mock(MimeMessage.class));

      describe("Send email", () -> {
        beforeEach(() -> {
          Mockito.when(applicationProperties.getEmailAllowedAddresses())
              .thenReturn(Arrays.asList("*@jucca.org", "postmasher@masher.xx"));
          alluMailService.setupEmailPattern();
          Mockito.when(applicationProperties.getEmailSenderAddress())
              .thenReturn("Allu Aluevaraus <noreply@allu.invalid>");
          Mockito.when(javaMailSender.createMimeMessage()).thenReturn(mockMimeMessage.get());
          Mockito.when(decisionService.getDecision(Mockito.anyInt()))
              .thenReturn("BODY".getBytes());
        });

        it("Should fail with forbidden email", () -> {
          try {
            alluMailService.sendDecision(123, Arrays.asList("yucca@jucca.org", "potsmasher@masher.xx"), "Cheep Cialis",
                "Body", Stream.empty());
            fail("Did not throw!");
          } catch (IllegalArgumentException e) {
            // this was expected
          } catch (Exception e) {
            fail("Threw wrong exception " + e.toString());
          }
        });

        it("Should accept allowed emails", () -> {
          alluMailService.sendDecision(123, Arrays.asList("yucca@jucca.org", "postmasher@masher.xx"), "Cheep Cialis",
              "Body", Stream.empty());
          Mockito.verify(javaMailSender).send(Mockito.any(MimeMessage.class));
        });

        it("Should add all attachments", () -> {
          alluMailService.sendDecision(123, Arrays.asList("yucca@jucca.org", "postmasher@masher.xx"),
              "iPhone 5 only $1!!", "BUY NOW!",
              Stream.of(new Attachment("eka", "EKA".getBytes()), new Attachment("toka", "TOKA".getBytes())));
          ArgumentCaptor<Multipart> contentCaptor = ArgumentCaptor.forClass(Multipart.class);
          Mockito.verify(mockMimeMessage.get()).setContent(contentCaptor.capture());
          // body, decision, and two attachments -> four parts:
          assertEquals(4, contentCaptor.getValue().getCount());
          assertEquals("eka", contentCaptor.getValue().getBodyPart(2).getFileName());
          assertEquals("toka", contentCaptor.getValue().getBodyPart(3).getFileName());
        });
      });
    });
  }
}
