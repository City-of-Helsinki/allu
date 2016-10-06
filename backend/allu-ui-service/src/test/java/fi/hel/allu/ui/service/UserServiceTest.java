package fi.hel.allu.ui.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.User;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.UserJson;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class UserServiceTest {

  @Test
  public void testFindUserByUserName() {
    final String userByUserNameUrl = "userNameUrl";
    final String userName = "username";
    ApplicationProperties applicationProperties = Mockito.mock(ApplicationProperties.class);
    Mockito.when(applicationProperties.getUserByUserNameUrl()).thenReturn(userByUserNameUrl);
    RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    Mockito.when(restTemplate.getForEntity(userByUserNameUrl, User.class, userName))
        .thenReturn(new ResponseEntity<User>(mockUser(), HttpStatus.OK));

    UserService userService = new UserService(applicationProperties, restTemplate);
    UserJson userJson = userService.findUserByUserName(userName);
    Assert.assertEquals(userName, userJson.getUserName());
  }

  @Test(expected = NoSuchEntityException.class)
  public void testFindNonExistentUserByUserName() {
    final String userByUserNameUrl = "userNameUrl";
    final String userName = "username";
    ApplicationProperties applicationProperties = Mockito.mock(ApplicationProperties.class);
    Mockito.when(applicationProperties.getUserByUserNameUrl()).thenReturn(userByUserNameUrl);
    RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    Mockito.when(restTemplate.getForEntity(userByUserNameUrl, User.class, userName)).thenReturn(new ResponseEntity(HttpStatus.NOT_FOUND));

    UserService userService = new UserService(applicationProperties, restTemplate);
    userService.findUserByUserName(userName);
  }

  private User mockUser() {
    return new User(1, "username", "realname", "email@email", "title", true, Collections.emptyList(), Collections.emptyList());
  }
}
