package fi.hel.allu.servicecore.service;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.Project;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ContactES;
import fi.hel.allu.search.domain.CustomerES;
import fi.hel.allu.search.domain.ProjectES;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.mapper.ProjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Service class to manage database syncing from model-service to
 * search-service.
 */
@Service
public class SearchSyncService {
  private static final Logger logger = LoggerFactory.getLogger(SearchSyncService.class);
  private static final int PAGE_SIZE = 100;

  private RestTemplate restTemplate;
  private ApplicationProperties applicationProperties;
  private ApplicationMapper applicationMapper;
  private ProjectMapper projectMapper;

  @Autowired
  public SearchSyncService(RestTemplate restTemplate, ApplicationProperties applicationProperties,
      ApplicationMapper applicationMapper, ProjectMapper projectMapper) {
    this.restTemplate = restTemplate;
    this.applicationProperties = applicationProperties;
    this.applicationMapper = applicationMapper;
    this.projectMapper = projectMapper;
  }

  /**
   * Sync application, project, customer, and contact data from model-service to
   * search-service
   */
  public void sync() {
    logger.debug("Database sync started");
    startSync();
    try {
      syncApplications();
      syncProjects();
      syncCustomers();
      syncContacts();
      endSync();
    } catch (SyncFailedException e) {
      logger.error("Sync failure: " + e.getMessage() + ", canceling sync.");
      cancelSync();
      if (e.getCause() != null) {
        throw (RuntimeException) e.getCause();
      }
    }
  }

  /*
   * Generic way to talk to server. Executes the given operation and checks that
   * the result is OK. If there's error, throws a SyncFailedException. Also, if
   * any exception is thrown, wraps it inside a SyncFailedException.
   */
  private <T> T talkToServer(String operationDescription, Supplier<ResponseEntity<T>> restOperation) {
    ResponseEntity<T> response;
    try {
      response = restOperation.get();
    } catch (RuntimeException e) {
      throw new SyncFailedException(operationDescription + ": Exception in REST", e);
    }
    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new SyncFailedException(operationDescription + " failed", response.getStatusCode());
    }
    return response.getBody();
  }

  private void startSync() {
    talkToServer("Start sync",
        () -> restTemplate.postForEntity(applicationProperties.getStartSearchSyncUrl(), null, Void.class));
  }

  private void endSync() {
    talkToServer("End sync",
        () -> restTemplate.postForEntity(applicationProperties.getCommitSearchSyncUrl(), null, Void.class));
  }

  private void cancelSync() {
    // Failure to cancel shouldn't cause SyncFailed, so skip talkToServer.
    restTemplate.postForEntity(applicationProperties.getCancelSearchSyncUrl(), null, Void.class);
  }

  /*
   * Generic way to sync one type of data. Calls fetcher to retrieve pages of
   * elements from model-service, then maps them to ES with mapper and calls
   * sender to transmit the results to search-service.
   */
  <T, U> void syncData(IntFunction<Page<T>> fetcher, Consumer<List<U>> sender, Function<T, U> mapper) {
    int page = 0;
    Page<T> fromModel;
    do {
      fromModel = fetcher.apply(page);
      if (fromModel.hasContent()) {
        List<U> toSearch = fromModel.getContent().stream().map(mapper).collect(Collectors.toList());
        sender.accept(toSearch);
      }
      page++;
    } while (!fromModel.isLast());
  }

  void syncApplications() {
    syncData(p -> fetchApplications(p), l -> sendApplications(l), a -> mapToES(a));
  }

  void syncProjects() {
    syncData(p -> fetchProjects(p), l -> sendProjects(l), p -> mapToES(p));
  }

  void syncCustomers() {
    syncData(p -> fetchCustomers(p), l -> sendCustomers(l), c -> mapToES(c));
  }

  void syncContacts() {
    syncData(p -> fetchContacts(p), l -> sendContacts(l), c -> mapToES(c));
  }

  Page<Application> fetchApplications(int pageNum) {
    ParameterizedTypeReference<Page<Application>> typeref = new ParameterizedTypeReference<Page<Application>>() {
    };
    return talkToServer("Fetch applications", () -> restTemplate
        .exchange(applicationProperties.getAllApplicationsUrl(), HttpMethod.GET, null, typeref, pageNum, PAGE_SIZE));
  }

  void sendApplications(List<ApplicationES> apps) {
    talkToServer("Send applications",
        () -> restTemplate.postForEntity(applicationProperties.getSyncApplicationsUrl(), apps, Void.class));
  }

  Page<Project> fetchProjects(int pageNum) {
    ParameterizedTypeReference<Page<Project>> typeref = new ParameterizedTypeReference<Page<Project>>() {
    };
    return talkToServer("Fetch projects", () -> restTemplate.exchange(applicationProperties.getAllProjectsUrl(),
        HttpMethod.GET, null, typeref, pageNum, PAGE_SIZE));
  }

  void sendProjects(List<ProjectES> projects) {
    talkToServer("Send projects",
        () -> restTemplate.postForEntity(applicationProperties.getSyncProjectsUrl(), projects, Void.class));
  }

  Page<Customer> fetchCustomers(int pageNum) {
    ParameterizedTypeReference<Page<Customer>> typeref = new ParameterizedTypeReference<Page<Customer>>() {
    };
    return talkToServer("Fetch customers", () -> restTemplate.exchange(applicationProperties.getAllCustomersUrl(),
        HttpMethod.GET, null, typeref, pageNum, PAGE_SIZE));
  }

  void sendCustomers(List<CustomerES> customers) {
    talkToServer("Send customers",
        () -> restTemplate.postForEntity(applicationProperties.getSyncCustomersUrl(), customers, Void.class));
  }

  Page<Contact> fetchContacts(int pageNum) {
    ParameterizedTypeReference<Page<Contact>> typeref = new ParameterizedTypeReference<Page<Contact>>() {
    };
    return talkToServer("Fetch contacts", () -> restTemplate.exchange(applicationProperties.getAllContactsUrl(),
        HttpMethod.GET, null, typeref, pageNum, PAGE_SIZE));
  }

  void sendContacts(List<ContactES> contacts) {
    talkToServer("Send contacts",
        () -> restTemplate.postForEntity(applicationProperties.getSyncProjectsUrl(), contacts, Void.class));
  }

  @SuppressWarnings("serial")
  private class SyncFailedException extends RuntimeException {

    public SyncFailedException(String message, Throwable cause) {
      super(message, cause);
    }

    public SyncFailedException(String message, HttpStatus statusCode) {
      super(message + "(status=" + statusCode.value() + ", " + statusCode.name() + ")");
      initCause(null);
    }
  }

  ApplicationES mapToES(Application application) {
    return applicationMapper.createApplicationESModel(applicationMapper.mapApplicationToJson(application));
  }

  ProjectES mapToES(Project project) {
    return projectMapper.createProjectESModel(projectMapper.mapProjectToJson(project));
  }

  CustomerES mapToES(Customer customer) {
    return new CustomerES(customer.getId(), customer.getName(), customer.getRegistryKey(), customer.getOvt(),
        customer.getType(), customer.isActive());
  }

  ContactES mapToES(Contact contact) {
    return new ContactES(contact.getId(), contact.getName(), contact.isActive());
  }

}
