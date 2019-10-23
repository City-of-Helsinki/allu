package fi.hel.allu.servicecore.service;

import java.net.URI;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import org.geolatte.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import fi.hel.allu.common.domain.geometry.Constants;
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

  private ApplicationProperties applicationProperties;
  private AsyncRestTemplate restTemplate;
  private ApplicationMapper applicationMapper;
  private CustomerMapper customerMapper;
  private ProjectMapper projectMapper;
  private LocationService locationService;

  private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
  private static final long DELAY = 500l;
  private static final long MAX_DELAY = 5000l;
  private static final long DELAY_MULTIPLIER = 2;

  @Autowired
  public SearchService(
    ApplicationProperties applicationProperties,
    AsyncRestTemplate restTemplate,
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

  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = DELAY, maxDelay = MAX_DELAY, multiplier = DELAY_MULTIPLIER))
  public void insertApplication(ApplicationJson applicationJson) {
    restTemplate.postForEntity(
      applicationProperties.getApplicationSearchCreateUrl(),
      new HttpEntity<>(applicationMapper.createApplicationESModel(applicationJson)),
      ApplicationES.class);
  }

  /**
   * Update multiple applications to search index.
   *
   * @param applicationJsons Applications to be updated.
   */
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = DELAY, maxDelay = MAX_DELAY, multiplier = DELAY_MULTIPLIER))
  public void updateApplications(List<ApplicationJson> applicationJsons) {
    updateApplications(applicationJsons, false);
  }

  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = DELAY, maxDelay = MAX_DELAY, multiplier = DELAY_MULTIPLIER))
  public void updateApplications(List<ApplicationJson> applicationJsons, boolean waitRefresh) {
    List<ApplicationES> applications =
      applicationJsons.stream().map(a -> applicationMapper.createApplicationESModel(a)).collect(Collectors.toList());
    URI uri = UriComponentsBuilder.fromHttpUrl(applicationProperties.getApplicationsSearchUpdateUrl())
      .queryParam("waitRefresh", waitRefresh)
      .buildAndExpand().toUri();

    put(uri.toString(), applications, waitRefresh);
  }

  /**
   * Updates tags of given application id into index
   * @param applicationId id of application to update
   * @param tagJsons list jsons containing tags which replace old tags
   */
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = DELAY, maxDelay = MAX_DELAY, multiplier = DELAY_MULTIPLIER))
  public void updateTags(int applicationId, List<ApplicationTagJson> tagJsons) {
    HashMap<Integer, Map<String, List<String>>> idToTags = new HashMap<>();
    idToTags.put(applicationId, Collections.singletonMap("applicationTags", applicationMapper.createTagES(tagJsons)));
    restTemplate.put(applicationProperties.getApplicationsSearchUpdatePartialUrl(), new HttpEntity<>(idToTags));
  }

  /**
   * Updates field of application with given value in search index
   */
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = DELAY, maxDelay = MAX_DELAY, multiplier = DELAY_MULTIPLIER))
  public <T> void updateApplicationField(int applicationId, String fieldName, T fieldValue, boolean waitRefresh) {
    HashMap<Integer, Map<String, T>> applicationFields = new HashMap<>();
    applicationFields.put(applicationId, Collections.singletonMap(fieldName, fieldValue));
    URI uri = UriComponentsBuilder.fromHttpUrl(applicationProperties.getApplicationsSearchUpdatePartialUrl())
      .queryParam("waitRefresh", waitRefresh)
      .buildAndExpand().toUri();

    put(uri.toString(), applicationFields, waitRefresh);
  }

  /**
   * Delete a note from search-service's database.
   *
   * @param applicationId note application's database ID
   */
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = DELAY, maxDelay = MAX_DELAY, multiplier = DELAY_MULTIPLIER))
  public void deleteNote(int applicationId) {
    restTemplate.delete(applicationProperties.getApplicationSearchRemoveUrl(), applicationId);
  }

  /**
   * Delete a draft froms search-service's database.
   * @param applicationId draft's database ID.
   */
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = DELAY, maxDelay = MAX_DELAY, multiplier = DELAY_MULTIPLIER))
  public void deleteDraft(int applicationId) {
    restTemplate.delete(applicationProperties.getApplicationSearchRemoveUrl(), applicationId);
  }

  /**
   * Insert project to search index.
   *
   * @param projectJson Project to be indexed.
   */
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = DELAY, maxDelay = MAX_DELAY, multiplier = DELAY_MULTIPLIER))
  public void insertProject(ProjectJson projectJson) {
    restTemplate.postForEntity(
      applicationProperties.getProjectSearchCreateUrl(),
      new HttpEntity<>(projectMapper.createProjectESModel(projectJson)),
      ApplicationES.class);
  }

  /**
   * Update project in search index.
   *
   * @param projectJson Project to be updated.
   */
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = DELAY, maxDelay = MAX_DELAY, multiplier = DELAY_MULTIPLIER))
  public void updateProject(ProjectJson projectJson) {
    restTemplate.put(
      applicationProperties.getProjectSearchUpdateUrl(),
      new HttpEntity<>(projectMapper.createProjectESModel(projectJson)),
      projectJson.getId().intValue());
  }

  /**
   * Update multiple projects to search index.
   *
   * @param projectJsons Projects to be updated.
   */
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = DELAY, maxDelay = MAX_DELAY, multiplier = DELAY_MULTIPLIER))
  public void updateProjects(List<ProjectJson> projectJsons) {
    List<ProjectES> projects = projectJsons.stream().map(p -> projectMapper.createProjectESModel(p)).collect(Collectors.toList());
    restTemplate.put(
      applicationProperties.getProjectsSearchUpdateUrl(),
      new HttpEntity<>(projects));
  }

  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = DELAY, maxDelay = MAX_DELAY, multiplier = DELAY_MULTIPLIER))
  public void deleteProject(int id) {
    restTemplate.delete(applicationProperties.getProjectSearchDeleteUrl(), id);
  }

  /**
   * Insert customers to search index.
   *
   * @param customerJson Customer to be indexed.
   */
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = DELAY, maxDelay = MAX_DELAY, multiplier = DELAY_MULTIPLIER))
  public void insertCustomer(CustomerJson customerJson) {
    restTemplate.postForEntity(
      applicationProperties.getCustomerSearchCreateUrl(),
      new HttpEntity<>(customerMapper.createCustomerES(customerJson)),
      Void.class);
  }

  /**
   * Update multiple customers to search index.
   *
   * @param customerJsons Customers to be updated.
   */
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = DELAY, maxDelay = MAX_DELAY, multiplier = DELAY_MULTIPLIER))
  public void updateCustomers(List<CustomerJson> customerJsons) {
    List<CustomerES> customers = customerJsons.stream()
      .map(a -> customerMapper.createCustomerES(a))
      .collect(Collectors.toList());
    restTemplate.put(
      applicationProperties.getCustomersSearchUpdateUrl(),
      new HttpEntity<>(customers));
  }

  /**
   * Insert contact to search index.
   *
   * @param contactJson Customer to be indexed.
   */
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = DELAY, maxDelay = MAX_DELAY, multiplier = DELAY_MULTIPLIER))
  public void insertContacts(List<ContactJson> contactJson) {
    List<ContactES> contactESList = contactJson.stream()
      .map(cJson -> new ContactES(cJson.getId(), cJson.getName(), cJson.isActive()))
      .collect(Collectors.toList());

    restTemplate.postForEntity(
      applicationProperties.getContactSearchCreateUrl(),
      new HttpEntity<>(contactESList),
      Void.class);
  }

  /**
   * Update multiple contacts to search index.
   *
   * @param contactJsons Contacts to be updated.
   */
  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = DELAY, maxDelay = MAX_DELAY, multiplier = DELAY_MULTIPLIER))
  public void updateContacts(List<ContactJson> contactJsons) {
    List<ContactES> contacts = customerMapper.createContactES(contactJsons);
    restTemplate.put(
      applicationProperties.getContactSearchUpdateUrl(),
      new HttpEntity<>(contacts));
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
      Geometry intersectingGeometry = locationService.transformCoordinates(queryParameters.getIntersectingGeometry(), Constants.ELASTIC_SEARCH_SRID);
      queryParameters.setIntersectingGeometry(intersectingGeometry);
    }
    return search(applicationProperties.getApplicationSearchUrl(), queryParameters, pageRequest, matchAny, Function.identity(),
      new ParameterizedTypeReference<RestResponsePage<ApplicationES>>() {});
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
    return search(applicationProperties.getProjectSearchUrl(), queryParameters, pageRequest, false, mapper,
      new ParameterizedTypeReference<RestResponsePage<Integer>>() {});
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
    return search(applicationProperties.getCustomerSearchUrl(), queryParameters, pageRequest, false, mapper,
      new ParameterizedTypeReference<RestResponsePage<Integer>>() {});
  }

  public Page<CustomerJson> searchCustomerByType(CustomerType type, QueryParameters queryParameters,
                                                 Pageable pageRequest, Boolean matchAny, Function<List<Integer>, List<CustomerJson>> mapper) {
    return search(applicationProperties.getCustomerSearchByTypeUrl(type), queryParameters, pageRequest, matchAny, mapper,
      new ParameterizedTypeReference<RestResponsePage<Integer>>() {});
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
    return search(applicationProperties.getContactSearchUrl(), queryParameters, pageRequest, false, mapper,
      new ParameterizedTypeReference<RestResponsePage<Integer>>() {});

  }

  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = DELAY, maxDelay = MAX_DELAY, multiplier = DELAY_MULTIPLIER))
  public void updateCustomerOfApplications(
    CustomerJson updatedCustomer, Map<Integer, List<CustomerRoleType>> applicationIdToCustomerRoleType) {
    restTemplate.put(
      applicationProperties.getCustomerApplicationsSearchUpdateUrl(),
      new HttpEntity<>(applicationIdToCustomerRoleType),
      updatedCustomer.getId());
  }

  @Retryable(maxAttempts = 3, backoff = @Backoff(delay = DELAY, maxDelay = MAX_DELAY, multiplier = DELAY_MULTIPLIER))
  public void updateContactsOfApplications(List<ApplicationWithContactsES> applicationWithContactsESs) {
    if (!applicationWithContactsESs.isEmpty()) {
      restTemplate.put(
        applicationProperties.getContactApplicationsSearchUpdateUrl(),
        new HttpEntity<>(applicationWithContactsESs));
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
                                Function<List<R>, List<T>> mapper, ParameterizedTypeReference<RestResponsePage<R>> typeref) {
    URI targetUri = PageRequestBuilder.fromUriString(searchUrl, pageRequest, matchAny);
    ResponseEntity<RestResponsePage<R>> response = restTemplate.getRestOperations().exchange(targetUri, HttpMethod.POST,
      new HttpEntity<>(queryParameters), typeref);

    final Page<R> responsePage = response.getBody();
    final PageRequest responsePageRequest = new PageRequest(responsePage.getNumber(),
      Math.max(1, responsePage.getNumberOfElements()), responsePage.getSort());

    final Page<T> result = new PageImpl<>(mapper.apply(responsePage.getContent()), responsePageRequest,
      responsePage.getTotalElements());
    return result;
  }

  private void put(String url, Object request, boolean waitRefresh, Object... uriVariables) {
    if (waitRefresh) {
      restTemplate.getRestOperations().put(url, request, uriVariables);
    } else {
      restTemplate.put(url, new HttpEntity<>(request), uriVariables);
    }
  }

  @Recover
  public void recover(Exception e) {
    logger.error("Search update operation failed", e);
  }
}
