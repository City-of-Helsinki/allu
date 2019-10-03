package fi.hel.allu.supervision.api.mapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.ApplicationStatusInfo;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.domain.SupervisionWorkItem;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.SupervisionTaskService;
import fi.hel.allu.supervision.api.domain.SupervisionTaskCreateJson;
import fi.hel.allu.supervision.api.domain.SupervisionTaskSearchResult;

@Component
public class SupervisionTaskMapper {

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;

  @Autowired
  private SupervisionTaskService supervisionTaskService;

  public SupervisionTaskSearchResult mapToSearchResult(SupervisionWorkItem item) {
    SupervisionTaskJson task = supervisionTaskService.findById(item.getId());
    return mapToSearchResult(task, item.getAddress());
  }

  public SupervisionTaskSearchResult mapToSearchResult(SupervisionTaskJson task) {
    return mapToSearchResult(task, supervisionTaskService.getTaskAddresses(task.getId()));
  }

  private SupervisionTaskSearchResult mapToSearchResult(SupervisionTaskJson task, String[] addresses) {
    ApplicationStatusInfo applicationStatusInfo = applicationServiceComposer.getApplicationStatus(task.getApplicationId());
    SupervisionTaskSearchResult result = new SupervisionTaskSearchResult();

    Optional.ofNullable(task.getOwner()).ifPresent(o -> result.setOwnerRealName(o.getRealName()));
    Optional.ofNullable(task.getOwner()).ifPresent(o -> result.setOwnerUserName(o.getUserName()));
    result.setApplicationIdentifier(applicationStatusInfo.getApplicationId());
    result.setApplicationStatus(applicationStatusInfo.getStatus());
    result.setActualFinishingTime(task.getActualFinishingTime());
    result.setApplicationId(task.getApplicationId());
    result.setCreationTime(task.getCreationTime());
    result.setDescription(task.getDescription());
    result.setId(task.getId());
    result.setLocationId(task.getLocationId());
    result.setPlannedFinishingTime(task.getPlannedFinishingTime());
    result.setResult(task.getResult());
    result.setStatus(task.getStatus());
    result.setType(task.getType());
    result.setApplicationType(applicationStatusInfo.getType());
    result.setAddresses(Optional.ofNullable(addresses).map(a -> Arrays.asList(a)).orElse(Collections.emptyList()));
    return result;
  }

  public SupervisionTask mapToModel(SupervisionTaskCreateJson supervisionTask) {
    return new SupervisionTask(supervisionTask.getApplicationId(), supervisionTask.getType(),
        supervisionTask.getOwnerId(), supervisionTask.getPlannedFinishingTime(), supervisionTask.getDescription());
  }

}
