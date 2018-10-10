package fi.hel.allu.servicecore.service;

import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import org.geolatte.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.mapper.CustomerMapper;
import fi.hel.allu.servicecore.mapper.ProjectMapper;
import fi.hel.allu.servicecore.util.PageRequestBuilder;
import fi.hel.allu.servicecore.util.RestResponsePage;

@Service
public class SearchService {

  private static final int ES_SRID = 4326;

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private ApplicationMapper applicationMapper;
  private CustomerMapper customerMapper;
  private ProjectMapper projectMapper;
  private LocationService locationService;

  @Autowired
  public SearchService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      ApplicationMapper applicationMapper,
      CustomerMapper customerMapper,
      ProjectMapper projectMapper,
      LocationService locationService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.applicationMapper = applicationMapper;
    this.customerMapper = customerMapper;
    this.projectMapper = projectMapper;
    this.locationService = locationService;
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
    restTemplate.delete(applicationProperties.getApplicationSearchRemoveUrl(), applicationId);
  }

  /**
   * Delete a draft froms search-service's database.
   * @param applicationId draft's database ID.
   */
  public void deleteDraft(int applicationId) {
    restTemplate.delete(applicationProperties.getApplicationSearchRemoveUrl(), applicationId);
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

  public void deleteProject(int id) {
    restTemplate.delete(applicationProperties.getProjectSearchDeleteUrl(), id);
  }

  /**
   * Insert customers to search index.
   *
   * @param customerJson Customer to be indexed.
   */
  public void insertCustomer(CustomerJson customerJson) {
    restTemplate.postForObject(
        applicationProperties.getCustomerSearchCreateUrl(),
        customerMapper.createCustomerES(customerJson),
        Void.class);
  }

  /**
   * Update multiple customers to search index.
   *
   * @param customerJsons Customers to be updated.
   */
  public void updateCustomers(List<CustomerJson> customerJsons) {
    List<CustomerES> customers = customerJsons.stream()
        .map(a -> customerMapper.createCustomerES(a))
        .collect(Collectors.toList());
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
        contactJson.stream()
            .map(cJson -> new ContactES(cJson.getId(), cJson.getName(), cJson.isActive()))
            .collect(Collectors.toList()),
        Void.class);
  }

  /**
   * Update multiple contacts to search index.
   *
   * @param contactJsons Contacts to be updated.
   */
  public void updateContacts(List<ContactJson> contactJsons) {
    List<ContactES> contacts = customerMapper.createContactES(contactJsons);
    restTemplate.put(
        applicationProperties.getContactSearchUpdateUrl(),
        contacts);
  }


  /**
   * Find applications by given fields.
   *
   * @param queryParameters list of query parameters
   * @pageRequest paging request for the search
   * @param mapper function that maps a list of application ids to the matching
   *          applications.
   * @return List of found applications.
   */
  public Page<ApplicationES> searchApplication(ApplicationQueryParameters queryParameters, Pageable pageRequest, Boolean matchAny) {
    if (queryParameters.getIntersectingGeometry() != null) {
      Geometry intersectingGeometry = locationService.transformCoordinates(queryParameters.getIntersectingGeometry(), ES_SRID);
      queryParameters.setIntersectingGeometry(intersectingGeometry);

    }
    return search(applicationProperties.getApplicationSearchUrl(), queryParameters, pageRequest, matchAny, Function.identity());
  }

  /**
   * Find projects by given fields.
   *
   * @param queryParameters list of query parameters
   * @pageRequest paging request for the search
   * @param mapper function that maps a list of project ids to the matching
   *          projects.
   * @return List of found projects.
   */
  public Page<ProjectJson> searchProject(QueryParameters queryParameters, Pageable pageRequest,
      Function<List<Integer>, List<ProjectJson>> mapper) {
    return search(applicationProperties.getProjectSearchUrl(), queryParameters, pageRequest, false, mapper);
  }

  /**
   * Find customers by given fields.
   *
   * @param queryParameters list of query parameters
   * @pageRequest paging request for the search
   * @param mapper function that maps a list of customer ids to the matching
   *          customers.
   * @return List of found customers.
   */
  public Page<CustomerJson> searchCustomer(QueryParameters queryParameters, Pageable pageRequest,
      Function<List<Integer>, List<CustomerJson>> mapper) {
    return search(applicationProperties.getCustomerSearchUrl(), queryParameters, pageRequest, false, mapper);
  }

  public Page<CustomerJson> searchCustomerByType(CustomerType type, QueryParameters queryParameters,
      Pageable pageRequest, Boolean matchAny, Function<List<Integer>, List<CustomerJson>> mapper) {
    return search(applicationProperties.getCustomerSearchByTypeUrl(type), queryParameters, pageRequest, matchAny, mapper);
  }

  /**
   * Find contacts by given fields.
   *
   * @param queryParameters list of query parameters
   * @pageRequest paging request for the search
   * @param mapper function that maps a list of contact ids to the matching
   *          contacts.
   * @return List of found contacts.
   */
  public Page<ContactJson> searchContact(QueryParameters queryParameters, Pageable pageRequest,
      Function<List<Integer>, List<ContactJson>> mapper) {
    return search(applicationProperties.getContactSearchUrl(), queryParameters, pageRequest, false, mapper);
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


  private <T, R> Page<T> search(String searchUrl, QueryParameters queryParameters, Pageable pageRequest, Boolean matchAny,
      Function<List<R>, List<T>> mapper) {
    ParameterizedTypeReference<RestResponsePage<R>> typeref = new ParameterizedTypeReference<RestResponsePage<R>>() {
    };

    URI targetUri = PageRequestBuilder.fromUriString(searchUrl, pageRequest, matchAny);
    ResponseEntity<RestResponsePage<R>> response = restTemplate.exchange(targetUri, HttpMethod.POST,
        new HttpEntity<>(queryParameters), typeref);

    final Page<R> responsePage = response.getBody();
    final PageRequest responsePageRequest = new PageRequest(responsePage.getNumber(),
        Math.max(1, responsePage.getNumberOfElements()), responsePage.getSort());

    final Page<T> result = new PageImpl<>(mapper.apply(responsePage.getContent()), responsePageRequest,
        responsePage.getTotalElements());
    return result;
  }

}
