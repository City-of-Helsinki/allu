package fi.hel.allu.servicecore.service;

import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.SupervisionTaskJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.mapper.SupervisionTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SupervisionTaskService {

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private UserService userService;

  @Autowired
  public SupervisionTaskService(ApplicationProperties applicationProperties, RestTemplate restTemplate, UserService userService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.userService = userService;
  }


  public SupervisionTaskJson findById(int id) {
    ResponseEntity<SupervisionTask> supervisionTasksResult = restTemplate.getForEntity(
        applicationProperties.getSupervisionTaskByIdUrl(), SupervisionTask.class);
    return getFullyPopulatedJson(Collections.singletonList(supervisionTasksResult.getBody())).get(0);
  }

  public List<SupervisionTaskJson> findByApplicationId(int applicationId) {
    ResponseEntity<SupervisionTask[]> supervisionTasksResult = restTemplate.getForEntity(
        applicationProperties.getSupervisionTaskByApplicationIdUrl(), SupervisionTask[].class, applicationId);
    return getFullyPopulatedJson(Arrays.asList(supervisionTasksResult.getBody()));
  }

  public SupervisionTaskJson update(SupervisionTaskJson supervisionTask) {
    HttpEntity<SupervisionTask> supervisionTaskHttpEntity = new HttpEntity<>(SupervisionTaskMapper.mapToModel(supervisionTask));
    ResponseEntity<SupervisionTask> supervisionTasksResult = restTemplate.exchange(
        applicationProperties.getSupervisionTaskUpdateUrl(),
        HttpMethod.PUT,
        supervisionTaskHttpEntity,
        SupervisionTask.class,
        supervisionTask.getId());
    return getFullyPopulatedJson(Collections.singletonList(supervisionTasksResult.getBody())).get(0);
  }

  public SupervisionTaskJson insert(SupervisionTaskJson supervisionTaskJson) {
    SupervisionTask task = SupervisionTaskMapper.mapToModel(supervisionTaskJson);
    task.setCreatorId(userService.getCurrentUser().getId());
    ResponseEntity<SupervisionTask> supervisionTasksResult = restTemplate.postForEntity(
        applicationProperties.getSupervisionTaskCreateUrl(), task, SupervisionTask.class);
    return getFullyPopulatedJson(Collections.singletonList(supervisionTasksResult.getBody())).get(0);
  }

  public void delete(int id) {
    restTemplate.delete(applicationProperties.getSupervisionTaskByIdUrl(), id);
  }

  private List<SupervisionTaskJson> getFullyPopulatedJson(List<SupervisionTask> supervisionTasks) {
    Map<Integer, UserJson> idToUser = supervisionTasks.stream()
        .map(st -> Arrays.asList(st.getCreatorId(), st.getHandlerId()))
        .flatMap(pair -> pair.stream())
        .filter(number -> number != null)
        .distinct()
        .collect(Collectors.toMap(id -> id, id -> userService.findUserById(id)));
    return mapSupervisionTasks(supervisionTasks, idToUser);
  }

  private List<SupervisionTaskJson> mapSupervisionTasks(List<SupervisionTask> supervisionTasks, Map<Integer, UserJson> idToUser) {
    return supervisionTasks.stream().map(st -> SupervisionTaskMapper.mapToJson(st, idToUser)).collect(Collectors.toList());
  }

}
