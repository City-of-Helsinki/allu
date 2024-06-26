package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.MultipartRequestBuilder;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ApplicationInterface;
import fi.hel.allu.pdf.domain.TerminationJson;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.StyleSheet;
import fi.hel.allu.servicecore.mapper.TerminationJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TerminationService {
  private static final String TEMPLATE_SUFFIX = "-termination";
  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;
  private final TerminationJsonMapper terminationJsonMapper;

  @Autowired
  public TerminationService(ApplicationProperties applicationProperties, RestTemplate restTemplate,
                            TerminationJsonMapper terminationJsonMapper) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.terminationJsonMapper = terminationJsonMapper;
  }

  public TerminationInfo getTerminationInfo(int applicationId) {
    return restTemplate.getForObject(applicationProperties.getTerminationInfoUrl(),
        TerminationInfo.class, applicationId);
  }

  public List<TerminationInfo> postTerminationInfoList(List<Integer> ids) {
    TerminationInfo[] results = restTemplate.postForObject(applicationProperties.getTerminationInfoUrlList(), ids,
                                                           TerminationInfo[].class);
    return Arrays.asList(results);
  }

  public TerminationInfo insertTerminationInfo(int applicationId, TerminationInfo terminationInfo) {
    return restTemplate.postForObject(applicationProperties.getTerminationInfoUrl(),
        terminationInfo, TerminationInfo.class, applicationId);
  }

  public TerminationInfo updateTerminationInfo(int applicationId, TerminationInfo terminationInfo) {
    HttpEntity<TerminationInfo> requestEntity = new HttpEntity<>(terminationInfo);
    return restTemplate.exchange(applicationProperties.getTerminationInfoUrl(), HttpMethod.PUT,
        requestEntity, TerminationInfo.class, applicationId).getBody();
  }

  public void removeTerminationInfo(int applicationId) {
    restTemplate.delete(applicationProperties.getTerminationInfoUrl(), applicationId);
  }

  public void generateTermination(int applicationId, ApplicationJson application) {
    TerminationInfo info = getTerminationInfo(applicationId);
    TerminationJson terminationJson = terminationJsonMapper.mapToDocumentJson(application, info, false);
    byte[] pdfData = restTemplate.postForObject(
        applicationProperties.getGeneratePdfUrl(), terminationJson, byte[].class,
        StyleSheet.name(application, TEMPLATE_SUFFIX));
    restTemplate.exchange(
        applicationProperties.getTerminationUrl(), HttpMethod.POST,
        MultipartRequestBuilder.buildByteArrayRequest("file", pdfData), Void.class, applicationId);
  }

  public byte[] getTermination(ApplicationJson application) {
    if (StatusType.TERMINATED.equals(application.getStatus())) {
      return getFinalTermination(application.getId());
    } else {
      // otherwise return preview
      return getTerminationPreview(application);
    }
  }

  public byte[] getFinalTermination(int applicationId) {
    return restTemplate.getForObject(applicationProperties.getTerminationUrl(), byte[].class, applicationId);
  }

  public byte[] getTerminationPreview(ApplicationJson application) {
    TerminationInfo info = getTerminationInfo(application.getId());
    TerminationJson terminationJson = terminationJsonMapper.mapToDocumentJson(application, info, true);
    return restTemplate.postForObject(
        applicationProperties.getGeneratePdfUrl(), terminationJson, byte[].class,
        StyleSheet.name(application, TEMPLATE_SUFFIX));
  }

  public List<Integer> fetchTerminatedApplications() {
    ResponseEntity<Integer[]> response = restTemplate.getForEntity(
        applicationProperties.getTerminatedApplicationsUrl(), Integer[].class);
    return Arrays.asList(response.getBody());
  }

  public ZonedDateTime getTerminationTime(Application application) {
    if (application.getStatus() == StatusType.TERMINATED || application.getStatus() == StatusType.ARCHIVED) {
      return Optional.ofNullable(getTerminationInfo(application.getId()))
              .map(TerminationInfo::getExpirationTime).orElse(null);
    }
    return null;
  }

  public List<ApplicationES> mapTerminationToEs(List<ApplicationES> applicationESList, List<Application> applications) {
    List<TerminationInfo> terminationInfoList = getTerminationInfoList(applications);
    Map<Integer, TerminationInfo> mapperTermination = terminationInfoList.stream().collect(Collectors.toMap(
            TerminationInfo::getApplicationId, Function.identity()));
    applicationESList.forEach(v -> addTerminationTimes(mapperTermination, v));
    return applicationESList;
  }

  public <T extends ApplicationInterface> List<TerminationInfo> getTerminationInfoList(List<T> applicationJsonList) {
    List<Integer> applicationIds = applicationJsonList.stream()
            .filter(e -> e.getStatus() == StatusType.TERMINATED || e.getStatus() == StatusType.ARCHIVED)
            .map(ApplicationInterface::getId).collect(Collectors.toList());
    return this.postTerminationInfoList(applicationIds);
  }

  private void addTerminationTimes(Map<Integer, TerminationInfo> terminationInfoMap, ApplicationES applicationES) {
    Integer id = applicationES.getId();
    if (terminationInfoMap.containsKey(id) && terminationInfoMap.get(id) != null) {
      applicationES.setTerminationTime(TimeUtil.dateToMillis(terminationInfoMap.get(id).getExpirationTime()));
    }
  }

}