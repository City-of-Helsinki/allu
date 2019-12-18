package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.servicecore.validation.ValidationMessageTranslator;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.servicecore.domain.BulkApprovalEntryJson;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BulkApprovalService {
  private final ApplicationService applicationService;
  private final ApplicationMapper applicationMapper;
  private final ApplicationHistoryService applicationHistoryService;
  private final ValidationMessageTranslator validationMessageTranslator;

  @Autowired
  public BulkApprovalService(ApplicationService applicationService, ApplicationMapper applicationMapper,
       ApplicationHistoryService applicationHistoryService, ValidationMessageTranslator validationMessageTranslator) {
    this.applicationService = applicationService;
    this.applicationMapper = applicationMapper;
    this.applicationHistoryService = applicationHistoryService;
    this.validationMessageTranslator = validationMessageTranslator;
  }

  public List<BulkApprovalEntryJson> getBulkApprovalEntries(List<Integer> ids) {
    return getApplicationsInDecisionMaking(ids)
      .map(this::toBulkApprovalEntryJson)
      .collect(Collectors.toList());
  }

  private Stream<Application> getApplicationsInDecisionMaking(List<Integer> ids) {
    return applicationService.findApplicationsById(ids).stream()
      .filter(application -> StatusType.DECISIONMAKING.equals(application.getStatus()));
  }

  private BulkApprovalEntryJson toBulkApprovalEntryJson(Application application) {
    BulkApprovalEntryJson entry = new BulkApprovalEntryJson();
    entry.setId(application.getId());
    entry.setApplicationId(application.getApplicationId());
    entry.setTargetState(application.getTargetState());
    entry.setDistributionList(applicationMapper.createDistributionEntryJsonList(application.getDecisionDistributionList()));
    setApprovalBlocked(entry, application);
    return entry;
  }

  private void setApprovalBlocked(BulkApprovalEntryJson entry, Application application) {
    if (isApprovalBlockedReplacedOperationalCondition(application)) {
      entry.setBulkApprovalBlocked(true);
      entry.setBulkApprovalBlockedReason(validationMessageTranslator.getTranslation("bulkApproval.blocked.choiceForOperationalConditionOrDecision"));
    }
  }

  private boolean isApprovalBlockedReplacedOperationalCondition(Application application) {
    boolean replacingApplication = application.getReplacesApplicationId() != null;
    boolean toDecisionOrOperationalCondition = application.getTargetState() == StatusType.DECISION
      || application.getTargetState() == StatusType.OPERATIONAL_CONDITION;
    return replacingApplication
      && toDecisionOrOperationalCondition
      && hasBeenInOperationalCondition(application);
  }

  private boolean hasBeenInOperationalCondition(Application application) {
    return ApplicationType.EXCAVATION_ANNOUNCEMENT.equals(application.getType())
      && applicationHistoryService.hasStatusInHistory(application.getId(), StatusType.OPERATIONAL_CONDITION);
  }
}
