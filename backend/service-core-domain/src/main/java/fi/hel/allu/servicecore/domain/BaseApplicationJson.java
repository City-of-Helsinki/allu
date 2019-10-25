package fi.hel.allu.servicecore.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationSpecifier;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.PublicityType;
import fi.hel.allu.common.validator.NotFalse;
import fi.hel.allu.servicecore.domain.mapper.UpdatableProperty;
import fi.hel.allu.servicecore.domain.validator.ValidApplication;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ValidApplication
@NotFalse(rules = {
  "recurringEndTime, lessThanYearActivity, Recurring applications start and end time duration has to be less than a year",
  "kind, kindsMatchType, Application kinds must be valid for the type",
  "extension, specifiersMatchKind, Application specifiers must be suitable for application kind"
})
public abstract class BaseApplicationJson {

  /**
   * Validation group for application draft constraints
   */
  public interface Draft {}

  private Integer id;
  private String applicationId;
  private StatusType status;
  @NotNull(message = "{application.type}", groups = {Draft.class, Default.class})
  private ApplicationType type;
  private List<ApplicationTagJson> applicationTags;
  private Integer metadataVersion;
  @NotBlank(message = "{application.name}")
  private String name;
  private ZonedDateTime receivedTime;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private ZonedDateTime recurringEndTime;

  @NotEmpty(message = "{application.locations}", groups = {Draft.class, Default.class})
  @Valid
  private List<LocationJson> locations;
  @NotNull(message = "{application.extension}")
  @Valid
  private ApplicationExtensionJson extension;
  @NotNull
  private PublicityType decisionPublicityType;
  private ZonedDateTime decisionTime;
  @NotNull
  private Boolean notBillable;
  private String notBillableReason;
  @NotEmpty
  private Map<ApplicationKind, List<ApplicationSpecifier>> kindsWithSpecifiers = new HashMap<>();
  private Integer invoiceRecipientId;

  private String customerReference;

  private boolean skipPriceCalculation = false;

  private Boolean invoicingChanged;

  private Integer invoicingPeriodLength;

  private Integer version;


  public BaseApplicationJson() {
  }

  public <U extends BaseApplicationJson> BaseApplicationJson(U application) {
    this.id = application.getId();
    this.applicationId = application.getApplicationId();
    this.status = application.getStatus();
    this.type = application.getType();
    this.applicationTags = application.getApplicationTags();
    this.metadataVersion = application.getMetadataVersion();
    this.name = application.getName();
    this.receivedTime = application.getReceivedTime();
    this.startTime = application.getStartTime();
    this.endTime = application.getEndTime();
    this.recurringEndTime = application.getRecurringEndTime();
    this.locations = application.getLocations();
    this.extension = application.getExtension();
    this.decisionPublicityType = application.getDecisionPublicityType();
    this.decisionTime = application.getDecisionTime();
    this.notBillable = application.getNotBillable();
    this.notBillableReason = application.getNotBillableReason();
    this.kindsWithSpecifiers = application.getKindsWithSpecifiers();
    this.invoiceRecipientId = application.getInvoiceRecipientId();
    this.customerReference = application.getCustomerReference();
    this.skipPriceCalculation = application.getSkipPriceCalculation();
    this.invoicingChanged = application.getInvoicingChanged();
    this.invoicingPeriodLength = application.getInvoicingPeriodLength();
    this.version = application.getVersion();
 }

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

  @ApiModelProperty(value = "Customer reference (to invoice)")
  public String getCustomerReference() {
    return customerReference;
  }

  @UpdatableProperty
  public void setCustomerReference(String customerReference) {
    this.customerReference = customerReference;
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
  public Boolean getInvoicingChanged() {
    return invoicingChanged;
  }

  public void setInvoicingChanged(Boolean invoicingChanged) {
    this.invoicingChanged = invoicingChanged;
  }

  @ApiModelProperty(value = "Time when application was received")
  public ZonedDateTime getReceivedTime() {
    return receivedTime;
  }

  @UpdatableProperty
  public void setReceivedTime(ZonedDateTime receivedTime) {
    this.receivedTime= receivedTime;
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

}
