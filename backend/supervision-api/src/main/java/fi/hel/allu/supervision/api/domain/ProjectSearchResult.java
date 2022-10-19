package fi.hel.allu.supervision.api.domain;

import java.time.ZonedDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Project search result")
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

  @Schema(description = "Id of the project")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(description = "Project identifier (hanketunniste)")
  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  @Schema(description = "Name of the project")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Schema(description = "Start time of the project")
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  @Schema(description = "End time of the project")
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  @Schema(description = "Owner (customer) of the project")
  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  @Schema(description = "Contact person of the project")
  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  @Schema(description = "Customer reference")
  public String getCustomerReference() {
    return customerReference;
  }

  public void setCustomerReference(String customerReference) {
    this.customerReference = customerReference;
  }

  @Schema(description = "Project locations (locations of applications belonging to project)")
  public List<LocationSearchResult> getLocations() {
    return locations;
  }

  public void setLocations(List<LocationSearchResult> locations) {
    this.locations = locations;
  }

}
