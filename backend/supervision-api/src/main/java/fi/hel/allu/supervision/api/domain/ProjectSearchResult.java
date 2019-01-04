package fi.hel.allu.supervision.api.domain;

import java.time.ZonedDateTime;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Project search result")
public class ProjectSearchResult {
  private Integer id;
  private String identifier;
  private String name;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private String ownerName;
  private String contactName;
  private String customerReference;
  private List<LocationSearchResult> locations;

  @ApiModelProperty(value = "Id of the project")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value = "Project identifier (hanketunniste)")
  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  @ApiModelProperty(value = "Name of the project")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @ApiModelProperty(value = "Start time of the project")
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  @ApiModelProperty(value = "End time of the project")
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  @ApiModelProperty(value = "Owner (customer) of the project")
  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  @ApiModelProperty(value = "Contact person of the project")
  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  @ApiModelProperty(value = "Customer reference")
  public String getCustomerReference() {
    return customerReference;
  }

  public void setCustomerReference(String customerReference) {
    this.customerReference = customerReference;
  }

  @ApiModelProperty(value = "Project locations (locations of applications belonging to project)")
  public List<LocationSearchResult> getLocations() {
    return locations;
  }

  public void setLocations(List<LocationSearchResult> locations) {
    this.locations = locations;
  }

}
