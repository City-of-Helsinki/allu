package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.User;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.UserJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;

  @Autowired
  public UserService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  public List<UserJson> findAllUsers() {
    ResponseEntity<User[]> userResults = restTemplate.getForEntity(
        applicationProperties.getUserListingUrl(), User[].class);
    return mapUsers(userResults.getBody());
  }

  public UserJson findUserByUserName(String userName) {
    ResponseEntity<User> userResults = restTemplate.getForEntity(
        applicationProperties.getUserByUserNameUrl(), User.class, userName);
    return mapUser(userResults.getBody());
  }

  public UserJson addUser(UserJson userJson) {
    if (userJson.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    User user = mapUserJson(userJson);
    ResponseEntity<User> userResults = restTemplate.postForEntity(
        applicationProperties.getUserCreateUrl(), user, User.class);
    return mapUser(userResults.getBody());
  }

  public void updateUser(UserJson userJson) {
    User user = mapUserJson(userJson);
    restTemplate.put(applicationProperties.getUserUpdateUrl(), user);
  }

  private List<UserJson> mapUsers(User[] users) {
    return Arrays.stream(users).map(u -> mapUser(u)).collect(Collectors.toList());
  }

  public UserJson mapUser(User user) {
    return new UserJson(
        user.getId(),
        user.getUserName(),
        user.getRealName(),
        user.getEmailAddress(),
        user.getTitle(),
        user.isActive(),
        user.getAllowedApplicationTypes(),
        user.getAssignedRoles());
  }

  public User mapUserJson(UserJson userJson) {
    return new User(
        userJson.getId(),
        userJson.getUserName(),
        userJson.getRealName(),
        userJson.getEmailAddress(),
        userJson.getTitle(),
        userJson.isActive(),
        userJson.getAllowedApplicationTypes(),
        userJson.getAssignedRoles());
  }
}
