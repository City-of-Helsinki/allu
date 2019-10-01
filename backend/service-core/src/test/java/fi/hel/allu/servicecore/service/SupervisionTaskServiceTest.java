package fi.hel.allu.servicecore.service;

import java.lang.reflect.Array;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import fi.hel.allu.servicecore.event.ApplicationArchiveEvent;
import fi.hel.allu.servicecore.mapper.SupervisionTaskMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.event.ApplicationUpdateEvent;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
  private ApplicationEventPublisher applicationEventPublisher;
  @InjectMocks
  private SupervisionTaskService supervisionTaskService;

  @Before
  public void setup() {
    when(applicationProperties.getSupervisionTaskByIdUrl()).thenReturn("http://task/id");
    when(applicationProperties.getSupervisionTaskApproveUrl()).thenReturn("http://task/id/approve");
    when(applicationProperties.getSupervisionTaskRejectUrl()).thenReturn("http://task/id/reject");
    when(applicationProperties.getSupervisionTaskCreateUrl()).thenReturn("http://task");
    when(applicationProperties.getSupervisionTaskUpdateUrl()).thenReturn("http://task/id/update");
    when(userService.getCurrentUser()).thenReturn(new UserJson(12));
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
    verifyPublishedEvents(Arrays.asList(ApplicationUpdateEvent.class));
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
    verifyPublishedEvents(Arrays.asList(ApplicationUpdateEvent.class, ApplicationUpdateEvent.class));
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
    verifyPublishedEvents(Arrays.asList(ApplicationArchiveEvent.class, ApplicationUpdateEvent.class));
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
    verifyPublishedEvents(Arrays.asList(ApplicationArchiveEvent.class, ApplicationUpdateEvent.class));
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
    verifyPublishedEvents(Arrays.asList(ApplicationUpdateEvent.class));
  }

  private SupervisionTaskJson createTaskJson(SupervisionTaskType type) {
    SupervisionTaskJson taskJson = new SupervisionTaskJson();
    taskJson.setApplicationId(APPLICATION_ID);
    taskJson.setType(type);
    taskJson.setCreator(new UserJson());
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

  private void verifyPublishedEvents(List<Class> expectedEventsTypes) {
    ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
    verify(applicationEventPublisher, times(expectedEventsTypes.size())).publishEvent(eventCaptor.capture());
    List<Object> events = eventCaptor.getAllValues();
    assertEquals(expectedEventsTypes.size(), events.size());
    List<Class> actualEventTypes = events.stream()
      .map(event -> event.getClass())
      .collect(Collectors.toList());
    assertEquals(expectedEventsTypes, actualEventTypes);
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
