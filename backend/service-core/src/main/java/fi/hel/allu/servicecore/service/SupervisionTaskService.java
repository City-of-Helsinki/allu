package fi.hel.allu.servicecore.service;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fi.hel.allu.model.domain.UpdateTaskOwners;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.types.ApplicationNotificationType;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.domain.SupervisionWorkItem;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionWorkItemJson;
import fi.hel.allu.servicecore.event.ApplicationArchiveEvent;
import fi.hel.allu.servicecore.event.ApplicationEventDispatcher;
import fi.hel.allu.servicecore.mapper.SupervisionTaskMapper;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;

@Service
public class SupervisionTaskService {

  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;
  private final UserService userService;
  private final ApplicationServiceComposer applicationServiceComposer;
  private final ApplicationEventPublisher archiveEventPublisher;
  private final ApplicationEventDispatcher applicationEventDispatcher;
  private final ApplicationHistoryService applicationHistoryService;
  private final SearchService searchService;

  @Autowired
  public SupervisionTaskService(ApplicationProperties applicationProperties, RestTemplate restTemplate,
                                UserService userService, ApplicationServiceComposer applicationServiceComposer,
                                ApplicationEventPublisher archiveEventPublisher, ApplicationHistoryService applicationHistoryService,
                                ApplicationEventDispatcher applicationEventDispatcher, SearchService searchService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.userService = userService;
    this.applicationServiceComposer = applicationServiceComposer;
    this.archiveEventPublisher = archiveEventPublisher;
    this.applicationEventDispatcher = applicationEventDispatcher;
    this.applicationHistoryService = applicationHistoryService;
    this.searchService = searchService;
  }


  public SupervisionTaskJson findById(int id) {
    ResponseEntity<SupervisionTask> supervisionTasksResult = restTemplate.getForEntity(
        applicationProperties.getSupervisionTaskByIdUrl(), SupervisionTask.class, id);
    return getFullyPopulatedJson(Collections.singletonList(supervisionTasksResult.getBody())).get(0);
  }

  public List<SupervisionTaskJson> findByApplicationId(int applicationId) {
    ResponseEntity<SupervisionTask[]> supervisionTasksResult = restTemplate.getForEntity(
        applicationProperties.getSupervisionTaskByApplicationIdUrl(), SupervisionTask[].class, applicationId);
    return getFullyPopulatedJson(Arrays.asList(supervisionTasksResult.getBody()));
  }

  public List<SupervisionTaskJson> findByLocationId(int locationId) {
    ResponseEntity<SupervisionTask[]> supervisionTasksResult = restTemplate.getForEntity(
        applicationProperties.getSupervisionTaskByLocationIdUrl(), SupervisionTask[].class, locationId);
    return getFullyPopulatedJson(Arrays.asList(supervisionTasksResult.getBody()));
  }


  public SupervisionTaskJson update(SupervisionTaskJson supervisionTask) {
    ResponseEntity<SupervisionTask> supervisionTasksResult = update(supervisionTask.getId(), SupervisionTaskMapper.mapToModel(supervisionTask));
    searchService.updateSupervisionTasks(getSupervisionWorkItem(supervisionTasksResult.getBody().getId()));
    return getFullyPopulatedJson(Collections.singletonList(supervisionTasksResult.getBody())).get(0);
  }

  private ResponseEntity<SupervisionTask> update(Integer id,
      SupervisionTask supervisionTask) {
    HttpEntity<SupervisionTask> supervisionTaskHttpEntity = new HttpEntity<>(supervisionTask);
    ResponseEntity<SupervisionTask> supervisionTasksResult = restTemplate.exchange(
        applicationProperties.getSupervisionTaskUpdateUrl(),
        HttpMethod.PUT,
        supervisionTaskHttpEntity,
        SupervisionTask.class,
        id);
    applicationHistoryService.addSupervisionUpdated(supervisionTask.getApplicationId(), supervisionTask.getType());
    applicationEventDispatcher.dispatchUpdateEvent(supervisionTask.getApplicationId(), supervisionTask.getCreatorId(),
        ApplicationNotificationType.SUPERVISION_UPDATED, supervisionTask.getType().name());
    return supervisionTasksResult;
  }

  public SupervisionTaskJson insert(SupervisionTaskJson supervisionTaskJson) {
    SupervisionTask task = SupervisionTaskMapper.mapToModel(supervisionTaskJson);
    return insert(task);
  }

  public  SupervisionTaskJson insert(SupervisionTask task) {
    task.setCreatorId(userService.getCurrentUser().getId());
    ResponseEntity<SupervisionTask> supervisionTasksResult = restTemplate.postForEntity(
        applicationProperties.getSupervisionTaskCreateUrl(), task, SupervisionTask.class);
    applicationServiceComposer.refreshSearchTags(task.getApplicationId());
    applicationHistoryService.addSupervisionAdded(task.getApplicationId(), task.getType());
    applicationEventDispatcher.dispatchUpdateEvent(task.getApplicationId(), task.getCreatorId(),
        ApplicationNotificationType.SUPERVISION_ADDED, task.getType().name());
    searchService.insertSupervisionTask(getSupervisionWorkItem(supervisionTasksResult.getBody().getId()));
    return getFullyPopulatedJson(Collections.singletonList(supervisionTasksResult.getBody())).get(0);
  }

  public SupervisionWorkItem getSupervisionWorkItem(Integer supervisionTaskId){
    return restTemplate.getForEntity(
            applicationProperties.getSupervisionTaskGetWorkItemUrl(),
            SupervisionWorkItem.class,
            supervisionTaskId).getBody();
  }

  public SupervisionTaskJson approve(SupervisionTaskJson taskJson) {
    validateApprovalAllowed(taskJson);
    HttpEntity<SupervisionTask> supervisionTaskHttpEntity = new HttpEntity<>(SupervisionTaskMapper.mapToModel(taskJson));
    ResponseEntity<SupervisionTask> supervisionTasksResult = restTemplate.exchange(
        applicationProperties.getSupervisionTaskApproveUrl(),
        HttpMethod.PUT,
        supervisionTaskHttpEntity,
        SupervisionTask.class,
        taskJson.getId());
    applicationServiceComposer.refreshSearchTags(taskJson.getApplicationId());
    archiveEventPublisher.publishEvent(new ApplicationArchiveEvent(taskJson.getApplicationId()));
    applicationEventDispatcher.dispatchUpdateEvent(taskJson.getApplicationId(), taskJson.getCreator().getId(),
        ApplicationNotificationType.SUPERVISION_APPROVED, taskJson.getType().name());
    applicationHistoryService.addSupervisionApproved(taskJson.getApplicationId(), taskJson.getType());
    return getFullyPopulatedJson(Collections.singletonList(supervisionTasksResult.getBody())).get(0);
  }

  private void validateApprovalAllowed(SupervisionTaskJson taskJson) {
    ApplicationJson applicationJson = applicationServiceComposer.findApplicationById(taskJson.getApplicationId());
    boolean hasStatusBlockingApproval = applicationJson.getStatus().isBeforeDecision();

    if ((taskJson.getType() == SupervisionTaskType.OPERATIONAL_CONDITION
      || taskJson.getType() == SupervisionTaskType.FINAL_SUPERVISION)
      && (applicationServiceComposer.isReplaced(taskJson.getApplicationId())
      || hasStatusBlockingApproval)) {
      // Do not allow approval of operational condition / final supervision if replacing application exists since
      // approval may change state of application
      throw new IllegalOperationException("application.replaced.notAllowed");
    }
  }

  public SupervisionTaskJson reject(SupervisionTaskJson taskJson, ZonedDateTime newSupervisionDate) {
    HttpEntity<SupervisionTask> supervisionTaskHttpEntity = new HttpEntity<>(SupervisionTaskMapper.mapToModel(taskJson));

    Map<String, Integer> uriParams = new HashMap<>();
    uriParams.put("id", taskJson.getId());

    URI uri = UriComponentsBuilder.fromHttpUrl(applicationProperties.getSupervisionTaskRejectUrl())
        .queryParam("newDate", newSupervisionDate)
        .buildAndExpand(uriParams).toUri();

    ResponseEntity<SupervisionTask> supervisionTasksResult = restTemplate.exchange(
        uri, HttpMethod.PUT, supervisionTaskHttpEntity, SupervisionTask.class);

    applicationServiceComposer.refreshSearchTags(taskJson.getApplicationId());
    applicationHistoryService.addSupervisionRejected(taskJson.getApplicationId(), taskJson.getType());
    applicationEventDispatcher.dispatchUpdateEvent(taskJson.getApplicationId(), taskJson.getCreator().getId(),
        ApplicationNotificationType.SUPERVISION_REJECTED, taskJson.getType().name());

    // TODO: send email to customer about new supervision date and reason of rejection
    return getFullyPopulatedJson(Collections.singletonList(supervisionTasksResult.getBody())).get(0);
  }

  public void delete(int id) {
    SupervisionTaskJson taskJson = findById(id);
    restTemplate.delete(applicationProperties.getSupervisionTaskByIdUrl(), id);
    applicationServiceComposer.refreshSearchTags(taskJson.getApplicationId());
    applicationHistoryService.addSupervisionRemoved(taskJson.getApplicationId(), taskJson.getType());
    archiveEventPublisher.publishEvent(new ApplicationArchiveEvent(taskJson.getApplicationId()));
    applicationEventDispatcher.dispatchUpdateEvent(taskJson.getApplicationId(), taskJson.getCreator().getId(),
        ApplicationNotificationType.SUPERVISION_REMOVED, taskJson.getType().name());
    searchService.deleteSupervisionTask(id);
  }

  public Page<SupervisionWorkItemJson> searchWorkItems(QueryParameters queryParameters, Pageable pageRequest) {
    Page<SupervisionWorkItem> result = search(queryParameters, pageRequest);
    return result.map(this::toWorkItem);
  }

  public Page<SupervisionWorkItem> search(QueryParameters queryParameters, Pageable pageRequest) {
    return searchService.searchSupervisionTask(queryParameters, pageRequest, false);
  }

  private SupervisionWorkItemJson toWorkItem(SupervisionWorkItem task) {
    UserJson creator = Optional.ofNullable(task.getCreator()).map(UserMapper::mapToUserJson).orElse(null);
    UserJson owner = Optional.ofNullable(task.getOwner()).map(UserMapper::mapToUserJson).orElse(null);
    return SupervisionTaskMapper.mapToWorkItem(task, creator, owner);
  }

  public List<Integer> getTaskCount(Integer applicationId) {
    ResponseEntity<Integer[]> response = restTemplate.getForEntity(applicationProperties.getSupervisionTaskCountUrl(),
                                                                 Integer[].class, applicationId);
    Integer[] result = response.getBody();
    return result != null ? Arrays.asList(result) : new ArrayList<>();
  }

  /**
   * Updates owner for given supervision tasks.
   *
   * @param updatedOwner owner to be set.
   * @param taskIds      Supervision tasks to be updated.
   */
  public void updateOwner(int updatedOwner, List<Integer> taskIds) {
    UserJson owner = userService.findUserById(updatedOwner);
    UpdateTaskOwners updateTaskOwners = new UpdateTaskOwners();
    updateTaskOwners.setTaskIds(taskIds);
    updateTaskOwners.setNewUser(UserMapper.mapToModelUser(owner));
    searchService.updateSupervisionTaskOwner(updateTaskOwners);
    restTemplate.put(applicationProperties.getSupervisionTaskOwnerUpdateUrl(), taskIds, updatedOwner);
  }

  /**
   * Removes owner from given supervision tasks.
   *
   * @param taskIds Supervision tasks to be updated.
   */
  public void removeOwner(List<Integer> taskIds) {
    searchService.removeSupervisionTaskOwner(taskIds);
    restTemplate.put(applicationProperties.getSupervisionTaskOwnerRemoveUrl(), taskIds);
  }

  private List<SupervisionTaskJson> getFullyPopulatedJson(List<SupervisionTask> supervisionTasks) {
    return SupervisionTaskMapper.maptoJson(supervisionTasks, idToUser(supervisionTasks));
  }

  private Map<Integer, UserJson> idToUser(List<SupervisionTask> supervisionTasks) {
    return supervisionTasks.stream()
        .flatMap(st -> Stream.of(st.getCreatorId(), st.getOwnerId()))
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toMap(id -> id, userService::findUserById));
  }

  public void updateSupervisionTaskDate(Integer applicationId, SupervisionTaskType taskType,
      ZonedDateTime date) {
    Optional<SupervisionTask> task = findOpenSupervisionTaskByApplicationIdAndType(applicationId, taskType);
    task.ifPresent(t -> updateTaskDate(t, date));
  }

  private void updateTaskDate(SupervisionTask task, ZonedDateTime date) {
    task.setPlannedFinishingTime(date);
    update(task.getId(), task);
  }

  private Optional<SupervisionTask> findOpenSupervisionTaskByApplicationIdAndType(int applicationId, SupervisionTaskType type) {
    SupervisionTask[] supervisionTasksResult = restTemplate.getForEntity(
        applicationProperties.getSupervisionTaskByApplicationIdAndTypeUrl(), SupervisionTask[].class, applicationId, type).getBody();
    return Stream.of(supervisionTasksResult).filter(task -> task.getStatus() == SupervisionTaskStatusType.OPEN).findFirst();
  }

  public void updateSupervisionTaskDate(int applicationId, SupervisionTaskType type, int locationId,
                                        ZonedDateTime date) {
    ResponseEntity<SupervisionTask[]> taskResponseEntity = restTemplate.getForEntity(
            applicationProperties.getSupervisionTaskByApplicationIdAndTypeAndLocationUrl(), SupervisionTask[].class,
            applicationId, type, locationId);
    if (taskResponseEntity.getBody() != null) {
      Arrays.stream(taskResponseEntity.getBody()).forEach(t -> {
        t.setPlannedFinishingTime(date);
        update(t.getId(), t);
      });
    }
  }

  public Map<Integer, List<SupervisionTask>> getSupervisionTaskHistoryForExternalOwner(Integer externalOwnerId,
      ZonedDateTime eventsAfter, List<Integer> includedExternalApplicationIds) {
    Map<String, Integer> uriParams = new HashMap<>();
    uriParams.put("externalownerid", externalOwnerId);
    URI uri = UriComponentsBuilder.fromHttpUrl(applicationProperties.getExternalOwnerSupervisionTaskHistoryUrl())
        .queryParam("eventsafter", eventsAfter)
        .buildAndExpand(uriParams).toUri();
    ParameterizedTypeReference<Map<Integer, List<SupervisionTask>>> typeRef = new ParameterizedTypeReference<Map<Integer, List<SupervisionTask>>>() {};
    return restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(includedExternalApplicationIds), typeRef).getBody();
  }

  public String[] getTaskAddresses(Integer id) {
    return restTemplate.getForObject(applicationProperties.getSupervisionTaskAddressByIdUrl(), String[].class, id);
  }
}