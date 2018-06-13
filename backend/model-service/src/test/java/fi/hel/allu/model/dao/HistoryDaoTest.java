package fi.hel.allu.model.dao;

import fi.hel.allu.common.types.ChangeType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.FieldChange;
import fi.hel.allu.model.testUtils.TestCommon;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class HistoryDaoTest {

  @Autowired
  HistoryDao historyDao;

  @Autowired
  TestCommon testCommon;

  @Before
  public void setUp() throws Exception {
    testCommon.deleteAllData();
  }

  @Test
  public void testNoHistory() {
    List<ChangeHistoryItem> changes = historyDao.getApplicationHistory(991);
    assertEquals(0, changes.size());
  }

  @Test
  public void testWithHistory() {
    // Add application and user
    int applicationId = testCommon.insertApplication("Test Application", "Test Handler");
    int userId = testCommon.insertUser("Test User").getId();
    // Add some changes to the application
    historyDao.addApplicationChange(applicationId,
        new ChangeHistoryItem(userId, null, ChangeType.CREATED, null, ZonedDateTime.now(), null));
    historyDao.addApplicationChange(applicationId,
        new ChangeHistoryItem(userId, null, ChangeType.STATUS_CHANGED, StatusType.HANDLING.name(), ZonedDateTime.now(), null));
    historyDao.addApplicationChange(applicationId,
        new ChangeHistoryItem(userId, null, ChangeType.CONTENTS_CHANGED, null, ZonedDateTime.now(),
            Arrays.asList(new FieldChange("/foo", "oldFoo", "newFoo"),
                new FieldChange("/bar", "oldBar", "newBar"))));
    // Check that the changes are there
    List<ChangeHistoryItem> changes = historyDao.getApplicationHistory(applicationId);
    assertEquals(3, changes.size());
    assertEquals(ChangeType.CONTENTS_CHANGED, changes.get(0).getChangeType());
    List<FieldChange> fields = changes.get(0).getFieldChanges();
    assertEquals(2, fields.size());
    assertEquals(ChangeType.STATUS_CHANGED, changes.get(1).getChangeType());
    assertEquals(StatusType.HANDLING.name(), changes.get(1).getChangeSpecifier());
    assertEquals(ChangeType.CREATED, changes.get(2).getChangeType());
  }

}
