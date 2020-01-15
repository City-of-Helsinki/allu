package fi.hel.allu.servicecore.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.MailSenderLog;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.mail.model.MailMessage.Attachment;
import fi.hel.allu.servicecore.domain.*;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Spectrum.class)
public class MailComposerServiceSpec {

  @Mock
  private AlluMailService alluMailService;
  @Mock
  private AlluMailService.MailBuilder mailBuilder;
  @Mock
  private LogService logService;
  @Mock
  private ApplicationService applicationService;
  @Mock
  private MailAttachmentService mailAttachmentService;

  private MailComposerService mailComposerService;

  {
    describe("Mail composer service", () -> {
      beforeEach(() -> {
        MockitoAnnotations.initMocks(this);
        Mockito.when(mailBuilder.withAttachments(Mockito.anyList())).thenReturn(mailBuilder);
        Mockito.when(mailBuilder.withBody(Mockito.anyString())).thenReturn(mailBuilder);
        Mockito.when(mailBuilder.withAttachment(Mockito.any(Attachment.class))).thenReturn(mailBuilder);
        Mockito.when(mailBuilder.withHtmlBody(Mockito.nullable(String.class))).thenReturn(mailBuilder);
        Mockito.when(mailBuilder.withInlineResources(Mockito.anyList())).thenReturn(mailBuilder);
        Mockito.when(mailBuilder.withSubject(Mockito.anyString())).thenReturn(mailBuilder);
        Mockito.when(mailBuilder.withModel(Mockito.anyMap())).thenReturn(mailBuilder);
        Mockito.when(alluMailService.newMailTo(Mockito.anyList())).thenReturn(mailBuilder);
        Mockito.when(mailBuilder.send()).thenReturn(new MailSenderLog());
        mailComposerService = new MailComposerService(alluMailService, mailAttachmentService, logService, applicationService);
      });
      describe("Create decision e-mail", () -> {
        final int APPLICATION_ID = 911;
        final UserJson handler = user(1, "userName", "realName");

        final Supplier<ApplicationJson> mockApplication = let(() -> Mockito.mock(ApplicationJson.class));
        final List<DistributionEntryJson> distribution = Collections
            .singletonList(emailDistribution("Pekka Pekanpekka", "pekkapekanpekka@pekka.org"));
        final List<Attachment> attachments = Arrays.asList(
            new Attachment("first", MediaType.APPLICATION_PDF_VALUE, null),
            new Attachment("second", MediaType.APPLICATION_PDF_VALUE, null),
            new Attachment("third", MediaType.IMAGE_PNG_VALUE, null)
        );

        beforeEach(() -> {
          Mockito.when(mockApplication.get().getId()).thenReturn(APPLICATION_ID);
          Mockito.when(mockApplication.get().getApplicationId()).thenReturn("HK_BLEU");
          Mockito.when(mockApplication.get().getType()).thenReturn(ApplicationType.NOTE);
          Mockito.when(mockApplication.get().getDecisionDistributionList()).thenReturn(distribution);
          Mockito.when(mockApplication.get().getHandler()).thenReturn(handler);
          Mockito.when(mailAttachmentService.forApplication(
              Mockito.any(ApplicationJson.class),
              Mockito.any(DecisionDocumentType.class),
              Mockito.anyString())
          ).thenReturn(attachments);
        });

        it("Sends e-mail with two decision attachments", () -> {
          DecisionDetailsJson decisionDetailsJson = new DecisionDetailsJson();
          decisionDetailsJson.setDecisionDistributionList(distribution);
          decisionDetailsJson.setMessageBody("MessageBody");
          mailComposerService.sendDecision(mockApplication.get(), decisionDetailsJson, DecisionDocumentType.DECISION);

          Mockito.verify(alluMailService).newMailTo(Mockito.anyList());
          Mockito.verify(mailBuilder).withSubject(Mockito.anyString());
          Mockito.verify(mailBuilder).withAttachments(Mockito.anyList());
          Mockito.verify(mailBuilder).withBody(Mockito.anyString());

          ArgumentCaptor<List> attachmentsCaptor = ArgumentCaptor.forClass(List.class);

          Mockito.verify(mailBuilder).withAttachments(attachmentsCaptor.capture());
          Mockito.verify(mailBuilder).send();

          assertEquals(attachments.size(), attachmentsCaptor.getValue().size());

          ArgumentCaptor<Map> modelCapture = ArgumentCaptor.forClass(Map.class);
          Mockito.verify(mailBuilder).withModel(modelCapture.capture());
          assertEquals(modelCapture.getValue().get("handlerName"), handler.getRealName());
        });

        it("Ignores handler for operational condition", () -> {
          DecisionDetailsJson decisionDetailsJson = new DecisionDetailsJson();
          decisionDetailsJson.setDecisionDistributionList(distribution);
          decisionDetailsJson.setMessageBody("MessageBody");
          mailComposerService.sendDecision(mockApplication.get(), decisionDetailsJson, DecisionDocumentType.OPERATIONAL_CONDITION);

          ArgumentCaptor<Map> modelCapture = ArgumentCaptor.forClass(Map.class);
          Mockito.verify(mailBuilder).withModel(modelCapture.capture());
          assertNull(modelCapture.getValue().get("handlerName"));
        });

        it("Ignores handler for work finished", () -> {
          DecisionDetailsJson decisionDetailsJson = new DecisionDetailsJson();
          decisionDetailsJson.setDecisionDistributionList(distribution);
          decisionDetailsJson.setMessageBody("MessageBody");
          mailComposerService.sendDecision(mockApplication.get(), decisionDetailsJson, DecisionDocumentType.WORK_FINISHED);

          ArgumentCaptor<Map> modelCapture = ArgumentCaptor.forClass(Map.class);
          Mockito.verify(mailBuilder).withModel(modelCapture.capture());
          assertNull(modelCapture.getValue().get("handlerName"));
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

  private UserJson user(Integer id, String userName, String realName) {
    UserJson user = new UserJson();
    user.setId(id);
    user.setUserName(userName);
    user.setRealName(realName);
    return user;
  }
}
