package fi.hel.allu.model.dao;

import fi.hel.allu.common.types.ChangeType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.FieldChange;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.service.ApplicationService;
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
  UserDao userDao;

  @Autowired
  TestCommon testCommon;
  @Autowired
  private ApplicationService applicationService;

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

  @Test
  public void testHistoryAnonymization() {
    int app1 = testCommon.insertApplication("Test Application1", "Test Handler1");
    int app2 = testCommon.insertApplication("Test Application2", "Test Handler2");
    int userId = testCommon.insertUser("Test User").getId();
    testCommon.insertUser("alluanon");
    User anonUser = userDao.findAnonymizationUser();

    List<FieldChange> changeList = Arrays.asList(
      new FieldChange("/foo", "oldFoo", "newFoo"),
      new FieldChange("/bar", "oldBar", "newBar")
    );

    historyDao.addApplicationChange(app1,
      new ChangeHistoryItem(userId, null, ChangeType.CONTENTS_CHANGED, null, ZonedDateTime.now(),
        changeList));
    historyDao.addApplicationChange(app1,
      new ChangeHistoryItem(userId, null, ChangeType.STATUS_CHANGED, null, ZonedDateTime.now(),null));
    historyDao.addApplicationChange(app2,
      new ChangeHistoryItem(userId, null, ChangeType.CONTENTS_CHANGED, null, ZonedDateTime.now(),
        changeList));
    historyDao.addApplicationChange(app2,
      new ChangeHistoryItem(userId, null, ChangeType.STATUS_CHANGED, null, ZonedDateTime.now(),null));

    historyDao.anonymizeHistoryFor(List.of(app1));

    List<ChangeHistoryItem> app1History = historyDao.getApplicationHistory(app1);
    List<ChangeHistoryItem> app2History = historyDao.getApplicationHistory(app2);
    assertEquals(1, app1History.size());
    assertEquals(ChangeType.STATUS_CHANGED, app1History.get(0).getChangeType());
    assertEquals(anonUser.getId(), app1History.get(0).getUserId());
    assertEquals(2, app2History.size());
    assertEquals(ChangeType.STATUS_CHANGED, app2History.get(0).getChangeType());
    assertEquals(userId, (int)app2History.get(0).getUserId());
    assertEquals(userId, (int)app2History.get(1).getUserId());
    assertEquals(ChangeType.CONTENTS_CHANGED, app2History.get(1).getChangeType());
    assertEquals("/foo", app2History.get(1).getFieldChanges().get(0).getFieldName());
    assertEquals("oldFoo", app2History.get(1).getFieldChanges().get(0).getOldValue());
    assertEquals("newFoo", app2History.get(1).getFieldChanges().get(0).getNewValue());
    assertEquals("/bar", app2History.get(1).getFieldChanges().get(1).getFieldName());
    assertEquals("oldBar", app2History.get(1).getFieldChanges().get(1).getOldValue());
    assertEquals("newBar", app2History.get(1).getFieldChanges().get(1).getNewValue());
  }
}
