package fi.hel.allu.ui.config;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {

  private String modelServiceHost;
  private String modelServicePort;
  private String searchServiceHost;
  private String searchServicePort;

  @Autowired
  public ApplicationProperties(@Value("${model.service.host}") @NotEmpty String modelServiceHost,
                               @Value("${model.service.port}") @NotEmpty String modelServicePort,
                               @Value("${search.service.host}") @NotEmpty String searchServiceHost,
                               @Value("${search.service.port}") @NotEmpty String searchServicePort) {
    this.modelServiceHost = modelServiceHost;
    this.modelServicePort = modelServicePort;
    this.searchServiceHost = searchServiceHost;
    this.searchServicePort = searchServicePort;
  }

  public static final String PATH_PREFIX = "http://";

  /**
   * Model-service path to create a new application
   */
  public static final String PATH_MODEL_APPLICATION_CREATE = "/applications";

  /**
   * Model-service path to update an application
   */
  public static final String PATH_MODEL_APPLICATION_UPDATE = "/applications/{applicationId}";

  /**
   * Model-service path to find application by identifier
   */
  public static final String PATH_MODEL_APPLICATION_FIND_BY_ID = "/applications/{applicationId}";

  /**
   * Model-service path to find application by handler
   */
  public static final String PATH_MODEL_APPLICATION_FIND_BY_HANDLER = "/applications//byhandler/{handlerId}";

  /**
   * Model-service path to find applications by location
   */
  public static final String PATH_MODEL_APPLICATION_FIND_BY_LOCATION = "/applications/search";

  /**
   * Model-service path to delete application's location
   */
  public static final String PATH_MODEL_APPLICATION_DELETE_LOCATION = "/applications/{applicationId}/location";

  /**
   * Model-service path to find attachments by application
   */
  public static final String PATH_MODEL_APPLICATION_FIND_ATTACHMENTS_BY_APPLICATION = "/applications/{applicationId}/attachments";

  /**
   * Model-service path to create a new person
   */
  public static final String PATH_MODEL_PERSON_CREATE = "/persons";

  /**
   * Model-service path to update person
   */
  public static final String PATH_MODEL_PERSON_UPDATE = "/persons/{personId}";

  /**
   * Model-service path to find person by identifier
   */
  public static final String PATH_MODEL_PERSON_FIND_BY_ID = "/persons/{personId}";

  /**
   * Model-service path to create a new project
   */
  public static final String PATH_MODEL_PROJECT_CREATE = "/projects";

  /**
   * Model-service path to update project
   */
  public static final String PATH_MODEL_PROJECT_UPDATE = "/projects/{projectId}";

  /**
   * Model-service path to find project by identifier
   */
  public static final String PATH_MODEL_PROJECT_FIND_BY_ID = "/projects/{projectId}";

  /**
   * Model-service path to create a new applicant
   */
  public static final String PATH_MODEL_APPLICANT_CREATE = "/applicants";

  /**
   * Model-service path to create a new applicant
   */
  public static final String PATH_MODEL_APPLICANT_UPDATE = "/applicants/{applicantId}";

  /**
   * Model-service path to find applicant by identifier
   */
  public static final String PATH_MODEL_APPLICANT_FIND_BY_ID = "/applicants/{applicantId}";

  /**
   * Model-service path to create a new customer
   */
  public static final String PATH_MODEL_CUSTOMER_CREATE = "/customers";

  /**
   * Model-service path to update customer
   */
  public static final String PATH_MODEL_CUSTOMER_UPDATE = "/customers/{customerId}";

  /**
   * Model-service path to find customer by identifier
   */
  public static final String PATH_MODEL_CUSTOMER_FIND_BY_ID = "/customers/{customerId}";

  /**
   * Model-service path to create a new organization
   */
  public static final String PATH_MODEL_ORGANIZATION_CREATE = "/organizations";

  /**
   * Model-service path to update organization
   */
  public static final String PATH_MODEL_ORGANIZATION_UPDATE = "/organizations/{organizationId}";

  /**
   * Model-service path to find organization by identifier
   */
  public static final String PATH_MODEL_ORGANIZATION_FIND_BY_ID = "/organizations/{organizationId}";

  /**
   * Model-service path to create a new contact
   */
  public static final String PATH_MODEL_CONTACT_CREATE = "/contacts";

  /**
   * Model-service path to update contact
   */
  public static final String PATH_MODEL_CONTACT_UPDATE = "/contacts/{contactId}";

  /**
   * Model-service path to find contact by identifier
   */
  public static final String PATH_MODEL_CONTACT_FIND_BY_ID = "/contacts/{contactId}";

  /**
   * Model-service path to find contacts by organization identifier
   */
  public static final String PATH_MODEL_CONTACT_FIND_BY_ORGANIZATION = "/contacts?organizationId={organizationId}";

  /**
   * Model-service path to find contacts by application identifier
   */
  public static final String PATH_MODEL_CONTACT_FIND_BY_APPLICATION = "/contacts?applicationId={applicationId}";

  /**
   * Model-service path to create a new location
   */
  public static final String PATH_MODEL_LOCATION_CREATE = "/locations";

  /**
   * Model-service path to update location
   */
  public static final String PATH_MODEL_LOCATION_UPDATE = "/locations/{locationId}";

  /**
   * Model-service path to find contact by identifier
   */
  public static final String PATH_MODEL_LOCATION_FIND_BY_ID = "/locations/{locationId}";

  /**
   * Model-service path to create attachment
   */
  public static final String PATH_MODEL_ATTACHMENT_CREATE = "/attachments";

  /**
   * Model-service path to create attachment
   */
  public static final String PATH_MODEL_ATTACHMENT_UPDATE = "/attachments/{attachmentId}";

  /**
   * Model-service path to find attachment by ID
   */
  public static final String PATH_MODEL_ATTACHMENT_FIND_BY_ID = "/attachments/{attachmentId}";

  /**
   * Model-service path to delete attachment
   */
  public static final String PATH_MODEL_ATTACHMENT_DELETE = "/attachments/{attachmentId}";

  /**
   * Model-service path to set attachment data
   */
  public static final String PATH_MODEL_ATTACHMENT_SET_DATA = "/attachments/{attachmentId}/data";

  /**
   * Model-service path to get attachment data
   */
  public static final String PATH_MODEL_ATTACHMENT_GET_DATA = "/attachments/{attachmentId}/data";

  /**
   * Search-service path to index a new application
   */
  public static final String PATH_SEARCH_APPLICATION_CREATE = "/applications";

  /**
   * Search-service path to update application index
   */
  public static final String PATH_SEARCH_APPLICATION_UPDATE = "/applications/{applicationId}";

  /**
   * Search-service path to find applications by querystring
   */
  public static final String PATH_SEARCH_APPLICATION_FIND_BY_QUERYSTRING = "/applications/search?queryString={queryString}";

  /**
   * Search-service path to find applications by fields
   */
  public static final String PATH_SEARCH_APPLICATION_FIND_BY_FIELDS = "/applications/search";


  /**
   * Create absolute url to model-service. Host and port values are read from the application.properties.
   *
   * @param path resource path that is added to url after host and port values
   * @return absolute url to model-service resource
   */
  public String getModelServiceUrl(String path) {
    return PATH_PREFIX + modelServiceHost + ":" + modelServicePort + path;
  }

  /**
   * Create absolute url to search-service. Host and port values are read from the application.properties.
   *
   * @param path resource path that is added to url after host and port values
   * @return absolute url to search-service resource
   */
  public String getSearchServiceUrl(String path) {
    return PATH_PREFIX + searchServiceHost + ":" + searchServicePort + path;
  }

  /**
   * @return  url to request metadata from model service.
   */
  public String getMetadataUrl() {
    return PATH_PREFIX + modelServiceHost + ":" + modelServicePort + "/meta/{applicationType}";
  }
}
