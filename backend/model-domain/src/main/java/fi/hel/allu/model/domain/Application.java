package fi.hel.allu.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationSpecifier;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.PublicityType;
import fi.hel.allu.common.util.TimeUtil;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.group.GroupSequenceProvider;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * In Finnish: hakemus
 */
@GroupSequenceProvider(ApplicationGroupSequenceProvider.class)
public class Application {

  /**
   * Validation group for complete application (non draft) constraints.
   */
  public interface Complete {}

  private Integer id;
  private String applicationId;
  private Integer projectId;
  private Integer owner;
  private Integer handler;
  @NotEmpty
  private List<CustomerWithContacts> customersWithContacts;
  private StatusType status;
  @NotNull
  private ApplicationType type;
  private List<ApplicationTag> applicationTags;
  private Integer metadataVersion;
  @NotBlank
  private String name;
  private ZonedDateTime creationTime;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private ZonedDateTime recurringEndTime;
  @NotNull
  @Valid
  private ApplicationExtension extension;
  private PublicityType decisionPublicityType = PublicityType.PUBLIC;
  private ZonedDateTime decisionTime;
  private Integer decisionMaker;
  private List<DistributionEntry> decisionDistributionList = new ArrayList<>();
  private Integer calculatedPrice;
  private Boolean notBillable;
  private String notBillableReason;
  private Map<ApplicationKind, List<ApplicationSpecifier>> kindsWithSpecifiers;
  private Integer invoiceRecipientId;
  private Integer replacesApplicationId;
  private Integer replacedByApplicationId;
  private String customerReference;
  private ZonedDateTime invoicingDate;
  private Boolean invoiced;
  private boolean skipPriceCalculation = false;

  /**
   * in Finnish: Hakemuksen tunniste
   * <p>This is the database id.
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * The application id used to distinguish different applications by HKR people. The format is XXYYZZZZZ where XX is abbreviation of the
   * application type name, YY is year and ZZZZZ is application type specific number.
   *
   * @return The application id.
   */
  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  /**
   * in Finnish: Hakemukseen liittyvän hankkeen tunniste
   */
  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  /**
   * in Finnish: Hakemuksen nimi
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * in Finnish: Hakemuksen omistaja
   *
   * @return id of the owner or <code>null</code> if no owner is linked to the application.
   */
  public Integer getOwner() {
    return owner;
  }

  public void setOwner(Integer owner) {
    this.owner = owner;
  }

  /**
   * in Finnish: Hakemuksen käsittelijä
   *
   * @return id of the handler or <code>null</code> if no handler is linked to the application.
   */
  public Integer getHandler() {
    return handler;
  }

  public void setHandler(Integer handler) {
    this.handler = handler;
  }

  /**
   * in Finnish: Hakemuksen tila
   */
  public StatusType getStatus() {
    return status;
  }

  public void setStatus(StatusType status) {
    this.status = status;
  }

  /**
   * in Finnish: Hakemuksen tyyppi
   */
  public ApplicationType getType() {
    return type;
  }

  public void setType(ApplicationType type) {
    this.type = type;
  }

  /**
   * in Finnish: Hakemuksen tagit.
   */
  public List<ApplicationTag> getApplicationTags() {
    return applicationTags;
  }

  public void setApplicationTags(List<ApplicationTag> applicationTags) {
    this.applicationTags = applicationTags;
  }

  /**
   * Returns the metadata version of the application.
   *
   * @return
   */
  public Integer getMetadataVersion() {
    return metadataVersion;
  }

  public void setMetadataVersion(Integer metadataVersion) {
    this.metadataVersion = metadataVersion;
  }

  /**
   * in Finnish: Hakemuksen luontiaika
   */
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  /**
   * The time reservation of a land area begins.
   *
   * @return  time reservation of a land area begins.
   */
  public ZonedDateTime getStartTime() {
    return TimeUtil.homeTime(startTime);
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  /**
   * The time reservation of a land area ends.
   *
   * @return  time reservation of a land area ends.
   */
  public ZonedDateTime getEndTime() {
    return TimeUtil.homeTime(endTime);
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  /**
   * The last moment the recurring application is active. Application may recur for certain time every year. For example, an area might
   * be used for storing snow every year and such application should be created as a recurring application instead of creating and
   * application for each year separately.
   * <p>
   * Application with start time as 1.12.2016 and end time 31.1.2017 having recurring end time 31.1.2020 is active until 31.1.2020.
   *
   * @return  The last year recurring application is active.
   */
  public ZonedDateTime getRecurringEndTime() {
    return recurringEndTime;
  }

  public void setRecurringEndTime(ZonedDateTime recurringEndTime) {
    this.recurringEndTime = recurringEndTime;
  }

  /**
   * in Finnish: Hakemukseen liittyvien asiakkaiden ja kontaktien tunnisteet.
   */
  public List<CustomerWithContacts> getCustomersWithContacts() {
    return this.customersWithContacts;
  }

  public void setCustomersWithContacts(List<CustomerWithContacts> customersWithContacts) {
    this.customersWithContacts = customersWithContacts;
  }

  /**
   * in Finnish: Tapahtuman tunniste
   */
  public ApplicationExtension getExtension() {
    return extension;
  }

  public void setExtension(ApplicationExtension applicationExtension) {
    this.extension = applicationExtension;
  }

  /**
   * Publicity of the decision.
   *
   * @return  Publicity of the decision.
   */
  public PublicityType getDecisionPublicityType() {
    return decisionPublicityType;
  }

  public void setDecisionPublicityType(PublicityType decisionPublicityType) {
    this.decisionPublicityType = decisionPublicityType;
  }

  /**
   * in Finnish: Päätöksen aikaleima
   * The time decision was made.
   *
   * @return  The time decision was made.
   */
  public ZonedDateTime getDecisionTime() {
    return decisionTime;
  }

  public void setDecisionTime(ZonedDateTime decisionTime) {
    this.decisionTime = decisionTime;
  }

  /**
   * The user who made the decision.
   *
   * @return  The user who made the decision.
   */
  public Integer getDecisionMaker() {
    return decisionMaker;
  }

  public void setDecisionMaker(Integer decisionMaker) {
    this.decisionMaker = decisionMaker;
  }

  /**
   * The distribution list of the decision.
   *
   * @return  The distribution list of the decision.
   */
  public List<DistributionEntry> getDecisionDistributionList() {
    return decisionDistributionList;
  }

  public void setDecisionDistributionList(List<DistributionEntry> decisionDistributionList) {
    this.decisionDistributionList = decisionDistributionList;
  }

  /**
   * Get the calculated price
   *
   * @return calculated price in cents
   */
  public Integer getCalculatedPrice() {
    return calculatedPrice;
  }

  public void setCalculatedPrice(Integer calculatedPrice) {
    this.calculatedPrice = calculatedPrice;
  }

  /**
   * Is this application not meant to be billed?
   *
   * @return true if the application is not billable
   */
  public Boolean getNotBillable() {
    return notBillable;
  }

  public void setNotBillable(Boolean notBillable) {
    this.notBillable = notBillable;
  }

  /**
   * Why is this application not billable?
   *
   * @return Explanation for not billing.
   */
  public String getNotBillableReason() {
    return notBillableReason;
  }

  public void setNotBillableReason(String notBillableReason) {
    this.notBillableReason = notBillableReason;
  }

  /**
   * Get the application kinds and their specifiers.
   *
   * @return Map where keys are the application kinds and values are a list of
   *         specifiers for that kind.
   */
  public Map<ApplicationKind, List<ApplicationSpecifier>> getKindsWithSpecifiers() {
    return kindsWithSpecifiers;
  }

  public void setKindsWithSpecifiers(Map<ApplicationKind, List<ApplicationSpecifier>> kindsWithSpecifiers) {
    this.kindsWithSpecifiers = kindsWithSpecifiers;
  }

  /**
   * Customer who is invoiced for this application
   * @return Id of the invoiced customer
   */
  public Integer getInvoiceRecipientId() {
    return invoiceRecipientId;
  }

  public void setInvoiceRecipientId(Integer invoiceRecipientId) {
    this.invoiceRecipientId = invoiceRecipientId;
  }

  /**
   * Get ID of the application which is replaced by this application.
   * @return applicationId of the replaced application
   */
  public Integer getReplacesApplicationId() {
    return replacesApplicationId;
  }

  public void setReplacesApplicationId(Integer replacesApplicationId) {
    this.replacesApplicationId = replacesApplicationId;
  }

  /**
   * Get ID of the application which has replaced this application.
   * @return applicationId which has replaced this application
   */
  public Integer getReplacedByApplicationId() {
    return replacedByApplicationId;
  }

  public void setReplacedByApplicationId(Integer replacedByApplicationId) {
    this.replacedByApplicationId = replacedByApplicationId;
  }

  /**
   * Get customer reference
   */
  public String getCustomerReference() {
    return customerReference;
  }

  public void setCustomerReference(String customerReference) {
    this.customerReference = customerReference;
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
    setKindsWithSpecifiers(Collections.singletonMap(kind, Collections.emptyList()));
  }

  public boolean hasTypeAndKind(ApplicationType type, ApplicationKind kind) {
    return type == this.type && kindsWithSpecifiers != null && kindsWithSpecifiers.containsKey(kind);
  }

  /**
   * Get invoicing date for application.
   */
  public ZonedDateTime getInvoicingDate() {
    return invoicingDate;
  }

  public void setInvoicingDate(ZonedDateTime invoicingDate) {
    this.invoicingDate = invoicingDate;
  }

  /**
   * Gets value indicating whether application is completely invoiced.
   */
  public Boolean getInvoiced() {
    return invoiced;
  }

  public void setInvoiced(Boolean invoiced) {
    this.invoiced = invoiced;
  }

  /**
   * Should the automatic price calculation be skipped for this application?
   */
  public boolean getSkipPriceCalculation() {
    return skipPriceCalculation;
  }

  public void setSkipPriceCalculation(boolean skipPriceCalculation) {
    this.skipPriceCalculation = skipPriceCalculation;
  }

}
