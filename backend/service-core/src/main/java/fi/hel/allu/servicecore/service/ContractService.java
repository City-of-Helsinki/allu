package fi.hel.allu.servicecore.service;

import java.time.ZonedDateTime;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.domain.ContractInfo;
import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ContractStatusType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.util.MultipartRequestBuilder;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.ConfigurationKey;
import fi.hel.allu.pdf.domain.DecisionJson;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.mapper.DecisionJsonMapper;

@Service
public class ContractService {

  private final ApplicationServiceComposer applicationServiceComposer;
  private final DecisionJsonMapper decisionJsonMapper;
  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;
  private final UserService userService;
  private final ConfigurationService configurationService;
  private final CommentService commentService;

  private static final String STYLE_SHEET_NAME = "PLACEMENT_CONTRACT-contract";

  public ContractService(ApplicationServiceComposer applicationServiceComposer, DecisionJsonMapper decisionJsonMapper,
      ApplicationProperties applicationProperties, RestTemplate restTemplate, UserService userService,
      ConfigurationService configurationService, CommentService commentService) {
    this.applicationServiceComposer = applicationServiceComposer;
    this.decisionJsonMapper = decisionJsonMapper;
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.userService = userService;
    this.configurationService = configurationService;
    this.commentService = commentService;
  }

  public byte[] getContractPreview(Integer applicationId) {
    return generateContractPdf(applicationId, null, true);
  }

  public byte[] getContract(Integer applicationId) {
    return restTemplate.getForObject(applicationProperties.getContractUrl(), byte[].class, applicationId);
  }

  public byte[] getFinalContract(Integer applicationId) {
    return restTemplate.getForObject(applicationProperties.getFinalContractUrl(), byte[].class, applicationId);
  }

  public byte[] getContractProposal(Integer applicationId) {
    validateIsInProposalState(getContractInfo(applicationId));
    return restTemplate.getForObject(applicationProperties.getContractUrl(), byte[].class, applicationId);
  }

  public byte[] createContractProposal(Integer applicationId) {
    ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
    validateProposalCreationAllowed(application);
    byte[] pdfData = generateContractPdf(application, null, false);
    restTemplate.exchange(applicationProperties.getContractProposalUrl(), HttpMethod.POST,
        MultipartRequestBuilder.buildByteArrayRequest("data", pdfData), String.class, applicationId);
    applicationServiceComposer.changeStatus(applicationId, StatusType.WAITING_CONTRACT_APPROVAL);
    applicationServiceComposer.updateApplicationHandler(applicationId, userService.getCurrentUser().getId());
    return pdfData;
  }

  public byte[] createApprovedContract(int applicationId, ContractApprovalInfo contractApprovalInfo) {
    if (!contractApprovalInfo.isContractAsAttachment() && !contractApprovalInfo.isFrameAgreementExists()) {
      throw new IllegalArgumentException("contract.approved.notallowed");
    }
    ContractInfo contractInfo = new ContractInfo();
    contractInfo.setFrameAgreementExists(contractApprovalInfo.isFrameAgreementExists());
    contractInfo.setContractAsAttachment(contractApprovalInfo.isContractAsAttachment());

    ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
    validateProposalCreationAllowed(application);
    byte[] pdfData = generateContractPdf(application, contractInfo, false);
    if (StringUtils.isNotBlank(contractApprovalInfo.getComment())) {
      commentService.addDecisionProposalComment(applicationId, contractApprovalInfo);
    }
    restTemplate.exchange(applicationProperties.getApprovedContractUrl(), HttpMethod.POST,
        MultipartRequestBuilder.buildByteArrayRequest("data", pdfData, Collections.singletonMap("contractinfo", contractInfo)), String.class, applicationId);
    applicationServiceComposer.changeStatus(applicationId, StatusType.DECISIONMAKING, contractApprovalInfo);
    return pdfData;
  }

  public void approveContract(Integer applicationId, String signer, ZonedDateTime signingTime) {
    ContractInfo contractInfo = getContractInfo(applicationId);
    validateIsInProposalState(contractInfo);
    contractInfo.setStatus(ContractStatusType.APPROVED);
    contractInfo.setSigner(signer);
    contractInfo.setResponseTime(signingTime);
    byte[] data = generateContractPdf(applicationId, contractInfo, false);
    HttpEntity<?> requestEntity = MultipartRequestBuilder.buildByteArrayRequest("file", data, Collections.singletonMap("info", contractInfo));
    restTemplate.exchange(applicationProperties.getContractUrl(), HttpMethod.PUT, requestEntity, Void.class, applicationId);

    // After contract approval move application to waiting for decision state and remove possible contract rejected -tag
    applicationServiceComposer.changeStatus(applicationId, StatusType.DECISIONMAKING, getDecisionMakerInfo());
    applicationServiceComposer.removeTag(applicationId, ApplicationTagType.CONTRACT_REJECTED);
  }

  // Fetch placement contract decision maker from configuration
  private StatusChangeInfoJson getDecisionMakerInfo() {
    String decisionMakerUsername = configurationService.getSingleValue(ConfigurationKey.PLACEMENT_CONTRACT_DECISION_MAKER);
    if (StringUtils.isNotBlank(decisionMakerUsername)) {
      UserJson user = userService.findUserByUserName(decisionMakerUsername);
      return new StatusChangeInfoJson(user.getId());
    }
    return null;
  }

  /**
   * Rejects contract if application has one.
   */
  public void rejectContractIfExists(Integer applicationId, String rejectReason) {
    ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
    if (application.getType() == ApplicationType.PLACEMENT_CONTRACT) {
      ContractInfo contractInfo = getContractInfo(applicationId);
      if (contractInfo != null) {
        setContractRejected(applicationId, rejectReason, contractInfo);
      }
    }
  }

  /**
   * Rejects contract proposal and moves application to pending state.
   */
  public void rejectContractProposal(Integer applicationId, String rejectReason) {
    ContractInfo contractInfo = getContractInfo(applicationId);
    validateIsInProposalState(contractInfo);
    setContractRejected(applicationId, rejectReason, contractInfo);
    // If contract is rejected move application to pending state and add corresponding tag
    applicationServiceComposer.changeStatus(applicationId, StatusType.PENDING);
    applicationServiceComposer.addTag(applicationId, new ApplicationTagJson(null, ApplicationTagType.CONTRACT_REJECTED, null));
  }

  protected void setContractRejected(Integer applicationId, String rejectReason, ContractInfo contractInfo) {
    contractInfo.setStatus(ContractStatusType.REJECTED);
    contractInfo.setResponseTime(ZonedDateTime.now());
    contractInfo.setRejectionReason(rejectReason);

    restTemplate.exchange(applicationProperties.getContractInfoUrl(), HttpMethod.PUT, new HttpEntity<>(contractInfo), Void.class, applicationId);
  }

  public ContractInfo getContractInfo(Integer applicationId) {
    return restTemplate.getForEntity(applicationProperties.getContractInfoUrl(), ContractInfo.class,
        applicationId).getBody();
  }

  public void generateFinalContract(int applicationId, ApplicationJson applicationJson) {
    if (applicationJson.getType() == ApplicationType.PLACEMENT_CONTRACT) {
      ContractInfo contractInfo = getContractInfo(applicationId);
      if (contractInfo != null && contractInfo.getStatus() == ContractStatusType.APPROVED) {
        byte[] contractData = generateContractPdf(applicationJson, contractInfo, false);
        restTemplate.exchange(applicationProperties.getFinalContractUrl(), HttpMethod.POST,
            MultipartRequestBuilder.buildByteArrayRequest("data", contractData), String.class, applicationId);
      }
    }
  }

  private byte[] generateContractPdf(Integer applicationId, ContractInfo contractInfo, boolean draft) {
    ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
    return generateContractPdf(application, contractInfo, draft);
  }

  private byte[] generateContractPdf(ApplicationJson application, ContractInfo contractInfo, boolean draft) {
    DecisionJson decisionJson = decisionJsonMapper.mapToDocumentJson(application, draft);
    setContractData(contractInfo, decisionJson);
    byte[] pdfData = restTemplate.postForObject(
        applicationProperties.getGeneratePdfUrl(), decisionJson, byte[].class,
        STYLE_SHEET_NAME);
    return pdfData;
  }

  protected void setContractData(ContractInfo contractInfo, DecisionJson decisionJson) {
    if (contractInfo != null) {
      decisionJson.setContractSigner(contractInfo.getSigner());
      decisionJson.setContractSigningDate(contractInfo.getResponseTime() != null
          ? TimeUtil.dateAsDateTimeString(contractInfo.getResponseTime()) : null);
      decisionJson.setContractAsAttachment(contractInfo.isContractAsAttachment());
      decisionJson.setFrameAgreement(contractInfo.isFrameAgreementExists());
    }
  }

  private void validateProposalCreationAllowed(ApplicationJson application) {
    if (application.getStatus() == StatusType.WAITING_CONTRACT_APPROVAL) {
      throw new IllegalOperationException("contract.waitingApproval");
    }
    if (application.getType() != ApplicationType.PLACEMENT_CONTRACT) {
      throw new IllegalOperationException("contract.applicationtype");
    }
  }

  private void validateIsInProposalState(ContractInfo contractInfo) {
    if (contractInfo == null || contractInfo.getStatus() != ContractStatusType.PROPOSAL) {
      throw new NoSuchEntityException("contractProposal.notFound");
    }
  }

  public boolean hasContract(Integer id) {
    return getContractInfo(id) != null;
  }

}
