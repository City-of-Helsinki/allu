package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.UserSearchCriteria;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.common.domain.user.Constants;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {


  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private IdentityServiceInterface identityService;

  @Autowired
  public UserService(ApplicationProperties applicationProperties, RestTemplate restTemplate, IdentityServiceInterface identityService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.identityService = identityService;
  }

  public UserJson getCurrentUser() {
    return findUserByUserName(identityService.getUsername());
  }

  public boolean isExternalUser() {
    return identityService.getUsername().equals(Constants.EXTERNAL_USER_USERNAME);
  }

  public List<UserJson> findAllUsers() {
    ResponseEntity<User[]> userResults = restTemplate.getForEntity(
        applicationProperties.getUserListingUrl(), User[].class);
    return mapUsers(userResults.getBody());
  }

  public List<UserJson> search(UserSearchCriteria usc) {
    ResponseEntity<User[]> userResults = restTemplate.postForEntity(
        applicationProperties.getUserSearchUrl(), usc, User[].class);
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

  public List<UserJson> findUserByRole(RoleType role) {
    ResponseEntity<User[]> userResults = restTemplate.getForEntity(
        applicationProperties.getUsersByRoleUrl(), User[].class, role);
    return mapUsers(userResults.getBody());
  }

  @Cacheable(value = "users", key = "#id")
  public UserJson findUserById(int id) {
    ResponseEntity<User> userResults = restTemplate.getForEntity(
        applicationProperties.getUserByIdUrl(), User.class, id);
    return UserMapper.mapToUserJson(userResults.getBody());
  }

  public List<UserJson> findByIds(List<Integer> ids) {
    User[] result = restTemplate.postForObject(applicationProperties.getUsersByIdUrl(), ids, User[].class);
    return Arrays.stream(result).map(p -> UserMapper.mapToUserJson(p)).collect(Collectors.toList());
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

  @CacheEvict(value="users", key="#userJson.id")
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