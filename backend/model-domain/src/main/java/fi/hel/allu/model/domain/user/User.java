package fi.hel.allu.model.domain.user;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.common.util.TimeUtil;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Allu user data, which consists of user roles, application access rights and user specific UI configuration data.
 */
public class User {
  private Integer id;
  private String userName;
  private String realName;
  private String emailAddress;
  private String title;
  private boolean isActive;
  private ZonedDateTime lastLogin;
  private List<ApplicationType> allowedApplicationTypes = new ArrayList<>();
  private List<RoleType> assignedRoles = new ArrayList<>();
  private List<Integer> cityDistrictIds = new ArrayList<>();

  public User() {
    // for JSON deserialization
  }

  public User(Integer id,
              String userName,
              String realName,
              String emailAddress,
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
    this.title = title;
    this.isActive = isActive;
    this.lastLogin = lastLogin;
    setAllowedApplicationTypes(allowedApplicationTypes);
    setAssignedRoles(assignedRoles);
    setCityDistrictIds(cityDistrictIds);
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Returns the user name as specified by Helsinki AD.
   *
   * @return  the user name as specified by Helsinki AD.
   */
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * Returns the real name (such as Einari Esimerkki) of the user.
   *
   * @return   the real name of the user.
   */
  public String getRealName() {
    return realName;
  }

  public void setRealName(String realName) {
    this.realName = realName;
  }

  /**
   * Returns the email address of the user.
   *
   * @return   the email address of the user.
   */
  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  /**
   * Returns the title of the user (such as Johtaja).
   *
   * @return  the title of the user (such as Johtaja).
   */
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Returns true if this user is active in the system. If user is removed or passivated, user is not active.
   *
   * @return  true if this user is active in the system. If user is removed or passivated, user is not active.
   */
  public boolean isActive() {
    return isActive;
  }

  public void setIsActive(boolean active) {
    isActive = active;
  }

  public void setActive(boolean active) {
    // JSON deserialization expects setActive() whereas QueryDSL expects setIsActive(). Nice!
    setIsActive(active);
  }

  /**
   * Returns last time the user logged in
   */
  public ZonedDateTime getLastLogin() {
    return TimeUtil.homeTime(lastLogin);
  }

  public void setLastLogin(ZonedDateTime lastLogin) {
    this.lastLogin = lastLogin;
  }

  /**
   * Returns list of application types user is allowed to access alter.
   *
   * @return  list of application types user is allowed to access alter.
   */
  public List<ApplicationType> getAllowedApplicationTypes() {
    return allowedApplicationTypes;
  }

  public void setAllowedApplicationTypes(List<ApplicationType> allowedApplicationTypes) {
    this.allowedApplicationTypes = Optional.ofNullable(allowedApplicationTypes).orElse(Collections.emptyList());
  }

  /**
   * Returns list of roles assigned to the user.
   *
   * @return  list of roles assigned to the user.
   */
  public List<RoleType> getAssignedRoles() {
    return assignedRoles;
  }

  public void setAssignedRoles(List<RoleType> assignedRoles) {
    this.assignedRoles = Optional.ofNullable(assignedRoles).orElse(Collections.emptyList());
  }

  /**
   * Returns list of city district ids assigned to the user.
   *
   * @return  list of city district ids assigned to the user.
   */
  public List<Integer> getCityDistrictIds() {
    return cityDistrictIds;
  }

  public void setCityDistrictIds(List<Integer> cityDistrictIds) {
    this.cityDistrictIds = Optional.ofNullable(cityDistrictIds).orElse(Collections.emptyList());
  }
}
