package fi.hel.allu.model.service;

import java.time.ZonedDateTime;

import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.SupervisionTaskDao;
import fi.hel.allu.model.domain.ApplicationTag;
import fi.hel.allu.model.domain.SupervisionTask;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Spectrum.class)
public class SupervisionTaskServiceSpec {

  private SupervisionTaskService service;

  private SupervisionTaskDao supervisionTaskDao;
  private ApplicationService applicationService;
  private LocationDao locationDao;

  {
    describe("SupervisionTaskService", () -> {
      beforeEach(() -> {
        supervisionTaskDao = mock(SupervisionTaskDao.class);
        applicationService = mock(ApplicationService.class);
        locationDao = mock(LocationDao.class);
        service = new SupervisionTaskService(supervisionTaskDao, locationDao, applicationService);
      });

      describe("Insert", () -> {
        final SupervisionTask task = createTask();

        beforeEach(() -> {
          when(supervisionTaskDao.insert(any(SupervisionTask.class)))
              .then(i -> i.getArgument(0));
        });

        it("should insert given task", () -> {
          service.insert(task);
          Mockito.verify(supervisionTaskDao).insert(eq(task));
        });

        it("should add tag", () -> {
          service.insert(task);
          Mockito.verify(applicationService).addTag(eq(task.getApplicationId()), any(ApplicationTag.class));
        });
      });

      describe("Approve", () -> {
        beforeEach(() -> {
          when(supervisionTaskDao.update(any(SupervisionTask.class)))
              .then(i -> i.getArgument(0));
        });

        it("should update approved task", () -> {
          SupervisionTask task = createTask();
          ArgumentCaptor<SupervisionTask> captor = ArgumentCaptor.forClass(SupervisionTask.class);
          service.approve(task);
          Mockito.verify(supervisionTaskDao).update(captor.capture());
          assertEquals(SupervisionTaskStatusType.APPROVED, captor.getValue().getStatus());
        });

        it("should update tags", () -> {
          SupervisionTask task = createTask();
          service.approve(task);
          ArgumentCaptor<ApplicationTag> addedTagsCaptor = ArgumentCaptor.forClass(ApplicationTag.class);
          Mockito.verify(applicationService).addTag(eq(task.getApplicationId()), addedTagsCaptor.capture());
          assertEquals(task.getCreatorId(), addedTagsCaptor.getValue().getAddedBy());
          Mockito.verify(applicationService, Mockito.times(1))
              .removeTagByTypes(eq(task.getApplicationId()), anyList());
        });
      });

      describe("Reject", () -> {
        beforeEach(() -> {
          when(supervisionTaskDao.update(any(SupervisionTask.class)))
              .then(i -> i.getArgument(0));
        });

        it("should update rejected task", () -> {
          SupervisionTask task = createTask();
          ArgumentCaptor<SupervisionTask> captor = ArgumentCaptor.forClass(SupervisionTask.class);
          service.reject(task, ZonedDateTime.now());
          Mockito.verify(supervisionTaskDao).update(captor.capture());
          assertEquals(SupervisionTaskStatusType.REJECTED, captor.getValue().getStatus());
        });

        it("should create new task based on rejected task", () -> {
          SupervisionTask task = createTask();
          task.setResult("result");
          ArgumentCaptor<SupervisionTask> captor = ArgumentCaptor.forClass(SupervisionTask.class);
          ZonedDateTime newTaskDate = ZonedDateTime.now().plusDays(10);
          service.reject(task, newTaskDate);
          Mockito.verify(supervisionTaskDao).insert(captor.capture());
          SupervisionTask newTask = captor.getValue();
          assertEquals(SupervisionTaskStatusType.OPEN, newTask.getStatus());
          assertEquals(newTaskDate, newTask.getPlannedFinishingTime());
          assertEquals(task.getResult(), newTask.getDescription());
        });

        it("should update tags", () -> {
          SupervisionTask task = createTask();
          service.reject(task, ZonedDateTime.now());
          ArgumentCaptor<ApplicationTag> addedTagsCaptor = ArgumentCaptor.forClass(ApplicationTag.class);
          Mockito.verify(applicationService).addTag(eq(task.getApplicationId()), addedTagsCaptor.capture());
          assertEquals(task.getCreatorId(), addedTagsCaptor.getValue().getAddedBy());
          Mockito.verify(applicationService, Mockito.times(1))
              .removeTagByTypes(eq(task.getApplicationId()), anyList());
        });
      });
    });
  }

  private SupervisionTask createTask() {
    SupervisionTask task = new SupervisionTask();
    task.setApplicationId(1);
    task.setType(SupervisionTaskType.SUPERVISION);
    task.setCreatorId(1);
    task.setOwnerId(1);
    task.setStatus(SupervisionTaskStatusType.OPEN);
    task.setPlannedFinishingTime(ZonedDateTime.now());
    task.setDescription("description");
    return task;
  }
}