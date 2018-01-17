package fi.hel.allu.model.controller;

import com.greghaskins.spectrum.Spectrum;
import com.querydsl.core.types.OrderSpecifier;

import fi.hel.allu.common.domain.SupervisionTaskSearchCriteria;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.SupervisionTaskDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.testUtils.SpeccyTestBase;
import fi.hel.allu.model.testUtils.WebTestCommon;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(Spectrum.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
public class SupervisionTaskDaoSpec extends SpeccyTestBase {

  @Autowired
  private WebTestCommon wtc;
  @Autowired
  private ApplicationDao applicationDao;
  @Autowired
  private SupervisionTaskDao supervisionTaskDao;

  private SupervisionTask existingSupervisionTask;
  private Application outdoorApp;
  private Application shortTermApp;
  private ZonedDateTime testTime = ZonedDateTime.now();

  {
    beforeEach(() -> {
      wtc.setup();
      outdoorApp = insertApplication(testCommon.dummyOutdoorApplication("existing", "Handlaaja"));
      existingSupervisionTask = supervisionTaskDao.insert(
          createTask(outdoorApp.getId(), SupervisionTaskType.SUPERVISION, outdoorApp.getOwner()));
      shortTermApp = insertApplication(testCommon.dummyBridgeBannerApplication("other", "otherUser"));
    });

    describe("Supervision task", () -> {
      context("Find", () -> {
        it("Find by id", () -> {
          Optional<SupervisionTask> st = supervisionTaskDao.findById(existingSupervisionTask.getId());
          assertTrue(st.isPresent());
          assertTaskEquals(existingSupervisionTask, st.get());
        });

        it("Find non-existent by id", () -> {
          Optional<SupervisionTask> st = supervisionTaskDao.findById(12345);
          assertFalse(st.isPresent());
        });

        it("Find by application id", () -> {
          List<SupervisionTask> supervisionTaskList = supervisionTaskDao.findByApplicationId(outdoorApp.getId());
          assertEquals(1, supervisionTaskList.size());
          assertTaskEquals(existingSupervisionTask, supervisionTaskList.get(0));
        });

        it("Find by missing application id", () -> {
          List<SupervisionTask> supervisionTaskList = supervisionTaskDao.findByApplicationId(12345);
          assertEquals(0, supervisionTaskList.size());
        });
      });

      it("Create", () -> {
        SupervisionTask newTask = createTask(outdoorApp.getId(), SupervisionTaskType.SUPERVISION, outdoorApp.getOwner());
        SupervisionTask created = supervisionTaskDao.insert(newTask);

        assertEquals(newTask.getType(), created.getType());
        assertEquals(newTask.getStatus(), created.getStatus());
        assertEquals(
            TimeUtil.dateToMillis(newTask.getPlannedFinishingTime()),
            TimeUtil.dateToMillis(created.getPlannedFinishingTime()));
      });

      it("Update", () -> {
        existingSupervisionTask.setResult("Testing was ok!");
        existingSupervisionTask.setStatus(SupervisionTaskStatusType.APPROVED);
        existingSupervisionTask.setActualFinishingTime(existingSupervisionTask.getPlannedFinishingTime().plusDays(1));
        SupervisionTask updatedST = supervisionTaskDao.update(existingSupervisionTask);
        assertEquals(existingSupervisionTask.getResult(), updatedST.getResult());
        assertEquals(existingSupervisionTask.getStatus(), updatedST.getStatus());
        assertEquals(
            TimeUtil.dateToMillis(existingSupervisionTask.getActualFinishingTime()), TimeUtil.dateToMillis(updatedST.getActualFinishingTime()));
      });

      it("Delete", () -> {
        supervisionTaskDao.delete(existingSupervisionTask.getId());
        Optional<SupervisionTask> deletedST = supervisionTaskDao.findById(existingSupervisionTask.getId());
        assertFalse(deletedST.isPresent());
      });

      context("Search", () -> {

        context("Sort order", () -> {
          final Sort sort = new Sort(new Order(Direction.ASC, "applicationId"),
              new Order(Direction.DESC, "actualFinishingTime"));

          it("Can convert to QueryDSL ordering", () -> {
            OrderSpecifier<?>[] orders = SupervisionTaskDao.toOrder(sort);
            assertEquals(2, orders.length);
          });

          it("Throws exception on invalid sort key", () -> {
            assertThrows(NoSuchEntityException.class).when(() -> SupervisionTaskDao.toOrder(new Sort("noSuchKey")));
          });

        });

        it("Find all when empty criteria", () -> {
          SupervisionTask taskForOther = createTask(shortTermApp.getId(), SupervisionTaskType.SUPERVISION, shortTermApp.getOwner());
          supervisionTaskDao.insert(taskForOther);

          List<SupervisionTask> result = supervisionTaskDao.search(new SupervisionTaskSearchCriteria());
          assertEquals(2, result.size());
        });

        it("Sort by creationTime when empty criteria", () -> {
          SupervisionTask taskForOther = createTask(shortTermApp.getId(), SupervisionTaskType.SUPERVISION,
              shortTermApp.getOwner());
          supervisionTaskDao.insert(taskForOther);

          Pageable pageRequest = new PageRequest(0, 10, new Sort(Direction.ASC, "creationTime"));
          List<SupervisionTask> result = supervisionTaskDao.search(new SupervisionTaskSearchCriteria(), pageRequest);

          assertEquals(2, result.size());
          assertFalse(result.get(0).getCreationTime().isAfter(result.get(1).getCreationTime()));

          pageRequest = new PageRequest(0, 10, new Sort(Direction.DESC, "creationTime"));
          result = supervisionTaskDao.search(new SupervisionTaskSearchCriteria(), pageRequest);

          assertEquals(2, result.size());
          assertFalse(result.get(1).getCreationTime().isAfter(result.get(0).getCreationTime()));
        });

        it("Sort by type when empty criteria", () -> {
          SupervisionTask taskForOther = createTask(shortTermApp.getId(), SupervisionTaskType.WARRANTY,
              shortTermApp.getOwner());
          supervisionTaskDao.insert(taskForOther);
          Pageable pageRequest = new PageRequest(0, 10, new Sort(Direction.ASC, "type"));
          List<SupervisionTask> result = supervisionTaskDao.search(new SupervisionTaskSearchCriteria(), pageRequest);

          assertEquals(2, result.size());
          // Warranty ("Takuuvalvonta") should precede Supervision ("Valvonta"):
          assertEquals(SupervisionTaskType.WARRANTY, result.get(0).getType());
          assertEquals(SupervisionTaskType.SUPERVISION, result.get(1).getType());
        });

        it("Sort by application type when empty criteria", () -> {
          SupervisionTask taskForOther = createTask(shortTermApp.getId(), SupervisionTaskType.WARRANTY,
              shortTermApp.getOwner());
          supervisionTaskDao.insert(taskForOther);
          Pageable pageRequest = new PageRequest(0, 10, new Sort(Direction.ASC, "application.type"));
          List<SupervisionTask> result = supervisionTaskDao.search(new SupervisionTaskSearchCriteria(), pageRequest);

          assertEquals(2, result.size());
          // Short term rental ("Lyhytaikainen maanvuokraus") should precede
          // Event ("Tapahtuma"):
          assertEquals(shortTermApp.getId(), result.get(0).getApplicationId());
          assertEquals(outdoorApp.getId(), result.get(1).getApplicationId());
        });

        it("Sort by application status when empty criteria", () -> {
          SupervisionTask taskForOther = createTask(shortTermApp.getId(), SupervisionTaskType.WARRANTY,
              shortTermApp.getOwner());
          supervisionTaskDao.insert(taskForOther);
          applicationDao.updateStatus(shortTermApp.getId(), StatusType.CANCELLED);
          applicationDao.updateStatus(outdoorApp.getId(), StatusType.HANDLING);
          Pageable pageRequest = new PageRequest(0, 10, new Sort(Direction.ASC, "application.status"));
          List<SupervisionTask> result = supervisionTaskDao.search(new SupervisionTaskSearchCriteria(), pageRequest);

          assertEquals(2, result.size());
          // HANDLING ("Käsittelyssä") should precede CANCELLED ("Peruttu"):
          assertEquals(outdoorApp.getId(), result.get(0).getApplicationId());
          assertEquals(shortTermApp.getId(), result.get(1).getApplicationId());
        });

        it("Find by application id", () -> {
          SupervisionTask taskForOther = createTask(shortTermApp.getId(), SupervisionTaskType.SUPERVISION, shortTermApp.getOwner());
          SupervisionTask inserted = supervisionTaskDao.insert(taskForOther);

          SupervisionTaskSearchCriteria search = new SupervisionTaskSearchCriteria();
          search.setApplicationId(shortTermApp.getApplicationId());
          List<SupervisionTask> result = supervisionTaskDao.search(search);
          assertEquals(1, result.size());
          assertTaskEquals(inserted, result.get(0));
        });

        it("Find by task type", () -> {
          SupervisionTask otherTask = createTask(outdoorApp.getId(), SupervisionTaskType.WARRANTY, outdoorApp.getOwner());
          supervisionTaskDao.insert(otherTask);

          SupervisionTaskSearchCriteria search = new SupervisionTaskSearchCriteria();
          search.setTaskTypes(Arrays.asList(SupervisionTaskType.WARRANTY));
          assertEquals(1, supervisionTaskDao.search(search).size());
          search.setTaskTypes(Arrays.asList(SupervisionTaskType.WARRANTY, SupervisionTaskType.SUPERVISION));
          assertEquals(2, supervisionTaskDao.search(search).size());
        });

        it("Find by application type", () -> {
          SupervisionTask taskForOther = createTask(shortTermApp.getId(), SupervisionTaskType.SUPERVISION, shortTermApp.getOwner());
          supervisionTaskDao.insert(taskForOther);

          SupervisionTaskSearchCriteria search = new SupervisionTaskSearchCriteria();
          search.setApplicationTypes(Arrays.asList(shortTermApp.getType()));
          assertEquals(1, supervisionTaskDao.search(search).size());
          search.setApplicationTypes(Arrays.asList(shortTermApp.getType(), outdoorApp.getType()));
          assertEquals(2, supervisionTaskDao.search(search).size());
        });

        it("Find by dates", () -> {
          SupervisionTask taskForOther = createTask(shortTermApp.getId(), SupervisionTaskType.SUPERVISION, shortTermApp.getOwner());
          taskForOther.setPlannedFinishingTime(ZonedDateTime.of(2017, 5, 5, 0, 0, 0, 0, ZoneId.systemDefault()));
          supervisionTaskDao.insert(taskForOther);

          SupervisionTaskSearchCriteria search = new SupervisionTaskSearchCriteria();
          search.setAfter(ZonedDateTime.of(2017, 5, 5, 0, 0, 0, 0, ZoneId.systemDefault()));
          assertEquals(2, supervisionTaskDao.search(search).size()); // added + existing
          search.setBefore(ZonedDateTime.of(2017, 5, 5, 0, 0, 0, 0, ZoneId.systemDefault()));
          assertEquals(1, supervisionTaskDao.search(search).size()); // added
          search.setAfter(null);
          assertEquals(1, supervisionTaskDao.search(search).size()); // added
        });

        it("Find by application status", () -> {
          SupervisionTask taskForOther = createTask(shortTermApp.getId(), SupervisionTaskType.SUPERVISION, shortTermApp.getOwner());
          supervisionTaskDao.insert(taskForOther);

          SupervisionTaskSearchCriteria search = new SupervisionTaskSearchCriteria();
          search.setApplicationStatus(Arrays.asList(shortTermApp.getStatus()));
          assertEquals(2, supervisionTaskDao.search(search).size());
        });

        context("Paging tests", () -> {
          beforeEach(() -> {
            for (int i = 0; i < 100; ++i) {
              SupervisionTask task_i = createTask(shortTermApp.getId(), SupervisionTaskType.SUPERVISION,
                  shortTermApp.getOwner());
              task_i.setDescription(String.format("00 - Task %03d", i));
              supervisionTaskDao.insert(task_i);
            }
          });

          it("Returns only 15 results when asked", () -> {
            SupervisionTaskSearchCriteria searchCriteria = new SupervisionTaskSearchCriteria();
            PageRequest pageRequest = new PageRequest(2, 15, Direction.ASC, "description");
            List<SupervisionTask> results = supervisionTaskDao.search(searchCriteria, pageRequest);
            assertEquals(15, results.size());
            for (int i = 0; i < 15; ++i) {
              assertEquals(String.format("00 - Task %03d", i + 30), results.get(i).getDescription());
            }
          });
        });
      });
    });
  }

  private void assertTaskEquals(SupervisionTask expected, SupervisionTask actual) {
    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getApplicationId(), actual.getApplicationId());
    assertEquals(expected.getType(), actual.getType());
    assertEquals(expected.getCreatorId(), actual.getCreatorId());
    assertEquals(expected.getHandlerId(), actual.getHandlerId());
    assertEquals(expected.getCreationTime(), actual.getCreationTime());
    assertEquals(expected.getPlannedFinishingTime(), actual.getPlannedFinishingTime());
    assertEquals(expected.getActualFinishingTime(), actual.getActualFinishingTime());
    assertEquals(expected.getStatus(), actual.getStatus());
    assertEquals(expected.getDescription(), actual.getDescription());
    assertEquals(expected.getResult(), actual.getResult());
  }

  private Application insertApplication(Application application) throws Exception {
    ResultActions resultActions = wtc.perform(post("/applications"), application).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Application.class);
  }

  private SupervisionTask createTask(Integer appId, SupervisionTaskType type, Integer handlerId) {
    return new SupervisionTask(
        null,
        appId,
        type,
        null,
        handlerId,
        testTime,
        testTime.plusDays(1),
        null,
        SupervisionTaskStatusType.OPEN,
        "just testing",
        null);
  }
}
