package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.servicecore.domain.mapper.UpdatableProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * in Finnish: Hakemus
 */
@ApiModel(value = "Application")
public class ApplicationJson extends BaseApplicationJson {

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

  @ApiModelProperty(value = "Application creation time", readOnly = true)
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
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
  public StatusType getTargetState() {
    return targetState;
  }

  public void setTargetState(StatusType targetState) {
    this.targetState = targetState;
  }

  @ApiModelProperty(hidden = true)
  public Integer getExternalOwnerId() {
    return externalOwnerId;
  }

  public void setExternalOwnerId(Integer externalOwnerId) {
    this.externalOwnerId = externalOwnerId;
  }

  @ApiModelProperty(hidden = true)
  public Integer getExternalApplicationId() {
    return externalApplicationId;
  }

  public void setExternalApplicationId(Integer externalApplicationId) {
    this.externalApplicationId = externalApplicationId;
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

  @ApiModelProperty(value = "Application customers with their contacts", readOnly = true)
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
}
