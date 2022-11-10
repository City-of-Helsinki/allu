package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Project (hanke)")
public class ProjectJson extends BaseProjectJson implements BaseJsonInterface {
  private Integer id;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private List<Integer> cityDistricts;
  private Integer parentId;
  @NotNull
  private CustomerJson customer;
  @NotNull
  private ContactJson contact;
  private UserJson creator;

  public ProjectJson() {
  }

  public ProjectJson(Integer id) {
    this.id = id;
  }

  @Schema(description = "Id of the project")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(description = "Start time of the project. Calculated from the applications of the project.")
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  @Schema(description = "End time of the project. Calculated from the applications of the project.")
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  @Schema(description = "City districts of the project. Calculated from the applications of the project.")
  public List<Integer> getCityDistricts() {
    return cityDistricts;
  }

  public void setCityDistricts(List<Integer> cityDistricts) {
    this.cityDistricts = cityDistricts;
  }

  @Schema(description = "Id of the parent project")
  public Integer getParentId() {
    return parentId;
  }

  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  @Schema(description = "Customer of the project")
  public CustomerJson getCustomer() {
    return customer;
  }

  public void setCustomer(CustomerJson customer) {
    this.customer = customer;
  }

  @Schema(description = "Contact of the project")
  public ContactJson getContact() {
    return contact;
  }

  public void setContact(ContactJson contact) {
    this.contact = contact;
  }

  @Schema(description = "Creator of the project")
  public UserJson getCreator() {
    return creator;
  }

  public void setCreator(UserJson creator) {
    this.creator = creator;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ProjectJson that = (ProjectJson) o;

    return id != null ? id.equals(that.id) : that.id == null;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}
