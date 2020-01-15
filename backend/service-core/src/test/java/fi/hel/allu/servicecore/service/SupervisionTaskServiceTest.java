package fi.hel.allu.servicecore.service;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.types.ApplicationNotificationType;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.event.ApplicationArchiveEvent;
import fi.hel.allu.servicecore.event.ApplicationEventDispatcher;
import fi.hel.allu.servicecore.mapper.SupervisionTaskMapper;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SupervisionTaskServiceTest {

  private static final int APPLICATION_ID = 17;
  private static final int USER_ID = 18;

  @Mock
  private ApplicationHistoryService historyService;
  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private RestTemplate restTemplate;
  @Mock
  private UserService userService;
  @Mock
  private ApplicationServiceComposer applicationServiceComposer;
  @Mock
  private ApplicationEventPublisher archiveEventPublisher;
  @Mock
  private ApplicationEventDispatcher eventDispatcher;

  @InjectMocks
  private SupervisionTaskService supervisionTaskService;

  @Before
  public void setup() {
    when(applicationProperties.getSupervisionTaskByIdUrl()).thenReturn("http://task/id");
    when(applicationProperties.getSupervisionTaskApproveUrl()).thenReturn("http://task/id/approve");
    when(applicationProperties.getSupervisionTaskRejectUrl()).thenReturn("http://task/id/reject");
    when(applicationProperties.getSupervisionTaskCreateUrl()).thenReturn("http://task");
    when(applicationProperties.getSupervisionTaskUpdateUrl()).thenReturn("http://task/id/update");
    when(userService.getCurrentUser()).thenReturn(new UserJson(USER_ID));
    when(userService.findUserById(USER_ID)).thenReturn(new UserJson(USER_ID));
  }

  @Test
  public void shouldAddHistoryWhenAdded() {
    setTaskCreationResult(SupervisionTaskType.FINAL_SUPERVISION);
    SupervisionTaskJson taskJson = createTaskJson(SupervisionTaskType.FINAL_SUPERVISION);
    supervisionTaskService.insert(taskJson);
    verify(historyService, times(1)).addSupervisionAdded(APPLICATION_ID, taskJson.getType());
  }

  @Test
  public void shouldPublishApplicationEventWhenAdded() {
    setTaskCreationResult(SupervisionTaskType.FINAL_SUPERVISION);
    SupervisionTaskJson taskJson = createTaskJson(SupervisionTaskType.FINAL_SUPERVISION);
    supervisionTaskService.insert(taskJson);
    verifyApplicationEventDispatched(ApplicationNotificationType.SUPERVISION_ADDED);
  }

  @Test
  public void shouldAddHistoryWhenUpdated() {
    setTaskCreationResult(SupervisionTaskType.FINAL_SUPERVISION);
    SupervisionTaskJson taskJson = createTaskJson(SupervisionTaskType.FINAL_SUPERVISION);
    SupervisionTaskJson updatedTask = supervisionTaskService.insert(taskJson);
    onUpdate(SupervisionTaskMapper.mapToModel(updatedTask));
    supervisionTaskService.update(updatedTask);
    verify(historyService, times(1)).addSupervisionUpdated(APPLICATION_ID, updatedTask.getType());
  }

  @Test
  public void shouldPublishApplicationEventWhenUpdated() {
    setTaskCreationResult(SupervisionTaskType.FINAL_SUPERVISION);
    SupervisionTaskJson taskJson = createTaskJson(SupervisionTaskType.FINAL_SUPERVISION);
    SupervisionTaskJson updatedTask = supervisionTaskService.insert(taskJson);
    onUpdate(SupervisionTaskMapper.mapToModel(updatedTask));
    supervisionTaskService.update(updatedTask);
    // Publish one for insert, one for update
    verifyApplicationEventDispatched(ApplicationNotificationType.SUPERVISION_ADDED, ApplicationNotificationType.SUPERVISION_UPDATED);
  }

  @Test
  public void shouldAddHistoryWhenRemoved() {
    setTaskSearchResult(SupervisionTaskType.OPERATIONAL_CONDITION);
    supervisionTaskService.delete(1);
    verify(historyService, times(1)).addSupervisionRemoved(APPLICATION_ID, SupervisionTaskType.OPERATIONAL_CONDITION);
  }

  @Test
  public void shouldPublishApplicationEventWhenRemoved() {
    setTaskSearchResult(SupervisionTaskType.OPERATIONAL_CONDITION);
    supervisionTaskService.delete(1);
    verify(archiveEventPublisher, times(1)).publishEvent(any(ApplicationArchiveEvent.class));
    verifyApplicationEventDispatched(ApplicationNotificationType.SUPERVISION_REMOVED);
  }

  @Test
  public void shouldAddHistoryWhenApproved() {
    setTaskApprovedResult(SupervisionTaskType.WARRANTY);
    supervisionTaskService.approve(createTaskJson(SupervisionTaskType.WARRANTY));
    verify(historyService, times(1)).addSupervisionApproved(APPLICATION_ID, SupervisionTaskType.WARRANTY);
  }

  @Test
  public void shouldPublishApplicationEventWhenApproved() {
    setTaskApprovedResult(SupervisionTaskType.WARRANTY);
    supervisionTaskService.approve(createTaskJson(SupervisionTaskType.WARRANTY));
    verify(archiveEventPublisher, times(1)).publishEvent(any(ApplicationArchiveEvent.class));
    verifyApplicationEventDispatched(ApplicationNotificationType.SUPERVISION_APPROVED);
  }

  @Test
  public void shouldAddHistoryWhenRejected() {
    setTaskRejectedResult(SupervisionTaskType.WARRANTY);
    supervisionTaskService.reject(createTaskJson(SupervisionTaskType.WARRANTY), ZonedDateTime.now());
    verify(historyService, times(1)).addSupervisionRejected(APPLICATION_ID, SupervisionTaskType.WARRANTY);
  }

  @Test
  public void shouldPublishApplicationEventWhenRejected() {
    setTaskRejectedResult(SupervisionTaskType.WARRANTY);
    supervisionTaskService.reject(createTaskJson(SupervisionTaskType.WARRANTY), ZonedDateTime.now());
    verifyApplicationEventDispatched(ApplicationNotificationType.SUPERVISION_REJECTED);
  }

  private SupervisionTaskJson createTaskJson(SupervisionTaskType type) {
    SupervisionTaskJson taskJson = new SupervisionTaskJson();
    taskJson.setId(99);
    taskJson.setApplicationId(APPLICATION_ID);
    taskJson.setType(type);
    taskJson.setCreator(new UserJson(USER_ID));
    return taskJson;
  }

  private void setTaskSearchResult(SupervisionTaskType type) {
    SupervisionTask task = createTask(type);
    when(restTemplate.getForEntity(
        eq(applicationProperties.getSupervisionTaskByIdUrl()),
        eq(SupervisionTask.class),
        any(Integer.class)
        )).thenReturn(ResponseEntity.ok(task));
  }

  private void setTaskCreationResult(SupervisionTaskType type) {
    SupervisionTask task = createTask(type);
    when(restTemplate.postForEntity(
        eq(applicationProperties.getSupervisionTaskCreateUrl()),
        any(SupervisionTask.class),
        eq(SupervisionTask.class))).thenReturn(ResponseEntity.ok(task));
  }

  private void setTaskApprovedResult(SupervisionTaskType type) {
    SupervisionTask task = createTask(type);
    when(restTemplate.exchange(
        eq(applicationProperties.getSupervisionTaskApproveUrl()),
        eq(HttpMethod.PUT),
        any(HttpEntity.class),
        eq(SupervisionTask.class),
        any(Integer.class)
        )).thenReturn(ResponseEntity.ok(task));
  }

  private void setTaskRejectedResult(SupervisionTaskType type) {
    SupervisionTask task = createTask(type);
    when(restTemplate.exchange(
        any(URI.class),
        eq(HttpMethod.PUT),
        any(HttpEntity.class),
        eq(SupervisionTask.class)
        )).thenReturn(ResponseEntity.ok(task));
  }

  private SupervisionTask createTask(SupervisionTaskType type) {
    SupervisionTask task = new SupervisionTask();
    task.setApplicationId(APPLICATION_ID);
    task.setType(type);
    task.setCreatorId(USER_ID);
    return task;
  }

  private void verifyApplicationEventDispatched(ApplicationNotificationType... types) {
    Stream.of(types).forEach(type ->
        verify(eventDispatcher, times(1)).dispatchUpdateEvent(anyInt(), anyInt(), eq(type), anyString()));
  }

  private void onUpdate(SupervisionTask task) {
    when(restTemplate.exchange(
      eq("http://task/id/update"),
      eq(HttpMethod.PUT),
      any(HttpEntity.class),
      eq(SupervisionTask.class),
      eq(task.getId())
    )).thenReturn(ResponseEntity.ok(task));
  }
}
