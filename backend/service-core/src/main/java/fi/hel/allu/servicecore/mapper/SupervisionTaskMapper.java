package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.servicecore.domain.SupervisionTaskJson;
import fi.hel.allu.servicecore.domain.UserJson;

import java.util.Map;

public class SupervisionTaskMapper {
  public static SupervisionTaskJson mapToJson(SupervisionTask supervisionTask, Map<Integer, UserJson> idToUser) {
    return new SupervisionTaskJson(
        supervisionTask.getId(),
        supervisionTask.getApplicationId(),
        supervisionTask.getType(),
        idToUser.get(supervisionTask.getCreatorId()),
        idToUser.get(supervisionTask.getHandlerId()),
        supervisionTask.getCreationTime(),
        supervisionTask.getPlannedFinishingTime(),
        supervisionTask.getActualFinishingTime(),
        supervisionTask.getStatus(),
        supervisionTask.getDescription(),
        supervisionTask.getResult());
  }

  public static SupervisionTask mapToModel(SupervisionTaskJson supervisionTaskJson) {
    return new SupervisionTask(
      supervisionTaskJson.getId(),
      supervisionTaskJson.getApplicationId(),
        supervisionTaskJson.getType(),
        supervisionTaskJson.getCreator() == null ? null : supervisionTaskJson.getCreator().getId(),
        supervisionTaskJson.getHandler() == null ? null : supervisionTaskJson.getHandler().getId(),
        supervisionTaskJson.getCreationTime(),
        supervisionTaskJson.getPlannedFinishingTime(),
        supervisionTaskJson.getActualFinishingTime(),
        supervisionTaskJson.getStatus(),
        supervisionTaskJson.getDescription(),
        supervisionTaskJson.getResult()
    );
  }
}
