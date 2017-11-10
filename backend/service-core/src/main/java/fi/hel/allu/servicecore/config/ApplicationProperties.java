package fi.hel.allu.servicecore.config;

import fi.hel.allu.common.domain.types.StatusType;

import java.util.List;

public class ApplicationProperties {

  private String modelServiceHost;
  private String modelServicePort;
  private String searchServiceHost;
  private String searchServicePort;
  private String pdfServiceHost;
  private String pdfServicePort;
  private List<String> emailAllowedAddresses;
  private String emailSenderAddress;
  private List<String> anonymousAccessPaths;

  public ApplicationProperties(
      String modelServiceHost,
      String modelServicePort,
      String searchServiceHost,
      String searchServicePort,
      String pdfServiceHost,
      String pdfServicePort,
      List<String> emailAllowedAddresses,
      String emailSenderAddress,
      List<String> anonymousAccessPaths) {
    this.modelServiceHost = modelServiceHost;
    this.modelServicePort = modelServicePort;
    this.searchServiceHost = searchServiceHost;
    this.searchServicePort = searchServicePort;
    this.pdfServiceHost = pdfServiceHost;
    this.pdfServicePort = pdfServicePort;
    this.emailAllowedAddresses = emailAllowedAddresses;
    this.emailSenderAddress = emailSenderAddress;
    this.anonymousAccessPaths = anonymousAccessPaths;
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
   * @return url to delete a note in model service.
   */
  public String getNoteDeleteUrl() {
    return getModelServiceUrl("/applications/note/{id}");
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
   * @return url to send multiple partial application search index updates.
   */
  public String getApplicationsSearchUpdatePartialUrl() {
    return getSearchServiceUrl("/applications/partialupdate");
  }

  /**
   * @return url to add application to search index.
   */
  public String getApplicationSearchCreateUrl() {
    return getSearchServiceUrl("/applications");
  }

  /**
   * @return url to delete note from search index.,
   */
  public String getNoteSearchRemoveUrl() {
    return getSearchServiceUrl("/applications/{id}");
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
   * @return url to searching customers
   */
  public String getUserSearchUrl() {
    return getModelServiceUrl("/users/search");
  }

  /**
   * @return url to fetch given user from model service.
   */
  public String getUserByUserNameUrl() {
    return getModelServiceUrl("/users/userName/{userName}");
  }

  /**
   * @return url to fetch users with given role from model service.
   */
  public String getUsersByRoleUrl() {
    return getModelServiceUrl("/users/role/{roleType}");
  }

  /**
   * @return url to fetch given user from model service.
   */
  public String getUserByIdUrl() {
    return getModelServiceUrl("/users/{id}");
  }

  /**
   * @return url for updating last login time of given user to model service.
   */
  public String getLastLoginUpdateUrl() {
    return getModelServiceUrl("/users/{id}/lastLogin");
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
   * @return url for getting the charge basis entries for an application
   */
  public String getChargeBasisUrl() {
    return getModelServiceUrl("/applications/{id}/charge-basis");
  }

  /**
   * @return url for setting the charge basis entries for an application
   */
  public String setChargeBasisUrl() {
    return getModelServiceUrl("/applications/{id}/charge-basis");
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
   * @return URL for getting an customer by id.
   */
  public String getCustomerByIdUrl() {
    return getModelServiceUrl("/customers/{id}");
  }

  /**
   * @return URL for getting a customer by business id.
   */
  public String getCustomerByBusinessIdUrl() {
    return getModelServiceUrl("/customers/businessid/{businessid}");
  }

  /**
   * @return URL for getting customers by ids.
   */
  public String getCustomersByIdUrl() {
    return getModelServiceUrl("/customers/find");
  }

  /**
   * @return URL for getting all customers.
   */
  public String getCustomersUrl() {
    return getModelServiceUrl("/customers");
  }

  /**
   * @return URL for creating a customer..
   */
  public String getCustomerCreateUrl() {
    return getModelServiceUrl("/customers");
  }

  /**
   * @return URL for updating a customer.
   */
  public String getCustomerUpdateUrl() {
    return getModelServiceUrl("/customers/{id}");
  }

  /**
   * @return URL for finding application ids of the applications having given customer.
   */
  public String getCustomerApplicationsUrl() {
    return getModelServiceUrl("/customers/applications/{id}");
  }

  /**
   * @return URL for getting customer's history
   */
  public String getCustomerHistoryUrl() {
    return getModelServiceUrl("/customers/{id}/history");
  }

  /**
   * @return URL for getting invoice recipients without SAP number.
   */
  public String getInvoiceRecipientsWithoutSAPNumberUrl() {
    return getModelServiceUrl("/customers/sap_id_missing");
  }


  /**
   * @return url to send customer search queries.
   */
  public String getCustomerSearchUrl() {
    return getSearchServiceUrl("/customers/search");
  }

  /**
   * @return url to send multiple customer search index updates.
   */
  public String getCustomersSearchUpdateUrl() {
    return getSearchServiceUrl("/customers/update");
  }

  /**
   * @return url to update applications to have the given customer in search index.
   */
  public String getCustomerApplicationsSearchUpdateUrl() {
    return getSearchServiceUrl("/customers/{id}/applications");
  }

  /**
   * @return url to add customer to search index.
   */
  public String getCustomerSearchCreateUrl() {
    return getSearchServiceUrl("/customers");
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
   * @return URL for getting a contact by customer.
   */
  public String getContactsByCustomerUrl() {
    return getModelServiceUrl("/contacts/customer/{customerId}");
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
   * Model-service path to tags (get, put)
   */
  public String getTagsUrl() {
    return getModelServiceUrl("/applications/{id}/tags");
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
   * @return url for retrieving the default recipients.
   */
  public String getDefaultRecipientListUrl() {
    return getModelServiceUrl("/default-recipients");
  }

  /**
   * @return url for retrieving the default recipient by id.
   */
  public String getDefaultRecipientByIdUrl() {
    return getModelServiceUrl("/default-recipients/{id}");
  }

  /**
   * @return url for adding default recipient.
   */
  public String getDefaultRecipientAddUrl() {
    return getModelServiceUrl("/default-recipients");
  }

  /**
   * @return url for updating default recipient.
   */
  public String getDefaultRecipientUpdateUrl() {
    return getModelServiceUrl("/default-recipients/{id}");
  }

  /**
   * @return url for deleting default recipient.
   */
  public String getDefaultRecipientDeleteUrl() {
    return getModelServiceUrl("/default-recipients/{id}");
  }

  /**
   * @return url for creating external users to model service.
   */
  public String getExternalUserCreateUrl() {
    return getModelServiceUrl("/externalusers");
  }

  /**
   * @return url for updating external users to model service.
   */
  public String getExternalUserUpdateUrl() {
    return getModelServiceUrl("/externalusers");
  }

  /**
   * @return url for listing all external users.
   */
  public String getExternalUserListingUrl() {
    return getModelServiceUrl("/externalusers");
  }

  /**
   * @return url for fetching given external user from model service.
   */
  public String getExternalUserByUserNameUrl() {
    return getModelServiceUrl("/externalusers/username/{username}");
  }

  /**
   * @return url for fetching given extarnl user from model service.
   */
  public String getExternalUserByIdUrl() {
    return getModelServiceUrl("/externalusers/{id}");
  }

  /**
   * @return url for updating last login time of given external user to model service.
   */
  public String getExternalUserLastLoginUpdateUrl() {
    return getModelServiceUrl("/externalusers/{id}/lastLogin");
  }

  /**
   * @return url for retrieving supervision task by id.
   */
  public String getSupervisionTaskByIdUrl() {
    return getModelServiceUrl("/supervisiontask/{id}");
  }

  /**
   * @return url for retrieving supervision tasks by application id.
   */
  public String getSupervisionTaskByApplicationIdUrl() {
    return getModelServiceUrl("/supervisiontask/application/{id}");
  }

  /**
   * @return url for creating new supervision task-
   */
  public String getSupervisionTaskCreateUrl() {
    return getModelServiceUrl("/supervisiontask");
  }

  /**
   * @return url for updating existing supervision task-
   */
  public String getSupervisionTaskUpdateUrl() {
    return getModelServiceUrl("/supervisiontask/{id}");
  }

  /**
   * @return url for approving supervision task.
   */
  public String getSupervisionTaskApproveUrl() {
    return getModelServiceUrl("/supervisiontask/{id}/approve");
  }

  /**
   * @return url for rejecting supervision task.
   */
  public String getSupervisionTaskRejectUrl() {
    return getModelServiceUrl("/supervisiontask/{id}/reject");
  }

  /**
   * @return url for searching supervision tasks by given criteria
   */
  public String getSupervisionTaskSearchUrl() {
    return getModelServiceUrl("/supervisiontask/search");
  }

  /**
   * @return url for deleting existing supervision task-
   */
  public String getSupervisionTaskDeleteUrl() {
    return getModelServiceUrl("/supervisiontask/{id}");
  }

  /**
   * @return url to update handler of supervision task.
   */
  public String getSupervisionTaskHandlerUpdateUrl() {
    return getModelServiceUrl("/supervisiontask/handler/{handlerId}");
  }

  /**
   * @return url to remove handler of supervision task.
   */
  public String getSupervisionTaskHandlerRemoveUrl() {
    return getModelServiceUrl("/supervisiontask/handler/remove");
  }

  /**
   * @return URL for finding invoices for application
   */
  public String getFindApplicationInvoicesUrl() {
    return getModelServiceUrl("/applications/{id}/invoices");
  }

  /**
   * @return the base URL for model service.
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

  /**
   * Get list of (url) paths allowed to be accessed by anonymous users. Controller methods bound to these won't be checked against normal
   * security measures.
   *
   * @return list of (url) paths allowed to be accessed by anonymous users.
   */
  public List<String> getAnonymousAccessPaths() {
    return anonymousAccessPaths;
  }
}
