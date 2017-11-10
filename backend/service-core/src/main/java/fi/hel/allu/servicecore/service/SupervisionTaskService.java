package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.SupervisionTaskSearchCriteria;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionWorkItemJson;
import fi.hel.allu.servicecore.mapper.SupervisionTaskMapper;
import fi.hel.allu.servicecore.util.PageRequestBuilder;
import fi.hel.allu.servicecore.util.RestResponsePage;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SupervisionTaskService {

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private UserService userService;
  private ApplicationServiceComposer applicationServiceComposer;

  @Autowired
  public SupervisionTaskService(ApplicationProperties applicationProperties, RestTemplate restTemplate,
                                UserService userService, ApplicationServiceComposer applicationServiceComposer) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.userService = userService;
    this.applicationServiceComposer = applicationServiceComposer;
  }


  public SupervisionTaskJson findById(int id) {
    ResponseEntity<SupervisionTask> supervisionTasksResult = restTemplate.getForEntity(
        applicationProperties.getSupervisionTaskByIdUrl(), SupervisionTask.class);
    return getFullyPopulatedJson(Collections.singletonList(supervisionTasksResult.getBody())).get(0);
  }

  public List<SupervisionTaskJson> findByApplicationId(int applicationId) {
    ResponseEntity<SupervisionTask[]> supervisionTasksResult = restTemplate.getForEntity(
        applicationProperties.getSupervisionTaskByApplicationIdUrl(), SupervisionTask[].class, applicationId);
    return getFullyPopulatedJson(Arrays.asList(supervisionTasksResult.getBody()));
  }

  public SupervisionTaskJson update(SupervisionTaskJson supervisionTask) {
    HttpEntity<SupervisionTask> supervisionTaskHttpEntity = new HttpEntity<>(SupervisionTaskMapper.mapToModel(supervisionTask));
    ResponseEntity<SupervisionTask> supervisionTasksResult = restTemplate.exchange(
        applicationProperties.getSupervisionTaskUpdateUrl(),
        HttpMethod.PUT,
        supervisionTaskHttpEntity,
        SupervisionTask.class,
        supervisionTask.getId());
    return getFullyPopulatedJson(Collections.singletonList(supervisionTasksResult.getBody())).get(0);
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
   * Updates handler for given supervision tasks.
   *
   * @param updatedHandler Handler to be set.
   * @param taskIds Supervision tasks to be updated.
   */
  public void updateHandler(int updatedHandler, List<Integer> taskIds) {
    restTemplate.put(applicationProperties.getSupervisionTaskHandlerUpdateUrl(), taskIds, updatedHandler);
  }

  /**
   * Removes handler from given supervision tasks.
   *
   * @param taskIds Supervision tasks to be updated.
   */
  public void removeHandler(List<Integer> taskIds) {
    restTemplate.put(applicationProperties.getSupervisionTaskHandlerRemoveUrl(), taskIds);
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
            userById.get(task.getHandlerId()))
    ).collect(Collectors.toList());
  }

  private Map<Integer, UserJson> idToUser(List<SupervisionTask> supervisionTasks) {
    return supervisionTasks.stream()
        .flatMap(st -> Stream.of(st.getCreatorId(), st.getHandlerId()))
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
}
