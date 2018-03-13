package fi.hel.allu.model.service;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.SupervisionTaskDao;
import fi.hel.allu.model.domain.ApplicationTag;
import fi.hel.allu.model.domain.SupervisionTask;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.ZonedDateTime;
import java.util.List;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(Spectrum.class)
public class SupervisionTaskServiceSpec {

  private SupervisionTaskService service;

  private SupervisionTaskDao supervisionTaskDao;
  private ApplicationDao applicationDao;

  {
    describe("SupervisionTaskService", () -> {
      beforeEach(() -> {
        supervisionTaskDao = mock(SupervisionTaskDao.class);
        applicationDao = mock(ApplicationDao.class);
        service = new SupervisionTaskService(supervisionTaskDao, applicationDao);
      });

      describe("Insert", () -> {
        final SupervisionTask task = createTask();

        beforeEach(() -> {
          when(supervisionTaskDao.insert(any(SupervisionTask.class)))
              .then(i -> i.getArgumentAt(0, SupervisionTask.class));
        });

        it("should insert given task", () -> {
          service.insert(task);
          Mockito.verify(supervisionTaskDao).insert(eq(task));
        });

        it("should add tag", () -> {
          service.insert(task);
          Mockito.verify(applicationDao).addTag(eq(task.getApplicationId()), any(ApplicationTag.class));
        });
      });

      describe("Approve", () -> {
        beforeEach(() -> {
          when(supervisionTaskDao.update(any(SupervisionTask.class)))
              .then(i -> i.getArgumentAt(0, SupervisionTask.class));
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
          Mockito.verify(applicationDao).addTag(eq(task.getApplicationId()), addedTagsCaptor.capture());
          assertEquals(task.getCreatorId(), addedTagsCaptor.getValue().getAddedBy());
          Mockito.verify(applicationDao, Mockito.times(2))
              .removeTagByType(eq(task.getApplicationId()), any(ApplicationTagType.class));
        });
      });

      describe("Reject", () -> {
        beforeEach(() -> {
          when(supervisionTaskDao.update(any(SupervisionTask.class)))
              .then(i -> i.getArgumentAt(0, SupervisionTask.class));
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
          Mockito.verify(applicationDao).addTag(eq(task.getApplicationId()), addedTagsCaptor.capture());
          assertEquals(task.getCreatorId(), addedTagsCaptor.getValue().getAddedBy());
          Mockito.verify(applicationDao, Mockito.times(1))
              .removeTagByType(eq(task.getApplicationId()), any(ApplicationTagType.class));
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
