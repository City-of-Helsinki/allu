package fi.hel.allu.servicecore.service;

import fi.hel.allu.model.domain.ExternalUser;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ExternalUserJson;
import fi.hel.allu.servicecore.mapper.ExternalUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
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

  public ExternalUserJson addUser(ExternalUserJson userJson) {
    if (userJson.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    ExternalUser user = ExternalUserMapper.mapToModelExternalUser(userJson);
    ResponseEntity<ExternalUser> userResults = restTemplate.postForEntity(
        applicationProperties.getExternalUserCreateUrl(), user, ExternalUser.class);
    return ExternalUserMapper.mapToExternalUserJson(userResults.getBody());
  }

  public void updateUser(ExternalUserJson userJson) {
    ExternalUser user = ExternalUserMapper.mapToModelExternalUser(userJson);
    restTemplate.put(applicationProperties.getExternalUserUpdateUrl(), user);
  }

  public void setLastLogin(int userId, ZonedDateTime loginTime) {
    restTemplate.put(applicationProperties.getExternalUserLastLoginUpdateUrl(), loginTime, userId);
  }

  private List<ExternalUserJson> mapUsers(ExternalUser[] users) {
    return Arrays.stream(users).map(u -> ExternalUserMapper.mapToExternalUserJson(u)).collect(Collectors.toList());
  }
}
