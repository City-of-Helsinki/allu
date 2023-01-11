package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

/**
 * In Finnish: Hanke
 */
public class Project implements IdInterface {

  private Integer id;
  private Integer parentId;
  private String name;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private Integer[] cityDistricts;
  private String customerReference;
  private String additionalInfo;
  private Integer customerId;
  private Integer contactId;
  private String identifier;
  private Integer creatorId;

  public Project() {
  }

  public Project(Project other) {
    id = other.id;
    parentId = other.parentId;
    name = other.name;
    startTime = other.startTime;
    endTime = other.endTime;
    cityDistricts = other.cityDistricts;
    customerReference = other.customerReference;
    additionalInfo = other.additionalInfo;
    customerId = other.customerId;
    contactId = other.contactId;
    identifier = other.identifier;
    creatorId = other.creatorId;
  }

  /**
   * Id of the project.
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Parent id of the project.
   */
  public Integer getParentId() {
    return parentId;
  }

  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  /**
   * The name of the project.
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Starting date of the project. This date is calculated from the applications which project consists of.
   */
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  /**
   * Ending date of the project. This date is calculated from the applications which project consists of.
   */
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  /**
   * Returns list of city districts related to the project.
   *
   * @return  list of city districts related to the project.
   */
  public Integer[] getCityDistricts() {
    return cityDistricts;
  }

  public void setCityDistricts(Integer[] cityDistricts) {
    this.cityDistricts = cityDistricts;
  }

  /**
   * Reference for the customer. May be a work number (työnumero) or some other customer specific reference.
   */
  public String getCustomerReference() {
    return customerReference;
  }

  public void setCustomerReference(String customerReference) {
    this.customerReference = customerReference;
  }

  /**
   * Additional project information.
   */
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  public Integer getContactId() {
    return contactId;
  }

  public void setContactId(Integer contactId) {
    this.contactId = contactId;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public Integer getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(Integer creatorId) {
    this.creatorId = creatorId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Project project = (Project) o;

    return id != null ? id.equals(project.id) : project.id == null;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}