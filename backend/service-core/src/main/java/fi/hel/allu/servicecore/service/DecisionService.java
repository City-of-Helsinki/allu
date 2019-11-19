package fi.hel.allu.servicecore.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.util.MultipartRequestBuilder;
import fi.hel.allu.pdf.domain.DecisionJson;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.StyleSheet;
import fi.hel.allu.servicecore.mapper.AnonymizedDecisionJsonMapper;
import fi.hel.allu.servicecore.mapper.DecisionJsonMapper;

@Service
public class DecisionService {

  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;
  private final DecisionJsonMapper decisionJsonMapper;
  private final AnonymizedDecisionJsonMapper anonymizedDecisionJsonMapper;
  private final ApplicationServiceComposer applicationServiceComposer;

  @Autowired
  public DecisionService(ApplicationProperties applicationProperties, RestTemplate restTemplate,
      ApplicationServiceComposer applicationServiceComposer, DecisionJsonMapper decisionJsonMapper,
      AnonymizedDecisionJsonMapper anonymizedDecisionJsonMapper) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.applicationServiceComposer = applicationServiceComposer;
    this.decisionJsonMapper = decisionJsonMapper;
    this.anonymizedDecisionJsonMapper = anonymizedDecisionJsonMapper;

  }

  /**
   * Generate the decision PDF for given application and save it to model
   * service
   *
   * @param applicationId
   *          the application's ID
   * @throws IOException
   *           when model-service responds with error
   */
  public void generateDecision(int applicationId, ApplicationJson application) throws IOException {
    saveDecisionData(decisionJsonMapper, applicationId, application, applicationProperties.getStoreDecisionUrl());
    saveDecisionData(anonymizedDecisionJsonMapper, applicationId, application, applicationProperties.getAnonymizedDecisionUrl());
  }

  private void saveDecisionData(DecisionJsonMapper mapper, int applicationId, ApplicationJson application, String decisionDataUrl)
      throws IOException {
    DecisionJson decisionJson = mapper.mapToDocumentJson(application, false);

    byte[] pdfData = restTemplate.postForObject(
        applicationProperties.getGeneratePdfUrl(), decisionJson, byte[].class,
        StyleSheet.name(application));
    ResponseEntity<String> response = restTemplate.exchange(
        decisionDataUrl, HttpMethod.POST,
        MultipartRequestBuilder.buildByteArrayRequest("file", pdfData), String.class, applicationId);
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new IOException(response.getBody());
    }
  }

  /**
   * Get the decision PDF for given application from the model service
   *
   * @param applicationId
   *          the application's ID
   * @return PDF data
   */
  public byte[] getDecision(int applicationId) {
    ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
    if (isDecisionDone(application)) {
      return getFinalDecision(applicationId);
    } else {
      return getDecisionPreview(application);
    }
  }

  public byte[] getFinalDecision(int applicationId) {
    return restTemplate.getForObject(applicationProperties.getDecisionUrl(), byte[].class, applicationId);
  }

  private boolean isDecisionDone(ApplicationJson application) {
    return application.getDecisionTime() != null;
  }

  /**
   * Get the decision preview PDF for given application from the model service
   *
   * @param application the application data whose PDF preview is created.
   * @return PDF data
   */
  public byte[] getDecisionPreview(ApplicationJson application) {
    DecisionJson decisionJson = decisionJsonMapper.mapToDocumentJson(application, true);
    return restTemplate.postForObject(applicationProperties.getGeneratePdfUrl(),
        decisionJson, byte[].class, StyleSheet.name(application));
  }





}
