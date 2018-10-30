package fi.hel.allu.servicecore.service;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import fi.hel.allu.common.domain.SupervisionTaskSearchCriteria;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionWorkItemJson;
import fi.hel.allu.servicecore.event.ApplicationArchiveEvent;
import fi.hel.allu.servicecore.mapper.SupervisionTaskMapper;
import fi.hel.allu.servicecore.util.PageRequestBuilder;
import fi.hel.allu.servicecore.util.RestResponsePage;

@Service
public class SupervisionTaskService {

  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;
  private final UserService userService;
  private final ApplicationServiceComposer applicationServiceComposer;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Autowired
  public SupervisionTaskService(ApplicationProperties applicationProperties, RestTemplate restTemplate,
                                UserService userService, ApplicationServiceComposer applicationServiceComposer,
                                ApplicationEventPublisher archiveEventPublisher) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.userService = userService;
    this.applicationServiceComposer = applicationServiceComposer;
    this.applicationEventPublisher = archiveEventPublisher;
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

  public SupervisionTaskJson update(SupervisionTaskJson supervisionTask) {
    ResponseEntity<SupervisionTask> supervisionTasksResult = update(supervisionTask.getId(), SupervisionTaskMapper.mapToModel(supervisionTask));
    return getFullyPopulatedJson(Collections.singletonList(supervisionTasksResult.getBody())).get(0);
  }

  private ResponseEntity<SupervisionTask> update(Integer id,
      SupervisionTask supervisionTask) {
    HttpEntity<SupervisionTask> supervisionTaskHttpEntity = new HttpEntity<>(supervisionTask);
    return restTemplate.exchange(
        applicationProperties.getSupervisionTaskUpdateUrl(),
        HttpMethod.PUT,
        supervisionTaskHttpEntity,
        SupervisionTask.class,
        id);
  }

  public SupervisionTaskJson insert(SupervisionTaskJson supervisionTaskJson) {
    SupervisionTask task = SupervisionTaskMapper.mapToModel(supervisionTaskJson);
    task.setCreatorId(userService.getCurrentUser().getId());
    ResponseEntity<SupervisionTask> supervisionTasksResult = restTemplate.postForEntity(
        applicationProperties.getSupervisionTaskCreateUrl(), task, SupervisionTask.class);
    applicationServiceComposer.refreshSearchTags(task.getApplicationId());
    return getFullyPopulatedJson(Collections.singletonList(supervisionTasksResult.getBody())).get(0);
  }

  public SupervisionTaskJson approve(SupervisionTaskJson taskJson) {
    HttpEntity<SupervisionTask> supervisionTaskHttpEntity = new HttpEntity<>(SupervisionTaskMapper.mapToModel(taskJson));
    ResponseEntity<SupervisionTask> supervisionTasksResult = restTemplate.exchange(
        applicationProperties.getSupervisionTaskApproveUrl(),
        HttpMethod.PUT,
        supervisionTaskHttpEntity,
        SupervisionTask.class,
        taskJson.getId());
    applicationServiceComposer.refreshSearchTags(taskJson.getApplicationId());
    applicationEventPublisher.publishEvent(new ApplicationArchiveEvent(taskJson.getApplicationId()));
    return getFullyPopulatedJson(Collections.singletonList(supervisionTasksResult.getBody())).get(0);
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
    // TODO: send email to customer about new supervision date and reason of rejection
    return getFullyPopulatedJson(Collections.singletonList(supervisionTasksResult.getBody())).get(0);
  }

  public void delete(int id) {
    SupervisionTaskJson taskJson = findById(id);
    restTemplate.delete(applicationProperties.getSupervisionTaskByIdUrl(), id);
    applicationServiceComposer.refreshSearchTags(taskJson.getApplicationId());
    applicationEventPublisher.publishEvent(new ApplicationArchiveEvent(taskJson.getApplicationId()));
  }

  public Page<SupervisionWorkItemJson> search(SupervisionTaskSearchCriteria searchCriteria,
      Pageable pageRequest) {
    ParameterizedTypeReference<RestResponsePage<SupervisionTask>> typeref = new ParameterizedTypeReference<RestResponsePage<SupervisionTask>>() {
    };

    URI targetUri = PageRequestBuilder.fromUriString(applicationProperties.getSupervisionTaskSearchUrl(), pageRequest);
    ResponseEntity<RestResponsePage<SupervisionTask>> response =
        restTemplate.exchange(targetUri, HttpMethod.POST, new HttpEntity<>(searchCriteria), typeref);

    final Page<SupervisionTask> responsePage = response.getBody();
    final PageRequest responsePageRequest =
        new PageRequest(responsePage.getNumber(), Math.max(1, responsePage.getNumberOfElements()),
            responsePage.getSort());

    final Page<SupervisionWorkItemJson> result = new PageImpl<>(
        toWorkItems(responsePage.getContent()), responsePageRequest, responsePage.getTotalElements());
    return result;
  }

  /**
   * Updates owner for given supervision tasks.
   *
   * @param updatedOwner owner to be set.
   * @param taskIds Supervision tasks to be updated.
   */
  public void updateOwner(int updatedOwner, List<Integer> taskIds) {
    restTemplate.put(applicationProperties.getSupervisionTaskOwnerUpdateUrl(), taskIds, updatedOwner);
  }

  /**
   * Removes owner from given supervision tasks.
   *
   * @param taskIds Supervision tasks to be updated.
   */
  public void removeOwner(List<Integer> taskIds) {
    restTemplate.put(applicationProperties.getSupervisionTaskOwnerRemoveUrl(), taskIds);
  }

  private List<SupervisionTaskJson> getFullyPopulatedJson(List<SupervisionTask> supervisionTasks) {
    return SupervisionTaskMapper.maptoJson(supervisionTasks, idToUser(supervisionTasks));
  }

  private List<SupervisionWorkItemJson> toWorkItems(List<SupervisionTask> tasks) {
    Map<Integer, UserJson> userById = idToUser(tasks);
    Map<Integer, ApplicationJson> applicationById = idToApplication(tasks);
    return tasks.stream().map(task ->
        SupervisionTaskMapper.mapToWorkItem(
            task,
            applicationById.get(task.getApplicationId()),
            userById.get(task.getCreatorId()),
            userById.get(task.getOwnerId()))
    ).collect(Collectors.toList());
  }

  private Map<Integer, UserJson> idToUser(List<SupervisionTask> supervisionTasks) {
    return supervisionTasks.stream()
        .flatMap(st -> Stream.of(st.getCreatorId(), st.getOwnerId()))
        .filter(number -> number != null)
        .distinct()
        .collect(Collectors.toMap(id -> id, id -> userService.findUserById(id)));
  }

  private Map<Integer, ApplicationJson> idToApplication(List<SupervisionTask> tasks) {
    return tasks.stream()
        .map(SupervisionTask::getApplicationId)
        .distinct()
        .collect(Collectors.toMap(Function.identity(), appId -> applicationServiceComposer.findApplicationById(appId)));
  }

  public void updateSupervisionTaskDate(Integer applicationId, SupervisionTaskType taskType,
      ZonedDateTime date) {
    SupervisionTask task = findByApplicationIdAndType(applicationId, taskType);
    if (task != null) {
      task.setPlannedFinishingTime(date);
      update(task.getId(), task);
    }
  }

  private SupervisionTask findByApplicationIdAndType(int applicationId, SupervisionTaskType type) {
    SupervisionTask[] supervisionTasksResult = restTemplate.getForEntity(
        applicationProperties.getSupervisionTaskByApplicationIdAndTypeUrl(), SupervisionTask[].class, applicationId, type).getBody();
    if (supervisionTasksResult.length > 0) {
      return supervisionTasksResult[0];
    }
    return null;
  }


  public boolean hasSupervisionTask(Integer applicationId, SupervisionTaskType taskType) {
    return findByApplicationIdAndType(applicationId, taskType) != null;
  }

}
