package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionWorkItemJson;

import java.time.ZonedDateTime;
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

  public static SupervisionWorkItemJson mapToWorkItem(SupervisionTask task, ApplicationJson application,
                                                      UserJson creator, UserJson handler) {
    SupervisionWorkItemJson workItem = new SupervisionWorkItemJson();
    workItem.setId(task.getId());
    workItem.setType(task.getType());
    workItem.setApplicationId(task.getApplicationId());
    workItem.setApplicationIdText(application.getApplicationId());
    workItem.setApplicationStatus(application.getStatus());
    workItem.setCreator(creator);
    workItem.setPlannedFinishingTime(task.getPlannedFinishingTime());
    application.getLocations().stream().findFirst()
        .ifPresent(loc -> workItem.setAddress(loc.getPostalAddress()));
    Optional.ofNullable(application.getProject())
        .ifPresent(project -> workItem.setProjectName(project.getName()));
    workItem.setHandler(handler);
    return workItem;
  }
}
