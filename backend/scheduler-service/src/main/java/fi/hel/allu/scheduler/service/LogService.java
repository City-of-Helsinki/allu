package fi.hel.allu.scheduler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.domain.MailSenderLog;
import fi.hel.allu.scheduler.config.ApplicationProperties;

@Service
public class LogService {

  private final RestTemplate restTemplate;
  private final ApplicationProperties applicationProperties;

  @Autowired
  public LogService(RestTemplate restTemplate, ApplicationProperties applicationProperties) {
    this.restTemplate = restTemplate;
    this.applicationProperties = applicationProperties;
  }

  public void addMailSenderLog(MailSenderLog mailSenderLog) {
    restTemplate.postForObject(applicationProperties.getMailSenderLogUrl(), mailSenderLog, Integer.class);
  }

}
