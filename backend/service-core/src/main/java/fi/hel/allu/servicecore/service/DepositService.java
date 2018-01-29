package fi.hel.allu.servicecore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.model.domain.Deposit;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.DepositJson;
import fi.hel.allu.servicecore.event.ApplicationArchiveEvent;
import fi.hel.allu.servicecore.mapper.DepositMapper;

@Service
public class DepositService {
  private ApplicationProperties applicationProperties;
  private UserService userService;
  private ApplicationServiceComposer applicationServiceComposer;
  private ApplicationEventPublisher applicationEventPublisher;
  private RestTemplate restTemplate;

  @Autowired
  public DepositService(ApplicationProperties applicationProperties, UserService userService,
      ApplicationServiceComposer applicationServiceComposer, ApplicationEventPublisher archiveEventPublisher, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.userService = userService;
    this.applicationServiceComposer = applicationServiceComposer;
    this.applicationEventPublisher = archiveEventPublisher;
    this.restTemplate = restTemplate;
  }

  public DepositJson findByApplicationId(int applicationId) {
    ResponseEntity<Deposit> result = restTemplate.getForEntity(applicationProperties.getDepositByApplicationIdUrl(), Deposit.class,
        applicationId);
    return getPopulatedJson(result.getBody());
  }

  private DepositJson getPopulatedJson(Deposit deposit) {
    if (deposit == null) {
      return null;
    }
    return DepositMapper.createDepositJson(deposit, userService.findUserById(deposit.getCreatorId()));
  }

  public DepositJson create(DepositJson depositJson) {
    Deposit deposit = DepositMapper.createDepositModel(depositJson);
    deposit.setCreatorId(userService.getCurrentUser().getId());
    ResponseEntity<Deposit> result = restTemplate.postForEntity(applicationProperties.getDepositCreateUrl(), deposit, Deposit.class);
    applicationServiceComposer.refreshSearchTags(depositJson.getApplicationId());
    return getPopulatedJson(result.getBody());
  }

  public DepositJson update(int id, DepositJson depositJson) {
    Deposit deposit = DepositMapper.createDepositModel(depositJson);
    ResponseEntity<Deposit> result = restTemplate.exchange(applicationProperties.getDepositUpdateUrl(), HttpMethod.PUT,
        new HttpEntity<>(deposit), Deposit.class, id);
    applicationServiceComposer.refreshSearchTags(depositJson.getApplicationId());
    applicationEventPublisher.publishEvent(new ApplicationArchiveEvent(deposit.getApplicationId()));
    return getPopulatedJson(result.getBody());
  }

  public void delete(int id) {
    Deposit deposit = restTemplate.getForEntity(applicationProperties.getDepositByIdUrl(), Deposit.class, id).getBody();
    restTemplate.delete(applicationProperties.getDepositDeleteUrl(), id);
    applicationServiceComposer.refreshSearchTags(deposit.getApplicationId());
    applicationEventPublisher.publishEvent(new ApplicationArchiveEvent(deposit.getApplicationId()));
  }
}
