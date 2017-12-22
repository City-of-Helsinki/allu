package fi.hel.allu.servicecore.service;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.DistributionType;
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
import java.util.stream.Stream;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class MailComposerServiceSpec {

  @Mock
  private AttachmentService attachmentService;
  @Mock
  private AlluMailService alluMailService;

  private MailComposerService mailComposerService;

  {
    describe("Mail composer service", () -> {
      beforeEach(() -> {
        MockitoAnnotations.initMocks(this);
        mailComposerService = new MailComposerService(alluMailService, attachmentService);
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
          mailComposerService.sendDecision(mockApplication.get(), decisionDetailsJson);

          ArgumentCaptor<Stream> attachmentsCaptor = ArgumentCaptor.forClass(Stream.class);
          Mockito.verify(alluMailService).sendDecision(Mockito.eq(APPLICATION_ID), Mockito.anyListOf(String.class),
              Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), attachmentsCaptor.capture());
          assertEquals(2, attachmentsCaptor.getValue().count());
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
