package fi.hel.allu.model.dao;

import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.testUtils.TestCommon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
class SupervisionTaskDaoTest {

    @Autowired
    private SupervisionTaskDao supervisionTaskDao;

    @Autowired
    TestCommon testCommon;

    private Integer applicationId;
    private Integer userId;
    SupervisionTask expectedTask;

    @BeforeEach
    void setUp() throws SQLException {
        testCommon.deleteAllData();
        applicationId = testCommon.insertApplication("Testihakemus", "Käsittelijä");
        userId = testCommon.insertUser("testuser").getId();
        expectedTask = new SupervisionTask();
        expectedTask.setApplicationId(applicationId);
        expectedTask.setType(SupervisionTaskType.FINAL_SUPERVISION);
        expectedTask.setCreatorId(userId);
        expectedTask.setOwnerId(userId);
        expectedTask.setPlannedFinishingTime(ZonedDateTime.now().plusDays(3));
        expectedTask.setStatus(SupervisionTaskStatusType.OPEN);
    }

    @Test
    void insertingTask(){
        SupervisionTask actualTask = supervisionTaskDao.insert(expectedTask);
        assertEquals(applicationId, actualTask.getApplicationId());
        assertNotNull(actualTask.getId());
    }

    @Test
    void findByApplication(){
        SupervisionTask createdTask = supervisionTaskDao.insert(expectedTask);
        List<SupervisionTask> actualTasks = supervisionTaskDao.findByApplicationId(createdTask.getApplicationId());
        assertEquals(1, actualTasks.size());
        assertEquals(applicationId, actualTasks.get(0).getApplicationId());
        assertNotNull(actualTasks.get(0).getId());
    }
}