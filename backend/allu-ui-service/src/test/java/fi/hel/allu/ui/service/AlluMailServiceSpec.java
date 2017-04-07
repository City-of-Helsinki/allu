package fi.hel.allu.ui.service;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.ui.config.ApplicationProperties;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

import java.util.Arrays;

import static com.greghaskins.spectrum.Spectrum.beforeEach;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
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

      MimeMessage mockMimeMessage = Mockito.mock(MimeMessage.class);

      describe("Send email", () -> {
        beforeEach(() -> {
          Mockito.when(applicationProperties.getEmailAllowedAddresses())
              .thenReturn(Arrays.asList("*@jucca.org", "postmasher@masher.xx"));
          alluMailService.setupEmailPattern();
          Mockito.when(applicationProperties.getEmailSenderAddress())
              .thenReturn("Allu Aluevaraus <noreply@allu.invalid>");
          Mockito.when(javaMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
          Mockito.when(decisionService.getDecision(Mockito.anyInt()))
              .thenReturn("BODY".getBytes());
        });

        it("Should fail with forbidden email", () -> {
          try {
            alluMailService.sendDecision(123, Arrays.asList("yucca@jucca.org", "potsmasher@masher.xx"), "Cheep Cialis",
                "Body");
            fail("Did not throw!");
          } catch (IllegalArgumentException e) {
            // this was expected
          } catch (Exception e) {
            fail("Threw wrong exception " + e.toString());
          }
        });

        it("Should accept allowed emails", () -> {
          alluMailService.sendDecision(123, Arrays.asList("yucca@jucca.org", "postmasher@masher.xx"),
              "Cheep Cialis", "Body");
          Mockito.verify(javaMailSender).send(Mockito.any(MimeMessage.class));
        });
      });
    });
  }
}
