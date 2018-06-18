package fi.hel.allu.servicecore.service;

import java.io.IOException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import fi.hel.allu.common.domain.ContractInfo;
import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.util.MultipartRequestBuilder;
import fi.hel.allu.pdf.domain.DecisionJson;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ApplicationTagJson;
import fi.hel.allu.servicecore.mapper.DecisionJsonMapper;

@Service
public class ContractService {

  private final ApplicationServiceComposer applicationServiceComposer;
  private final DecisionJsonMapper decisionJsonMapper;
  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;

  private static final String STYLE_SHEET_NAME = "PLACEMENT_CONTRACT-contract";

  public ContractService(ApplicationServiceComposer applicationServiceComposer, DecisionJsonMapper decisionJsonMapper,
      ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationServiceComposer = applicationServiceComposer;
    this.decisionJsonMapper = decisionJsonMapper;
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  public byte[] createContractProposal(Integer applicationId) throws IOException {
    ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
    validateProposalCreationAllowed(application);
    DecisionJson decisionJson = decisionJsonMapper.mapDecisionJson(application, false);
    byte[] pdfData = restTemplate.postForObject(
        applicationProperties.getGeneratePdfUrl(), decisionJson, byte[].class,
        STYLE_SHEET_NAME);
    ResponseEntity<String> response = restTemplate.exchange(applicationProperties.getContractProposalUrl(), HttpMethod.POST,
        MultipartRequestBuilder.buildByteArrayRequest("data", pdfData), String.class, applicationId);
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new IOException(response.getBody());
    }
    return pdfData;
  }

  private void validateProposalCreationAllowed(ApplicationJson application) {
    if (application.getStatus() == StatusType.WAITING_CONTRACT_APPROVAL) {
      throw new IllegalOperationException("contract.waitingApproval");
    }
    if (application.getType() != ApplicationType.PLACEMENT_CONTRACT) {
      throw new IllegalOperationException("contract.applicationtype");
    }
  }

  public byte[] getContractProposal(Integer applicationId) {
    return restTemplate.getForObject(applicationProperties.getContractProposalUrl(), byte[].class, applicationId);
  }

  public byte[] getApprovedContract(Integer applicationId) {
    return restTemplate.getForObject(applicationProperties.getContractApprovedUrl(), byte[].class, applicationId);
  }

  public void approveContract(Integer applicationId, MultipartFile file) throws IOException {
    HttpEntity<?> requestEntity = MultipartRequestBuilder.buildByteArrayRequest("data", file.getBytes());
    restTemplate.exchange(applicationProperties.getContractApprovedUrl(), HttpMethod.POST, requestEntity, Void.class, applicationId);
    // After contract approval move application to waiting for decision state and remove possible contract rejected -tag
    applicationServiceComposer.changeStatus(applicationId, StatusType.DECISIONMAKING);
    applicationServiceComposer.removeTag(applicationId, ApplicationTagType.CONTRACT_REJECTED);
  }

  public void rejectContract(Integer applicationId, String rejectReason) {
    restTemplate.postForObject(applicationProperties.getContractRejectedUrl(), rejectReason, Void.class, applicationId);
    // If contract is rejected move application to pending state and add corresponding tag
    applicationServiceComposer.changeStatus(applicationId, StatusType.PENDING);
    applicationServiceComposer.addTag(applicationId, new ApplicationTagJson(null, ApplicationTagType.CONTRACT_REJECTED, null));
  }

  public ContractInfo getContractInfo(Integer applicationId) {
    return restTemplate.getForEntity(applicationProperties.getContractInfoUrl(), ContractInfo.class,
        applicationId).getBody();
  }

}
