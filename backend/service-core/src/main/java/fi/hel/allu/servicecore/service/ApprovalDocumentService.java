package fi.hel.allu.servicecore.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.domain.DocumentSearchCriteria;
import fi.hel.allu.common.domain.DocumentSearchResult;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.MultipartRequestBuilder;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.pdf.domain.DecisionJson;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.DecisionDetailsJson;
import fi.hel.allu.servicecore.domain.DecisionDocumentType;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.mapper.AnonymizedApprovalDocumentMapper;
import fi.hel.allu.servicecore.mapper.ApprovalDocumentMapper;

@Service
public class ApprovalDocumentService {

  private static final String TEMPLATE_NAME_POSTFIX = "-approval";
  private static final Set<ApplicationType> hasFinalApprovalDocument = new HashSet<>(
          Arrays.asList(ApplicationType.AREA_RENTAL, ApplicationType.EXCAVATION_ANNOUNCEMENT));

  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;
  private final ApprovalDocumentMapper approvalDocumentMapper;
  private final ApprovalDocumentMapper anonymizedDocumentMapper;
  private final ApplicationServiceComposer applicationServiceComposer;
  private final UserService userService;
  private final MailComposerService mailComposerService;
  private final InvoicingPeriodService invoicingPeriodService;

  @Autowired
  public ApprovalDocumentService(ApplicationProperties applicationProperties,
                                 RestTemplate restTemplate,
                                 ApprovalDocumentMapper approvalDocumentMapper,
                                 ApplicationServiceComposer applicationServiceComposer,
                                 UserService userService,
                                 @Lazy MailComposerService mailComposerService,
                                 InvoicingPeriodService invoicingPeriodService,
                                 AnonymizedApprovalDocumentMapper anonymizedDocumentMapper) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.approvalDocumentMapper = approvalDocumentMapper;
    this.applicationServiceComposer = applicationServiceComposer;
    this.userService = userService;
    this.mailComposerService = mailComposerService;
    this.invoicingPeriodService = invoicingPeriodService;
    this.anonymizedDocumentMapper = anonymizedDocumentMapper;
  }

  public byte[] getApprovalDocument(Integer applicationId,
      ApprovalDocumentType type, List<ChargeBasisEntry> chargeBasisEntries) {
    final ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
    if ((type == ApprovalDocumentType.OPERATIONAL_CONDITION &&
        (application.getStatus().ordinal() >= StatusType.OPERATIONAL_CONDITION.ordinal() ||
        (application.getStatus()== StatusType.DECISIONMAKING && application.getTargetState() == StatusType.FINISHED))) ||
        (type == ApprovalDocumentType.WORK_FINISHED && application.getStatus().ordinal() >= StatusType.FINISHED.ordinal())) {
      try {
        return getFinalApprovalDocument(applicationId, type);
      } catch (NoSuchElementException e) {
        return generateApprovalDocumentPreview(application, type, chargeBasisEntries);
      }
    } else {
      return generateApprovalDocumentPreview(application, type, chargeBasisEntries);
    }
  }

  public byte[] getFinalApprovalDocument(Integer applicationId, ApprovalDocumentType type) {
    return restTemplate.getForObject(applicationProperties.getApprovalDocumentUrl(), byte[].class, applicationId, type);
  }

  public byte[] getAnonymizedDocument(Integer applicationId, ApprovalDocumentType type) {
    return restTemplate.getForObject(applicationProperties.getAnonymizedApprovalDocumentUrl(), byte[].class, applicationId, type);
  }

  public void createFinalApprovalDocument(ApplicationJson prevApplication,
      ApplicationJson application, List<ChargeBasisEntry> chargeBasisEntries) {
    if (hasFinalApprovalDocument.contains(application.getType())) {
      if (application.getStatus()== StatusType.OPERATIONAL_CONDITION) {
        generateFinalApprovalDocument(prevApplication, application, ApprovalDocumentType.OPERATIONAL_CONDITION,
            getOperationalConditionPeriodEntries(application.getId(), chargeBasisEntries));
      } else if (application.getStatus() == StatusType.FINISHED) {
        generateFinalApprovalDocument(prevApplication, application, ApprovalDocumentType.WORK_FINISHED, chargeBasisEntries);
      }
    }
  }

  private byte[] generateApprovalDocumentPreview(ApplicationJson application,
      ApprovalDocumentType type, List<ChargeBasisEntry> chargeBasisEntries) {
    List<ChargeBasisEntry> documentChargeBasisEntries;
    if (type == ApprovalDocumentType.OPERATIONAL_CONDITION) {
      documentChargeBasisEntries = getOperationalConditionPeriodEntries(application.getId(), chargeBasisEntries);
    } else {
      documentChargeBasisEntries = chargeBasisEntries;
    }

    final DecisionJson decisionJson = approvalDocumentMapper.mapApprovalDocument(application, documentChargeBasisEntries, true, type);
    clearDeciderData(decisionJson);
    return restTemplate.postForObject(applicationProperties.getGeneratePdfUrl(),
        decisionJson, byte[].class, styleSheetName(application, type));
  }

  private List<ChargeBasisEntry> getOperationalConditionPeriodEntries(Integer applicationId, List<ChargeBasisEntry> chargeBasisEntries) {
    List<InvoicingPeriod> periods = invoicingPeriodService.getInvoicingPeriods(applicationId);
    if (periods.size() != 0) {
      return invoicingPeriodService.getInvoicingPeriods(applicationId)
          .stream()
          .filter(p -> p.getInvoicableStatus() == StatusType.OPERATIONAL_CONDITION)
          .findFirst()
          .map(p -> getEntriesOfPeriod(p, chargeBasisEntries))
          .orElse(Collections.emptyList());
    } else {
      return chargeBasisEntries;
    }
  }

  private List<ChargeBasisEntry> getEntriesOfPeriod(InvoicingPeriod period, List<ChargeBasisEntry> chargeBasisEntries) {
    return chargeBasisEntries.stream()
        .filter(c -> Objects.equals(period.getId(), c.getInvoicingPeriodId()))
        .collect(Collectors.toList());
  }

  private void generateFinalApprovalDocument(ApplicationJson prevApplication, ApplicationJson application,
      ApprovalDocumentType type, List<ChargeBasisEntry> chargeBasisEntries) {
    saveDocumentData(approvalDocumentMapper, prevApplication, application, type, chargeBasisEntries, applicationProperties.getApprovalDocumentUrl());
    saveDocumentData(anonymizedDocumentMapper, prevApplication, application, type, chargeBasisEntries, applicationProperties.getAnonymizedApprovalDocumentUrl());
    if (prevApplication.getStatus() != StatusType.DECISIONMAKING) {
      // Send the created approval document using previously defined distribution list
      final DecisionDetailsJson details = new DecisionDetailsJson();
      details.setDecisionDistributionList(application.getDecisionDistributionList());
      details.setMessageBody(""); // Todo: Use some predefined text?
      mailComposerService.sendDecision(application, details, approvalDocumentTypeTodecisionDocumentType(type));
    }
  }

  private void saveDocumentData(ApprovalDocumentMapper mapper, ApplicationJson prevApplication, ApplicationJson application, ApprovalDocumentType type,
      List<ChargeBasisEntry> chargeBasisEntries, String documentDataUrl) {
    final DecisionJson decisionJson = mapper.mapApprovalDocument(application, chargeBasisEntries, false, type);
    if (prevApplication.getStatus() != StatusType.DECISIONMAKING) {
      clearDeciderData(decisionJson);
    } else {
      setDeciderData(decisionJson);
    }

    final byte[] document = restTemplate.postForObject(applicationProperties.getGeneratePdfUrl(),
        decisionJson, byte[].class, styleSheetName(application, type));
    restTemplate.exchange(
        documentDataUrl, HttpMethod.POST,
        MultipartRequestBuilder.buildByteArrayRequest("file", document), String.class, application.getId(), type);
  }

  private String styleSheetName(ApplicationJson application, ApprovalDocumentType documentType) {
    return application.getType() + TEMPLATE_NAME_POSTFIX;
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

  private DecisionDocumentType approvalDocumentTypeTodecisionDocumentType(ApprovalDocumentType type) {
    switch (type) {
      case OPERATIONAL_CONDITION:
        return DecisionDocumentType.OPERATIONAL_CONDITION;
      case WORK_FINISHED:
        return DecisionDocumentType.WORK_FINISHED;
      default:
        return DecisionDocumentType.DECISION;
    }
  }

  public List<DocumentSearchResult> searchApprovalDocuments(DocumentSearchCriteria searchCriteria, ApprovalDocumentType documentType) {
    ParameterizedTypeReference<List<DocumentSearchResult>> typeRef = new ParameterizedTypeReference<List<DocumentSearchResult>>() {};
    return restTemplate.exchange(applicationProperties.getApprovalDocumentSearchUrl(), HttpMethod.POST,
        new HttpEntity<>(searchCriteria), typeRef, documentType).getBody();
  }

}
