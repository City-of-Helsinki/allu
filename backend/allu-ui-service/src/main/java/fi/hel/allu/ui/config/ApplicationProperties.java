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
  private String pdfServiceHost;
  private String pdfServicePort;
  private String geocodeUrl;
  private String streetSearchUrl;
  private String wfsUsername;
  private String wfsPassword;
  private String oauth2TokenUrl;
  private String oauth2ClientId;
  private String oauth2RedirectUri;
  private String oauth2Certificate;

  @Autowired
  public ApplicationProperties(@Value("${model.service.host}") @NotEmpty String modelServiceHost,
                               @Value("${model.service.port}") @NotEmpty String modelServicePort,
                               @Value("${search.service.host}") @NotEmpty String searchServiceHost,
                               @Value("${search.service.port}") @NotEmpty String searchServicePort,
                               @Value("${pdf.service.host}") @NotEmpty String pdfServiceHost,
                               @Value("${pdf.service.port}") @NotEmpty String pdfServicePort,
                               @Value("${wfs.template.street.geocode}") @NotEmpty String geocodeUrl,
                               @Value("${wfs.template.street.search}") @NotEmpty String streetSearchUrl,
                               @Value("${wfs.username}") @NotEmpty String wfsUsername,
                               @Value("${wfs.password}") @NotEmpty String wfsPassword,
                               @Value("${oauth2.url.token}") @NotEmpty String oauth2TokenUrl,
                               @Value("${oauth2.clientid}") @NotEmpty String oauth2ClientId,
                               @Value("${oauth2.redirect.uri}") @NotEmpty String oauth2RedirectUri,
                               @Value("${oauth2.x509.certificate}") @NotEmpty String oauth2Certificate) {
    this.modelServiceHost = modelServiceHost;
    this.modelServicePort = modelServicePort;
    this.searchServiceHost = searchServiceHost;
    this.searchServicePort = searchServicePort;
    this.pdfServiceHost = pdfServiceHost;
    this.pdfServicePort = pdfServicePort;
    this.geocodeUrl = geocodeUrl;
    this.streetSearchUrl = streetSearchUrl;
    this.wfsUsername = wfsUsername;
    this.wfsPassword = wfsPassword;
    this.oauth2TokenUrl = oauth2TokenUrl;
    this.oauth2ClientId = oauth2ClientId;
    this.oauth2RedirectUri = oauth2RedirectUri;
    this.oauth2Certificate = oauth2Certificate;
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
   * Model-service path to find contacts by applicant identifier
   */
  public static final String PATH_MODEL_CONTACT_FIND_BY_APPLICANT = "/contacts?applicantId={applicantId}";

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
   * Model-service path to store decision
   */
  public static final String PATH_MODEL_DECISION_STORE = "/applications/{id}/decision";

  /**
   * Model-service path to retrieve decision
   */
  public static final String PATH_MODEL_DECISION_GET = "/applications/{id}/decision";

  /**
   * PDF-service path to generate pdf
   */
  public static final String PATH_PDF_GENERATE = "/generate?stylesheet={stylesheet}";

  /**
   * Create absolute url to model-service. Host and port values are read from
   * the application.properties.
   *
   * @param path
   *          resource path that is added to url after host and port values
   * @return absolute url to model-service resource
   */
  public String getModelServiceUrl(String path) {
    return modelServiceBaseUrl() + path;
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
   * Create absolute url to pdf-service. Host and port values are read from the
   * application.properties.
   *
   * @param path
   *          resource path that is added to url after host and port values
   * @return absolute url to pdf-service resource
   */
  public String getPdfServiceUrl(String path) {
    return PATH_PREFIX + pdfServiceHost + ":" + pdfServicePort + path;
  }

  /**
   * @return url to request metadata from model service.
   */
  public String getMetadataUrl() {
    return getModelServiceUrl("/meta/{applicationType}");
  }

  /**
   * @return url to send application search queries.
   */
  public String getApplicationSearchUrl() {
    return getSearchServiceUrl("/applications/search");
  }

  /**
   * @return url to send multiple application search index updates.
   */
  public String getApplicationsSearchUpdateUrl() {
    return getSearchServiceUrl("/applications/update");
  }

  /**
   * @return url to add application to search index.
   */
  public String getApplicationSearchCreateUrl() {
    return getSearchServiceUrl("/applications");
  }

  /**
   * @return url to send application search queries.
   */
  public String getProjectSearchUrl() {
    return getSearchServiceUrl("/projects/search");
  }

  /**
   * @return url to send project search index updates.
   */
  public String getProjectSearchUpdateUrl() {
    return getSearchServiceUrl("/projects/{applicationId}");
  }

  /**
   * @return url to send multiple project search index updates.
   */
  public String getProjectsSearchUpdateUrl() {
    return getSearchServiceUrl("/projects/update");
  }

  /**
   * @return url to add project to search index.
   */
  public String getProjectSearchCreateUrl() {
    return getSearchServiceUrl("/projects");
  }

  /**
   * @return  url to request applications by their project.
   */
  public String getApplicationsByProjectUrl() {
    return getModelServiceUrl("/projects/{id}/applications");
  }

  /**
   * @return  url to request update to applications of given project.
   */
  public String getApplicationProjectUpdateUrl() {
    return getModelServiceUrl("/projects/{id}/applications");
  }

  /**
   * @return url to find possibly multiple applications by given identifiers.
   */
  public String getApplicationsByIdUrl() {
    return getModelServiceUrl("/applications/find");
  }

  /**
   * @return url to update handler of applications.
   */
  public String getApplicationHandlerUpdateUrl() {
    return getModelServiceUrl("/applications/handler/{handlerId}");
  }

  /**
   * @return url to remove handler of applications.
   */
  public String getApplicationHandlerRemoveUrl() {
    return getModelServiceUrl("/applications/handler/remove");
  }

  /**
   * @return url to create users to model service.
   */
  public String getUserCreateUrl() {
    return getModelServiceUrl("/users");
  }

  /**
   * @return url to update users to model service.
   */
  public String getUserUpdateUrl() {
    return getModelServiceUrl("/users");
  }

  /**
   * @return url to list users.
   */
  public String getUserListingUrl() {
    return getModelServiceUrl("/users");
  }

  /**
   * @return url to fetch given user from model service.
   */
  public String getUserByUserNameUrl() {
    return getModelServiceUrl("/users/userName/{userName}");
  }

  public String getUserByIdUrl() {
    return getModelServiceUrl("/users/{id}");
  }

  /**
   * Returns URL for geocoding a street address.
   * @return  Request URL for geocoding a street address.
   */
  public String getStreetGeocodeUrl() {
    return this.geocodeUrl;
  }

  /**
   * @return  url to search streets.
   */
  public String getStreetSearchUrl() {
    return this.streetSearchUrl;
  }

  /**
   * @return url for fetching fixed locations (such as Narinkka) for given application type.
   */
  public String getFixedLocationUrl() {
    return getModelServiceUrl("/locations/fixed-location");
  }

  /**
   * @return url for fetching the list of city districts.
   */
  public String getCityDistrictUrl() {
    return getModelServiceUrl("/locations/city-district");
  }

  /**
   * @return url for fetching projects by ids.
   */
  public String getProjectsByIdUrl() {
    return getModelServiceUrl("/projects/find");
  }

  /**
   * @return url for fetching project children.
   */
  public String getProjectChildrenUrl() {
    return getModelServiceUrl("/projects/{id}/children");
  }

  /**
   * @return url for fetching project parents.
   */
  public String getProjectParentsUrl() {
    return getModelServiceUrl("/projects/{id}/parents");
  }

  /**
   * @return  url for creating a new project.
   */
  public String getProjectCreateUrl() {
    return getModelServiceUrl("/projects");
  }

  /**
   * @return  url for updating existing project.
   */
  public String getProjectUpdateUrl() {
    return getModelServiceUrl("/projects/{id}");
  }

  /**
   * @return  url for updating parent of existing project.
   */
  public String getProjectParentUpdateUrl() {
    return getModelServiceUrl("/projects/{id}/parentProject/{parentProject}");
  }

  /**
   * @return  url for updating project information of given projects.
   */
  public String getProjectInformationUpdateUrl() {
    return getModelServiceUrl("/projects/update");
  }

  /**
   * @return url for retrieving the cable info standard texts.
   */
  public String getCableInfoTextListUrl() {
    return getModelServiceUrl("/applications/cable-info/texts");
  }

  /**
   * @return url for adding a cable info standard text.
   */
  public String getCableInfoTextAddUrl() {
    return getModelServiceUrl("/applications/cable-info/texts");
  }

  /**
   * @return url for updating a cable info standard text.
   */
  public String getCableInfoTextUpdateUrl() {
    return getModelServiceUrl("/applications/cable-info/texts/{id}");
  }

  /**
   * @return url for deleting a cable info standard text.
   */
  public String getCableInfoTextDeleteUrl() {
    return getModelServiceUrl("/applications/cable-info/texts/{id}");
  }

  /**
   * @return url for getting the invoice rows for an application
   */
  public String getInvoiceRowsUrl() {
    return getModelServiceUrl("/applications/{id}/invoice-rows");
  }

  /**
   * Returns username for the WFS service.
   *
   * @return username for the WFS service.
   */
  public String getWfsUsername() {
    return wfsUsername;
  }

  /**
   * Returns password for the WFS service.
   *
   * @return password for the WFS service.
   */
  public String getWfsPassword() {
    return wfsPassword;
  }

  /**
   * Returns the code for token exchange URI.
   *
   * @return  the code for token exchange URI.
   */
  public String getOauth2TokenUrl() {
    return oauth2TokenUrl;
  }

  /**
   * Returns the OAuth2 client id.
   *
   * @return  the OAuth2 client id.
   */
  public String getOauth2ClientId() {
    return oauth2ClientId;
  }

  /**
   * Returns the OAuth2 redirect uri.
   *
   * @return  the OAuth2 redirect uri.
   */
  public String getOauth2RedirectUri() {
    return oauth2RedirectUri;
  }

  /**
   * Returns the OAuth2 public certificate for verifying token signing.
   *
   * @return  the OAuth2 public certificate for verifying token signing.
   */
  public String getOauth2Certificate() {
    return oauth2Certificate;
  }

  /**
   * @return  the base URL for model service.
   */
  private String modelServiceBaseUrl() {
    return PATH_PREFIX + modelServiceHost + ":" + modelServicePort;
  }
}
