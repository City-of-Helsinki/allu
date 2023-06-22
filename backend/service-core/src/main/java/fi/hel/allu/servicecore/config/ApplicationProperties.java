package fi.hel.allu.servicecore.config;

import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.ConfigurationKey;

import java.util.List;

public class ApplicationProperties {

  private final String modelServiceHost;
  private final String modelServicePort;
  private final String searchServiceHost;
  private final String searchServicePort;
  private final String pdfServiceHost;
  private final String pdfServicePort;
  private final List<String> emailAllowedAddresses;
  private final String emailSenderAddress;
  private final List<String> anonymousAccessPaths;
  private final String paymentClassUrl;
  private final String paymentClassUsername;
  private final String paymentClassPassword;
  private final String cityDistrictUrl;

  public ApplicationProperties(
      String modelServiceHost,
      String modelServicePort,
      String searchServiceHost,
      String searchServicePort,
      String pdfServiceHost,
      String pdfServicePort,
      List<String> emailAllowedAddresses,
      String emailSenderAddress,
      List<String> anonymousAccessPaths,
      String paymentClassUrl,
      String paymentClassUsername,
      String paymentClassPassword,
      String cityDistrictUrl) {
    this.modelServiceHost = modelServiceHost;
    this.modelServicePort = modelServicePort;
    this.searchServiceHost = searchServiceHost;
    this.searchServicePort = searchServicePort;
    this.pdfServiceHost = pdfServiceHost;
    this.pdfServicePort = pdfServicePort;
    this.emailAllowedAddresses = emailAllowedAddresses;
    this.emailSenderAddress = emailSenderAddress;
    this.anonymousAccessPaths = anonymousAccessPaths;
    this.paymentClassUrl = paymentClassUrl;
    this.paymentClassUsername = paymentClassUsername;
    this.paymentClassPassword = paymentClassPassword;
    this.cityDistrictUrl = cityDistrictUrl;
  }

  public static final String PATH_PREFIX = "http://";

  /**
   * Model-service path to find application by identifier
   */
  public static final String PATH_MODEL_APPLICATION_FIND_BY_ID = "/applications/{applicationId}";

  /**
   * Model-service path to find attachments by application
   */
  public static final String PATH_MODEL_APPLICATION_FIND_ATTACHMENTS_BY_APPLICATION = "/applications/{applicationId}/attachments";

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
   * Model-service path to get attachment data size
   */
  public static final String PATH_MODEL_ATTACHMENT_GET_SIZE = "/attachments/{attachmentId}/size";

  /**
   * Model-service path to create a new application
   */
  public static final String PATH_MODEL_APPLICATION_REPLACE = "/applications/{id}/replace?userId={userId}";

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
  private String getPdfServiceUrl(String path) {
    return PATH_PREFIX + pdfServiceHost + ":" + pdfServicePort + path;
  }

  /**
   * @return url to generate PDF
   */
  public String getGeneratePdfUrl() {
    return getPdfServiceUrl("/generate?stylesheet={stylesheet}");
  }

  /**
   * @return url to store decision
   */
  public String getStoreDecisionUrl() {
    return getModelServiceUrl("/applications/{id}/decision");
  }

  /**
   * @return url to retrieve decision
   */
  public String getDecisionUrl() {
    return getModelServiceUrl("/applications/{id}/decision");
  }

  public String getAnonymizedDecisionUrl() {
    return getModelServiceUrl("/applications/{id}/decision/anonymized");
  }

  public String getDecisionSearchUrl() {
    return getModelServiceUrl("/applications/decisions/search");
  }

  public String getApprovalDocumentUrl() {
    return getModelServiceUrl("/applications/{id}/approvalDocument/{type}");
  }

  public String getAnonymizedApprovalDocumentUrl() {
    return getModelServiceUrl("/applications/{id}/approvalDocument/{type}/anonymized");
  }

  public String getApprovalDocumentSearchUrl() {
    return getModelServiceUrl("/applications/approvalDocument/{type}/search");
  }


  public String getTerminationUrl() {
    return getModelServiceUrl("/applications/{id}/termination");
  }

  public String getTerminationInfoUrl() {
    return getTerminationUrl() + "/info";
  }

  public String getTerminationInfoUrlList() {
    return getTerminatedApplicationsUrl() + "/find";
  }

  public String getTerminatedApplicationsUrl() {
    return getModelServiceUrl("/applications/terminated");
  }

  /**
   * @return url to request metadata from model service.
   */
  public String getMetadataUrl() {
    return getModelServiceUrl("/meta/{applicationType}");
  }

  public String getMetadataTranslationUrl() {
    return getModelServiceUrl("/meta/translation/{type}/{text}");
  }

  /**
   * @return url to request metadata from model service.
   */
  public String getMetadataVersionedUrl() {
    return getModelServiceUrl("/meta/{applicationType}/{version}");
  }

  /**
   * @return url to create an application in model service.
   */
  public String getApplicationCreateUrl() {
    return getModelServiceUrl("/applications?userId={userId}");
  }

  /**
   * @return url to update an application in model service.
   */
  public String getApplicationUpdateUrl() {
    return getModelServiceUrl("/applications/{applicationId}?userId={userId}");
  }

  /**
   * @return url to search application identifiers by prefix
   */
  public String getApplicationIdentifierUrl() {
    return getModelServiceUrl("/applications/identifiers");
  }

  /**
   * @return url to get current application version
   */
  public String getApplicationVersionUrl() {
    return getModelServiceUrl("/applications/{id}/version");
  }

  /**
   * @return url to delete a note in model service.
   */
  public String getNoteDeleteUrl() {
    return getModelServiceUrl("/applications/note/{id}");
  }

  /**
   * @return url to delete a draft in model service.
   */
  public String getDraftDeleteUrl() {
    return getModelServiceUrl("/applications/drafts/{id}");
  }

  /**
   * @return url to replace a customer along with contacts for an application
   */
  public String getReplaceCustomerWithContactsUrl() {
    return getModelServiceUrl("/applications/{id}/customerWithContacts");
  }

  public String getApplicationCustomerByRoleUrl() {
    return getModelServiceUrl("/applications/{id}/customerWithContacts/{roleType}");
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

  public String getApplicationsSearchUpdateCustomersWithContactsUrl() {
    return getSearchServiceUrl("/applications/{id}/customersWithContacts");
  }

  /**
   * @return url to add application to search index.
   */
  public String getApplicationSearchCreateUrl() {
    return getSearchServiceUrl("/applications");
  }

  public String getSupervisionTaskSearchCreateUrl() {
    return getSearchServiceUrl("/supervisiontasks");
  }

  /**
   * @return url to delete application from search index.,
   */
  public String getApplicationSearchRemoveUrl() {
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
   * @return url to delete project from search index.
   */
  public String getProjectSearchDeleteUrl() {
    return getSearchServiceUrl("/projects/{id}");
  }

  /**
   * @return  url to request applications by their project.
   */
  public String getApplicationsByProjectUrl() {
    return getModelServiceUrl("/projects/{id}/applications");
  }

  public String getProjectApplicationsAddUrl() {
    return getModelServiceUrl("/projects/{id}/applications?userId={userId}");
  }

  public String getProjectApplicationRemoveUrl() {
    return getModelServiceUrl("/projects/applications/{id}?userId={userId}");
  }

  public String getProjectHistoryUrl() {
    return getModelServiceUrl("/projects/{id}/history");
  }

  public String getProjectNextProjectNumberUrl() {
    return getModelServiceUrl("/projects/nextProjectNumber");
  }

  /**
   * @return url to find possibly multiple applications by given identifiers.
   */
  public String getApplicationsByIdUrl() {
    return getModelServiceUrl("/applications/find");
  }

/**
   * @return url to update owner of applications.
   */
  public String getApplicationOwnerUpdateUrl() {
    return getModelServiceUrl("/applications/owner/{ownerId}");
  }

  public String getApplicationHandlerUpdateUrl() {
    return getModelServiceUrl("/applications/{id}/handler/{handlerId}");
  }


  /**
   * @return url to remove owner of applications.
   */
  public String getApplicationOwnerRemoveUrl() {
    return getModelServiceUrl("/applications/owner/remove");
  }

  public String getApplicationStatusUpdateUrl(StatusType statusType) {
    return getModelServiceUrl("/applications/{id}/status/" + statusType.toString().toLowerCase());
  }

  public String getApplicationStatusUrl() {
    return getModelServiceUrl("/applications/{id}/status");
  }

  public String getApplicationStatusReturnUrl() {
    return getModelServiceUrl("/applications/{id}/status/return");
  }

  public String getApplicationOwnerUrl() {
    return getModelServiceUrl("/applications/{id}/owner");
  }

  public String getApplicationExternalOwnerUrl() {
    return getModelServiceUrl("/applications/{id}/externalowner");
  }

  public String getApplicationIdForExternalIdUrl() {
    return getModelServiceUrl("/applications/external/{externalid}/applicationid");
  }


  public String getApplicationHandlerUrl() {
    return getModelServiceUrl("/applications/{id}/handler");
  }

  public String getApplicationDecisionMakerUrl() {
    return getModelServiceUrl("/applications/{id}/decisionmaker");
  }

  /**
   * @return url to distribution list of application.
   */
  public String getApplicationDistributionListUrl() {
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
    return getModelServiceUrl("/users/userName/?userName={userName}");
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

  public String getUsersByIdUrl() {
    return getModelServiceUrl("/users/find");
  }

  public String getUsersByApplicationIdUrl() {
    return getModelServiceUrl("/users/owners");
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
  public String getCommentsFindCountByApplicationUrl() {
    return getModelServiceUrl("/applications/{applicationId}/comments/count");
  }

  public String getLatestApplicationCommentUrl() {
    return getModelServiceUrl("/applications/{applicationId}/comments/latest");
  }

  public String getCommentsFindByApplicationUrl() {
    return getModelServiceUrl("/applications/{applicationId}/comments");
  }

  public String getCommentsFindByApplicationsUrl() {
    return getModelServiceUrl("/applications/comments/find");
  }

  public String getCommentsFindByApplicationsGroupingUrl() {
    return getModelServiceUrl("/applications/comments/find/mapping");
  }

  public String getCommentsFindByProjectUrl() {
    return getModelServiceUrl("/projects/{projectId}/comments");
  }

  public String getCommentsFindByIdUrl() {
    return getModelServiceUrl("/comments/{id}");
  }

  /**
   * Get URL to add a comment for an application
   *
   * @return the URL
   */
  public String getApplicationCommentsCreateUrl() {
    return getModelServiceUrl("/applications/{applicationId}/comments");
  }

  public String getProjectCommentsCreateUrl() {
    return getModelServiceUrl("/projects/{projectId}/comments");
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
   * @return url for fetching locations by their application.
   */
  public String getLocationsByApplicationIdUrl() {
    return getModelServiceUrl("/locations/application/{applicationId}");
  }

  /**
   * @return url for checking whether geometry is valid.
   */
  public String getIsValidGeometryUrl() {
    return getModelServiceUrl("/locations/geometry/isvalid");
  }


  /**
   * @return url for transforming geometry coordinates
   */
  public String getTransformGeometryUrl() {
    return getModelServiceUrl("/locations/geometry/transform");
  }

  /**
   * @return url for simplifying geometry
   */
  public String getSimplifyGeometryUrl() {
    return getModelServiceUrl("/locations/geometry/simplify");
  }


  /**
   * @return url for fetching fixed locations (such as Narinkka) for given application type.
   */
  public String getFixedLocationUrl() {
    return getModelServiceUrl("/locations/fixed-location");
  }

  public String getAllFixedLocationsUrl() {
    return getModelServiceUrl("/locations/fixed-location/all");
  }

  public String postSupervisionStatusUpdate() {
    return getSearchServiceUrl("/supervisiontasks/update/status/");
  }

  public String getSupervisionTaskSearchDeleteUrl() {
    return getSearchServiceUrl("/supervisiontasks/{id}");
  }

  public String getFixedLocationByIdUrl() {
    return getModelServiceUrl("/locations/fixed-location/{id}") ;
  }

  public String getFixedLocationAreasUrl() {
    return getModelServiceUrl("/locations/fixed-location-areas");
  }

  /**
   * @return url for fetching the list of city districts.
   */
  public String getCityDistrictsUrl() {
    return getModelServiceUrl("/locations/city-districts");
  }

  public String getCityDistrictNameUrl() {
    return getModelServiceUrl("/locations/city-districts/{id}/name");
  }

  public String getCityDistrictByIdUrl() {
    return getModelServiceUrl("/locations/city-districts/{id}");
  }


  public String getFindSupervisionTaskOwnerUrl() {
    return getModelServiceUrl("/locations/city-districts/{cityDistrictId}/supervisor/{type}");
  }

  public String getInsertLocationUrl() {
    return getModelServiceUrl("/locations/?userId={userId}");
  }

  public String getDeleteLocationUrl() {
    return getModelServiceUrl("/locations/{id}?userId={userId}");
  }

  public String getUpdateLocationUrl() {
    return getModelServiceUrl("/locations/{id}?userId={userId}");
  }

  public String getLocationUrl() {
    return getModelServiceUrl("/locations/{id}");
  }

  public String getLocationsUrl() {
    return getModelServiceUrl("/locations/multi");
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
    return getModelServiceUrl("/projects/{id}/parentProject/{parentProject}?userId={userId}");
  }

  /**
   * @return  url for updating project information of given projects.
   */
  public String getProjectInformationUpdateUrl() {
    return getModelServiceUrl("/projects/update?userId={userId}");
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
   * @return url for getting / updating the charge basis entries for an application
   */
  public String getChargeBasisUrl() {
    return getModelServiceUrl("/applications/{id}/charge-basis");
  }

  public String getChargeBasisEntryUrl() {
    return getModelServiceUrl("/applications/{id}/charge-basis/{entryId}");
  }

  public String getChargeBasisEntriesRecalculateUrl() {
    return getModelServiceUrl("/applications/{id}/charge-basis/recalculate");
  }

  public String getSingleInvoiceChargeBasisUrl() {
    return getModelServiceUrl("/applications/{id}/single-invoice-charge-basis");
  }

  public String getSetChargeBasisInvoicableUrl() {
    return getModelServiceUrl("/applications/{id}/charge-basis/{entryId}/invoicable");
  }

  public String getLocationInvoicableSumUrl() {
    return getModelServiceUrl("/applications/{id}//location/{locationid}/invoicable/sum");
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

  public String getApplicationDefaultAttachmentUrl() {
    return getModelServiceUrl("/attachments/applications/{applicationId}/default");
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

  public String getApplicationHistoryUrl(Boolean noReplaced) {
    String url = String.format("/applications/{applicationId}/history?noReplaced=%b", noReplaced);
    return getModelServiceUrl(url);
  }

  public String getApplicationHistoryHasStatusUrl() {
    return getModelServiceUrl("/applications/{applicationId}/history/hasstatus/{status}");
  }

  /**
   * @return URL for posting new application's history item
   */
  public String getAddApplicationHistoryUrl() {
    return getModelServiceUrl("/applications/{applicationId}/history");
  }

  public String getExternalOwnerApplicationHistoryUrl() {
    return getModelServiceUrl("/externalowner/{externalownerid}/applications/history");
  }

  public String getExternalOwnerSupervisionTaskHistoryUrl() {
    return getModelServiceUrl("/supervisiontask/externalowner/{externalownerid}/history");
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

  public String getCustomerByApplicationIdUrl() {
    return getModelServiceUrl("/applications/{applicationId}/customers");
  }

  /**
   * @return URL for getting customers by ids.
   */
  public String getCustomersByIdUrl() {
    return getModelServiceUrl("/customers/find");
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
  public String getInvoiceRecipientsWithoutSapNumberUrl() {
    return getModelServiceUrl("/customers/sap_id_missing");
  }

  /**
   * @return URL for getting number of invoice recipients without SAP number.
   */
  public String getNrOfInvoiceRecipientsWithoutSapNumberUrl() {
    return getModelServiceUrl("/customers/sap_id_missing/count");
  }

  public String getCustomerUpdateLogUrl() {
    return getModelServiceUrl("/customers/updatelog");
  }

  public String getCustomerUpdateLogProcessedUrl() {
    return getModelServiceUrl("/customers/updatelog/processed");
  }

  /**
   * @return url to send customer search queries.
   */
  public String getCustomerSearchUrl() {
    return getSearchServiceUrl("/customers/search");
  }

  public String getCustomerSearchByTypeUrl(CustomerType type) {
    return getSearchServiceUrl("/customers/search/" + type.name());
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

  public String getTagsDeleteUrl() {
    return getModelServiceUrl("/applications/{id}/tags/{tagType}");
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

  public String getExternalUserSetPasswordUrl() {
    return getModelServiceUrl("/externalusers/{id}/password");
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

  public String getSupervisionTaskAddressByIdUrl() {
    return getModelServiceUrl("/supervisiontask/{id}/address");
  }

  /**
   *
   * @return url for updating supervisionTask information on elasticsearch
   */
  public String getSupervisionTaksSearchUpdateUrl() {
    return getSearchServiceUrl("/supervisiontasks/update");
  }

  /**
   * @return url for retrieving supervision tasks by application id.
   */
  public String getSupervisionTaskByApplicationIdUrl() {
    return getModelServiceUrl("/supervisiontask/application/{id}");
  }

  public String getSupervisionTaskByLocationIdUrl() {
    return getModelServiceUrl("/supervisiontask/location/{id}");
  }

  /**
   * @return url for retrieving supervision tasks by application id.
   */
  public String getSupervisionTaskByApplicationIdAndTypeUrl() {
    return getModelServiceUrl("/supervisiontask/application/{id}/type/{type}");
  }

  public String getSupervisionTaskByApplicationIdAndTypeAndLocationUrl() {
    return getModelServiceUrl("/supervisiontask/application/{id}/type/{type}?locationId={locationId}");
  }

  /**
   * @return url for creating new supervision task-
   */
  public String getSupervisionTaskCreateUrl() {
    return getModelServiceUrl("/supervisiontask");
  }

  public String getSupervisionTaskGetWorkItemUrl() {
    return getModelServiceUrl("/supervisiontask/{id}/workitem");
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
    return getSearchServiceUrl("/supervisiontasks/search");
  }

  /**
   * @return url for deleting existing supervision task-
   */
  public String getSupervisionTaskDeleteUrl() {
    return getModelServiceUrl("/supervisiontask/{id}");
  }

  /**
   *
   * @return url for retrieving all supervisionTaskWorkItems from model-service
   */
  public String getAllSupervisionTasksUrl() {
    return getModelServiceUrl("/supervisiontask/all");
  }

  /**
   * @return url to update owner of supervision task.
   */
  public String getSupervisionTaskOwnerUpdateUrl() {
    return getModelServiceUrl("/supervisiontask/owner/{ownerId}");
  }
  public String getSupervisionTaskOwnerUpdateSearchUrl() {
    return getSearchServiceUrl("/supervisiontasks/owner/update");
  }

  public String getSupervisionTaskSearchOwnerRemoveUrl() {
    return getSearchServiceUrl("/supervisiontasks/owner/remove");
  }

  public String getSupervisionTaskCountUrl() {
    return getModelServiceUrl("/supervisiontask/{applicationId}/count");
  }

  /**
   * @return url to remove owner of supervision task.
   */
  public String getSupervisionTaskOwnerRemoveUrl() {
    return getModelServiceUrl("/supervisiontask/owner/remove");
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

  /**
   * @return URL for finding application ids having given invoice recipient
   */
  public String getInvoiceRecipientsApplicationsUrl() {
    return getModelServiceUrl("/customers/invoicerecipients/{id}/applications");
  }

  /**
   * @return URL for releasing pending invoice
   */
  public String getReleasePendingInvoiceUrl() {
    return getModelServiceUrl("/applications/invoices/{id}/release-pending");
  }

  /**
   * @return URL for setting target state for applications
   */
  public String getSetTargetStateUrl() {
    return getModelServiceUrl("/applications/{id}/targetstate");
  }

  public String getClearTargetStateUrl() {
    return getModelServiceUrl("/applications/{id}/targetstate/clear");
  }

  /**
  * @return url for getting deposit by application id.
   */
  public String getDepositByApplicationIdUrl() {
    return getModelServiceUrl("/applications/{id}/deposit");
  }

  /**
   * @return url for getting deposit by id
   */
  public String getDepositByIdUrl() {
    return getModelServiceUrl("/deposit/{id}");
  }

  /**
   * @return url for creating new deposit
   */
  public String getDepositCreateUrl() {
    return getModelServiceUrl("/deposit");
  }

  /**
   * @return url for updating deposit
   */
  public String getDepositUpdateUrl() {
    return getModelServiceUrl("/deposit/{id}");
  }

  /**
   * @return url for deleting deposit
   */
  public String getDepositDeleteUrl() {
    return getModelServiceUrl("/deposit/{id}");
  }

  /**
   * @return url to read all applications from database with paging
   */
  public String getAllApplicationsUrl() {
    return getModelServiceUrl("/applications?page={page}&size={size}");
  }

  /**
   * @return url to read all projects from database with paging
   */
  public String getAllProjectsUrl() {
    return getModelServiceUrl("/projects?page={page}&size={size}");
  }

  /**
   * @return url to read all customers from database with paging
   */
  public String getAllCustomersUrl() {
    return getModelServiceUrl("/customers?page={page}&size={size}");
  }

  /**
   * @return url to read all applications from database with paging
   */
  public String getAllContactsUrl() {
    return getModelServiceUrl("/contacts?page={page}&size={size}");
  }

  /**
   * @return url to start syncing data to search service
   */
  public String getStartSearchSyncUrl() {
    return getSearchServiceUrl("/applications/sync/start");
  }

  /**
   * @return url to commit syncing data to search service
   */
  public String getCommitSearchSyncUrl() {
    return getSearchServiceUrl("/applications/sync/commit");
  }

  /**
   * @return url to cancel syncing data to search service
   */
  public String getCancelSearchSyncUrl() {
    return getSearchServiceUrl("/applications/sync/cancel");
  }

  /**
   * @return url to send applications to search service while syncing
   */
  public String getSyncApplicationsUrl() {
    return getSearchServiceUrl("/applications/sync/data");
  }

  /**
   * @return url to send projects to search service while syncing
   */
  public String getSyncProjectsUrl() {
    return getSearchServiceUrl("/projects/sync/data");
  }

  /**
   * @return url to send customers to search service while syncing
   */
  public String getSyncCustomersUrl() {
    return getSearchServiceUrl("/customers/sync/data");
  }

  /**
   * @return url to send contacts to search service while syncing
   */
  public String getSyncContactsUrl() {
    return getSearchServiceUrl("/contacts/sync/data");
  }

  public String getSyncSupervisionTaskUrl() {return getSearchServiceUrl("/supervisiontasks/sync/data");}

  /**
   * @return url to fetch finished applications
   */
  public String getFinishedApplicationsUrl() {
     return getModelServiceUrl("/applications/finished");
  }

  /**
   * @return url to fetch finished notes
   */
  public String getFinishedNotesUrl() {
     return getModelServiceUrl("/applications/notes/finished");
  }

  public String getStoredFilterUrl() {
    return getModelServiceUrl("/stored-filter/{id}");
  }

  public String getStoredFilterCreateUrl() {
    return getModelServiceUrl("/stored-filter");
  }

  public String getStoredFilterFindByUserUrl() {
    return getModelServiceUrl("/user/{userId}/stored-filter");
  }

  public String getStoredFilterSetAsDefaultUrl() {
    return getModelServiceUrl("/stored-filter/{id}/set-default");
  }

  public String getPersonAuditLogUrl() {
    return getModelServiceUrl("/personauditlog/log");
  }

  public String getCodeSetFindByIdUrl() {
    return getModelServiceUrl("/codesets/{id}");
  }

  public String getCodeSetFindByIdsUrl() {
    return getModelServiceUrl("/codesets/find");
  }

  public String getCodeSetFindByTypeUrl() {
    return getModelServiceUrl("/codesets/search?type={type}");
  }

  public String getCodeSetFindByTypeAndCodeUrl() {
    return getModelServiceUrl("/codesets/find?type={type}&code={code}");
  }

  public String getExternalApplicationCreateUrl() {
    return getModelServiceUrl("/applications/{id}/originalapplication");
  }

  public String getInformationRequestCreateUrl() {
    return getModelServiceUrl("/applications/{id}/informationrequest");
  }

  public String getApplicationOpenInformationRequestFindUrl() {
    return getModelServiceUrl("/applications/{id}/informationrequest/open");
  }

  public String getApplicationActiveInformationRequestFindUrl() {
    return getModelServiceUrl("/applications/{id}/informationrequest/active");
  }

  public String getApplicationInformationRequestFindAllUrl() {
    return getModelServiceUrl("/applications/{id}/informationrequest");
  }


  public String getInformationRequestUrl() {
    return getModelServiceUrl("/informationrequests/{id}");
  }

  public String getInformationRequestCloseUrl() {
    return getModelServiceUrl("/informationrequests/{id}/close");
  }

  public String getInformationRequestResponseUrl() {
    return getModelServiceUrl("/informationrequests/{id}/response");
  }

  public String getInformationRequestResponseFindUrl() {
    return getModelServiceUrl("/applications/{id}/informationrequests/response");
  }

  public String getInformationRequestResponseFieldsFindUrl() {
    return getModelServiceUrl("/informationrequests/{id}/responsefields");
  }

  public String getApplicationInvoiceRecipientUrl() {
    return getModelServiceUrl("/applications/{id}/invoicerecipient");
  }

  public String getMailSenderLogUrl() {
    return getModelServiceUrl("/logs/mailsender");
  }

  public String getContractProposalUrl() {
    return getModelServiceUrl("/applications/{id}/contract/proposal");
  }

  public String getApprovedContractUrl() {
    return getModelServiceUrl("/applications/{id}/contract/approved");
  }

  public String getFinalContractUrl() {
    return getModelServiceUrl("/applications/{id}/contract/final");
  }

  public String getContractUrl() {
    return getModelServiceUrl("/applications/{id}/contract");
  }

  public String getContractInfoUrl() {
    return getModelServiceUrl("/applications/{id}/contract/info");
  }

  public String getConfigurationUrlForKey(ConfigurationKey key) {
    return getModelServiceUrl("/configurations/" + key);
  }

  public String getConfigurationUrl() {
    return getModelServiceUrl("/configurations");
  }

  public String getConfigurationUpdateUrl() {
    return getModelServiceUrl("/configurations/{id}");
  }

  public String getNotificationConfigurationUrl() {
    return getModelServiceUrl("/configurations/notification");
  }

  public String getCustomerOperationalConditionUrl() {
    return getModelServiceUrl("/applications/{id}/customeroperationalcondition");
  }

  public String getCustomerValidityUrl() {
    return getModelServiceUrl("/applications/{id}/customervalidity");
  }

  public String getCustomerWorkFinishedUrl() {
    return getModelServiceUrl("/applications/{id}/customerworkfinished");
  }

  public String getCustomerLocationValidityUrl() {
    return getModelServiceUrl("/applications/{id}/locations/{id}/customervalidity");
  }

  public String getOperationalConditionUrl() {
    return getModelServiceUrl("/applications/{id}/operationalcondition");
  }

  public String getWorkFinishedUrl() {
    return getModelServiceUrl("/applications/{id}/workfinished");
  }

  public String getSetRequiredTasksUrl() {
    return getModelServiceUrl("/excavationannouncements/{id}/requiredtasks");
  }

  public String getClientApplicationDataDeleteUrl() {
    return getModelServiceUrl("/applications/{id}/clientapplicationdata");
  }

  public String getInvoicingPeriodUpdateUrl() {
    return getModelServiceUrl("/applications/{id}/invoicingperiods?periodLength={periodLength}");
  }

  public String getInvoicingPeriodsUrl() {
    return getModelServiceUrl("/applications/{id}/invoicingperiods");
  }

  public String getRecurringApplicationPeriodsUrl() {
    return getModelServiceUrl("/applications/{id}/recurring/invoicingperiods");
  }

  public String getExcavationAnnouncementPeriodsUrl() {
    return getModelServiceUrl("/applications/{id}/excavation/invoicingperiods");
  }

  /**
   * WFS payment class URL
   * @return
   */
  public String getPaymentClassUrl() {
    return paymentClassUrl;
  }

  public String getPaymentClassUsername() {
    return paymentClassUsername;
  }

  public String getPaymentClassPassword() {
    return paymentClassPassword;
  }

  public String getCityDistrictUpdateUrl() {
    return cityDistrictUrl;
  }

  public String getUpsertCityDistrictsUrl() {
    return getModelServiceUrl("/citydistricts");
  }

  public String getOwnerNotificationUrl() {
    return getModelServiceUrl("/applications/{ids}/ownernotification");
  }

  public String getReplacingApplicationIdUrl() {
    return getModelServiceUrl("/applications/{id}/replacing");
  }

  public String getPricelistPaymentClassesUrl() {
    return getModelServiceUrl("/prices/paymentclasses?type={type}&kind={kind}");
  }
}