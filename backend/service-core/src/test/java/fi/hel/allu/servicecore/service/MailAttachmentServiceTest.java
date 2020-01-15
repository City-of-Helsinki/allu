package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.mail.model.MailMessage;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.AttachmentInfoJson;
import fi.hel.allu.servicecore.domain.DecisionDocumentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MailAttachmentServiceTest {

  @Mock
  private DecisionService decisionService;
  @Mock
  private ContractService contractService;
  @Mock
  private ApprovalDocumentService approvalDocumentService;
  @Mock
  private AttachmentService attachmentService;
  @Mock
  private TerminationService terminationService;

  private MailAttachmentService mailAttachmentService;

  private final Integer APPLICATION_ID = 1;
  private final byte[] DECISION_DATA = "DECISION_DATA".getBytes();
  private final byte[] CONTRACT_DATA = "CONTRACT_DATA".getBytes();
  private final byte[] OPERATIONAL_CONDITION_DATA = "OPERATIONAL_CONDITION_DATA".getBytes();
  private final byte[] WORK_FINISHED_DATA = "WORK_FINISHED_DATA".getBytes();
  private final byte[] TERMINATION_DATA = "TERMINATION_DATA".getBytes();
  private final byte[] ATTACHMENT_DATA = "ATTACHMENT_DATA".getBytes();

  @Before
  public void setup() {
    mailAttachmentService = new MailAttachmentService(decisionService,
      contractService, approvalDocumentService, attachmentService, terminationService);

    when(decisionService.getDecision(eq(APPLICATION_ID))).thenReturn(DECISION_DATA);
    when(contractService.getContract(eq(APPLICATION_ID))).thenReturn(CONTRACT_DATA);
    when(approvalDocumentService.getFinalApprovalDocument(eq(APPLICATION_ID), eq(ApprovalDocumentType.OPERATIONAL_CONDITION)))
      .thenReturn(OPERATIONAL_CONDITION_DATA);
    when(approvalDocumentService.getFinalApprovalDocument(eq(APPLICATION_ID), eq(ApprovalDocumentType.WORK_FINISHED)))
      .thenReturn(WORK_FINISHED_DATA);
    when(terminationService.getFinalTermination(eq(APPLICATION_ID))).thenReturn(TERMINATION_DATA);
    when(attachmentService.getAttachmentData(anyInt())).thenReturn(ATTACHMENT_DATA);
  }

  @Test
  public void shouldAddContractDocumentForPlacementContract() {
    List<MailMessage.Attachment> attachments = mailAttachmentService.forApplication(
      applicationWithType(ApplicationType.PLACEMENT_CONTRACT),
      DecisionDocumentType.DECISION,
      "contract");

    verify(contractService, times(1)).getContract(eq(APPLICATION_ID));
    verify(attachmentService, never()).getAttachmentData(anyInt());
    assertEquals(1, attachments.size());
    assertEquals("contract", attachments.get(0).getFilename());
    assertEquals(CONTRACT_DATA, attachments.get(0).getBytes());
  }

  @Test
  public void shouldAddDecisionAttachmentsFromApplicationForPlacementContract() {
    ApplicationJson application = applicationWithType(ApplicationType.PLACEMENT_CONTRACT);
    application.setAttachmentList(Arrays.asList(
      createAttachmentInfo(1, true),
      createAttachmentInfo(2, true),
      createAttachmentInfo(3, false)
    ));

    List<MailMessage.Attachment> attachments = mailAttachmentService.forApplication(
      application,
      DecisionDocumentType.DECISION,
      "contract");

    verify(contractService, times(1)).getContract(eq(APPLICATION_ID));
    verify(attachmentService, times(2)).getAttachmentData(anyInt());
    assertEquals("Expected contract document and two additional attachments", 3, attachments.size());
  }

  @Test
  public void shouldAddDecisionDocumentWhenDocumentTypeIsDecision() {
    List<MailMessage.Attachment> attachments = mailAttachmentService.forApplication(
      applicationWithType(ApplicationType.AREA_RENTAL),
      DecisionDocumentType.DECISION,
      "decision");

    verify(decisionService, times(1)).getDecision(eq(APPLICATION_ID));
    verify(attachmentService, never()).getAttachmentData(anyInt());
    assertEquals(1, attachments.size());
    assertEquals("decision", attachments.get(0).getFilename());
    assertEquals(DECISION_DATA, attachments.get(0).getBytes());
  }

  @Test
  public void shouldAddDecisionAttachmentsWhenDocumentTypeIsDecision() {
    ApplicationJson application = applicationWithType(ApplicationType.AREA_RENTAL);
    application.setAttachmentList(Arrays.asList(
      createAttachmentInfo(1, true),
      createAttachmentInfo(2, false)
    ));

    List<MailMessage.Attachment> attachments = mailAttachmentService.forApplication(
      application,
      DecisionDocumentType.DECISION,
      "decision");

    verify(decisionService, times(1)).getDecision(eq(APPLICATION_ID));
    verify(attachmentService, times(1)).getAttachmentData(anyInt());
    assertEquals("Expected contract document and additional attachment", 2, attachments.size());
  }


  @Test
  public void shouldAddApprovalDocumentWhenDocumentTypeIsOperationalCondition() {
    List<MailMessage.Attachment> attachments = mailAttachmentService.forApplication(
      applicationWithType(ApplicationType.EXCAVATION_ANNOUNCEMENT),
      DecisionDocumentType.OPERATIONAL_CONDITION,
      "operational_condition");

    verify(approvalDocumentService, times(1))
      .getFinalApprovalDocument(eq(APPLICATION_ID), eq(ApprovalDocumentType.OPERATIONAL_CONDITION));
    verify(attachmentService, never()).getAttachmentData(anyInt());
    assertEquals(1, attachments.size());
    assertEquals("operational_condition", attachments.get(0).getFilename());
    assertEquals(OPERATIONAL_CONDITION_DATA, attachments.get(0).getBytes());
  }

  @Test
  public void shouldNotAddDecisionDocumentsWhenOperationalCondition() {
    ApplicationJson application = applicationWithType(ApplicationType.AREA_RENTAL);
    application.setAttachmentList(Arrays.asList(createAttachmentInfo(1, true)));

    List<MailMessage.Attachment> attachments = mailAttachmentService.forApplication(
      application,
      DecisionDocumentType.OPERATIONAL_CONDITION,
      "operational_condition");

    verify(attachmentService, never()).getAttachmentData(anyInt());
    assertEquals("Expected only operational condition document", 1, attachments.size());
  }

  @Test
  public void shouldAddApprovalDocumentWhenDocumentTypeIsWorkFinished() {
    List<MailMessage.Attachment> attachments = mailAttachmentService.forApplication(
      applicationWithType(ApplicationType.EXCAVATION_ANNOUNCEMENT),
      DecisionDocumentType.WORK_FINISHED,
      "work_finished");

    verify(approvalDocumentService, times(1))
      .getFinalApprovalDocument(eq(APPLICATION_ID), eq(ApprovalDocumentType.WORK_FINISHED));
    verify(attachmentService, never()).getAttachmentData(anyInt());
    assertEquals(1, attachments.size());
    assertEquals("work_finished", attachments.get(0).getFilename());
    assertEquals(WORK_FINISHED_DATA, attachments.get(0).getBytes());
  }

  @Test
  public void shouldNotAddDecisionDocumentsWhenWorkFinished() {
    ApplicationJson application = applicationWithType(ApplicationType.AREA_RENTAL);
    application.setAttachmentList(Arrays.asList(createAttachmentInfo(1, true)));

    List<MailMessage.Attachment> attachments = mailAttachmentService.forApplication(
      application,
      DecisionDocumentType.WORK_FINISHED,
      "work_finished");

    verify(attachmentService, never()).getAttachmentData(anyInt());
    assertEquals("Expected only work finished document", 1, attachments.size());
  }

  @Test
  public void shouldAddTerminationDocumentDocumentTypeIsTermination() {
    List<MailMessage.Attachment> attachments = mailAttachmentService.forApplication(
      applicationWithType(ApplicationType.PLACEMENT_CONTRACT),
      DecisionDocumentType.TERMINATION,
      "termination");

    verify(terminationService, times(1)).getFinalTermination(eq(APPLICATION_ID));
    verify(attachmentService, never()).getAttachmentData(anyInt());
    assertEquals(1, attachments.size());
    assertEquals("termination", attachments.get(0).getFilename());
    assertEquals(TERMINATION_DATA, attachments.get(0).getBytes());
  }

  @Test
  public void shouldNotAddDecisionDocumentsWhenTermination() {
    ApplicationJson application = applicationWithType(ApplicationType.PLACEMENT_CONTRACT);
    application.setAttachmentList(Arrays.asList(createAttachmentInfo(1, true)));

    List<MailMessage.Attachment> attachments = mailAttachmentService.forApplication(
      application,
      DecisionDocumentType.TERMINATION,
      "termination");

    verify(attachmentService, never()).getAttachmentData(anyInt());
    assertEquals("Expected only work termination document", 1, attachments.size());
  }

  @Test
  public void shouldAddDecisionDocumentForOtherApplications() {
    List<MailMessage.Attachment> attachments = mailAttachmentService.forApplication(
      applicationWithType(ApplicationType.EVENT),
      DecisionDocumentType.DECISION,
      "decision");

    verify(decisionService, times(1)).getDecision(eq(APPLICATION_ID));
    verify(attachmentService, never()).getAttachmentData(anyInt());
    assertEquals(1, attachments.size());
    assertEquals("decision", attachments.get(0).getFilename());
    assertEquals(DECISION_DATA, attachments.get(0).getBytes());
  }

  @Test
  public void shouldAddDecisionAttachmentsFromApplicationForOtherApplications() {
    ApplicationJson application = applicationWithType(ApplicationType.EVENT);
    application.setAttachmentList(Arrays.asList(
      createAttachmentInfo(1, true),
      createAttachmentInfo(2, false)
    ));

    List<MailMessage.Attachment> attachments = mailAttachmentService.forApplication(
      application,
      DecisionDocumentType.DECISION,
      "decision");

    verify(decisionService, times(1)).getDecision(eq(APPLICATION_ID));
    verify(attachmentService, times(1)).getAttachmentData(anyInt());
    assertEquals("Expected contract document and additional attachment", 2, attachments.size());
  }

  private ApplicationJson applicationWithType(ApplicationType type) {
    ApplicationJson application =  new ApplicationJson();
    application.setId(APPLICATION_ID);
    application.setType(type);
    application.setAttachmentList(Collections.emptyList());
    return application;
  }

  private AttachmentInfoJson createAttachmentInfo(Integer id, Boolean decisionAttachment) {
    AttachmentInfoJson attachmentInfo = new AttachmentInfoJson();
    attachmentInfo.setId(id);
    attachmentInfo.setDecisionAttachment(decisionAttachment);
    return attachmentInfo;
  }
}
