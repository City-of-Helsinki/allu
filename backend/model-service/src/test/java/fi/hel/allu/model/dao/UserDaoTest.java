package fi.hel.allu.model.dao;

import fi.hel.allu.common.exception.NonUniqueException;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.RoleType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.User;
import fi.hel.allu.model.testUtils.TestCommon;
import org.junit.Assert;
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
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class UserDaoTest {

  private static final List<Integer> TEST_CITY_DISTRICTS = Arrays.asList(1, 2);

  @Autowired
  private UserDao userDao;

  @Autowired
  TestCommon testCommon;

  @Before
  public void setup() throws Exception {
    testCommon.deleteAllData();
  }

  @Test
  public void testFetchList() {
    User user1 = createDummyUser("username1");
    User user2 = createDummyUser("username2");
    User insertedUser1 = userDao.insert(user1);
    User insertedUser2 = userDao.insert(user2);

    List<User> users = userDao.findAll();

    Assert.assertEquals(2, users.size());
    Assert.assertEquals(2, users.get(0).getAssignedRoles().size());
    Assert.assertEquals(1, users.get(0).getAllowedApplicationTypes().size());
    Assert.assertEquals(TEST_CITY_DISTRICTS.size(), users.get(0).getCityDistrictIds().size());
  }

  @Test
  public void TestFindMatching() {
    // Set-up: add two users with partially overlapping roles and application
    // types
    User user1 = createDummyUser("User1");
    user1.setAllowedApplicationTypes(
        Arrays.asList(ApplicationType.CABLE_REPORT, ApplicationType.EXCAVATION_ANNOUNCEMENT));
    user1.setAssignedRoles(Arrays.asList(RoleType.ROLE_ADMIN, RoleType.ROLE_INVOICING));
    user1.setCityDistrictIds(Arrays.asList(1, 2));
    User insertedUser1 = userDao.insert(user1);
    User user2 = createDummyUser("User2");
    user2.setAllowedApplicationTypes(Arrays.asList(ApplicationType.CABLE_REPORT, ApplicationType.EVENT));
    user2.setAssignedRoles(Arrays.asList(RoleType.ROLE_ADMIN, RoleType.ROLE_DECISION));
    user2.setCityDistrictIds(Arrays.asList(1, 3));
    User insertedUser2 = userDao.insert(user2);
    assertNotEquals(insertedUser1.getId(), insertedUser2.getId());

    // Try different searches. First, one that should return both:
    List<User> users = userDao.findMatching(RoleType.ROLE_ADMIN, ApplicationType.CABLE_REPORT, 1);
    assertEquals(2, users.size());
    assertEquals(1, users.stream().filter(u -> u.getId().equals(insertedUser1.getId())).count());
    assertEquals(1, users.stream().filter(u -> u.getId().equals(insertedUser2.getId())).count());

    // Second should only return user1:
    users = userDao.findMatching(RoleType.ROLE_ADMIN, ApplicationType.EXCAVATION_ANNOUNCEMENT, 1);
    assertEquals(1, users.size());
    assertEquals(1, users.stream().filter(u -> u.getId().equals(insertedUser1.getId())).count());

    // So should this
    users = userDao.findMatching(RoleType.ROLE_ADMIN, ApplicationType.CABLE_REPORT, 2);
    assertEquals(1, users.size());
    assertEquals(1, users.stream().filter(u -> u.getId().equals(insertedUser1.getId())).count());

    // This should only return user2:
    users = userDao.findMatching(RoleType.ROLE_DECISION, ApplicationType.CABLE_REPORT, 1);
    assertEquals(1, users.size());
    assertEquals(1, users.stream().filter(u -> u.getId().equals(insertedUser2.getId())).count());

    // This should match neither:
    users = userDao.findMatching(RoleType.ROLE_DECISION, ApplicationType.EXCAVATION_ANNOUNCEMENT, 1);
    assertEquals(0, users.size());
  }

  @Test
  public void testInsertWithNoRolesOrTypes() {
    User user = new User();
    user.setIsActive(true);
    user.setEmailAddress("email");
    user.setRealName("realname");
    user.setTitle("title");
    user.setUserName("username");
    User insertedUser = userDao.insert(user);
    Assert.assertEquals(0, insertedUser.getAssignedRoles().size());
    Assert.assertEquals(0, insertedUser.getAllowedApplicationTypes().size());
  }

  @Test
  public void testInsertSelect() {
    User user = createDummyUser("username");
    User insertedUser = userDao.insert(user);

    Assert.assertTrue(insertedUser.isActive());
    Assert.assertEquals("email", insertedUser.getEmailAddress());
    Assert.assertEquals(2, insertedUser.getAssignedRoles().size());
    Assert.assertTrue(insertedUser.getAssignedRoles().contains(RoleType.ROLE_ADMIN));
    Assert.assertTrue(insertedUser.getAssignedRoles().contains(RoleType.ROLE_VIEW));
    Assert.assertEquals(1, insertedUser.getAllowedApplicationTypes().size());
    Assert.assertEquals(TEST_CITY_DISTRICTS.size(), insertedUser.getCityDistrictIds().size());
    Assert.assertTrue(insertedUser.getAllowedApplicationTypes().contains(ApplicationType.EVENT));
    Assert.assertTrue(insertedUser.getCityDistrictIds().containsAll(TEST_CITY_DISTRICTS));
  }

  @Test(expected = NonUniqueException.class)
  public void testInsertDuplicateUserName() {
    User user = createDummyUser("username");
    userDao.insert(user);
    userDao.insert(user);
  }

  @Test
  public void testUpdate() {
    User user = createDummyUser("username");

    User insertedUser = userDao.insert(user);
    insertedUser.setEmailAddress("updatedemail");
    insertedUser.setCityDistrictIds(Arrays.asList(3));
    userDao.update(insertedUser);
    User updatedUser = userDao.findById(insertedUser.getId()).get();

    Assert.assertTrue(updatedUser.isActive());
    Assert.assertEquals("updatedemail", updatedUser.getEmailAddress());
    Assert.assertEquals(2, updatedUser.getAssignedRoles().size());
    Assert.assertTrue(updatedUser.getAssignedRoles().contains(RoleType.ROLE_ADMIN));
    Assert.assertTrue(updatedUser.getAssignedRoles().contains(RoleType.ROLE_VIEW));
    Assert.assertEquals(1, updatedUser.getAllowedApplicationTypes().size());
    Assert.assertTrue(updatedUser.getAllowedApplicationTypes().contains(ApplicationType.EVENT));
    Assert.assertEquals(1, updatedUser.getCityDistrictIds().size());
  }

  @Test
  public void testSetLastLogin() {
    User user = createDummyUser("username");
    User insertedUser = userDao.insert(user);

    Assert.assertNull(insertedUser.getLastLogin());

    ZonedDateTime loginTime = ZonedDateTime.now();
    userDao.setLastLogin(insertedUser.getId(), loginTime);

    userDao.findById(insertedUser.getId())
    .ifPresent(u -> Assert.assertEquals(loginTime, u.getLastLogin()));
  }

  private User createDummyUser(String userName) {
    User user = new User();
    user.setAssignedRoles(Arrays.asList(RoleType.ROLE_ADMIN, RoleType.ROLE_VIEW));
    user.setIsActive(true);
    user.setAllowedApplicationTypes(Arrays.asList(ApplicationType.EVENT));
    user.setCityDistrictIds(TEST_CITY_DISTRICTS);
    user.setEmailAddress("email");
    user.setRealName("realname");
    user.setTitle("title");
    user.setUserName(userName);
    return user;
  }
}
