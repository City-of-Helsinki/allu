package fi.hel.allu.model.controller;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import fi.hel.allu.model.domain.Location;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Variable;
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
import fi.hel.allu.model.domain.SupervisionWorkItem;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.testUtils.SpeccyTestBase;
import fi.hel.allu.model.testUtils.TestCommon;
import fi.hel.allu.model.testUtils.WebTestCommon;

import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.*;
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
  @Autowired
  private TestCommon testCommon;

  private User testUser;
  private SupervisionTask existingSupervisionTask;
  private Application outdoorApp;
  private Application shortTermApp;
  private ZonedDateTime testTime = ZonedDateTime.now();

  {
    beforeEach(() -> {
      wtc.setup();
      testUser = testCommon.insertUser("testuser");
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

      context("Update", () -> {
        it("Basic update", () -> {
          existingSupervisionTask.setResult("Testing was ok!");
          existingSupervisionTask.setStatus(SupervisionTaskStatusType.APPROVED);
          existingSupervisionTask.setActualFinishingTime(existingSupervisionTask.getPlannedFinishingTime().plusDays(1));
          SupervisionTask updatedST = supervisionTaskDao.update(existingSupervisionTask);
          assertEquals(existingSupervisionTask.getResult(), updatedST.getResult());
          assertEquals(existingSupervisionTask.getStatus(), updatedST.getStatus());
          assertEquals(
            TimeUtil.dateToMillis(existingSupervisionTask.getActualFinishingTime()), TimeUtil.dateToMillis(updatedST.getActualFinishingTime()));
        });

        it("should not update type", () -> {
          SupervisionTaskType originalType = existingSupervisionTask.getType();
          existingSupervisionTask.setResult("Testing was ok!");
          existingSupervisionTask.setType(SupervisionTaskType.FINAL_SUPERVISION);
          SupervisionTask updatedST = supervisionTaskDao.update(existingSupervisionTask);
          assertEquals(originalType, updatedST.getType());
        });
      });


      it("Delete", () -> {
        supervisionTaskDao.delete(existingSupervisionTask.getId());
        Optional<SupervisionTask> deletedST = supervisionTaskDao.findById(existingSupervisionTask.getId());
        assertFalse(deletedST.isPresent());
      });

      context("Search", () -> {

        context("Sort order", () -> {
          final Sort sort = Sort.by(Sort.Order.asc("applicationId"),
               Sort.Order.desc("actualFinishingTime"));
          it("Can convert to QueryDSL ordering", () -> {
            OrderSpecifier<?>[] orders = SupervisionTaskDao.toOrder(sort);
            assertEquals(2, orders.length);
          });

          it("Throws exception on invalid sort key", () -> {
            assertThrows(NoSuchEntityException.class).when(() -> SupervisionTaskDao.toOrder(Sort.by("noSuchKey")));
          });

        });

        it("Find all when empty criteria", () -> {
          SupervisionTask taskForOther = createTask(shortTermApp.getId(), SupervisionTaskType.SUPERVISION, shortTermApp.getOwner());
          supervisionTaskDao.insert(taskForOther);

          List<SupervisionWorkItem> result = supervisionTaskDao.search(new SupervisionTaskSearchCriteria(), PageRequest.of(0, 100)).getContent();
          assertEquals(2, result.size());
        });

        it("Sort by type when empty criteria", () -> {
          SupervisionTask taskForOther = createTask(shortTermApp.getId(), SupervisionTaskType.WARRANTY,
              shortTermApp.getOwner());
          supervisionTaskDao.insert(taskForOther);
          Pageable pageRequest = PageRequest.of(0, 10, Sort.by(Direction.ASC, "type"));
          Page<SupervisionWorkItem> result = supervisionTaskDao.search(new SupervisionTaskSearchCriteria(), pageRequest);

          assertEquals(2, result.getNumberOfElements());
          // Warranty ("Takuuvalvonta") should precede Supervision ("Valvonta"):
          assertEquals(SupervisionTaskType.WARRANTY, result.getContent().get(0).getType());
          assertEquals(SupervisionTaskType.SUPERVISION, result.getContent().get(1).getType());
        });

        it("Sort by application type when empty criteria", () -> {
          SupervisionTask taskForOther = createTask(shortTermApp.getId(), SupervisionTaskType.WARRANTY,
              shortTermApp.getOwner());
          supervisionTaskDao.insert(taskForOther);
          Pageable pageRequest = PageRequest.of(0, 10, Sort.by(Direction.ASC, "application.type"));
          Page<SupervisionWorkItem> result = supervisionTaskDao.search(new SupervisionTaskSearchCriteria(), pageRequest);

          assertEquals(2, result.getNumberOfElements());
          // Short term rental ("Lyhytaikainen maanvuokraus") should precede
          // Event ("Tapahtuma"):
          assertEquals(shortTermApp.getId(), result.getContent().get(0).getApplicationId());
          assertEquals(outdoorApp.getId(), result.getContent().get(1).getApplicationId());
        });

        it("Sort by application status when empty criteria", () -> {
          SupervisionTask taskForOther = createTask(shortTermApp.getId(), SupervisionTaskType.WARRANTY,
              shortTermApp.getOwner());
          supervisionTaskDao.insert(taskForOther);
          applicationDao.updateStatus(shortTermApp.getId(), StatusType.CANCELLED);
          applicationDao.updateStatus(outdoorApp.getId(), StatusType.HANDLING);
          Pageable pageRequest = PageRequest.of(0, 10, Sort.by(Direction.ASC, "application.status"));
          Page<SupervisionWorkItem> result = supervisionTaskDao.search(new SupervisionTaskSearchCriteria(), pageRequest);

          assertEquals(2, result.getNumberOfElements());
          // HANDLING ("Käsittelyssä") should precede CANCELLED ("Peruttu"):
          assertEquals(outdoorApp.getId(), result.getContent().get(0).getApplicationId());
          assertEquals(shortTermApp.getId(), result.getContent().get(1).getApplicationId());
        });

        it("Find by application id", () -> {
          SupervisionTask taskForOther = createTask(shortTermApp.getId(), SupervisionTaskType.SUPERVISION, shortTermApp.getOwner());
          supervisionTaskDao.insert(taskForOther);

          SupervisionTaskSearchCriteria search = new SupervisionTaskSearchCriteria();
          search.setApplicationId(shortTermApp.getApplicationId());
          List<SupervisionWorkItem> result = supervisionTaskDao.search(search, PageRequest.of(0, 100)).getContent();
          assertEquals(1, result.size());
        });

        it("Find by task type", () -> {
          SupervisionTask otherTask = createTask(outdoorApp.getId(), SupervisionTaskType.WARRANTY, outdoorApp.getOwner());
          supervisionTaskDao.insert(otherTask);

          SupervisionTaskSearchCriteria search = new SupervisionTaskSearchCriteria();
          search.setTaskTypes(Arrays.asList(SupervisionTaskType.WARRANTY));
          assertEquals(1, supervisionTaskDao.search(search, PageRequest.of(0, 100)).getTotalElements());
          search.setTaskTypes(Arrays.asList(SupervisionTaskType.WARRANTY, SupervisionTaskType.SUPERVISION));
          assertEquals(2, supervisionTaskDao.search(search, PageRequest.of(0, 100)).getTotalElements());
        });

        it("Find by application type", () -> {
          SupervisionTask taskForOther = createTask(shortTermApp.getId(), SupervisionTaskType.SUPERVISION, shortTermApp.getOwner());
          supervisionTaskDao.insert(taskForOther);

          SupervisionTaskSearchCriteria search = new SupervisionTaskSearchCriteria();
          search.setApplicationTypes(Arrays.asList(shortTermApp.getType()));
          assertEquals(1, supervisionTaskDao.search(search, PageRequest.of(0, 100)).getTotalElements());
          search.setApplicationTypes(Arrays.asList(shortTermApp.getType(), outdoorApp.getType()));
          assertEquals(2, supervisionTaskDao.search(search, PageRequest.of(0, 100)).getTotalElements());
        });

        it("Find by dates", () -> {
          SupervisionTask taskForOther = createTask(shortTermApp.getId(), SupervisionTaskType.SUPERVISION, shortTermApp.getOwner());
          taskForOther.setPlannedFinishingTime(ZonedDateTime.of(2017, 5, 5, 0, 0, 0, 0, ZoneId.systemDefault()));
          supervisionTaskDao.insert(taskForOther);

          SupervisionTaskSearchCriteria search = new SupervisionTaskSearchCriteria();
          search.setAfter(ZonedDateTime.of(2017, 5, 5, 0, 0, 0, 0, ZoneId.systemDefault()));
          assertEquals(2, supervisionTaskDao.search(search, PageRequest.of(0, 100)).getTotalElements()); // added + existing
          search.setBefore(ZonedDateTime.of(2017, 5, 5, 0, 0, 0, 0, ZoneId.systemDefault()));
          assertEquals(1, supervisionTaskDao.search(search, PageRequest.of(0, 100)).getTotalElements()); // added
          search.setAfter(null);
          assertEquals(1, supervisionTaskDao.search(search, PageRequest.of(0, 100)).getTotalElements()); // added
        });

        it("Find by application status", () -> {
          SupervisionTask taskForOther = createTask(shortTermApp.getId(), SupervisionTaskType.SUPERVISION, shortTermApp.getOwner());
          supervisionTaskDao.insert(taskForOther);

          SupervisionTaskSearchCriteria search = new SupervisionTaskSearchCriteria();
          search.setApplicationStatus(Arrays.asList(shortTermApp.getStatus()));
          assertEquals(2, supervisionTaskDao.search(search, PageRequest.of(0, 100)).getTotalElements());
        });

        it("Find by city district", () -> {
          Application app = insertApplication(testCommon.dummyOutdoorApplicationWithLocation("event", "owner"));
          Location location = app.getLocations().get(0);

          SupervisionTask task = createTask(app.getId(), SupervisionTaskType.SUPERVISION, app.getOwner());
          task.setLocationId(location.getId());
          supervisionTaskDao.insert(task);

          SupervisionTaskSearchCriteria location1Search = new SupervisionTaskSearchCriteria();
          location1Search.setCityDistrictIds(Arrays.asList(location.getCityDistrictId()));
          assertEquals(1, supervisionTaskDao.search(location1Search, PageRequest.of(0, 100)).getTotalElements());
        });

        it("Find by city district override", () -> {
          Application app = testCommon.dummyOutdoorApplicationWithLocation("event", "owner");
          app.getLocations().get(0).setCityDistrictIdOverride(2);
          app = insertApplication(app);
          Location location = app.getLocations().get(0);

          SupervisionTask task = createTask(app.getId(), SupervisionTaskType.SUPERVISION, app.getOwner());
          task.setLocationId(location.getId());
          supervisionTaskDao.insert(task);

          SupervisionTaskSearchCriteria location1Search = new SupervisionTaskSearchCriteria();
          location1Search.setCityDistrictIds(Arrays.asList(location.getCityDistrictIdOverride()));
          assertEquals(1, supervisionTaskDao.search(location1Search, PageRequest.of(0, 100)).getTotalElements());
        });

        it("Find only by specified city districts", () -> {
          Application app1 = insertApplication(testCommon.dummyOutdoorApplicationWithLocation("event1", "owner1"));
          Location location1 = app1.getLocations().get(0);

          SupervisionTask app1Task1 = createTask(app1.getId(), SupervisionTaskType.SUPERVISION, app1.getOwner());
          app1Task1.setLocationId(location1.getId());
          SupervisionTask app1Task2 = createTask(app1.getId(), SupervisionTaskType.SUPERVISION, app1.getOwner());

          supervisionTaskDao.insert(app1Task1);
          supervisionTaskDao.insert(app1Task2);

          Application app2 = testCommon.dummyOutdoorApplicationWithLocation("event2", "owner2");
          app2.getLocations().get(0).setCityDistrictIdOverride(2);
          app2 = insertApplication(app2);
          Location location2 = app2.getLocations().get(0);

          SupervisionTask app2Task = createTask(app2.getId(), SupervisionTaskType.SUPERVISION, app2.getOwner());

          supervisionTaskDao.insert(app2Task);

          SupervisionTaskSearchCriteria location1Search = new SupervisionTaskSearchCriteria();
          location1Search.setCityDistrictIds(Arrays.asList(location1.getCityDistrictId()));
          assertEquals("Expected to find both tasks", 2, supervisionTaskDao.search(location1Search, PageRequest.of(0, 100)).getTotalElements());

          SupervisionTaskSearchCriteria location2Search = new SupervisionTaskSearchCriteria();
          location2Search.setCityDistrictIds(Arrays.asList(location2.getCityDistrictIdOverride()));
          assertEquals("Expected to find single task", 1, supervisionTaskDao.search(location2Search, PageRequest.of(0, 100)).getTotalElements());
        });

        context("Paging tests", () -> {
          final Variable<Page<SupervisionWorkItem>> results = new Variable<>();

          beforeEach(() -> {
            for (int i = 0; i < 100; ++i) {
              SupervisionTask task_i = createTask(shortTermApp.getId(), SupervisionTaskType.SUPERVISION,
                  shortTermApp.getOwner());
              task_i.setDescription(String.format("00 - Task %03d", i));
              supervisionTaskDao.insert(task_i);
            }
            SupervisionTaskSearchCriteria searchCriteria = new SupervisionTaskSearchCriteria();
            PageRequest pageRequest = PageRequest.of(2, 15, Direction.ASC, "type");
            results.set(supervisionTaskDao.search(searchCriteria, pageRequest));
          });

          it("Returns only 15 results when asked", () -> {
            assertEquals(15, results.get().getNumberOfElements());
          });

          it("Returns the correct total amount of elements", () -> {
            // 1 inserted "shortTermApp" + 100 inserted "task_i" = 101
            assertEquals(101, results.get().getTotalElements());
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
    assertEquals(expected.getOwnerId(), actual.getOwnerId());
    assertEquals(expected.getCreationTime(), actual.getCreationTime());
    assertEquals(expected.getPlannedFinishingTime(), actual.getPlannedFinishingTime());
    assertEquals(expected.getActualFinishingTime(), actual.getActualFinishingTime());
    assertEquals(expected.getStatus(), actual.getStatus());
    assertEquals(expected.getDescription(), actual.getDescription());
    assertEquals(expected.getResult(), actual.getResult());
  }

  private Application insertApplication(Application application) throws Exception {
    ResultActions resultActions = wtc.perform(post("/applications?userId=" + testUser.getId()), application).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Application.class);
  }

  private SupervisionTask createTask(Integer appId, SupervisionTaskType type, Integer ownerId) {
    return new SupervisionTask(
        null,
        appId,
        type,
        null,
        ownerId,
        testTime,
        testTime.plusDays(1),
        null,
        SupervisionTaskStatusType.OPEN,
        "just testing",
        null,
        null);
  }
}
