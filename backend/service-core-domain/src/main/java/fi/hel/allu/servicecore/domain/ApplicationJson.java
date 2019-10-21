package fi.hel.allu.servicecore.domain;

import fi.hel.allu.servicecore.domain.mapper.UpdatableProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.List;

/**
 * in Finnish: Hakemus
 */
@ApiModel(value = "Application")
public class ApplicationJson extends BaseApplicationJson {

  @Valid
  private ProjectJson project;
  private UserJson owner;
  private UserJson handler;
  @Valid
  private UserJson decisionMaker;
  @Valid
  private List<DistributionEntryJson> decisionDistributionList;

  @Valid
  private List<AttachmentInfoJson> attachmentList;
  @Valid
  private List<CommentJson> comments;

  private ClientApplicationDataJson clientApplicationData;

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

  public ClientApplicationDataJson getClientApplicationData() {
    return clientApplicationData;
  }

  public void setClientApplicationData(ClientApplicationDataJson clientApplicationData) {
    this.clientApplicationData = clientApplicationData;
  }

  @ApiModelProperty(value = "Application customers with their contacts", readOnly = true)
  public List<CustomerWithContactsJson> getCustomersWithContacts() {
    return customersWithContacts;
  }

  public void setCustomersWithContacts(List<CustomerWithContactsJson> customersWithContacts) {
    this.customersWithContacts = customersWithContacts;
  }
}
