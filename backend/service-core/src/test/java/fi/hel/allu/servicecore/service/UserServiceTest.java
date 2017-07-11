package fi.hel.allu.servicecore.service;

import fi.hel.allu.model.domain.User;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.UserJson;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class UserServiceTest {

  private static final String userByUserNameUrl = "userNameUrl";
  private static final String userName = "username";

  @Test
  public void testFindUserByUserName() {
    ApplicationProperties applicationProperties = Mockito.mock(ApplicationProperties.class);
    Mockito.when(applicationProperties.getUserByUserNameUrl()).thenReturn(userByUserNameUrl);
    RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    Mockito.when(restTemplate.getForEntity(userByUserNameUrl, User.class, userName))
        .thenReturn(new ResponseEntity<User>(mockUser(), HttpStatus.OK));

    UserService userService = new UserService(applicationProperties, restTemplate, null);
    UserJson userJson = userService.findUserByUserName(userName);
    Assert.assertEquals(userName, userJson.getUserName());
  }

  private User mockUser() {
    return new User(
        1,
        "username",
        "realname",
        "email@email",
        "title",
        true,
        null,
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList());
  }
}
