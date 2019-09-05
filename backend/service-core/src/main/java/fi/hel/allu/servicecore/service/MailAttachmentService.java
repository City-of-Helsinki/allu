package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.mail.model.MailMessage.Attachment;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.DecisionDocumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for creating document attachments for Email
 */
@Service
public class MailAttachmentService {

  private final DecisionService decisionService;
  private final ContractService contractService;
  private final ApprovalDocumentService approvalDocumentService;
  private final AttachmentService attachmentService;

  @Autowired
  public MailAttachmentService(DecisionService decisionService,
                               ContractService contractService,
                               ApprovalDocumentService approvalDocumentService,
                               AttachmentService attachmentService) {
    this.decisionService = decisionService;
    this.contractService = contractService;
    this.approvalDocumentService = approvalDocumentService;
    this.attachmentService = attachmentService;
  }

  public List<Attachment> forApplication(ApplicationJson application, DecisionDocumentType type, String attachmentName) {
    List<Attachment> attachments = new ArrayList<>();
    switch (type) {
      case DECISION:
        attachments.add(decisionDocumentForApplication(application, attachmentName));
        attachments.addAll(attachments(application));
        break;
      case OPERATIONAL_CONDITION:
        attachments.add(forOperationalCondition(attachmentName, application.getId()));
        break;
      case WORK_FINISHED:
        attachments.add(forWorkFinished(attachmentName, application.getId()));
        break;
    }
    return attachments;
  }

  private Attachment decisionDocumentForApplication(ApplicationJson application, String attachmentName) {
    if (application.getType() == ApplicationType.PLACEMENT_CONTRACT) {
      return forContract(attachmentName, application.getId());
    } else {
      return forDecision(attachmentName, application.getId());
    }
  }

  private Attachment forDecision(String pdfName, Integer applicationId) {
    return new Attachment(pdfName, MediaType.APPLICATION_PDF_VALUE, decisionService.getDecision(applicationId));
  }

  private Attachment forContract(String pdfName, Integer applicationId) {
    return new Attachment(pdfName, MediaType.APPLICATION_PDF_VALUE, contractService.getContract(applicationId));
  }

  private Attachment forOperationalCondition(String pdfName, Integer applicationId) {
    return new Attachment(pdfName, MediaType.APPLICATION_PDF_VALUE,
        approvalDocumentService.getFinalApprovalDocument(applicationId, ApprovalDocumentType.OPERATIONAL_CONDITION));

  }

  private Attachment forWorkFinished(String pdfName, Integer applicationId) {
    return new Attachment(pdfName, MediaType.APPLICATION_PDF_VALUE,
        approvalDocumentService.getFinalApprovalDocument(applicationId, ApprovalDocumentType.WORK_FINISHED));
  }

  private List<Attachment> attachments(ApplicationJson application) {
    return application.getAttachmentList().stream()
        .filter(ai -> ai.isDecisionAttachment())
        .map(ai -> new Attachment(ai.getName(), ai.getMimeType(), attachmentService.getAttachmentData(ai.getId())))
        .collect(Collectors.toList());
  }
}
