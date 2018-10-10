package fi.hel.allu.servicecore.service;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.MailSenderLog;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.mail.model.MailMessage.Attachment;
import fi.hel.allu.mail.model.MailMessage.InlineResource;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.AttachmentInfoJson;
import fi.hel.allu.servicecore.domain.DecisionDetailsJson;
import fi.hel.allu.servicecore.domain.DistributionEntryJson;

import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import fi.hel.allu.servicecore.domain.DecisionDocumentType;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class MailComposerServiceSpec {

  @Mock
  private AttachmentService attachmentService;
  @Mock
  private AlluMailService alluMailService;
  @Mock
  private AlluMailService.MailBuilder mailBuilder;
  @Mock
  private LogService logService;
  @Mock
  private ApplicationService applicationService;

  private MailComposerService mailComposerService;

  {
    describe("Mail composer service", () -> {
      beforeEach(() -> {
        MockitoAnnotations.initMocks(this);
        Mockito.when(mailBuilder.withAttachments(Mockito.anyListOf(Attachment.class))).thenReturn(mailBuilder);
        Mockito.when(mailBuilder.withBody(Mockito.anyString())).thenReturn(mailBuilder);
        Mockito.when(mailBuilder.withDecision(Mockito.anyString(), Mockito.anyInt())).thenReturn(mailBuilder);
        Mockito.when(mailBuilder.withHtmlBody(Mockito.anyString())).thenReturn(mailBuilder);
        Mockito.when(mailBuilder.withInlineResources(Mockito.anyListOf(InlineResource.class))).thenReturn(mailBuilder);
        Mockito.when(mailBuilder.withSubject(Mockito.anyString())).thenReturn(mailBuilder);
        Mockito.when(mailBuilder.withModel(Mockito.anyMapOf(String.class, Object.class))).thenReturn(mailBuilder);
        Mockito.when(alluMailService.newMailTo(Mockito.anyListOf(String.class))).thenReturn(mailBuilder);
        Mockito.when(mailBuilder.send()).thenReturn(new MailSenderLog());
        mailComposerService = new MailComposerService(alluMailService, attachmentService, logService, applicationService);
      });
      describe("Create decision e-mail", () -> {
        final int APPLICATION_ID = 911;
        final int ATTACHMENT_ID_1 = 111;
        final int ATTACHMENT_ID_2 = 222;
        final int ATTACHMENT_ID_3 = 333;
        final Supplier<ApplicationJson> mockApplication = let(() -> Mockito.mock(ApplicationJson.class));
        final List<DistributionEntryJson> distribution = Collections
            .singletonList(emailDistribution("Pekka Pekanpekka", "pekkapekanpekka@pekka.org"));

        beforeEach(() -> {
          Mockito.when(mockApplication.get().getId()).thenReturn(APPLICATION_ID);
          Mockito.when(mockApplication.get().getApplicationId()).thenReturn("HK_BLEU");
          Mockito.when(mockApplication.get().getType()).thenReturn(ApplicationType.NOTE);
          Mockito.when(mockApplication.get().getDecisionDistributionList()).thenReturn(distribution);
          Mockito.when(attachmentService.getAttachmentData(Mockito.anyInt())).thenReturn("ATTACHMENTDATA".getBytes());
        });

        it("Sends e-mail with two decision attachments", () -> {
          Mockito.when(mockApplication.get().getAttachmentList())
              .thenReturn(Arrays.asList(
                  attachment("eka", ATTACHMENT_ID_1, true),
                  attachment("toka", ATTACHMENT_ID_2, true),
                  attachment("kolmas", ATTACHMENT_ID_3, false)));

          DecisionDetailsJson decisionDetailsJson = new DecisionDetailsJson();
          decisionDetailsJson.setDecisionDistributionList(distribution);
          decisionDetailsJson.setMessageBody("MessageBody");
          mailComposerService.sendDecision(mockApplication.get(), decisionDetailsJson, DecisionDocumentType.DECISION);

          Mockito.verify(alluMailService).newMailTo(Mockito.anyListOf(String.class));
          Mockito.verify(mailBuilder).withSubject(Mockito.anyString());
          Mockito.verify(mailBuilder).withDecision(Mockito.anyString(), Mockito.eq(APPLICATION_ID));
          Mockito.verify(mailBuilder).withBody(Mockito.anyString());

          @SuppressWarnings("deprecation")
          ArgumentCaptor<List<Attachment>> attachmentsCaptor = new ArgumentCaptor<>();

          Mockito.verify(mailBuilder).withAttachments(attachmentsCaptor.capture());
          Mockito.verify(mailBuilder).send();

          assertEquals(2, attachmentsCaptor.getValue().size());
        });
      });

    });
  }

  private DistributionEntryJson emailDistribution(String name, String email) {
    DistributionEntryJson distributionEntryJson = new DistributionEntryJson();
    distributionEntryJson.setDistributionType(DistributionType.EMAIL);
    distributionEntryJson.setName(name);
    distributionEntryJson.setEmail(email);
    return distributionEntryJson;
  }

  private AttachmentInfoJson attachment(String name, int id, boolean decisionAttachment) {
    AttachmentInfoJson attachmentInfoJson = new AttachmentInfoJson();
    attachmentInfoJson.setName(name);
    attachmentInfoJson.setId(id);
    attachmentInfoJson.setDecisionAttachment(decisionAttachment);
    return attachmentInfoJson;
  }
}
