package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationSpecifier;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.PublicityType;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used for saving application history.
 */
public class ApplicationForHistory {

  private Integer id;
  private String applicationId;
  private ProjectJson project;
  private UserJson owner;
  private UserJson handler;
  private StatusType status;
  private ApplicationType type;
  private List<ApplicationTagJson> applicationTags;
  private Integer metadataVersion;
  private String name;
  private ZonedDateTime creationTime;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private ZonedDateTime recurringEndTime;
  private Map<CustomerRoleType, CustomerWithContactsJson> customersWithContacts;
  private List<LocationJson> locations;
  private ApplicationExtensionJson extension;
  private PublicityType decisionPublicityType;
  private ZonedDateTime decisionTime;
  private UserJson decisionMaker;
  private List<DistributionEntryJson> decisionDistributionList;
  private List<AttachmentInfoJson> attachmentList;
  private List<CommentJson> comments;
  private Integer calculatedPrice;
  private Boolean notBillable;
  private String notBillableReason;
  private Map<ApplicationKind, List<ApplicationSpecifier>> kindsWithSpecifiers = new HashMap<>();
  private Integer invoiceRecipientId;

  private Integer replacesApplicationId;
  private Integer replacedByApplicationId;

  private String customerReference;
  private ZonedDateTime invoicingDate;
  private Boolean invoiced;

  private boolean skipPriceCalculation = false;

  private String identificationNumber;
  private Integer externalOwnerId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public ProjectJson getProject() {
    return project;
  }

  public void setProject(ProjectJson project) {
    this.project = project;
  }

  public UserJson getOwner() {
    return owner;
  }

  public void setOwner(UserJson owner) {
    this.owner = owner;
  }

  public UserJson getHandler() {
    return handler;
  }

  public void setHandler(UserJson handler) {
    this.handler = handler;
  }

  public StatusType getStatus() {
    return status;
  }

  public void setStatus(StatusType status) {
    this.status = status;
  }

  public ApplicationType getType() {
    return type;
  }

  public void setType(ApplicationType type) {
    this.type = type;
  }

  public List<ApplicationTagJson> getApplicationTags() {
    return applicationTags;
  }

  public void setApplicationTags(List<ApplicationTagJson> applicationTags) {
    this.applicationTags = applicationTags;
  }

  public Integer getMetadataVersion() {
    return metadataVersion;
  }

  public void setMetadataVersion(Integer metadataVersion) {
    this.metadataVersion = metadataVersion;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  public ZonedDateTime getRecurringEndTime() {
    return recurringEndTime;
  }

  public void setRecurringEndTime(ZonedDateTime recurringEndTime) {
    this.recurringEndTime = recurringEndTime;
  }

  public Map<CustomerRoleType, CustomerWithContactsJson> getCustomersWithContacts() {
    return customersWithContacts;
  }

  public void setCustomersWithContacts(Map<CustomerRoleType, CustomerWithContactsJson> customersWithContacts) {
    this.customersWithContacts = customersWithContacts;
  }

  public List<LocationJson> getLocations() {
    return locations;
  }

  public void setLocations(List<LocationJson> locations) {
    this.locations = locations;
  }

  public ApplicationExtensionJson getExtension() {
    return extension;
  }

  public void setExtension(ApplicationExtensionJson event) {
    this.extension = event;
  }

  public PublicityType getDecisionPublicityType() {
    return decisionPublicityType;
  }

  public void setDecisionPublicityType(PublicityType decisionPublicityType) {
    this.decisionPublicityType = decisionPublicityType;
  }

  public ZonedDateTime getDecisionTime() {
    return decisionTime;
  }

  public void setDecisionTime(ZonedDateTime decisionTime) {
    this.decisionTime = decisionTime;
  }

  public UserJson getDecisionMaker() {
    return decisionMaker;
  }

  public void setDecisionMaker(UserJson decisionMaker) {
    this.decisionMaker = decisionMaker;
  }

  public List<DistributionEntryJson> getDecisionDistributionList() {
    return decisionDistributionList;
  }

  public void setDecisionDistributionList(List<DistributionEntryJson> decisionDistributionList) {
    this.decisionDistributionList = decisionDistributionList;
  }

  public List<AttachmentInfoJson> getAttachmentList() {
    return attachmentList;
  }

  public void setAttachmentList(List<AttachmentInfoJson> attachmentList) {
    this.attachmentList = attachmentList;
  }

  public List<CommentJson> getComments() {
    return comments;
  }

  public void setComments(List<CommentJson> comments) {
    this.comments = comments;
  }

  public Integer getCalculatedPrice() {
    return calculatedPrice;
  }

  public void setCalculatedPrice(Integer calculatedPrice) {
    this.calculatedPrice = calculatedPrice;
  }

  public Boolean getNotBillable() {
    return notBillable;
  }

  public void setNotBillable(Boolean notBillable) {
    this.notBillable = notBillable;
  }

  public String getNotBillableReason() {
    return notBillableReason;
  }

  public void setNotBillableReason(String notBillableReason) {
    this.notBillableReason = notBillableReason;
  }

  public Map<ApplicationKind, List<ApplicationSpecifier>> getKindsWithSpecifiers() {
    return kindsWithSpecifiers;
  }

  public void setKindsWithSpecifiers(Map<ApplicationKind, List<ApplicationSpecifier>> kindsWithSpecifiers) {
    this.kindsWithSpecifiers = kindsWithSpecifiers;
  }

  public Integer getInvoiceRecipientId() {
    return invoiceRecipientId;
  }

  public void setInvoiceRecipientId(Integer invoiceRecipientId) {
    this.invoiceRecipientId = invoiceRecipientId;
  }

  public Integer getReplacesApplicationId() {
    return replacesApplicationId;
  }

  public void setReplacesApplicationId(Integer replacesApplicationId) {
    this.replacesApplicationId = replacesApplicationId;
  }

  public Integer getReplacedByApplicationId() {
    return replacedByApplicationId;
  }

  public void setReplacedByApplicationId(Integer replacedByApplicationId) {
    this.replacedByApplicationId = replacedByApplicationId;
  }

  public String getCustomerReference() {
    return customerReference;
  }

  public void setCustomerReference(String customerReference) {
    this.customerReference = customerReference;
  }

  public ZonedDateTime getInvoicingDate() {
    return invoicingDate;
  }

  public void setInvoicingDate(ZonedDateTime invoicingDate) {
    this.invoicingDate = invoicingDate;
  }

  public Boolean getInvoiced() {
    return invoiced;
  }

  public void setInvoiced(Boolean invoiced) {
    this.invoiced = invoiced;
  }

  public boolean getSkipPriceCalculation() {
    return skipPriceCalculation;
  }

  public void setSkipPriceCalculation(boolean skipPriceCalculation) {
    this.skipPriceCalculation = skipPriceCalculation;
  }

  public Integer getExternalOwnerId() {
    return externalOwnerId;
  }

  public void setExternalOwnerId(Integer externalOwnerId) {
    this.externalOwnerId = externalOwnerId;
  }

  public String getIdentificationNumber() {
    return identificationNumber;
  }

  public void setIdentificationNumber(String identificationNumber) {
    this.identificationNumber = identificationNumber;
  }
}
