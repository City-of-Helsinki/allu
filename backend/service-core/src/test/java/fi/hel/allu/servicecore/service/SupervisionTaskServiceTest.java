package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.types.ApplicationNotificationType;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.domain.SupervisionWorkItem;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.event.ApplicationArchiveEvent;
import fi.hel.allu.servicecore.event.ApplicationEventDispatcher;
import fi.hel.allu.servicecore.mapper.SupervisionTaskMapper;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupervisionTaskServiceTest {

    private static final int ID = 99;
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
    @Mock
    private SearchService searchService;
    @InjectMocks
    private SupervisionTaskService supervisionTaskService;

    @BeforeEach
    public void setup() {
        lenient().when(applicationProperties.getSupervisionTaskByIdUrl()).thenReturn("http://task/id");
        lenient().when(applicationProperties.getSupervisionTaskApproveUrl()).thenReturn("http://task/id/approve");
        lenient().when(applicationProperties.getSupervisionTaskRejectUrl()).thenReturn("http://task/id/reject");
        lenient().when(applicationProperties.getSupervisionTaskCreateUrl()).thenReturn("http://task");
        lenient().when(applicationProperties.getSupervisionTaskUpdateUrl()).thenReturn("http://task/id/update");
        lenient().when(userService.getCurrentUser()).thenReturn(new UserJson(USER_ID));
        lenient().when(userService.findUserById(USER_ID)).thenReturn(new UserJson(USER_ID));
    }

    @Test
    void shouldAddHistoryWhenAdded() {
        setTaskCreationResult();
        setTaskSearchInsert();
        SupervisionTaskJson taskJson = createTaskJson(SupervisionTaskType.FINAL_SUPERVISION);
        supervisionTaskService.insert(taskJson);
        verify(historyService, times(1)).addSupervisionAdded(APPLICATION_ID, taskJson.getType());
    }

    @Test
    void shouldPublishApplicationEventWhenAdded() {
        setTaskCreationResult();
        setTaskSearchInsert();
        SupervisionTaskJson taskJson = createTaskJson(SupervisionTaskType.FINAL_SUPERVISION);
        supervisionTaskService.insert(taskJson);
        verifyApplicationEventDispatched(ApplicationNotificationType.SUPERVISION_ADDED);
    }

    @Test
    void shouldAddHistoryWhenUpdated() {
        setTaskCreationResult();
        setTaskSearchInsert();
        SupervisionTaskJson taskJson = createTaskJson(SupervisionTaskType.FINAL_SUPERVISION);
        SupervisionTaskJson updatedTask = supervisionTaskService.insert(taskJson);
        onUpdate(SupervisionTaskMapper.mapToModel(updatedTask));
        supervisionTaskService.update(updatedTask);
        verify(historyService, times(1)).addSupervisionUpdated(APPLICATION_ID, updatedTask.getType());
    }

    @Test
    void shouldPublishApplicationEventWhenUpdated() {
        setTaskCreationResult();
        setTaskSearchInsert();
        SupervisionTaskJson taskJson = createTaskJson(SupervisionTaskType.FINAL_SUPERVISION);
        SupervisionTaskJson updatedTask = supervisionTaskService.insert(taskJson);
        onUpdate(SupervisionTaskMapper.mapToModel(updatedTask));
        supervisionTaskService.update(updatedTask);
        // Publish one for insert, one for update
        verifyApplicationEventDispatched(ApplicationNotificationType.SUPERVISION_ADDED,
                                         ApplicationNotificationType.SUPERVISION_UPDATED);
    }


    @Test
    void shouldUpdateElasticsearchWhenUpdated() {
        setTaskCreationResult();
        SupervisionTaskJson taskJson = createTaskJson(SupervisionTaskType.FINAL_SUPERVISION);
        when(applicationProperties.getSupervisionTaskGetWorkItemUrl()).thenReturn("http://task/elastic/update");
        SupervisionWorkItem supervisionWorkItem = new SupervisionWorkItem();
        supervisionWorkItem.setId(taskJson.getId());
        onUpdate(createTask(SupervisionTaskType.FINAL_SUPERVISION));
        onSupervisionWorkItem(supervisionWorkItem);
        supervisionTaskService.update(taskJson);
        verify(searchService, times(1)).updateSupervisionTasks(supervisionWorkItem);
    }

    @Test
    void shouldAddHistoryWhenRemoved() {
        setTaskSearchResult();
        supervisionTaskService.delete(1);
        verify(historyService, times(1)).addSupervisionRemoved(APPLICATION_ID,
                                                               SupervisionTaskType.OPERATIONAL_CONDITION);
    }

    @Test
    void shouldPublishApplicationEventWhenRemoved() {
        setTaskSearchResult();
        supervisionTaskService.delete(1);
        verify(archiveEventPublisher, times(1)).publishEvent(any(ApplicationArchiveEvent.class));
        verifyApplicationEventDispatched(ApplicationNotificationType.SUPERVISION_REMOVED);
    }

    @Test
    void shouldUpdateElasticsearchWhenRemoved() {
        setTaskSearchResult();
        supervisionTaskService.delete(1);
        verify(searchService, times(1)).deleteSupervisionTask(1);
    }

    @Test
    void shouldAddHistoryWhenApproved() {
        setTaskApprovedResult(SupervisionTaskType.WARRANTY);
        ApplicationJson applicationJson = new ApplicationJson();
        applicationJson.setStatus(StatusType.DECISION);
        when(applicationServiceComposer.findApplicationById(anyInt())).thenReturn(applicationJson);
        supervisionTaskService.approve(createTaskJson(SupervisionTaskType.WARRANTY));
        verify(historyService, times(1)).addSupervisionApproved(APPLICATION_ID, SupervisionTaskType.WARRANTY);
    }

    @Test
    void shouldPublishApplicationEventWhenApproved() {
        setTaskApprovedResult(SupervisionTaskType.WARRANTY);
        ApplicationJson applicationJson = new ApplicationJson();
        applicationJson.setStatus(StatusType.DECISION);
        when(applicationServiceComposer.findApplicationById(anyInt())).thenReturn(applicationJson);
        supervisionTaskService.approve(createTaskJson(SupervisionTaskType.WARRANTY));
        verify(archiveEventPublisher, times(1)).publishEvent(any(ApplicationArchiveEvent.class));
        verifyApplicationEventDispatched(ApplicationNotificationType.SUPERVISION_APPROVED);
    }

    @Test
    void shouldNotAddHistoryWhenApprovalIsNotAllowed() {
        setTaskApprovedResult(SupervisionTaskType.FINAL_SUPERVISION);
        ApplicationJson applicationJson = new ApplicationJson();
        applicationJson.setStatus(StatusType.HANDLING);
        when(applicationServiceComposer.findApplicationById(anyInt())).thenReturn(applicationJson);
        try {
            supervisionTaskService.approve(createTaskJson(SupervisionTaskType.FINAL_SUPERVISION));
        } catch (IllegalOperationException exception) {
            assertEquals("application.replaced.notAllowed", exception.getMessage());
        }
        verify(historyService, times(0)).addSupervisionApproved(APPLICATION_ID, SupervisionTaskType.FINAL_SUPERVISION);
    }

    @Test
    void shouldNotPublishApplicationEventWhenApprovalIsNotAllowed() {
        setTaskApprovedResult(SupervisionTaskType.FINAL_SUPERVISION);
        ApplicationJson applicationJson = new ApplicationJson();
        applicationJson.setStatus(StatusType.HANDLING);
        lenient().when(applicationServiceComposer.findApplicationById(anyInt())).thenReturn(applicationJson);
        try {
            supervisionTaskService.approve(createTaskJson(SupervisionTaskType.FINAL_SUPERVISION));
        } catch (IllegalOperationException exception) {
            assertEquals("application.replaced.notAllowed", exception.getMessage());
        }
        verify(archiveEventPublisher, times(0)).publishEvent(any(ApplicationArchiveEvent.class));
    }

    @Test
    void shouldAddHistoryWhenRejected() {
        setTaskRejectedResult();
        supervisionTaskService.reject(createTaskJson(SupervisionTaskType.WARRANTY), ZonedDateTime.now());
        verify(historyService, times(1)).addSupervisionRejected(APPLICATION_ID, SupervisionTaskType.WARRANTY);
    }

    @Test
    void shouldPublishApplicationEventWhenRejected() {
        setTaskRejectedResult();
        supervisionTaskService.reject(createTaskJson(SupervisionTaskType.WARRANTY), ZonedDateTime.now());
        verifyApplicationEventDispatched(ApplicationNotificationType.SUPERVISION_REJECTED);
    }

    private SupervisionTaskJson createTaskJson(SupervisionTaskType type) {
        SupervisionTaskJson taskJson = new SupervisionTaskJson();
        taskJson.setId(ID);
        taskJson.setApplicationId(APPLICATION_ID);
        taskJson.setType(type);
        taskJson.setCreator(new UserJson(USER_ID));
        return taskJson;
    }

    private void setTaskSearchInsert() {
        lenient().when(restTemplate.getForEntity(
                eq(applicationProperties.getSupervisionTaskGetWorkItemUrl()),
                eq(SupervisionWorkItem.class),
                any(Integer.class)
        )).thenReturn(ResponseEntity.ok(new SupervisionWorkItem()));
    }

    private void setTaskSearchResult() {
        SupervisionTask task = createTask(SupervisionTaskType.OPERATIONAL_CONDITION);
        lenient().when(restTemplate.getForEntity(
                eq(applicationProperties.getSupervisionTaskByIdUrl()),
                eq(SupervisionTask.class),
                any(Integer.class)
        )).thenReturn(ResponseEntity.ok(task));
    }

    private void setTaskCreationResult() {
        SupervisionTask task = createTask(SupervisionTaskType.FINAL_SUPERVISION);
        lenient().when(restTemplate.postForEntity(
                eq(applicationProperties.getSupervisionTaskCreateUrl()),
                any(SupervisionTask.class),
                eq(SupervisionTask.class))).thenReturn(ResponseEntity.ok(task));
    }

    private void setTaskApprovedResult(SupervisionTaskType type) {
        SupervisionTask task = createTask(type);
        lenient().when(restTemplate.exchange(
                eq(applicationProperties.getSupervisionTaskApproveUrl()),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(SupervisionTask.class),
                any(Integer.class)
        )).thenReturn(ResponseEntity.ok(task));
    }

    private void setTaskRejectedResult() {
        SupervisionTask task = createTask(SupervisionTaskType.WARRANTY);
        lenient().when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(SupervisionTask.class)
        )).thenReturn(ResponseEntity.ok(task));
    }

    private SupervisionTask createTask(SupervisionTaskType type) {
        SupervisionTask task = new SupervisionTask();
        task.setId(ID);
        task.setApplicationId(APPLICATION_ID);
        task.setType(type);
        task.setCreatorId(USER_ID);
        return task;
    }

    private void verifyApplicationEventDispatched(ApplicationNotificationType... types) {
        Stream.of(types).forEach(type ->
                                         verify(eventDispatcher, times(1)).dispatchUpdateEvent(anyInt(), anyInt(),
                                                                                               eq(type), anyString()));
    }

    private void onUpdate(SupervisionTask task) {
        lenient().when(restTemplate.exchange(
                eq("http://task/id/update"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(SupervisionTask.class),
                eq(task.getId())
        )).thenReturn(ResponseEntity.ok(task));
    }

    private void onSupervisionWorkItem(SupervisionWorkItem supervisionWorkItem) {
        lenient().when(restTemplate.getForEntity(
                eq("http://task/elastic/update"),
                eq(SupervisionWorkItem.class),
                eq(supervisionWorkItem.getId())
        )).thenReturn(ResponseEntity.ok(supervisionWorkItem));
    }
}