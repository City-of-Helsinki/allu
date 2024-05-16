package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.domain.SupervisionWorkItem;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionWorkItemJson;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SupervisionTaskMapper {
  public static List<SupervisionTaskJson> maptoJson(List<SupervisionTask> tasks, Map<Integer, UserJson> idToUser) {
    return tasks.stream().map(st -> SupervisionTaskMapper.mapToJson(st, idToUser)).collect(Collectors.toList());
  }

  public static SupervisionTaskJson mapToJson(SupervisionTask supervisionTask, Map<Integer, UserJson> idToUser) {
    return new SupervisionTaskJson(
        supervisionTask.getId(),
        supervisionTask.getApplicationId(),
        supervisionTask.getType(),
        idToUser.get(supervisionTask.getCreatorId()),
        idToUser.get(supervisionTask.getOwnerId()),
        supervisionTask.getCreationTime(),
        supervisionTask.getPlannedFinishingTime(),
        supervisionTask.getActualFinishingTime(),
        supervisionTask.getStatus(),
        supervisionTask.getDescription(),
        supervisionTask.getResult(),
        supervisionTask.getLocationId(),
        supervisionTask.getSupervisedLocations());
  }

  public static SupervisionTask mapToModel(SupervisionTaskJson supervisionTaskJson) {
    return new SupervisionTask(
        supervisionTaskJson.getId(),
        supervisionTaskJson.getApplicationId(),
        supervisionTaskJson.getType(),
        Optional.ofNullable(supervisionTaskJson.getCreator()).map(UserJson::getId).orElse(null),
        Optional.ofNullable(supervisionTaskJson.getOwner()).map(UserJson::getId).orElse(null),
        supervisionTaskJson.getCreationTime(),
        supervisionTaskJson.getPlannedFinishingTime(),
        supervisionTaskJson.getActualFinishingTime(),
        supervisionTaskJson.getStatus(),
        supervisionTaskJson.getDescription(),
        supervisionTaskJson.getResult(),
        supervisionTaskJson.getLocationId()
    );
  }

  public static SupervisionWorkItemJson mapToWorkItem(SupervisionWorkItem task,
                                                      UserJson creator, UserJson owner) {
    SupervisionWorkItemJson workItem = new SupervisionWorkItemJson();
    workItem.setId(task.getId());
    workItem.setType(task.getType().getValue());
    workItem.setApplicationId(task.getApplicationId());
    workItem.setApplicationIdText(task.getApplicationIdText());
    workItem.setApplicationStatus(task.getApplicationStatus());
    workItem.setCreator(creator);
    workItem.setPlannedFinishingTime(task.getPlannedFinishingTime());
    workItem.setAddress(Optional.ofNullable(task.getAddress()).map(a -> String.join(", ", a)).orElse(null));
    workItem.setProjectName(task.getProjectName());
    workItem.setOwner(owner);
    return workItem;
  }
}