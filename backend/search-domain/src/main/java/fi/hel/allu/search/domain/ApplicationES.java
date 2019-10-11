package fi.hel.allu.search.domain;


import fi.hel.allu.common.util.RecurringApplication;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * ElasticSearch application mapping.
 * <p>NOTE! Whenever you change this class, make sure that the ElasticSearch mapping (i.e. the data schema) is also changed accordingly,
 * because otherwise for example date mappings may not work.
 */
public class ApplicationES {
  @NotBlank
  private Integer id;
  private String applicationId;
  private UserES owner;
  private StatusTypeES status;
  private ApplicationTypeES type;
  private List<String> applicationTags;
  private String name;
  private Long creationTime;
  private Long receivedTime;
  private Long startTime;
  private Long endTime;
  private List<ESFlatValue> applicationTypeData;
  private CompactProjectES project;
  private Long decisionTime;
  private List<LocationES> locations;
  private RoleTypedCustomerES customers;
  private RecurringApplication recurringApplication;
  private Integer nrOfComments;
  private String identificationNumber;
  private Boolean ownerNotification;
  private String latestComment;

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

  public UserES getOwner() {
    return owner;
  }

  public void setOwner(UserES owner) {
    this.owner = owner;
  }

  public StatusTypeES getStatus() {
    return status;
  }

  public void setStatus(StatusTypeES status) {
    this.status = status;
  }

  public ApplicationTypeES getType() {
    return type;
  }

  public void setType(ApplicationTypeES type) {
    this.type = type;
  }

  public List<String> getApplicationTags() {
    return applicationTags;
  }

  public void setApplicationTags(List<String> applicationTags) {
    this.applicationTags = applicationTags;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(Long creationTime) {
    this.creationTime = creationTime;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  public List<ESFlatValue> getApplicationTypeData() {
    return applicationTypeData;
  }

  public void setApplicationTypeData(List<ESFlatValue> applicationTypeData) {
    this.applicationTypeData = applicationTypeData;
  }

  public CompactProjectES getProject() {
    return project;
  }

  public void setProject(CompactProjectES project) {
    this.project = project;
  }

  public Long getDecisionTime() {
    return decisionTime;
  }

  public void setDecisionTime(Long decisionTime) {
    this.decisionTime = decisionTime;
  }

  public List<LocationES> getLocations() {
    return locations;
  }

  public void setLocations(List<LocationES> locations) {
    this.locations = locations;
  }

  public RoleTypedCustomerES getCustomers() {
    return customers;
  }

  public void setCustomers(RoleTypedCustomerES customers) {
    this.customers = customers;
  }

  public RecurringApplication getRecurringApplication() {
    return recurringApplication;
  }

  public void setRecurringApplication(RecurringApplication recurringApplication) {
    this.recurringApplication = recurringApplication;
  }

  public Integer getNrOfComments() {
    return nrOfComments;
  }

  public void setNrOfComments(Integer nrOfComments) {
    this.nrOfComments = nrOfComments;
  }

  public Long getReceivedTime() {
    return receivedTime;
  }

  public void setReceivedTime(Long receivedTime) {
    this.receivedTime = receivedTime;
  }

  public String getIdentificationNumber() {
    return identificationNumber;
  }

  public void setIdentificationNumber(String identificationNumber) {
    this.identificationNumber = identificationNumber;
  }

  public Boolean getOwnerNotification() {
    return ownerNotification;
  }

  public void setOwnerNotification(Boolean ownerNotification) {
    this.ownerNotification = ownerNotification;
  }

  public String getLatestComment() {
    return latestComment;
  }

  public void setLatestComment(String latestComment) {
    this.latestComment = latestComment;
  }

}
