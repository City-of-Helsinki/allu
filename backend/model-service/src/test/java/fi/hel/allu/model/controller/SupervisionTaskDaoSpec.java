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