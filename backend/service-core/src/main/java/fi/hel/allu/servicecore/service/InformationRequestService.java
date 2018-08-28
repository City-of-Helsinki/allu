package fi.hel.allu.servicecore.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.domain.ExternalApplication;
import fi.hel.allu.common.domain.InformationRequestResponse;
import fi.hel.allu.common.domain.types.InformationRequestFieldKey;
import fi.hel.allu.model.domain.InformationRequest;
import fi.hel.allu.model.domain.InformationRequestField;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.InformationRequestFieldJson;
import fi.hel.allu.servicecore.domain.InformationRequestJson;
import fi.hel.allu.servicecore.domain.informationrequest.InformationRequestResponseJson;
import fi.hel.allu.servicecore.mapper.ApplicationJsonMapper;

@Service
public class InformationRequestService {

  private final RestTemplate restTemplate;
  private final ApplicationProperties applicationProperties;
  private final UserService userService;

  @Autowired
  public InformationRequestService(ApplicationProperties applicationProperties, RestTemplate restTemplate, UserService userService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.userService = userService;
  }

  public InformationRequestJson createForApplication(int id, InformationRequestJson informationRequest) {
    InformationRequest request = toInformationRequestModel(informationRequest);
    request.setCreatorId(userService.getCurrentUser().getId());
    ResponseEntity<InformationRequest> result = restTemplate
        .postForEntity(applicationProperties.getInformationRequestCreateUrl(), request, InformationRequest.class, id);
    return toInformationRequestJson(result.getBody());
  }

  public InformationRequestJson update(int id, InformationRequestJson informationRequest) {
    InformationRequest request = toInformationRequestModel(informationRequest);
    ResponseEntity<InformationRequest> result = restTemplate.exchange(applicationProperties.getInformationRequestUrl(), HttpMethod.PUT,
        new HttpEntity<>(request), InformationRequest.class, id);
    return toInformationRequestJson(result.getBody());
  }

  public void delete(int id) {
    restTemplate.delete(applicationProperties.getInformationRequestUrl(), id);
  }

  public InformationRequestJson findOpenByApplicationId(int id) {
    InformationRequest request = restTemplate.getForObject(applicationProperties.getApplicationOpenInformationRequestFindUrl(), InformationRequest.class, id);
    return toInformationRequestJson(request);
  }

  private InformationRequestJson toInformationRequestJson(InformationRequest request) {
    if (request == null) {
      return null;
    }
    return new InformationRequestJson(request.getId(), request.getApplicationId(), toInformationRequestJsonFields(request.getFields()), request.getStatus());
  }

  private List<InformationRequestFieldJson> toInformationRequestJsonFields(List<InformationRequestField> fields) {
    return fields.stream().map(f -> new InformationRequestFieldJson(f.getFieldKey(), f.getDescription())).collect(Collectors.toList());
  }

  private InformationRequest toInformationRequestModel(InformationRequestJson informationRequest) {
    return new InformationRequest(informationRequest.getId(), informationRequest.getApplicationId(),
        informationRequest.getStatus(),
        toInformationRequestModelFields(informationRequest.getId(), informationRequest.getFields()));
  }

  private List<InformationRequestField> toInformationRequestModelFields(Integer requestId, List<InformationRequestFieldJson> fields) {
    return fields.stream().map(f -> new InformationRequestField(requestId, f.getFieldKey(), f.getDescription())).collect(Collectors.toList());
  }

  public void addResponse(Integer requestId, ExternalApplication extApp,
      List<InformationRequestFieldKey> updatedFields) {
    InformationRequestResponse response = new InformationRequestResponse(requestId, updatedFields, extApp);
    restTemplate.postForObject(applicationProperties.getInformationRequestResponseUrl(), response, Void.class, requestId);
  }

  public InformationRequest findById(Integer id) {
    return restTemplate.getForObject(applicationProperties.getInformationRequestUrl(), InformationRequest.class, id);
  }

  public InformationRequestJson closeInformationRequest(Integer id) {
    ResponseEntity<InformationRequest> responseEntity = restTemplate.exchange(
        applicationProperties.getInformationRequestCloseUrl(),
        HttpMethod.PUT,
        new HttpEntity<>(null),
        InformationRequest.class,
        id);
    return toInformationRequestJson(responseEntity.getBody());
  }

  public InformationRequestResponseJson findResponseForApplication(Integer applicationId) throws IOException {
    InformationRequestResponse response = restTemplate.getForObject(
        applicationProperties.getInformationRequestResponseFindUrl(), InformationRequestResponse.class, applicationId);
    return toResponseJson(applicationId, response);
  }

  private static InformationRequestResponseJson toResponseJson(Integer applicationId,
      InformationRequestResponse response) throws IOException {
    ApplicationJson application =
        ApplicationJsonMapper.getApplicationFromJson(response.getApplication().getApplicationData());
    return new InformationRequestResponseJson(response.getInformationRequestId(), applicationId, application,
        response.getResponseFields());
  }

}
