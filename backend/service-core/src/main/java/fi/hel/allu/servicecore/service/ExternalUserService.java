package fi.hel.allu.servicecore.service;

import fi.hel.allu.model.domain.ExternalUser;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ExternalUserJson;
import fi.hel.allu.servicecore.mapper.ExternalUserMapper;
import fi.hel.allu.servicecore.security.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExternalUserService {

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;

  @Autowired
  public ExternalUserService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  public List<ExternalUserJson> findAllUsers() {
    ResponseEntity<ExternalUser[]> userResults = restTemplate.getForEntity(
        applicationProperties.getExternalUserListingUrl(), ExternalUser[].class);
    return mapUsers(userResults.getBody());
  }

  public ExternalUserJson findUserById(int id) {
    ResponseEntity<ExternalUser> userResults = restTemplate.getForEntity(
        applicationProperties.getExternalUserByIdUrl(), ExternalUser.class, id);
    return ExternalUserMapper.mapToExternalUserJson(userResults.getBody());
  }

  public ExternalUserJson findUserByUserName(String username) {
    ResponseEntity<ExternalUser> userResults = restTemplate.getForEntity(
        applicationProperties.getExternalUserByUserNameUrl(), ExternalUser.class, username);
    return ExternalUserMapper.mapToExternalUserJson(userResults.getBody());
  }

  public ExternalUserJson addUser(String jwtSecret, ExternalUserJson userJson) {
    if (userJson.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    ExternalUser user = ExternalUserMapper.mapToModelExternalUser(userJson);
    user.setToken(createToken(jwtSecret, userJson));
    ResponseEntity<ExternalUser> userResults = restTemplate.postForEntity(
        applicationProperties.getExternalUserCreateUrl(), user, ExternalUser.class);
    return ExternalUserMapper.mapToExternalUserJson(userResults.getBody());
  }

  public void updateUser(String jwtSecret, ExternalUserJson userJson) {
    ExternalUser user = ExternalUserMapper.mapToModelExternalUser(userJson);
    ExternalUserJson existingUser = findUserById(userJson.getId());
    if (hasTokenChanged(userJson, existingUser)) {
      user.setToken(createToken(jwtSecret, userJson));
    }
    restTemplate.put(applicationProperties.getExternalUserUpdateUrl(), user);
  }

  public void setLastLogin(int userId, ZonedDateTime loginTime) {
    restTemplate.put(applicationProperties.getExternalUserLastLoginUpdateUrl(), loginTime, userId);
  }

  private List<ExternalUserJson> mapUsers(ExternalUser[] users) {
    return Arrays.stream(users).map(u -> ExternalUserMapper.mapToExternalUserJson(u)).collect(Collectors.toList());
  }

  private String createToken(String jwtSecret, ExternalUserJson externalUserJson) {
    TokenUtil tokenUtil = new TokenUtil(jwtSecret);
    Map<String, Object> roleMap = Collections.singletonMap(TokenUtil.PROPERTY_ROLE_ALLU_PUBLIC, externalUserJson.getAssignedRoles());
    return tokenUtil.createToken(externalUserJson.getExpirationTime(), externalUserJson.getUsername(), roleMap);
  }

  private boolean hasTokenChanged(ExternalUserJson updatedUser, ExternalUserJson existingUser) {
    if (!existingUser.getExpirationTime().equals(updatedUser.getExpirationTime())) {
      return true;
    }
    if (updatedUser.getAssignedRoles().size() != existingUser.getAssignedRoles().size() ||
        !updatedUser.getAssignedRoles().containsAll(existingUser.getAssignedRoles())) {
      return true;
    }

    return false;
  }
}
