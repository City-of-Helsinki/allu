package fi.hel.allu.servicecore.service;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.model.domain.user.ExternalUser;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ExternalUserJson;
import fi.hel.allu.servicecore.mapper.ExternalUserMapper;

@Service
public class ExternalUserService {

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private PasswordEncoder passwordEncoder;

  @Autowired
  public ExternalUserService(ApplicationProperties applicationProperties, RestTemplate restTemplate, PasswordEncoder passwordEncoder) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.passwordEncoder = passwordEncoder;
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
    setPassword(userJson, userResults.getBody().getId());
    return ExternalUserMapper.mapToExternalUserJson(userResults.getBody());
  }

  protected void setPassword(ExternalUserJson userJson, Integer externalUserId) {
    restTemplate.exchange(applicationProperties.getExternalUserSetPasswordUrl(), HttpMethod.PUT,
        new HttpEntity<>(passwordEncoder.encode(userJson.getPassword())), Void.class, externalUserId);
  }

  public void updateUser(ExternalUserJson userJson) {
    ExternalUser user = ExternalUserMapper.mapToModelExternalUser(userJson);
    restTemplate.put(applicationProperties.getExternalUserUpdateUrl(), user);
    if (StringUtils.isNotBlank(userJson.getPassword())) {
      setPassword(userJson, user.getId());
    }
  }

  public void setLastLogin(int userId, ZonedDateTime loginTime) {
    restTemplate.put(applicationProperties.getExternalUserLastLoginUpdateUrl(), loginTime, userId);
  }

  private List<ExternalUserJson> mapUsers(ExternalUser[] users) {
    return Arrays.stream(users).map(u -> ExternalUserMapper.mapToExternalUserJson(u)).collect(Collectors.toList());
  }
}
