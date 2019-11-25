package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.StatusType;
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

  @Autowired
  public BulkApprovalService(ApplicationService applicationService, ApplicationMapper applicationMapper,
       ApplicationHistoryService applicationHistoryService) {
    this.applicationService = applicationService;
    this.applicationMapper = applicationMapper;
    this.applicationHistoryService = applicationHistoryService;
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
    entry.setBulkApprovalBlocked(isApprovalBlocked(application.getId()));
    entry.setBulkApprovalBlockedReason(""); // TODO: translated reason
    return entry;
  }

  private boolean isApprovalBlocked(Integer applicationId) {
    return applicationHistoryService.hasStatusInHistory(applicationId, StatusType.OPERATIONAL_CONDITION);
  }
}
