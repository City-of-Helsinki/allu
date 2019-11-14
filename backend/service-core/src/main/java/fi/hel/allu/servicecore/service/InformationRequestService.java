package fi.hel.allu.servicecore.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fi.hel.allu.common.domain.ApplicationStatusInfo;
import fi.hel.allu.common.domain.types.InformationRequestStatus;
import fi.hel.allu.common.domain.user.Constants;
import fi.hel.allu.servicecore.domain.informationrequest.InformationRequestSummaryJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.domain.ExternalApplication;
import fi.hel.allu.common.domain.InformationRequestResponse;
import fi.hel.allu.common.domain.types.InformationRequestFieldKey;
import fi.hel.allu.common.domain.types.StatusType;
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
  private final ExternalUserService externalUserService;
  private final ApplicationServiceComposer applicationServiceComposer;

  @Autowired
  public InformationRequestService(ApplicationProperties applicationProperties, RestTemplate restTemplate,
      UserService userService, ApplicationServiceComposer applicationServiceComposer, ExternalUserService externalUserService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.userService = userService;
    this.applicationServiceComposer = applicationServiceComposer;
    this.externalUserService = externalUserService;
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

  public InformationRequestJson findRequestById(int id) {
    return toInformationRequestJson(findById(id));
  }

  public InformationRequestResponseJson findResponseForRequest(int requestId) throws IOException {
    InformationRequest request = findById(requestId);

    InformationRequestResponse response = restTemplate.getForObject(
      applicationProperties.getInformationRequestResponseUrl(), InformationRequestResponse.class, requestId);
    return toResponseJson(request.getApplicationId(), response);
  }

  public InformationRequestJson findOpenByApplicationId(int id) {
    InformationRequest request = restTemplate.getForObject(applicationProperties.getApplicationOpenInformationRequestFindUrl(), InformationRequest.class, id);
    return toInformationRequestJson(request);
  }

  public InformationRequestJson findByApplicationId(int id) {
    InformationRequest request = restTemplate.getForObject(applicationProperties.getApplicationActiveInformationRequestFindUrl(), InformationRequest.class, id);
    return toInformationRequestJson(request);
  }

  public List<InformationRequestSummaryJson> findSummariesByApplicationId(int id) {
    ResponseEntity<InformationRequest[]> result = restTemplate.getForEntity(
      applicationProperties.getApplicationInformationRequestFindAllUrl(), InformationRequest[].class, id);
    return Arrays.stream(result.getBody())
      .map(this::createSummary)
      .collect(Collectors.toList());
  }

  public InformationRequest createForResponse(Integer applicationId, List<InformationRequestFieldKey> updatedKeys) {
    InformationRequest request = createInformationRequestModel(applicationId, updatedKeys);
    request.setCreatorId(userService.getCurrentUser().getId());
    return restTemplate
      .postForEntity(applicationProperties.getInformationRequestCreateUrl(), request, InformationRequest.class, applicationId)
      .getBody();
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

  private InformationRequest createInformationRequestModel(Integer applicationId, List<InformationRequestFieldKey> fieldKeys) {
    List<InformationRequestField> updatedFields = fieldKeys.stream()
      .map(key -> new InformationRequestField(null, key, null))
      .collect(Collectors.toList());
    return new InformationRequest(null, applicationId, InformationRequestStatus.OPEN, updatedFields);
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
    updateStatusAfterClose(responseEntity.getBody().getApplicationId());
    return toInformationRequestJson(responseEntity.getBody());
  }

  public InformationRequestResponseJson findResponseForApplication(Integer applicationId) throws IOException {
    InformationRequestResponse response = restTemplate.getForObject(
        applicationProperties.getInformationRequestResponseFindUrl(), InformationRequestResponse.class, applicationId);
    return toResponseJson(applicationId, response);
  }

  private void updateStatusAfterClose(Integer applicationId) {
    ApplicationStatusInfo statusInfo = applicationServiceComposer.getApplicationStatus(applicationId);
    if (statusInfo.getStatus() == StatusType.INFORMATION_RECEIVED) {
      applicationServiceComposer.changeStatus(applicationId, StatusType.HANDLING);
    }
  }

  private static InformationRequestResponseJson toResponseJson(Integer applicationId,
      InformationRequestResponse response) throws IOException {
    ApplicationJson application =
        ApplicationJsonMapper.getApplicationFromJson(response.getApplication().getApplicationData());
    return new InformationRequestResponseJson(response.getInformationRequestId(), applicationId, application,
        response.getResponseFields());
  }

  private InformationRequestSummaryJson createSummary(InformationRequest request) {
    InformationRequestFieldKey[] response = restTemplate.getForObject(
      applicationProperties.getInformationRequestResponseFieldsFindUrl(), InformationRequestFieldKey[].class, request.getId());
    InformationRequestSummaryJson summary = new InformationRequestSummaryJson();
    summary.setInformationRequestId(request.getId());
    summary.setApplicationId(request.getApplicationId());
    summary.setStatus(request.getStatus());
    summary.setCreationTime(request.getCreationTime());
    summary.setResponseReceived(request.getResponseReceived());

    Optional.ofNullable(userService.findUserById(request.getCreatorId())).ifPresent(creator -> {
      summary.setCreator(creator.getRealName());
      summary.setUpdateWithoutRequest(creator.getUserName().equals(Constants.EXTERNAL_USER_USERNAME));
    });

    Optional.ofNullable(applicationServiceComposer.getApplicationExternalOwner(request.getApplicationId()))
      .map(ownerId -> this.externalUserService.findUserById(ownerId))
      .ifPresent(externalOwner -> summary.setRespondent(externalOwner.getName()));

    summary.setRequestedFields(toInformationRequestJsonFields(request.getFields()));

    List<InformationRequestFieldJson> responseFields = Arrays.stream(response)
      .map(fieldKey -> new InformationRequestFieldJson(fieldKey, null))
      .collect(Collectors.toList());
    summary.setResponseFields(responseFields);

    return summary;
  }
}
