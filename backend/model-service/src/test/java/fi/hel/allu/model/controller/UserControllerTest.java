package fi.hel.allu.model.controller;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.UserDao;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.testUtils.TestCommon;
import fi.hel.allu.model.testUtils.WebTestCommon;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class UserControllerTest {
  @Autowired
  WebTestCommon wtc;

  @Autowired
  TestCommon testCommon;

  @Autowired
  UserDao userDao;

  @Before
  public void setup() throws Exception {
    wtc.setup();
  }

  @Test
  public void testLoadNonExistentUser() throws Exception {
    wtc.perform(get("/users/123456789")).andExpect(status().is4xxClientError());
  }

  @Test
  public void testLoadUserById() throws Exception {
    User user = testCommon.insertUser("testuser");
    ResultActions resultActions = wtc.perform(get("/users/" + user.getId())).andExpect(status().isOk());
    User parsedUser = wtc.parseObjectFromResult(resultActions, User.class);
    Assert.assertEquals(user.getUserName(), parsedUser.getUserName());
  }

  @Test
  public void testLoadUserByUserName() throws Exception {
    User user = testCommon.insertUser("testuser");
    ResultActions resultActions = wtc.perform(get("/users/userName/?userName=" + user.getUserName())).andExpect(status().isOk());
    User parsedUser = wtc.parseObjectFromResult(resultActions, User.class);
    Assert.assertEquals(user.getUserName(), parsedUser.getUserName());
  }

  @Test
  public void testLoadAllUsers() throws Exception {
    testCommon.insertUser("testuser");
    ResultActions resultActions = wtc.perform(get("/users")).andExpect(status().isOk());
    List<User> parsedUsers = wtc.parseObjectFromResult(resultActions, List.class);
    Assert.assertEquals(1, parsedUsers.size());
  }

  @Test
  public void testAddUser() throws Exception {
    User user = testCommon.insertUser("testuser");
    user.setId(null);
    user.setUserName("differenttobeunique");
    ResultActions resultActions = wtc.perform(post("/users"), user).andExpect(status().isOk());
    User parsedUser = wtc.parseObjectFromResult(resultActions, User.class);
    Assert.assertEquals(user.getUserName(), parsedUser.getUserName());
  }

  @Test
  public void testUpdateUser() throws Exception {
    User user = testCommon.insertUser("testuser");
    user.setEmailAddress("updated@email");
    wtc.perform(put("/users"), user).andExpect(status().isOk());
  }
}

