package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.servicecore.domain.mapper.UpdatableProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * in Finnish: Hakemus
 */
@Schema(description = "Application")
public class ApplicationJson extends BaseApplicationJson implements StartTimeInterface {

  @Valid
  private ProjectJson project;
  private UserJson owner;
  private UserJson handler;
  private ZonedDateTime creationTime;
  @Valid
  private UserJson decisionMaker;
  @Valid
  private List<DistributionEntryJson> decisionDistributionList;
  @Valid
  private List<AttachmentInfoJson> attachmentList;
  @Valid
  private List<CommentJson> comments;
  private Integer calculatedPrice;

  private Integer replacesApplicationId;
  private Integer replacedByApplicationId;

  private ZonedDateTime invoicingDate;
  private Boolean invoiced;

  private ClientApplicationDataJson clientApplicationData;
  private String identificationNumber;
  private StatusType targetState;

  /**
   * Id of the external owner (external_user.id)
   */
  private Integer externalOwnerId;
  private Integer externalApplicationId;

  private Boolean ownerNotification;

  private ZonedDateTime terminationTime;

  @NotEmpty(message = "{application.customersWithContacts}")
  private List<CustomerWithContactsJson> customersWithContacts;

  public <U extends CreateApplicationJson> ApplicationJson(U application) {
    super(application);
  }

  public ApplicationJson() {
  }

  @Schema(description = "Project this application belongs to")
  public ProjectJson getProject() {
    return project;
  }

  @UpdatableProperty
  public void setProject(ProjectJson project) {
    this.project = project;
  }

  @Schema(description = "Owner of the application", accessMode = Schema.AccessMode.READ_ONLY)
  public UserJson getOwner() {
    return owner;
  }

  public void setOwner(UserJson owner) {
    this.owner = owner;
  }

  @Schema(description = "Handler of the application", accessMode = Schema.AccessMode.READ_ONLY)
  public UserJson getHandler() {
    return handler;
  }

  public void setHandler(UserJson handler) {
    this.handler = handler;
  }

  @Schema(description = "Application creation time", accessMode = Schema.AccessMode.READ_ONLY)
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  @Schema(description = "The user who made the decision", accessMode = Schema.AccessMode.READ_ONLY)
  public UserJson getDecisionMaker() {
    return decisionMaker;
  }

  public void setDecisionMaker(UserJson decisionMaker) {
    this.decisionMaker = decisionMaker;
  }

  @Schema(description = "Decision distribution list", accessMode = Schema.AccessMode.READ_ONLY)
  public List<DistributionEntryJson> getDecisionDistributionList() {
    return decisionDistributionList;
  }

  public void setDecisionDistributionList(List<DistributionEntryJson> decisionDistributionList) {
    this.decisionDistributionList = decisionDistributionList;
  }

  @Schema(description = "Attachments of the application", accessMode = Schema.AccessMode.READ_ONLY)
  public List<AttachmentInfoJson> getAttachmentList() {
    return attachmentList;
  }

  public void setAttachmentList(List<AttachmentInfoJson> attachmentList) {
    this.attachmentList = attachmentList;
  }

  @Schema(description = "Comments of the application", accessMode = Schema.AccessMode.READ_ONLY)
  public List<CommentJson> getComments() {
    return comments;
  }

  public void setComments(List<CommentJson> comments) {
    this.comments = comments;
  }

  @Schema(description = "Calculated price of the application (in cents)", accessMode = Schema.AccessMode.READ_ONLY)
  public Integer getCalculatedPrice() {
    return calculatedPrice;
  }

  public void setCalculatedPrice(Integer calculatedPrice) {
    this.calculatedPrice = calculatedPrice;
  }

  @Schema(description = "ID of the application which is replaced by this application", accessMode = Schema.AccessMode.READ_ONLY)
  public Integer getReplacesApplicationId() {
    return replacesApplicationId;
  }

  public void setReplacesApplicationId(Integer replacesApplicationId) {
    this.replacesApplicationId = replacesApplicationId;
  }

  @Schema(description = "ID of the application which has replaced this application", accessMode = Schema.AccessMode.READ_ONLY)
  public Integer getReplacedByApplicationId() {
    return replacedByApplicationId;
  }

  public void setReplacedByApplicationId(Integer replacedByApplicationId) {
    this.replacedByApplicationId = replacedByApplicationId;
  }

  @Schema(description = "Invoicing date of the application")
  public ZonedDateTime getInvoicingDate() {
    return invoicingDate;
  }

  @UpdatableProperty
  public void setInvoicingDate(ZonedDateTime invoicingDate) {
    this.invoicingDate = invoicingDate;
  }

  @Schema(description = "True if application is (completely) invoiced", accessMode = Schema.AccessMode.READ_ONLY)
  public Boolean getInvoiced() {
    return invoiced;
  }

  public void setInvoiced(Boolean invoiced) {
    this.invoiced = invoiced;
  }

  public ClientApplicationDataJson getClientApplicationData() {
    return clientApplicationData;
  }

  public void setClientApplicationData(ClientApplicationDataJson clientApplicationData) {
    this.clientApplicationData = clientApplicationData;
  }

  @Schema(description = "Application identification number (asiointitunnus)")
  public String getIdentificationNumber() {
    return identificationNumber;
  }

  @UpdatableProperty
  public void setIdentificationNumber(String identificationNumber) {
    this.identificationNumber = identificationNumber;
  }

  @Schema(hidden = true)
  public StatusType getTargetState() {
    return targetState;
  }

  public void setTargetState(StatusType targetState) {
    this.targetState = targetState;
  }

  @Schema(hidden = true)
  public Integer getExternalOwnerId() {
    return externalOwnerId;
  }

  public void setExternalOwnerId(Integer externalOwnerId) {
    this.externalOwnerId = externalOwnerId;
  }

  @Schema(hidden = true)
  public Integer getExternalApplicationId() {
    return externalApplicationId;
  }

  public void setExternalApplicationId(Integer externalApplicationId) {
    this.externalApplicationId = externalApplicationId;
  }
  @Schema(description = "Value indicating whether application is received from external system", accessMode = Schema.AccessMode.READ_ONLY)
  public boolean isExternalApplication() {
    return externalOwnerId != null;
  }

  @Schema(description = "Value indicating whether application requires owners attention (has changes made by other users etc)", accessMode = Schema.AccessMode.READ_ONLY)
  public Boolean getOwnerNotification() {
    return ownerNotification;
  }

  public void setOwnerNotification(Boolean ownerNotification) {
    this.ownerNotification = ownerNotification;
  }

  @Schema(description = "Application customers with their contacts", accessMode = Schema.AccessMode.READ_ONLY)
  public List<CustomerWithContactsJson> getCustomersWithContacts() {
    return customersWithContacts;
  }

  public void setCustomersWithContacts(List<CustomerWithContactsJson> customersWithContacts) {
    this.customersWithContacts = customersWithContacts;
  }

  public ZonedDateTime getTerminationTime() {
    return terminationTime;
  }

  public void setTerminationTime(ZonedDateTime terminationTime) {
    this.terminationTime = terminationTime;
  }

  public boolean isNotAreaRental() {
    if(getType() != null) {
      return getType() != ApplicationType.AREA_RENTAL;
    }
    return true;
  }
}
