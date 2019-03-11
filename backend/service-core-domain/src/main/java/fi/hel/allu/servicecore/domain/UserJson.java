package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.RoleType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * JSON mapping for Allu user data.
 */
@ApiModel(value = "Allu user")
public class UserJson {
  private Integer id;
  @NotBlank(message = "{user.userName}")
  private String userName;
  @NotBlank(message = "{user.realName}")
  private String realName;
  private String emailAddress;
  private String phone;
  @NotBlank(message = "{user.title}")
  private String title;
  @NotBlank(message = "{user.isActive}")
  private boolean isActive;
  private ZonedDateTime lastLogin;
  private List<ApplicationType> allowedApplicationTypes = new ArrayList<>();
  private List<RoleType> assignedRoles = new ArrayList<>();
  private List<Integer> cityDistrictIds;

  public UserJson() {
    // for JSON deserialization
  }

  public UserJson(Integer id) {
    this.id = id;
  }

  public UserJson(
      Integer id,
      String userName,
      String realName,
      String emailAddress,
      String phone,
      String title,
      boolean isActive,
      ZonedDateTime lastLogin,
      List<ApplicationType> allowedApplicationTypes,
      List<RoleType> assignedRoles,
      List<Integer> cityDistrictIds) {
    this.id = id;
    this.userName = userName;
    this.realName = realName;
    this.emailAddress = emailAddress;
    this.phone = phone;
    this.title = title;
    this.isActive = isActive;
    this.lastLogin = lastLogin;
    this.allowedApplicationTypes = allowedApplicationTypes;
    this.assignedRoles = assignedRoles;
    this.cityDistrictIds = cityDistrictIds;
  }

  @ApiModelProperty(value = "Id of the user")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  @ApiModelProperty(value = "User name (as specified by AD)")
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }


  @ApiModelProperty(value = "User full name")
  public String getRealName() {
    return realName;
  }

  public void setRealName(String realName) {
    this.realName = realName;
  }

  @ApiModelProperty(value = "User email address")
  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  @ApiModelProperty(value = "User phone number")
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  @ApiModelProperty(value = "Title of the user")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @ApiModelProperty(value = "True if the user is active in the system.")
  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }

  @ApiModelProperty(value = "Last login time")
  public ZonedDateTime getLastLogin() {
    return lastLogin;
  }

  public void setLastLogin(ZonedDateTime lastLogin) {
    this.lastLogin = lastLogin;
  }

  @ApiModelProperty(value = "Application types user is allowed to alter.")
  public List<ApplicationType> getAllowedApplicationTypes() {
    return allowedApplicationTypes;
  }

  public void setAllowedApplicationTypes(List<ApplicationType> allowedApplicationTypes) {
    this.allowedApplicationTypes = allowedApplicationTypes;
  }

  @ApiModelProperty(value = "List of roles assigned to the user")
  public List<RoleType> getAssignedRoles() {
    return assignedRoles;
  }

  public void setAssignedRoles(List<RoleType> assignedRoles) {
    this.assignedRoles = assignedRoles;
  }

  @ApiModelProperty(value = "List of city district IDs assigned to the user")
  public List<Integer> getCityDistrictIds() {
    return cityDistrictIds;
  }

  public void setCityDistrictIds(List<Integer> cityDistrictIds) {
    this.cityDistrictIds = cityDistrictIds;
  }
}
