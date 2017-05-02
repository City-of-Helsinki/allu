package fi.hel.allu.ui.config;

import fi.hel.allu.common.types.StatusType;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

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
  private List<String> emailAllowedAddresses;
  private String emailSenderAddress;

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
                               @Value("${oauth2.x509.certificate}") @NotEmpty String oauth2Certificate,
                               @Value("#{'${email.allowed.addresses:}'.split(',')}") List<String> emailAllowedAddresses,
                               @Value("${email.sender.address}") @NotEmpty String emailSenderAddress) {
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
    this.emailAllowedAddresses = emailAllowedAddresses;
    this.emailSenderAddress = emailSenderAddress;
  }

  public static final String PATH_PREFIX = "http://";

  /**
   * Model-service path to create a new application
   */
  public static final String PATH_MODEL_APPLICATION_CREATE = "/applications";

  /**
   * Model-service path to find application by identifier
   */
  public static final String PATH_MODEL_APPLICATION_FIND_BY_ID = "/applications/{applicationId}";

  /**
   * Model-service path to find applications by location
   */
  public static final String PATH_MODEL_APPLICATION_FIND_BY_LOCATION = "/applications/search";

  /**
   * Model-service path to find attachments by application
   */
  public static final String PATH_MODEL_APPLICATION_FIND_ATTACHMENTS_BY_APPLICATION = "/applications/{applicationId}/attachments";

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
  public static final String PATH_MODEL_ATTACHMENT_UPDATE = "/attachments/{attachmentId}";

  /**
   * Model-service path to find attachment by ID
   */
  public static final String PATH_MODEL_ATTACHMENT_FIND_BY_ID = "/attachments/{attachmentId}";

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
   * @return url to request metadata from model service.
   */
  public String getMetadataVersionedUrl() {
    return getModelServiceUrl("/meta/{applicationType}/{version}");
  }

  /**
   * @return url to update an application in model service.
   */
  public String getApplicationUpdateUrl() {
    return getModelServiceUrl("/applications/{applicationId}");
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

  public String getApplicationStatusUpdateUrl(StatusType statusType) {
    return getModelServiceUrl("/applications/{id}/status/" + statusType.toString().toLowerCase());
  }

  /**
   * @return url to replace distribution list of application.
   */
  public String getApplicationReplaceDistributionListUrl() {
    return getModelServiceUrl("/applications/{id}/decision-distribution-list");
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
   * Get URL to find all comments for an application
   *
   * @return the URL
   */
  public String getCommentsFindByApplicationUrl() {
    return getModelServiceUrl("/comments/applications/{applicationId}");
  }

  /**
   * Get URL to add a comment for an application
   *
   * @return the URL
   */
  public String getCommentsCreateUrl() {
    return getModelServiceUrl("/comments/applications/{applicationId}");
  }

  /**
   * Get URL to update an existing comment
   *
   * @return the URL
   */
  public String getCommentsUpdateUrl() {
    return getModelServiceUrl("/comments/{commentId}");
  }

  /**
   * Get URL to delete an existing comment
   *
   * @return the URL
   */
  public String getCommentsDeleteUrl() {
    return getModelServiceUrl("/comments/{commentId}");
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
   * @return url for creating locations.
   */
  public String getLocationsCreateUrl() {
    return getModelServiceUrl("/locations");
  }

  /**
   * @return url for updating application's locations.
   */
  public String getUpdateApplicationLocationsUrl() {
    return getModelServiceUrl("/locations/application/{applicationId}");
  }

  /**
   * @return url for deleting locations.
   */
  public String getLocationsDeleteUrl() {
    return getModelServiceUrl("/locations/delete");
  }

  /**
   * @return url for fetching locations by their application.
   */
  public String getLocationsByApplicationIdUrl() {
    return getModelServiceUrl("/locations/application/{applicationId}");
  }

  /**
   * @return url for deleting locations by their application.
   */
  // TODO: remove when locations are removed from the application class
  public String getDeleteLocationsByApplicationIdUrl() {
    return getModelServiceUrl("/locations/application/{applicationId}");
  }

  /**
   * @return url for fetching fixed locations (such as Narinkka) for given application type.
   */
  public String getFixedLocationUrl() {
    return getModelServiceUrl("/locations/fixed-location");
  }

  /**
   * @return url for fetching fixed location areas for given application type.
   */
  public String getFixedLocationAreaUrl() {
    return getModelServiceUrl("/locations/fixed-location-areas");
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
   * @return url for retrieving the default texts for given application type.
   */
  public String getDefaultTextListUrl() {
    return getModelServiceUrl("/defaulttext/applicationtype/{applicationType}");
  }

  /**
   * @return url for retrieving the default text by id.
   */
  public String getDefaultTextByIdUrl() {
    return getModelServiceUrl("/defaulttext/{id}");
  }

  /**
   * @return url for adding default text.
   */
  public String getDefaultTextAddUrl() {
    return getModelServiceUrl("/defaulttext");
  }

  /**
   * @return url for updating default text.
   */
  public String getDefaultTextUpdateUrl() {
    return getModelServiceUrl("/defaulttext/{id}");
  }

  /**
   * @return url for deleting default text.
   */
  public String getDefaultTextDeleteUrl() {
    return getModelServiceUrl("/defaulttext/{id}");
  }


  /**
   * @return url for getting the invoice rows for an application
   */
  public String getInvoiceRowsUrl() {
    return getModelServiceUrl("/applications/{id}/invoice-rows");
  }

  /**
   * @return url for setting the invoice rows for an application
   */
  public String setInvoiceRowsUrl() {
    return getModelServiceUrl("/applications/{id}/invoice-rows");
  }

  /**
   * @return url for deleting attachment from application.
   */
  public String getAddAttachmentUrl() {
    return getModelServiceUrl("/attachments/applications/{applicationId}");
  }

  /**
   * @return url for deleting attachment from application.
   */
  public String getDeleteAttachmentUrl() {
    return getModelServiceUrl("/attachments/applications/{applicationId}/{attachmentId}");
  }

  /**
   * @return url for adding a default attachment.
   */
  public String getAddDefaultAttachmentUrl() {
    return getModelServiceUrl("/attachments/default");
  }

  /**
   * @return url for updating a default attachment.
   */
  public String getUpdateDefaultAttachmentUrl() {
    return getModelServiceUrl("/attachments/default/{id}");
  }

  /**
   * @return url for getting information of a default attachment.
   */
  public String getDefaultAttachmentInfoUrl() {
    return getModelServiceUrl("/attachments/default/{id}");
  }

  /**
   * @return url for getting information of all default attachments.
   */
  public String getAllDefaultAttachmentInfoUrl() {
    return getModelServiceUrl("/attachments/default");
  }

  /**
   * @return url for getting information of all default attachments for a given application type.
   */
  public String getDefaultAttachmentInfoByApplicationTypeUrl() {
    return getModelServiceUrl("/attachments/default/applicationType/{applicationType}");
  }

  /**
   * @return url for deleting default attachment.
   */
  public String getDeleteDefaultAttachmentUrl() {
    return getModelServiceUrl("/attachments/default/{attachmentId}");
  }

  /**
   * @return URL for getting application's history
   */
  public String getApplicationHistoryUrl() {
    return getModelServiceUrl("/applications/{applicationId}/history");
  }

  /**
   * @return URL for posting new application's history item
   */
  public String getAddApplicationHistoryUrl() {
    return getModelServiceUrl("/applications/{applicationId}/history");
  }

  /**
   * @return URL for getting an applicant by id.
   */
  public String getApplicantByIdUrl() {
    return getModelServiceUrl("/applicants/{id}");
  }

  /**
   * @return URL for getting applicant by ids.
   */
  public String getApplicantsByIdUrl() {
    return getModelServiceUrl("/applicants/find");
  }

  /**
   * @return URL for getting all applicants.
   */
  public String getApplicantsUrl() {
    return getModelServiceUrl("/applicants");
  }

  /**
   * @return URL for creating an applicant.
   */
  public String getApplicantCreateUrl() {
    return getModelServiceUrl("/applicants");
  }

  /**
   * @return URL for updating an applicant.
   */
  public String getApplicantUpdateUrl() {
    return getModelServiceUrl("/applicants/{id}");
  }

  /**
   * @return URL for finding application ids of the applications having given applicant.
   */
  public String getApplicantApplicationsUrl() {
    return getModelServiceUrl("/applicants/applications/{id}");
  }

  /**
   * @return url to send applicant search queries.
   */
  public String getApplicantSearchUrl() {
    return getSearchServiceUrl("/applicants/search");
  }

  /**
   * @return url to send applicant search queries with partial words.
   */
  public String getApplicantSearchPartialUrl() {
    return getSearchServiceUrl("/applicants/search/{fieldName}");
  }

  /**
   * @return url to send multiple applicant search index updates.
   */
  public String getApplicantsSearchUpdateUrl() {
    return getSearchServiceUrl("/applicants/update");
  }

  /**
   * @return url to update applications to have the given applicant in search index.
   */
  public String getApplicantApplicationsSearchUpdateUrl() {
    return getSearchServiceUrl("/applicants/{id}/applications");
  }

  /**
   * @return url to add applicant to search index.
   */
  public String getApplicantSearchCreateUrl() {
    return getSearchServiceUrl("/applicants");
  }

  /**
   * @return URL for getting a contact by id.
   */
  public String getContactByIdUrl() {
    return getModelServiceUrl("/contacts/{id}");
  }

  /**
   * @return URL for getting contacts by ids.
   */
  public String getContactsByIdUrl() {
    return getModelServiceUrl("/contacts/find");
  }

  /**
   * @return URL for getting a contact by applicant.
   */
  public String getContactsByApplicantUrl() {
    return getModelServiceUrl("/contacts/applicant/{applicationId}");
  }

  /**
   * @return URL for creating contact.
   */
  public String getContactCreateUrl() {
    return getModelServiceUrl("/contacts");
  }

  /**
   * @return URL for updating a contact by id.
   */
  public String getContactUpdateUrl() {
    return getModelServiceUrl("/contacts");
  }

  /**
   * Model-service path to find contacts by application identifier
   */
  public String getContactsByApplicationUrl() {
    return getModelServiceUrl("/contacts/application/{applicationId}");
  }

  /**
   * Model-service path to find all contacts of applications having given contact.
   */
  public String getContactsRelatedByApplicationUrl() {
    return getModelServiceUrl("/contacts/application/related");
  }

  /**
   * Model-service path to find contacts by application identifier
   */
  public String getContactsUpdateApplicationUrl() {
    return getModelServiceUrl("/contacts?applicationId={applicationId}");
  }

  /**
   * @return url to find contact from search index.
   */
  public String getContactSearchUrl() {
    return getSearchServiceUrl("/contacts/search");
  }

  /**
   * @return url to add contact to search index.
   */
  public String getContactSearchCreateUrl() {
    return getSearchServiceUrl("/contacts");
  }

  /**
   * @return url to send multiple contact search index updates.
   */
  public String getContactSearchUpdateUrl() {
    return getSearchServiceUrl("/contacts/update");
  }

  /**
   * @return url to update contacts of multiple applications in search index.
   */
  public String getContactApplicationsSearchUpdateUrl() {
    return getSearchServiceUrl("/contacts/applications");
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

  /**
   * @return  list of allowed email recipients.
   */
  public List<String> getEmailAllowedAddresses() {
    return emailAllowedAddresses;
  }

  /**
   * Get the address that should be used when sending email from the system
   *
   * @return sender address
   */
  public String getEmailSenderAddress() {
    return emailSenderAddress;
  }
}
