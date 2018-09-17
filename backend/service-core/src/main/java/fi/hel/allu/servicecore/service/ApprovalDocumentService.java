package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.MultipartRequestBuilder;
import fi.hel.allu.pdf.domain.DecisionJson;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.mapper.DecisionJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApprovalDocumentService {

  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;
  private final DecisionJsonMapper decisionJsonMapper;
  private final ApplicationServiceComposer applicationServiceComposer;

  @Autowired
  public ApprovalDocumentService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      DecisionJsonMapper decisionJsonMapper,
      ApplicationServiceComposer applicationServiceComposer) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.decisionJsonMapper = decisionJsonMapper;
    this.applicationServiceComposer = applicationServiceComposer;
  }

  public byte[] getApprovalDocument(Integer applicationId, ApprovalDocumentType type) {
    final ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
    if (application.getStatus().isBeforeDecision()) {
      return generateApprovalDocumentPreview(application, type);
    } else {
      return restTemplate.getForObject(applicationProperties.getApprovalDocumentUrl(), byte[].class, applicationId, type);
    }
  }

  public void createFinalApprovalDocument(ApplicationJson prevApplication, ApplicationJson application) {
    if (application.getType() == ApplicationType.EXCAVATION_ANNOUNCEMENT) {
      if (application.getStatus()== StatusType.OPERATIONAL_CONDITION) {
        generateFinalApprovalDocument(prevApplication, application, ApprovalDocumentType.OPERATIONAL_CONDITION);
      } else if (application.getStatus() == StatusType.FINISHED) {
        generateFinalApprovalDocument(prevApplication, application, ApprovalDocumentType.WORK_FINISHED);
      }
    }
  }

  private byte[] generateApprovalDocumentPreview(ApplicationJson application, ApprovalDocumentType type) {
    final DecisionJson decisionJson = decisionJsonMapper.mapDecisionJson(application, true);
    return restTemplate.postForObject(applicationProperties.getGeneratePdfUrl(),
        decisionJson, byte[].class, styleSheetName(application, type));
  }

  private void generateFinalApprovalDocument(ApplicationJson prevApplication, ApplicationJson application, ApprovalDocumentType type) {
    final DecisionJson decisionJson = decisionJsonMapper.mapDecisionJson(application, false);
    setApprovalDocumentData(decisionJson, prevApplication, application);
    final byte[] document = restTemplate.postForObject(applicationProperties.getGeneratePdfUrl(),
        decisionJson, byte[].class, styleSheetName(application, type));
    restTemplate.exchange(
      applicationProperties.getApprovalDocumentUrl(), HttpMethod.POST,
      MultipartRequestBuilder.buildByteArrayRequest("file", document), String.class, application.getId(), type);

  }
  private String styleSheetName(ApplicationJson application, ApprovalDocumentType documentType) {
    return application.getType() + "-" + documentType;
  }

  private void setApprovalDocumentData(DecisionJson decision, ApplicationJson prevApplication, ApplicationJson application) {
    // Todo: Set approver, approving time etc. what is needed in Approval document (which is not yet defined).
  }
}
