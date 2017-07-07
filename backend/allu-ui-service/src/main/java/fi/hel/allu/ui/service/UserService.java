package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.User;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.ui.mapper.UserMapper;
import fi.hel.allu.ui.security.AlluUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
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

  public UserJson getCurrentUser() {
    AlluUser alluUser = (AlluUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
    return findUserByUserName(alluUser.getUsername());
  }

  public List<UserJson> findAllUsers() {
    ResponseEntity<User[]> userResults = restTemplate.getForEntity(
        applicationProperties.getUserListingUrl(), User[].class);
    return mapUsers(userResults.getBody());
  }

  public List<UserJson> findAllActiveUsers() {
    List<UserJson> allUsers = findAllUsers();
    return allUsers.stream().filter(u -> u.isActive()).collect(Collectors.toList());
  }

  public UserJson findUserByUserName(String userName) {
    ResponseEntity<User> userResults = restTemplate.getForEntity(
        applicationProperties.getUserByUserNameUrl(), User.class, userName);
    return UserMapper.mapToUserJson(userResults.getBody());
  }

  public UserJson findUserById(int id) {
    ResponseEntity<User> userResults = restTemplate.getForEntity(
        applicationProperties.getUserByIdUrl(), User.class, id);
    return UserMapper.mapToUserJson(userResults.getBody());
  }

  public UserJson addUser(UserJson userJson) {
    if (userJson.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    User user = UserMapper.mapToModelUser(userJson);
    ResponseEntity<User> userResults = restTemplate.postForEntity(
        applicationProperties.getUserCreateUrl(), user, User.class);
    return UserMapper.mapToUserJson(userResults.getBody());
  }

  public void updateUser(UserJson userJson) {
    User user = UserMapper.mapToModelUser(userJson);
    restTemplate.put(applicationProperties.getUserUpdateUrl(), user);
  }

  public void setLastLogin(int userId, ZonedDateTime loginTime) {
    restTemplate.put(applicationProperties.getLastLoginUpdateUrl(), loginTime, userId);
  }

  private List<UserJson> mapUsers(User[] users) {
    return Arrays.stream(users).map(u -> UserMapper.mapToUserJson(u)).collect(Collectors.toList());
  }
}
