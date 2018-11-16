package fi.hel.allu.supervision.api.mapper;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.ApplicationStatusInfo;
import fi.hel.allu.model.domain.SupervisionWorkItem;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.SupervisionTaskService;
import fi.hel.allu.servicecore.service.UserService;
import fi.hel.allu.supervision.api.domain.SupervisionTaskSearchResult;

@Component
public class SupervisionTaskSearchResultMapper {

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;

  @Autowired
  private UserService userService;

  @Autowired
  private SupervisionTaskService supervisionTaskService;

  public SupervisionTaskSearchResult mapToSearchResult(SupervisionWorkItem item) {
    UserJson owner = Optional.ofNullable(item.getOwnerId()).map(id -> userService.findUserById(id)).orElse(null);
    ApplicationStatusInfo applicationStatusInfo = applicationServiceComposer.getApplicationStatus(item.getApplicationId());
    SupervisionTaskJson task = supervisionTaskService.findById(item.getId());
    SupervisionTaskSearchResult result = new SupervisionTaskSearchResult();
    Optional.ofNullable(owner).ifPresent(o -> result.setOwnerRealName(o.getRealName()));
    Optional.ofNullable(owner).ifPresent(o -> result.setOwnerUserName(o.getUserName()));
    result.setApplicationIdentifier(applicationStatusInfo.getApplicationId());
    result.setApplicationStatus(applicationStatusInfo.getStatus());
    result.setActualFinishingTime(task.getActualFinishingTime());
    result.setApplicationId(task.getApplicationId());
    result.setCreationTime(task.getCreationTime());
    result.setDescription(task.getDescription());
    result.setId(task.getId());
    result.setPlannedFinishingTime(task.getPlannedFinishingTime());
    result.setResult(task.getResult());
    result.setStatus(task.getStatus());
    result.setType(task.getType());
    return result;
  }


}
