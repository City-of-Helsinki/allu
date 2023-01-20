package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.geometry.Constants;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.SupervisionWorkItem;
import fi.hel.allu.model.domain.UpdateTaskOwners;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.mapper.CustomerMapper;
import fi.hel.allu.servicecore.mapper.ProjectMapper;
import fi.hel.allu.servicecore.util.PageRequestBuilder;
import fi.hel.allu.servicecore.util.RestResponsePage;
import org.geolatte.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

@Service
public class SearchService {

  private final ApplicationProperties applicationProperties;
  private final WebClient webClient;

  private final ApplicationMapper applicationMapper;
  private final CustomerMapper customerMapper;
  private final ProjectMapper projectMapper;
  private final LocationService locationService;

  private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
  private static final long REQUEST_TIME_OUT_SECONDS = 10;
  private static final int RETRY_COUNT = 2;
  private static final int RETRY_FIRST_BACKOFF_SECONDS = 3;
  private static final int MAX_IN_MEMORY_SIZE = 16 * 1024 * 1024;

  @Autowired
  public SearchService(
    ApplicationProperties applicationProperties,
    ApplicationMapper applicationMapper,
    CustomerMapper customerMapper,
    ProjectMapper projectMapper,
    LocationService locationService,
    WebClient.Builder webClientBuilder) {
    this.applicationProperties = applicationProperties;
    this.applicationMapper = applicationMapper;
    this.customerMapper = customerMapper;
    this.projectMapper = projectMapper;
    this.locationService = locationService;
    ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder().codecs(
            codecs -> codecs.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE)).build();
    webClient = webClientBuilder.exchangeStrategies(exchangeStrategies)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  public void insertApplication(ApplicationJson applicationJson) {
    executePostWithRetry(applicationProperties.getApplicationSearchCreateUrl(),
     applicationMapper.createApplicationESModel(applicationJson));
  }

  /**
   * Update multiple applications to search index.
   *
   * @param applicationJsons Applications to be updated.
   */
  public void updateApplications(List<ApplicationJson> applicationJsons) {
    updateApplications(applicationJsons, false);
  }

  public void updateApplications(List<ApplicationJson> applicationJsons, boolean waitRefresh) {
    List<ApplicationES> applications =
      applicationJsons.stream().map(applicationMapper::createApplicationESModel).collect(Collectors.toList());
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
  public void updateTags(int applicationId, List<ApplicationTagJson> tagJsons) {
    HashMap<Integer, Map<String, List<String>>> idToTags = new HashMap<>();
    idToTags.put(applicationId, Collections.singletonMap("applicationTags", applicationMapper.createTagES(tagJsons)));
    executePutWithRetry(applicationProperties.getApplicationsSearchUpdatePartialUrl(), idToTags);
  }

  /**
   * Updates field of application with given value in search index
   */
  public <T> void updateApplicationField(int applicationId, String fieldName, T fieldValue, boolean waitRefresh) {
    HashMap<Integer, Map<String, T>> applicationFields = new HashMap<>();
    applicationFields.put(applicationId, Collections.singletonMap(fieldName, fieldValue));
    URI uri = UriComponentsBuilder.fromHttpUrl(applicationProperties.getApplicationsSearchUpdatePartialUrl())
      .queryParam("waitRefresh", waitRefresh)
      .buildAndExpand().toUri();

    put(uri.toString(), applicationFields, waitRefresh);
  }

  public <T> void updateApplicationField(List<Integer> applicationIds, String fieldName, T fieldValue, boolean waitRefresh) {
    HashMap<Integer, Map<String, T>> applicationFields = new HashMap<>();
    applicationIds.forEach(id ->  applicationFields.put(id, Collections.singletonMap(fieldName, fieldValue)));
    URI uri = UriComponentsBuilder.fromHttpUrl(applicationProperties.getApplicationsSearchUpdatePartialUrl())
            .queryParam("waitRefresh", waitRefresh)
            .buildAndExpand().toUri();
    put(uri.toString(), applicationFields, waitRefresh);
  }

  /**
   * Update the customer and related contacts for an application
   *
   * @param applicationId application id
   * @param customerWithContactsJson customer structure to update for the application
   */
  public void updateApplicationCustomerWithContacts(int applicationId, CustomerWithContactsJson customerWithContactsJson) {
    Map<CustomerRoleType, CustomerWithContactsES> customersByRoleType = Collections.singletonMap(
      customerWithContactsJson.getRoleType(), customerMapper.createWithContactsES(customerWithContactsJson));
    executePutWithRetry(applicationProperties.getApplicationsSearchUpdateCustomersWithContactsUrl(), customersByRoleType, applicationId);
  }

  /**
   * Delete a note from search-service's database.
   *
   * @param applicationId note application's database ID
   */
  public void deleteNote(int applicationId) {
    executeDeleteWithRetry(applicationProperties.getApplicationSearchRemoveUrl(), applicationId);
  }

  /**
   * Delete a draft froms search-service's database.
   * @param applicationId draft's database ID.
   */
  public void deleteDraft(int applicationId) {
    executeDeleteWithRetry(applicationProperties.getApplicationSearchRemoveUrl(), applicationId);
  }

  /**
   * Insert project to search index.
   *
   * @param projectJson Project to be indexed.
   */
  public void insertProject(ProjectJson projectJson) {
    executePostWithRetry(applicationProperties.getProjectSearchCreateUrl(), projectMapper.createProjectESModel(projectJson));
  }

  /**
   * Update project in search index.
   *
   * @param projectJson Project to be updated.
   */
  public void updateProject(ProjectJson projectJson) {
    executePutWithRetry(applicationProperties.getProjectSearchUpdateUrl(), projectMapper.createProjectESModel(projectJson), projectJson.getId());
  }

  /**
   * Update multiple projects to search index.
   *
   * @param projectJsons Projects to be updated.
   */
  public void updateProjects(List<ProjectJson> projectJsons) {
    List<ProjectES> projects = projectJsons.stream().map(projectMapper::createProjectESModel).collect(Collectors.toList());
    executePutWithRetry(applicationProperties.getProjectsSearchUpdateUrl(), projects);
  }

  public void deleteProject(int id) {
    executeDeleteWithRetry(applicationProperties.getProjectSearchDeleteUrl(), id);
  }

  /**
   * Insert customers to search index.
   *
   * @param customerJson Customer to be indexed.
   */
  public void insertCustomer(CustomerJson customerJson) {
    executePostWithRetry(applicationProperties.getCustomerSearchCreateUrl(), customerMapper.createCustomerES(customerJson));
  }

  /**
   * Update multiple customers to search index.
   *
   * @param customerJsons Customers to be updated.
   */
  public void updateCustomers(List<CustomerJson> customerJsons) {
    List<CustomerES> customers = customerJsons.stream()
      .map(customerMapper::createCustomerES)
      .collect(Collectors.toList());
    executePutWithRetry(applicationProperties.getCustomersSearchUpdateUrl(), customers);
  }

  /**
   * Insert contact to search index.
   *
   * @param contactJson Customer to be indexed.
   */
  public void insertContacts(List<ContactJson> contactJson) {
    List<ContactES> contactESList = contactJson.stream()
      .map(cJson -> new ContactES(cJson.getId(), cJson.getName(), cJson.isActive()))
      .collect(Collectors.toList());
    executePostWithRetry(applicationProperties.getContactSearchCreateUrl(), contactESList);
  }

  /**
   * Update multiple contacts to search index.
   *
   * @param contactJsons Contacts to be updated.
   */
  public void updateContacts(List<ContactJson> contactJsons) {
    List<ContactES> contacts = customerMapper.createContactES(contactJsons);
    executePutWithRetry(applicationProperties.getContactSearchUpdateUrl(), contacts);
  }

  /**
   * Find applications by given fields.
   *
   * @param queryParameters list of query parameters
   * @pageRequest paging request for the search
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

  public Page<SupervisionWorkItem> searchSupervisionTask(QueryParameters queryParameters, Pageable pageRequest, Boolean matchAny) {
    return search(applicationProperties.getSupervisionTaskSearchUrl(), queryParameters, pageRequest, matchAny, Function.identity(),
                  new ParameterizedTypeReference<RestResponsePage<SupervisionWorkItem>>() {});
  }

  public void updateSupervisionTasksStatus(Integer applicationId, StatusType statusType) {

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(applicationProperties.postSupervisionStatusUpdate());

    builder.queryParam("applicationId", applicationId);
    String uri = builder.build().toUri().toString();
    executePostWithRetry(uri, statusType);
  }

  public void updateSupervisionTasks(SupervisionWorkItem supervisionWorkItem) {
    List<SupervisionWorkItem> tasks = new ArrayList<>();
    tasks.add(supervisionWorkItem);
    executePutWithRetry(applicationProperties.getSupervisionTaksSearchUpdateUrl(), new ArrayList<>(tasks));
  }

  public void deleteSupervisionTask(Integer id) {
    executeDeleteWithRetry(applicationProperties.getSupervisionTaskSearchDeleteUrl(), id);
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

  public void updateCustomerOfApplications(CustomerJson updatedCustomer, Map<Integer, List<CustomerRoleType>> applicationIdToCustomerRoleType) {
    executePutWithRetry(applicationProperties.getCustomerApplicationsSearchUpdateUrl(), applicationIdToCustomerRoleType, updatedCustomer.getId());
  }

  public void updateContactsOfApplications(List<ApplicationWithContactsES> applicationWithContactsESs) {
    if (!applicationWithContactsESs.isEmpty()) {
      executePutWithRetry(applicationProperties.getContactApplicationsSearchUpdateUrl(), applicationWithContactsESs);
    }
  }

  public void insertSupervisionTask(SupervisionWorkItem supervisionWorkItem){
    executePostWithRetry(applicationProperties.getSupervisionTaskSearchCreateUrl(),
                         supervisionWorkItem);
  }

  public void updateSupervisionTaskOwner(UpdateTaskOwners updateTaskOwners){
    executePutWithRetry(applicationProperties.getSupervisionTaskOwnerUpdateSearchUrl(),
                        updateTaskOwners);
  }

  public void removeSupervisionTaskOwner(List<Integer> taskIds){
    executePutWithRetry(applicationProperties.getSupervisionTaskSearchOwnerRemoveUrl(),
                        taskIds);
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
    unorderedList.sort(Comparator.comparingInt(listItem -> idToOrder.get(objectToKey.applyAsInt(listItem))));
  }


  private <T, R> Page<T> search(String searchUrl, QueryParameters queryParameters, Pageable pageRequest, Boolean matchAny,
                                Function<List<R>, List<T>> mapper, ParameterizedTypeReference<RestResponsePage<R>> typeref) {
    URI targetUri = PageRequestBuilder.fromUriString(searchUrl, pageRequest, matchAny);
    RestResponsePage<R> responsePage = webClient
        .post()
        .uri(targetUri)
        .bodyValue(queryParameters)
        .retrieve()
        .bodyToMono(typeref)
        .block(Duration.ofSeconds(REQUEST_TIME_OUT_SECONDS));

    final PageRequest responsePageRequest = PageRequest.of(responsePage.getNumber(),
      Math.max(1, responsePage.getNumberOfElements()), responsePage.getSort());
     return new PageImpl<>(mapper.apply(responsePage.getContent()), responsePageRequest,
      responsePage.getTotalElements());
  }

  private <T> void put(String url, T request, boolean waitRefresh, Object... uriVariables) {
    if (waitRefresh) {
      webClient
        .put()
        .uri(url, uriVariables)
        .bodyValue(request)
        .retrieve()
              .bodyToMono(Void.class)
        .block(Duration.ofSeconds(REQUEST_TIME_OUT_SECONDS));
    } else {
      executePutWithRetry(url, request, uriVariables);
    }
  }

  private <T> void executePutWithRetry(String uri, T requestBody, Object... uriVariables) {
    webClient
    .put()
    .uri(uri, uriVariables)
    .bodyValue(requestBody)
    .retrieve()
    .bodyToMono(Void.class)
      .retryWhen(Retry.backoff(RETRY_COUNT, Duration.ofSeconds(RETRY_FIRST_BACKOFF_SECONDS)))
    .subscribe(c -> {}, t -> onError(t, uri, HttpMethod.PUT));
  }

  public <T> void executePostWithRetry(String uri, T requestBody) {
    webClient
    .post()
    .uri(uri)
    .bodyValue(requestBody)
    .retrieve()
    .bodyToMono(Void.class)
    .retryWhen(Retry.backoff(RETRY_COUNT, Duration.ofSeconds(RETRY_FIRST_BACKOFF_SECONDS)))
    .subscribe(c -> {}, t -> onError(t, uri, HttpMethod.POST));
  }

  public void executeDeleteWithRetry(String uri, Object... uriVariables) {
    webClient
    .delete()
    .uri(uri, uriVariables)
    .retrieve()
    .bodyToMono(Void.class)
      .retryWhen(Retry.backoff(RETRY_COUNT, Duration.ofSeconds(RETRY_FIRST_BACKOFF_SECONDS)))
    .subscribe(c -> {}, t -> onError(t, uri, HttpMethod.DELETE));
  }

  private void  onError(Throwable t, String uri, HttpMethod method) {
    logger.warn("Operation " + method.name() + " failed on " + uri, t);
  }

}