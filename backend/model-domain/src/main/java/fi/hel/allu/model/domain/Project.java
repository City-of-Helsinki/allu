package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

/**
 * In Finnish: Hanke
 */
public class Project {

  private Integer id;
  private Integer parentId;
  private String name;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private String ownerName;
  private String contactName;
  private String email;
  private String phone;
  private String customerReference;
  private String additionalInfo;

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
   * Owner of the project. Usually the company that owns the project.
   */
  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  /**
   * Contact for the project. Usually the name of the person who acts as the contact for the project.
   */
  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  /**
   * The email address of the project (contact).
   */
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * The phone number of the project (contact).
   */
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
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
