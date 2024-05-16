package fi.hel.allu.servicecore.service;

import fi.hel.allu.model.domain.*;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ContactES;
import fi.hel.allu.search.domain.CustomerES;
import fi.hel.allu.search.domain.ProjectES;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.mapper.ProjectMapper;
import fi.hel.allu.servicecore.util.RestResponsePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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
  private static final int PAGE_SIZE = 1000;

  private final RestTemplate restTemplate;
  private final ApplicationProperties applicationProperties;
  private final ProjectMapper projectMapper;
  private final ApplicationServiceComposer applicationServiceComposer;

  @Autowired
  public SearchSyncService(RestTemplate restTemplate, ApplicationProperties applicationProperties,
                           ProjectMapper projectMapper, ApplicationServiceComposer applicationServiceComposer) {
    this.restTemplate = restTemplate;
    this.applicationProperties = applicationProperties;
    this.projectMapper = projectMapper;
    this.applicationServiceComposer = applicationServiceComposer;
  }

  /**
   * Sync application, project, customer, and contact data from model-service to
   * search-service
   */
  @Async
  public void sync() {
    logger.debug("Database sync started");
    startSync();
    try {
      syncApplications();
      syncProjects();
      syncCustomers();
      syncContacts();
      syncSupervisionTasks();
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
    logger.info("Data sync started");
    talkToServer("Start sync",
        () -> restTemplate.postForEntity(applicationProperties.getStartSearchSyncUrl(), null, Void.class));
  }

  private void endSync() {
    logger.info("Data sync finished");
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
  private <T, U> void syncData(IntFunction<Page<T>> fetcher, Consumer<List<U>> sender, Function<T, U> mapper) {
    int page = 0;
    Page<T> fromModel;
    do {
      fromModel = fetcher.apply(page);
      logger.info("Page {} / {}", page + 1, fromModel.getTotalPages());
      if (fromModel.getNumberOfElements() > 0) {
        List<U> toSearch = fromModel.getContent().stream().map(mapper).collect(Collectors.toList());
        sender.accept(toSearch);
      }
      page++;
    } while (!fromModel.isLast());
  }

  private <T, U> void syncDataList(IntFunction<Page<T>> fetcher, Consumer<List<U>> sender, Function<List<T>, List<U>> mapper) {
    int page = 0;
    Page<T> fromModel;
    do {
      fromModel = fetcher.apply(page);
      logger.info("Page {} / {}", page + 1, fromModel.getTotalPages());
      if (fromModel.getNumberOfElements() > 0) {
        List<U> toSearch = mapper.apply(fromModel.getContent());
        sender.accept(toSearch);
      }
      page++;
    } while (!fromModel.isLast());
  }

  private void syncApplications() {
    logger.info("Application sync started");
    syncDataList(this::fetchApplications, this::sendApplications, this::mapListToES);
    logger.info("Application sync finished");
  }

  private void syncProjects() {
    logger.info("Project sync started");
    syncData(this::fetchProjects, this::sendProjects, this::mapToES);
    logger.info("Project sync finished");
  }

  private void syncCustomers() {
    logger.info("Customer sync started");
    syncData(this::fetchCustomers, this::sendCustomers, this::mapToES);
    logger.info("Customer sync finished");
  }

  private void syncContacts() {
    logger.info("Contact sync started");
    syncData(this::fetchContacts, this::sendContacts, this::mapToES);
    logger.info("Contact sync finished");
  }

  private void syncSupervisionTasks() {
    logger.info("SupervisionTasks sync started");
    int page = 0;
    Page<SupervisionWorkItem> fromModel;
    do {
      fromModel = fetchSupervisionTasks(page);
      logger.info("Page {} / {}", page + 1, fromModel.getTotalPages());
      if (fromModel.getNumberOfElements() > 0) {
        sendSupervisionTasks(fromModel.getContent());
      }
      page++;
    } while (!fromModel.isLast());
    logger.info("SupervisionTasks sync finished");
  }

  private Page<Application> fetchApplications(int pageNum) {
    ParameterizedTypeReference<RestResponsePage<Application>> typeref = new ParameterizedTypeReference<RestResponsePage<Application>>() {
    };
    return talkToServer("Fetch applications", () -> restTemplate
        .exchange(applicationProperties.getAllApplicationsUrl(), HttpMethod.GET, null, typeref, pageNum, PAGE_SIZE));
  }

  private void sendApplications(List<ApplicationES> apps) {
    talkToServer("Send applications",
        () -> restTemplate.postForEntity(applicationProperties.getSyncApplicationsUrl(), apps, Void.class));
  }

  private Page<Project> fetchProjects(int pageNum) {
    ParameterizedTypeReference<RestResponsePage<Project>> typeref = new ParameterizedTypeReference<RestResponsePage<Project>>() {
    };
    return talkToServer("Fetch projects", () -> restTemplate.exchange(applicationProperties.getAllProjectsUrl(),
        HttpMethod.GET, null, typeref, pageNum, PAGE_SIZE));
  }

  private void sendProjects(List<ProjectES> projects) {
    talkToServer("Send projects",
        () -> restTemplate.postForEntity(applicationProperties.getSyncProjectsUrl(), projects, Void.class));
  }

  private Page<Customer> fetchCustomers(int pageNum) {
    ParameterizedTypeReference<RestResponsePage<Customer>> typeref = new ParameterizedTypeReference<RestResponsePage<Customer>>() {
    };
    return talkToServer("Fetch customers", () -> restTemplate.exchange(applicationProperties.getAllCustomersUrl(),
        HttpMethod.GET, null, typeref, pageNum, PAGE_SIZE));
  }

  private void sendCustomers(List<CustomerES> customers) {
    talkToServer("Send customers",
        () -> restTemplate.postForEntity(applicationProperties.getSyncCustomersUrl(), customers, Void.class));
  }

  private Page<Contact> fetchContacts(int pageNum) {
    ParameterizedTypeReference<RestResponsePage<Contact>> typeref = new ParameterizedTypeReference<RestResponsePage<Contact>>() {
    };
    return talkToServer("Fetch contacts", () -> restTemplate.exchange(applicationProperties.getAllContactsUrl(),
        HttpMethod.GET, null, typeref, pageNum, PAGE_SIZE));
  }

  private Page<SupervisionWorkItem> fetchSupervisionTasks(int pageNum) {
    ParameterizedTypeReference<RestResponsePage<SupervisionWorkItem>> typeref = new ParameterizedTypeReference<RestResponsePage<SupervisionWorkItem>>() {
    };
    return talkToServer("Fetch supervisiontasks", () -> restTemplate.exchange(applicationProperties.getAllSupervisionTasksUrl(),
                                                                      HttpMethod.GET, null, typeref, pageNum, PAGE_SIZE));
  }

  private void sendContacts(List<ContactES> contacts) {
    talkToServer("Send contacts",
        () -> restTemplate.postForEntity(applicationProperties.getSyncContactsUrl(), contacts, Void.class));
  }

  private void sendSupervisionTasks(List<SupervisionWorkItem> supervisionTasks) {
    talkToServer("Send supervisiontasks",
                 () -> restTemplate.postForEntity(applicationProperties.getSyncSupervisionTaskUrl(), supervisionTasks, Void.class));
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

  private List<ApplicationES> mapListToES(List<Application> application) {
   return applicationServiceComposer.getCompactPopulatedApplicationEsList(application);
  }

  private ProjectES mapToES(Project project) {
    return projectMapper.createProjectESModel(projectMapper.mapProjectToJson(project));
  }

  private CustomerES mapToES(Customer customer) {
    return new CustomerES(customer.getId(), customer.getName(), customer.getRegistryKey(), customer.getOvt(),
        customer.getType(), customer.isActive(), customer.isInvoicingOnly(), customer.getSapCustomerNumber());
  }

  private ContactES mapToES(Contact contact) {
    return new ContactES(contact.getId(), contact.getName(), contact.isActive());
  }
}