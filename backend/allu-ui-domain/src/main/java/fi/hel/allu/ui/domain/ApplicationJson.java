package fi.hel.allu.ui.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.hel.allu.common.types.*;
import fi.hel.allu.common.validator.NotFalse;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * in Finnish: Hakemus
 */
@NotFalse(rules = {
    "recurringEndTime, lessThanYearActivity, Recurring applications start and end time duration has to be less than a year",
    "kind, kindMatchesType, Application kind must be valid for the type",
    "extension, specifiersMatchKind, Application specifiers must be suitable for application kind" })
public class ApplicationJson {

  private Integer id;
  private String applicationId;
  @Valid
  private ProjectJson project;
  private UserJson handler;
  private StatusType status;
  @NotNull(message = "{application.type}")
  private ApplicationType type;
  @NotNull(message = "{application.kind}")
  private ApplicationKind kind;
  private List<ApplicationTagJson> applicationTags;
  private Integer metadataVersion;
  @NotBlank(message = "{application.name}")
  private String name;
  private ZonedDateTime creationTime;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private ZonedDateTime recurringEndTime;
  @NotEmpty(message = "{application.customersWithContacts}")
  private List<CustomerWithContactsJson> customersWithContacts;
  @Valid
  private List<LocationJson> locations;
  @NotNull(message = "{application.extension}")
  @Valid
  private ApplicationExtensionJson extension;
  @NotNull
  private DistributionType decisionDistributionType;
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
  private Integer priceOverride;
  private String priceOverrideReason;

  /**
  /**
   * in Finnish: Hakemuksen tunniste tietokannassa
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * In Finnish: Hakemuksen tunniste ihmisille
   * <p>The human readable application id. The format is XXYYZZZZZ where XX is application type abbreviation, YY is year and ZZZZZ is
   * serial number for the given year. For example TP1600001.
   */
  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  /**
   * in Finnish: Hanke, johon hakemus liittyy
   */
  public ProjectJson getProject() {
    return project;
  }

  public void setProject(ProjectJson project) {
    this.project = project;
  }

  /**
   * in Finnish: Hakemuksen käsittelijä
   */
  public UserJson getHandler() {
    return handler;
  }

  public void setHandler(UserJson handler) {
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
   * in Finnish: Hakemuksen laji
   */
  public ApplicationKind getKind() {
    return kind;
  }

  public void setKind(ApplicationKind kind) {
    this.kind = kind;
  }

  /**
   * in Finnish: Hakemuksen tagit
   */
  public List<ApplicationTagJson> getApplicationTags() {
    return applicationTags;
  }

  public void setApplicationTags(List<ApplicationTagJson> applicationTags) {
    this.applicationTags = applicationTags;
  }

  /**
   * @return Metadata version related to the application.
   */
  public Integer getMetadataVersion() {
    return metadataVersion;
  }

  public void setMetadataVersion(Integer metadataVersion) {
    this.metadataVersion = metadataVersion;
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
   * in Finnish: Hakemuksen luontiaika
   */
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  /**
   * The starting time the application is active i.e. the starting time certain land area is reserved by the application.
   *
   * @return  starting time the application is active i.e. the starting time certain land area is reserved by the application.
   */
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  /**
   * The ending time the application is active i.e. the time certain land area stops being reserved by the application.
   *
   * @return  ending time the application is active i.e. the time certain land area stops being reserved by the application.
   */
  public ZonedDateTime getEndTime() {
    return endTime;
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
   * in Finnish: Hakemuksen asiakkuus ja kontaktitiedot
   */
  public List<CustomerWithContactsJson> getCustomersWithContacts() {
    return customersWithContacts;
  }

  public void setCustomersWithContacts(List<CustomerWithContactsJson> customersWithContacts) {
    this.customersWithContacts = customersWithContacts;
  }

  /**
   * in Finnish: Hakemuksen sijainti
   */
  public List<LocationJson> getLocations() {
    return locations;
  }

  public void setLocations(List<LocationJson> locations) {
    this.locations = locations;
  }

  /**
   * in Finnish: Tapahtuma
   */
  public ApplicationExtensionJson getExtension() {
    return extension;
  }

  public void setExtension(ApplicationExtensionJson event) {
    this.extension = event;
  }

  /**
   * The distribution type of the decision. Used for sending decision to customer.
   *
   * @return  The distribution type of the decision. Used for sending decision to customer.
   */
  public DistributionType getDecisionDistributionType() {
    return decisionDistributionType;
  }

  public void setDecisionDistributionType(DistributionType decisionDistributionType) {
    this.decisionDistributionType = decisionDistributionType;
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
  public UserJson getDecisionMaker() {
    return decisionMaker;
  }

  public void setDecisionMaker(UserJson decisionMaker) {
    this.decisionMaker = decisionMaker;
  }

  /**
   * The distribution list of the decision.
   *
   * @return  The distribution list of the decision.
   */
  public List<DistributionEntryJson> getDecisionDistributionList() {
    return decisionDistributionList;
  }

  public void setDecisionDistributionList(List<DistributionEntryJson> decisionDistributionList) {
    this.decisionDistributionList = decisionDistributionList;
  }

  /**
   * in Finnish: Hakemuksen liitteet
   */
  public List<AttachmentInfoJson> getAttachmentList() {
    return attachmentList;
  }

  public void setAttachmentList(List<AttachmentInfoJson> attachmentList) {
    this.attachmentList = attachmentList;
  }

  /**
   * in Finnish: Hakemuksen kommentit
   */
  public List<CommentJson> getComments() {
    return comments;
  }

  public void setComments(List<CommentJson> comments) {
    this.comments = comments;
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
   * Get the manually overridden price
   *
   * @return overridden price in cents
   */
  public Integer getPriceOverride() {
    return priceOverride;
  }

  public void setPriceOverride(Integer priceOverride) {
    this.priceOverride = priceOverride;
  }

  /**
   * Get the explanation text for manual price override
   *
   * @return price override reason
   */
  public String getPriceOverrideReason() {
    return priceOverrideReason;
  }

  public void setPriceOverrideReason(String priceOverrideReason) {
    this.priceOverrideReason = priceOverrideReason;
  }

  @JsonIgnore
  public boolean getLessThanYearActivity() {
    if (recurringEndTime != null && startTime != null && endTime != null) {
      return startTime.plusYears(1).isAfter(endTime);
    }
    return true;
  }

  @JsonIgnore
  public boolean getKindMatchesType() {
    if (kind != null && type != null) {
      return kind.getTypes().contains(type);
    }
    return true;
  }

  @JsonIgnore
  public boolean getSpecifiersMatchKind() {
    if (kind != null && extension != null && extension.getSpecifiers() != null) {
      return extension.getSpecifiers().stream().allMatch(s -> kind.equals(s.getKind()));
    }
    return true;
  }
}
