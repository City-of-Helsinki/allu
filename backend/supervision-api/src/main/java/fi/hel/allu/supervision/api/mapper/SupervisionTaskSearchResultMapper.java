package fi.hel.allu.supervision.api.mapper;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.ApplicationStatusInfo;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.UserService;
import fi.hel.allu.supervision.api.domain.SupervisionTaskSearchResult;

@Component
public class SupervisionTaskSearchResultMapper {

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;

  @Autowired
  private UserService userService;

  public SupervisionTaskSearchResult mapToSearchResult(SupervisionTask s) {
    UserJson owner = Optional.ofNullable(s.getOwnerId()).map(id -> userService.findUserById(id)).orElse(null);
    ApplicationStatusInfo applicationStatusInfo = applicationServiceComposer.getApplicationStatus(s.getApplicationId());
    SupervisionTaskSearchResult result = new SupervisionTaskSearchResult();
    Optional.ofNullable(owner).ifPresent(o -> result.setOwnerRealName(o.getRealName()));
    Optional.ofNullable(owner).ifPresent(o -> result.setOwnerUserName(o.getUserName()));
    result.setApplicationIdentifier(applicationStatusInfo.getApplicationId());
    result.setApplicationStatus(applicationStatusInfo.getStatus());
    result.setActualFinishingTime(s.getActualFinishingTime());
    result.setApplicationId(s.getApplicationId());
    result.setCreationTime(s.getCreationTime());
    result.setDescription(s.getDescription());
    result.setId(s.getId());
    result.setPlannedFinishingTime(s.getPlannedFinishingTime());
    result.setResult(s.getResult());
    result.setStatus(s.getStatus());
    result.setType(s.getType());
    return result;
  }


}
