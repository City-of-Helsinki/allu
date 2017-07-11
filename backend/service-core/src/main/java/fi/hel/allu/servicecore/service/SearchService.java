package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@Service
public class SearchService {
  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private ApplicationMapper applicationMapper;
  private ProjectMapper projectMapper;

  @Autowired
  public SearchService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      ApplicationMapper applicationMapper,
      ProjectMapper projectMapper) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.applicationMapper = applicationMapper;
    this.projectMapper = projectMapper;
  }

  public void insertApplication(ApplicationJson applicationJson) {
    restTemplate.postForObject(
        applicationProperties.getApplicationSearchCreateUrl(),
        applicationMapper.createApplicationESModel(applicationJson),
        ApplicationES.class);
  }

  /**
   * Update multiple applications to search index.
   *
   * @param applicationJsons Applications to be updated.
   */
  public void updateApplications(List<ApplicationJson> applicationJsons) {
    List<ApplicationES> applications =
        applicationJsons.stream().map(a -> applicationMapper.createApplicationESModel(a)).collect(Collectors.toList());
    restTemplate.put(
        applicationProperties.getApplicationsSearchUpdateUrl(),
        applications);
  }

  /**
   * Updates tags of given application id into index
   * @param applicationId id of application to update
   * @param tagJsons list jsons containing tags which replace old tags
   */
  public void updateTags(int applicationId, List<ApplicationTagJson> tagJsons) {
    HashMap<Integer, Map<String, List<String>>> idToTags = new HashMap<>();
    idToTags.put(applicationId, Collections.singletonMap("applicationTags", applicationMapper.createTagES(tagJsons)));
    restTemplate.put(applicationProperties.getApplicationsSearchUpdatePartialUrl(), idToTags);
  }

  /**
   * Delete a note from search-service's database.
   *
   * @param applicationId note application's database ID
   */
  public void deleteNote(int applicationId) {
    restTemplate.delete(applicationProperties.getNoteSearchRemoveUrl(), applicationId);
  }

  /**
   * Insert project to search index.
   *
   * @param projectJson Project to be indexed.
   */
  public void insertProject(ProjectJson projectJson) {
    restTemplate.postForObject(
        applicationProperties.getProjectSearchCreateUrl(),
        projectMapper.createProjectESModel(projectJson),
        ApplicationES.class);
  }

  /**
   * Update project in search index.
   *
   * @param projectJson Project to be updated.
   */
  public void updateProject(ProjectJson projectJson) {
    restTemplate.put(
        applicationProperties.getProjectSearchUpdateUrl(),
        projectMapper.createProjectESModel(projectJson),
        projectJson.getId().intValue());
  }

  /**
   * Update multiple projects to search index.
   *
   * @param projectJsons Projects to be updated.
   */
  public void updateProjects(List<ProjectJson> projectJsons) {
    List<ProjectES> projects = projectJsons.stream().map(p -> projectMapper.createProjectESModel(p)).collect(Collectors.toList());
    restTemplate.put(
        applicationProperties.getProjectsSearchUpdateUrl(),
        projects);
  }

  /**
   * Insert customers to search index.
   *
   * @param customerJson Customer to be indexed.
   */
  public void insertCustomer(CustomerJson customerJson) {
    restTemplate.postForObject(
        applicationProperties.getCustomerSearchCreateUrl(),
        applicationMapper.createCustomerES(customerJson),
        Void.class);
  }

  /**
   * Update multiple customers to search index.
   *
   * @param customerJsons Customers to be updated.
   */
  public void updateCustomers(List<CustomerJson> customerJsons) {
    List<CustomerES> customers = customerJsons.stream().map(a -> applicationMapper.createCustomerES(a)).collect(Collectors.toList());
    restTemplate.put(
        applicationProperties.getCustomersSearchUpdateUrl(),
        customers);
  }

  /**
   * Insert contact to search index.
   *
   * @param contactJson Customer to be indexed.
   */
  public void insertContacts(List<ContactJson> contactJson) {
    restTemplate.postForObject(
        applicationProperties.getContactSearchCreateUrl(),
        contactJson.stream().map(cJson -> new ContactES(cJson.getId(), cJson.getName(), cJson.isActive())).collect(Collectors.toList()),
        Void.class);
  }

  /**
   * Update multiple contacts to search index.
   *
   * @param contactJsons Contacts to be updated.
   */
  public void updateContacts(List<ContactJson> contactJsons) {
    List<ContactES> contacts = applicationMapper.createContactES(contactJsons);
    restTemplate.put(
        applicationProperties.getContactSearchUpdateUrl(),
        contacts);
  }


  /**
   * Find applications by given fields.
   *
   * @param queryParameters list of query parameters
   * @return List of ids of found applications.
   */
  public List<Integer> searchApplication(QueryParameters queryParameters) {
    return search(applicationProperties.getApplicationSearchUrl(), queryParameters);
  }

  /**
   * Find projects by given fields.
   *
   * @param queryParameters list of query parameters
   * @return List of ids of found projects.
   */
  public List<Integer> searchProject(QueryParameters queryParameters) {
    return search(applicationProperties.getProjectSearchUrl(), queryParameters);
  }

  /**
   * Find customers by given fields.
   *
   * @param queryParameters list of query parameters
   * @return List of ids of found customers.
   */
  public List<Integer> searchCustomer(QueryParameters queryParameters) {
    return search(applicationProperties.getCustomerSearchUrl(), queryParameters);
  }

  /**
   * Find contacts by given fields.
   *
   * @param queryParameters list of query parameters
   * @return List of ids of found contacts.
   */
  public List<Integer> searchContact(QueryParameters queryParameters) {
    return search(applicationProperties.getContactSearchUrl(), queryParameters);
  }

  public void updateCustomerOfApplications(
      CustomerJson updatedCustomer, Map<Integer, List<CustomerRoleType>> applicationIdToCustomerRoleType) {
    restTemplate.put(
        applicationProperties.getCustomerApplicationsSearchUpdateUrl(),
        applicationIdToCustomerRoleType,
        updatedCustomer.getId());
  }

  public void updateContactsOfApplications(List<ApplicationWithContactsES> applicationWithContactsESs) {
    if (!applicationWithContactsESs.isEmpty()) {
      restTemplate.put(
          applicationProperties.getContactApplicationsSearchUpdateUrl(),
          applicationWithContactsESs);
    }
  }

  /**
   * Utility method for ordering database results according to the order of search results.
   *
   * @param ids           Ids in the order.
   * @param unorderedList List to be ordered by order of id list
   * @param objectToKey   Function that returns key of given object.
   */
  public static <T> void orderByIdList(List<Integer> ids, List<T> unorderedList, ToIntFunction<T> objectToKey) {
    Map<Integer, Integer> idToOrder = new HashMap<>();
    for (int i = 0; i < ids.size(); ++i) {
      idToOrder.put(ids.get(i), i);
    }
    Collections.sort(unorderedList, Comparator.comparingInt(listItem -> idToOrder.get(objectToKey.applyAsInt(listItem))));
  }


  private List<Integer> search(String searchUrl, QueryParameters queryParameters) {
    ResponseEntity<Integer[]> searchResult = restTemplate.postForEntity(
        searchUrl, queryParameters, Integer[].class);

    return Arrays.asList(searchResult.getBody());
  }
}
