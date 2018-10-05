package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.MultipartRequestBuilder;
import fi.hel.allu.pdf.domain.DecisionJson;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.mapper.DecisionJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

@Service
public class ApprovalDocumentService {

  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;
  private final DecisionJsonMapper decisionJsonMapper;
  private final ApplicationServiceComposer applicationServiceComposer;
  private final UserService userService;

  @Autowired
  public ApprovalDocumentService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      DecisionJsonMapper decisionJsonMapper,
      ApplicationServiceComposer applicationServiceComposer,
      UserService userService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.decisionJsonMapper = decisionJsonMapper;
    this.applicationServiceComposer = applicationServiceComposer;
    this.userService = userService;
  }

  public byte[] getApprovalDocument(Integer applicationId, ApprovalDocumentType type) {
    final ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
    if ((type == ApprovalDocumentType.OPERATIONAL_CONDITION &&
        (application.getStatus().ordinal() >= StatusType.OPERATIONAL_CONDITION.ordinal() ||
        (application.getStatus()== StatusType.DECISIONMAKING && application.getTargetState() == StatusType.FINISHED))) ||
        (type == ApprovalDocumentType.WORK_FINISHED && application.getStatus().ordinal() >= StatusType.FINISHED.ordinal())) {
      try {
        return restTemplate.getForObject(applicationProperties.getApprovalDocumentUrl(), byte[].class, applicationId, type);
      } catch (NoSuchElementException e) {
        return generateApprovalDocumentPreview(application, type);
      }
    } else {
      return generateApprovalDocumentPreview(application, type);
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
    clearDeciderData(decisionJson);
    return restTemplate.postForObject(applicationProperties.getGeneratePdfUrl(),
        decisionJson, byte[].class, styleSheetName(application, type));
  }

  private void generateFinalApprovalDocument(ApplicationJson prevApplication, ApplicationJson application, ApprovalDocumentType type) {
    final DecisionJson decisionJson = decisionJsonMapper.mapDecisionJson(application, false);
    if (prevApplication.getStatus() != StatusType.DECISIONMAKING) {
      clearDeciderData(decisionJson);
    } else {
      setDeciderData(decisionJson);
    }
    final byte[] document = restTemplate.postForObject(applicationProperties.getGeneratePdfUrl(),
        decisionJson, byte[].class, styleSheetName(application, type));
    restTemplate.exchange(
      applicationProperties.getApprovalDocumentUrl(), HttpMethod.POST,
      MultipartRequestBuilder.buildByteArrayRequest("file", document), String.class, application.getId(), type);

  }
  private String styleSheetName(ApplicationJson application, ApprovalDocumentType documentType) {
    return application.getType() + "-" + documentType;
  }

  private void clearDeciderData(DecisionJson decision) {
    decision.setDeciderName(null);
    decision.setDeciderTitle(null);
    decision.setDecisionTimestamp(null);
  }

  private void setDeciderData(DecisionJson decision) {
    final UserJson user = userService.getCurrentUser();
    decision.setDeciderName(user.getRealName());
    decision.setDeciderTitle(user.getTitle());
  }
}
