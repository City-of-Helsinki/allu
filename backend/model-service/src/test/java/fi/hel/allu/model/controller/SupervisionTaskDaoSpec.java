package fi.hel.allu.model.controller;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.SupervisionTaskDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.testUtils.SpeccyTestBase;
import fi.hel.allu.model.testUtils.WebTestCommon;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.*;
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
  private SupervisionTaskDao supervisionTaskDao;

  private SupervisionTask newSupervisionTask;
  private SupervisionTask testSupervisionTask;
  private Application testApplication;
  private ZonedDateTime testTime = ZonedDateTime.now();

  {
    beforeEach(() -> {
      wtc.setup();

      Application newApplication = testCommon.dummyOutdoorApplication("Test Application", "Handlaaja");
      ResultActions resultActions = wtc.perform(post("/applications"), newApplication).andExpect(status().isOk());
      testApplication = wtc.parseObjectFromResult(resultActions, Application.class);


      newSupervisionTask = new SupervisionTask(
          null,
          testApplication.getId(),
          SupervisionTaskType.SUPERVISION,
          null,
          null,
          testTime,
          testTime.plusDays(1),
          null,
          SupervisionTaskStatusType.OPEN,
          "just testing",
          null);
      testSupervisionTask = supervisionTaskDao.insert(newSupervisionTask);
    });

    describe("Supervision task", () -> {
      context("Find", () -> {
        it("Find by id", () -> {
          Optional<SupervisionTask> st = supervisionTaskDao.findById(testSupervisionTask.getId());
          assertTrue(st.isPresent());
        });
        it("Find non-existent by id", () -> {
          Optional<SupervisionTask> st = supervisionTaskDao.findById(12345);
          assertFalse(st.isPresent());
        });
        it("Find by application id", () -> {
          List<SupervisionTask> supervisionTaskList = supervisionTaskDao.findByApplicationId(testApplication.getId());
          assertEquals(1, supervisionTaskList.size());
        });
        it("Find by missing application id", () -> {
          List<SupervisionTask> supervisionTaskList = supervisionTaskDao.findByApplicationId(12345);
          assertEquals(0, supervisionTaskList.size());
        });
      });
      it("Create", () -> {
        assertEquals(newSupervisionTask.getType(), testSupervisionTask.getType());
        assertEquals(newSupervisionTask.getStatus(), testSupervisionTask.getStatus());
        assertEquals(
            TimeUtil.dateToMillis(newSupervisionTask.getPlannedFinishingTime()),
            TimeUtil.dateToMillis(testSupervisionTask.getPlannedFinishingTime()));
      });
      it("Update", () -> {
        testSupervisionTask.setResult("Testing was ok!");
        testSupervisionTask.setStatus(SupervisionTaskStatusType.APPROVED);
        testSupervisionTask.setActualFinishingTime(testSupervisionTask.getPlannedFinishingTime().plusDays(1));
        SupervisionTask updatedST = supervisionTaskDao.update(testSupervisionTask);
        assertEquals(testSupervisionTask.getResult(), updatedST.getResult());
        assertEquals(testSupervisionTask.getStatus(), updatedST.getStatus());
        assertEquals(
            TimeUtil.dateToMillis(testSupervisionTask.getActualFinishingTime()), TimeUtil.dateToMillis(updatedST.getActualFinishingTime()));
      });
      it("Delete", () -> {
        supervisionTaskDao.delete(testSupervisionTask.getId());
        Optional<SupervisionTask> deletedST = supervisionTaskDao.findById(testSupervisionTask.getId());
        assertFalse(deletedST.isPresent());
      });
    });

  }

}
