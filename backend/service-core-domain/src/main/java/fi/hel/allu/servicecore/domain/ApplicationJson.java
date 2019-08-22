package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationSpecifier;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.PublicityType;
import fi.hel.allu.common.validator.NotFalse;
import fi.hel.allu.servicecore.domain.mapper.*;
import fi.hel.allu.servicecore.domain.validator.ValidApplication;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * in Finnish: Hakemus
 */
@ApiModel(value = "Application")
@NotFalse(rules = {
    "recurringEndTime, lessThanYearActivity, Recurring applications start and end time duration has to be less than a year",
    "kind, kindsMatchType, Application kinds must be valid for the type",
    "extension, specifiersMatchKind, Application specifiers must be suitable for application kind"
 })
@ValidApplication
public class ApplicationJson {

  /**
   * Validation group for application draft constraints
   */
  public interface Draft {}

  private Integer id;
  private String applicationId;
  @Valid
  private ProjectJson project;
  private UserJson owner;
  private UserJson handler;
  private StatusType status;
  @NotNull(message = "{application.type}", groups = {Draft.class, Default.class})
  private ApplicationType type;
  private List<ApplicationTagJson> applicationTags;
  private Integer metadataVersion;
  @NotBlank(message = "{application.name}")
  private String name;
  private ZonedDateTime creationTime;
  private ZonedDateTime receivedTime;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private ZonedDateTime recurringEndTime;
  @NotEmpty(message = "{application.customersWithContacts}")
  private List<CustomerWithContactsJson> customersWithContacts;
  @NotEmpty(message = "{application.locations}", groups = {Draft.class, Default.class})
  @Valid
  private List<LocationJson> locations;
  @NotNull(message = "{application.extension}")
  @Valid
  private ApplicationExtensionJson extension;
  @NotNull
  private PublicityType decisionPublicityType;
  private ZonedDateTime decisionTime;
  @Valid
  private UserJson decisionMaker;
  @Valid
  private List<DistributionEntryJson> decisionDistributionList;
  @Valid
  private List<AttachmentInfoJson> attachmentList;
  @Valid
  private List<CommentJson> comments;
  private Integer calculatedPrice;
  @NotNull
  private Boolean notBillable;
  private String notBillableReason;
  @NotEmpty
  private Map<ApplicationKind, List<ApplicationSpecifier>> kindsWithSpecifiers = new HashMap<>();
  private Integer invoiceRecipientId;

  private Integer replacesApplicationId;
  private Integer replacedByApplicationId;

  private String customerReference;
  private ZonedDateTime invoicingDate;
  private Boolean invoiced;

  private boolean skipPriceCalculation = false;

  private ClientApplicationDataJson clientApplicationData;
  private String identificationNumber;
  private Boolean invoicingChanged;
  private StatusType targetState;

  /**
   * Id of the external owner (external_user.id)
   */
  private Integer externalOwnerId;
  private Integer externalApplicationId;
  private Integer invoicingPeriodLength;

  private Integer version;

  private Boolean ownerNotification;

  @ApiModelProperty(value = "ID of the application", readOnly = true)
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value = "Human readable application identifier (hakemustunniste). The format is XXYYZZZZZ where XX is application type abbreviation, " +
      "YY is year and ZZZZZ is serial number for the given year. For example TP1600001", readOnly = true)
  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  @ApiModelProperty(value = "Project this application belongs to")
  public ProjectJson getProject() {
    return project;
  }

  @UpdatableProperty
  public void setProject(ProjectJson project) {
    this.project = project;
  }

  @ApiModelProperty(value = "Owner of the application", readOnly = true)
  public UserJson getOwner() {
    return owner;
  }

  public void setOwner(UserJson owner) {
    this.owner = owner;
  }

  @ApiModelProperty(value = "Handler of the application", readOnly = true)
  public UserJson getHandler() {
    return handler;
  }

  public void setHandler(UserJson handler) {
    this.handler = handler;
  }

  @ApiModelProperty(value = "Status of the application", readOnly = true)
  public StatusType getStatus() {
    return status;
  }

  public void setStatus(StatusType status) {
    this.status = status;
  }

  @ApiModelProperty(value = "Application type. Cannot be changed after creation", readOnly = true)
  public ApplicationType getType() {
    return type;
  }

  public void setType(ApplicationType type) {
    this.type = type;
  }

  @ApiModelProperty(value = "Application tags", readOnly = true)
  public List<ApplicationTagJson> getApplicationTags() {
    return applicationTags;
  }

  public void setApplicationTags(List<ApplicationTagJson> applicationTags) {
    this.applicationTags = applicationTags;
  }

  /**
   * @return Metadata version related to the application.
   */
  @ApiModelProperty(hidden = true)
  public Integer getMetadataVersion() {
    return metadataVersion;
  }

  public void setMetadataVersion(Integer metadataVersion) {
    this.metadataVersion = metadataVersion;
  }

  @ApiModelProperty(value = "Name of the application", required = true)
  public String getName() {
    return name;
  }

  @UpdatableProperty
  public void setName(String name) {
    this.name = name;
  }

  @ApiModelProperty(value = "Application creation time", readOnly = true)
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  @ApiModelProperty(value = "The starting time the application is active i.e. the starting time certain land area is reserved by the application")
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  @UpdatableProperty
  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  @ApiModelProperty(value = "The ending time the application is active i.e. the time certain land area stops being reserved by the application.")
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  @UpdatableProperty
  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  @ApiModelProperty(value = "The last moment the recurring application is active. Application may recur for certain time every year. For example, an area might " +
      "be used for storing snow every year and such application should be created as a recurring application instead of creating " +
      "application for each year separately.")
  public ZonedDateTime getRecurringEndTime() {
    return recurringEndTime;
  }

  @UpdatableProperty
  public void setRecurringEndTime(ZonedDateTime recurringEndTime) {
    this.recurringEndTime = recurringEndTime;
  }

  @ApiModelProperty(value = "Application customers with their contacts", readOnly = true)
  public List<CustomerWithContactsJson> getCustomersWithContacts() {
    return customersWithContacts;
  }

  public void setCustomersWithContacts(List<CustomerWithContactsJson> customersWithContacts) {
    this.customersWithContacts = customersWithContacts;
  }

  @ApiModelProperty(value = "Application locations", readOnly = true)
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

  @ApiModelProperty(value = "Decision publicity type")
  public PublicityType getDecisionPublicityType() {
    return decisionPublicityType;
  }

  @UpdatableProperty
  public void setDecisionPublicityType(PublicityType decisionPublicityType) {
    this.decisionPublicityType = decisionPublicityType;
  }

  @ApiModelProperty(value = "The time the decision was made", readOnly = true)
  public ZonedDateTime getDecisionTime() {
    return decisionTime;
  }

  public void setDecisionTime(ZonedDateTime decisionTime) {
    this.decisionTime = decisionTime;
  }

  @ApiModelProperty(value = "The user who made the decision", readOnly = true)
  public UserJson getDecisionMaker() {
    return decisionMaker;
  }

  public void setDecisionMaker(UserJson decisionMaker) {
    this.decisionMaker = decisionMaker;
  }

  @ApiModelProperty(value = "Decision distribution list", readOnly = true)
  public List<DistributionEntryJson> getDecisionDistributionList() {
    return decisionDistributionList;
  }

  public void setDecisionDistributionList(List<DistributionEntryJson> decisionDistributionList) {
    this.decisionDistributionList = decisionDistributionList;
  }

  @ApiModelProperty(value = "Attachments of the application", readOnly = true)
  public List<AttachmentInfoJson> getAttachmentList() {
    return attachmentList;
  }

  public void setAttachmentList(List<AttachmentInfoJson> attachmentList) {
    this.attachmentList = attachmentList;
  }

  @ApiModelProperty(value = "Comments of the application", readOnly = true)
  public List<CommentJson> getComments() {
    return comments;
  }

  public void setComments(List<CommentJson> comments) {
    this.comments = comments;
  }

  @ApiModelProperty(value = "Calculated price of the application (in cents)", readOnly = true)
  public Integer getCalculatedPrice() {
    return calculatedPrice;
  }

  public void setCalculatedPrice(Integer calculatedPrice) {
    this.calculatedPrice = calculatedPrice;
  }

  @ApiModelProperty(value = "True if application is not billed")
  public Boolean getNotBillable() {
    return notBillable;
  }

  @UpdatableProperty
  public void setNotBillable(Boolean notBillable) {
    this.notBillable = notBillable;
  }

  @ApiModelProperty(value = "Explanation for not billing")
  public String getNotBillableReason() {
    return notBillableReason;
  }

  @UpdatableProperty
  public void setNotBillableReason(String notBillableReason) {
    this.notBillableReason = notBillableReason;
  }

  @ApiModelProperty(value = "Application kinds with their specifiers")
  public Map<ApplicationKind, List<ApplicationSpecifier>> getKindsWithSpecifiers() {
    return kindsWithSpecifiers;
  }

  @UpdatableProperty
  public void setKindsWithSpecifiers(Map<ApplicationKind, List<ApplicationSpecifier>> kindsWithSpecifiers) {
    this.kindsWithSpecifiers = kindsWithSpecifiers;
  }

  @ApiModelProperty(value = "Id of the customer who is invoiced for this application")
  public Integer getInvoiceRecipientId() {
    return invoiceRecipientId;
  }

  @UpdatableProperty
  public void setInvoiceRecipientId(Integer invoiceRecipientId) {
    this.invoiceRecipientId = invoiceRecipientId;
  }

  /**
   * Get the application kind for this application -- only works correctly with
   * application types that don't have multiple kinds.
   *
   * @return
   */
  @JsonIgnore
  public ApplicationKind getKind() {
    if (kindsWithSpecifiers == null) {
      return null;
    }
    if (kindsWithSpecifiers.size() > 1) {
      throw new IllegalStateException("Application has multiple kinds");
    }
    return kindsWithSpecifiers.keySet().stream().findFirst().orElse(null);
  }

  public void setKind(ApplicationKind kind) {
    if (extension == null) {
      throw new IllegalStateException("Extension not set");
    }
    if (kind != null) {
      setKindsWithSpecifiers(Collections.singletonMap(kind, Collections.emptyList()));
    }
  }

  @JsonIgnore
  public boolean getLessThanYearActivity() {
    if (recurringEndTime != null && startTime != null && endTime != null) {
      return startTime.plusYears(1).isAfter(endTime);
    }
    return true;
  }

  @JsonIgnore
  public boolean getKindsMatchType() {
    if (type != null && kindsWithSpecifiers != null) {
      return kindsWithSpecifiers.keySet().stream().allMatch(k -> k.getTypes().contains(type));
    }
    return true;
  }

  @JsonIgnore
  public boolean getSpecifiersMatchKind() {
    if (kindsWithSpecifiers != null) {
      return kindsWithSpecifiers.entrySet().stream()
          .allMatch(e -> e.getValue().stream().allMatch(s -> e.getKey().equals(s.getKind())));
    }
    return true;
  }

  @ApiModelProperty(value = "ID of the application which is replaced by this application", readOnly = true)
  public Integer getReplacesApplicationId() {
    return replacesApplicationId;
  }

  public void setReplacesApplicationId(Integer replacesApplicationId) {
    this.replacesApplicationId = replacesApplicationId;
  }

  @ApiModelProperty(value = "ID of the application which has replaced this application", readOnly = true)
  public Integer getReplacedByApplicationId() {
    return replacedByApplicationId;
  }

  public void setReplacedByApplicationId(Integer replacedByApplicationId) {
    this.replacedByApplicationId = replacedByApplicationId;
  }

  @ApiModelProperty(value = "Customer reference (to invoice)")
  public String getCustomerReference() {
    return customerReference;
  }

  @UpdatableProperty
  public void setCustomerReference(String customerReference) {
    this.customerReference = customerReference;
  }

  @ApiModelProperty(value = "Invoicing date of the application")
  public ZonedDateTime getInvoicingDate() {
    return invoicingDate;
  }

  @UpdatableProperty
  public void setInvoicingDate(ZonedDateTime invoicingDate) {
    this.invoicingDate = invoicingDate;
  }

  @ApiModelProperty(value = "True if application is (completely) invoiced", readOnly = true)
  public Boolean getInvoiced() {
    return invoiced;
  }

  public void setInvoiced(Boolean invoiced) {
    this.invoiced = invoiced;
  }

  @ApiModelProperty(value = "True if the automatic price calculation should not be done for this application")
  public boolean getSkipPriceCalculation() {
    return skipPriceCalculation;
  }

  @UpdatableProperty
  public void setSkipPriceCalculation(boolean skipPriceCalculation) {
    this.skipPriceCalculation = skipPriceCalculation;
  }

  @ApiModelProperty(hidden = true)
  public Integer getExternalOwnerId() {
    return externalOwnerId;
  }

  public void setExternalOwnerId(Integer externalOwnerId) {
    this.externalOwnerId = externalOwnerId;
  }

  public ClientApplicationDataJson getClientApplicationData() {
    return clientApplicationData;
  }

  public void setClientApplicationData(ClientApplicationDataJson clientApplicationData) {
    this.clientApplicationData = clientApplicationData;
  }

  @ApiModelProperty(value = "Application identification number (asiointitunnus)")
  public String getIdentificationNumber() {
    return identificationNumber;
  }

  @UpdatableProperty
  public void setIdentificationNumber(String identificationNumber) {
    this.identificationNumber = identificationNumber;
  }

  @ApiModelProperty(hidden = true)
  public Boolean getInvoicingChanged() {
    return invoicingChanged;
  }

  public void setInvoicingChanged(Boolean invoicingChanged) {
    this.invoicingChanged = invoicingChanged;
  }

  @ApiModelProperty(hidden = true)
  public StatusType getTargetState() {
    return targetState;
  }

  public void setTargetState(StatusType targetState) {
    this.targetState = targetState;
  }

  @ApiModelProperty(value = "Time when application was received")
  public ZonedDateTime getReceivedTime() {
    return receivedTime;
  }

  @UpdatableProperty
  public void setReceivedTime(ZonedDateTime receivedTime) {
    this.receivedTime= receivedTime;
  }

  @ApiModelProperty(hidden = true)
  public Integer getExternalApplicationId() {
    return externalApplicationId;
  }

  public void setExternalApplicationId(Integer externalApplicationId) {
    this.externalApplicationId = externalApplicationId;
  }

  @ApiModelProperty(value = "Invoicing period length for this application", readOnly = true)
  public Integer getInvoicingPeriodLength() {
    return invoicingPeriodLength;
  }

  public void setInvoicingPeriodLength(Integer invoicingPeriodLength) {
    this.invoicingPeriodLength = invoicingPeriodLength;
  }

  @ApiModelProperty(value = "Application version number. Used for optimistic locking, required when updating application", readOnly = true)
  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  @ApiModelProperty(value = "Value indicating whether application is received from external system", readOnly = true)
  public boolean isExternalApplication() {
    return externalOwnerId != null;
  }

  @ApiModelProperty(value = "Value indicating whether application requires owners attention (has changes made by other users etc)", readOnly = true)
  public Boolean getOwnerNotification() {
    return ownerNotification;
  }

  public void setOwnerNotification(Boolean ownerNotification) {
    this.ownerNotification = ownerNotification;
  }

}
