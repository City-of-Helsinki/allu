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
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class UserDaoTest {
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
  }

  @Test
  public void testFindWithNoRolesOrTypes() {
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
    Assert.assertTrue(insertedUser.getAllowedApplicationTypes().contains(ApplicationType.OUTDOOREVENT));
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
    userDao.update(insertedUser);
    User updatedUser = userDao.findById(insertedUser.getId()).get();

    Assert.assertTrue(updatedUser.isActive());
    Assert.assertEquals("updatedemail", updatedUser.getEmailAddress());
    Assert.assertEquals(2, updatedUser.getAssignedRoles().size());
    Assert.assertTrue(updatedUser.getAssignedRoles().contains(RoleType.ROLE_ADMIN));
    Assert.assertTrue(updatedUser.getAssignedRoles().contains(RoleType.ROLE_VIEW));
    Assert.assertEquals(1, updatedUser.getAllowedApplicationTypes().size());
    Assert.assertTrue(updatedUser.getAllowedApplicationTypes().contains(ApplicationType.OUTDOOREVENT));
  }

  private User createDummyUser(String userName) {
    User user = new User();
    user.setAssignedRoles(Arrays.asList(RoleType.ROLE_ADMIN, RoleType.ROLE_VIEW));
    user.setIsActive(true);
    user.setAllowedApplicationTypes(Arrays.asList(ApplicationType.OUTDOOREVENT));
    user.setEmailAddress("email");
    user.setRealName("realname");
    user.setTitle("title");
    user.setUserName(userName);
    return user;
  }
}
