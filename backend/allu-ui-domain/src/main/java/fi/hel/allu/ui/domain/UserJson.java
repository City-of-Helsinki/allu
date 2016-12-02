package fi.hel.allu.ui.domain;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.RoleType;

import org.hibernate.validator.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON mapping for Allu user data.
 */
public class UserJson {
  private Integer id;
  @NotBlank(message = "{user.userName}")
  private String userName;
  @NotBlank(message = "{user.realName}")
  private String realName;
  @NotBlank(message = "{user.emailAddress}")
  private String emailAddress;
  @NotBlank(message = "{user.title}")
  private String title;
  @NotBlank(message = "{user.isActive}")
  private boolean isActive;
  private List<ApplicationType> allowedApplicationTypes = new ArrayList<>();
  private List<RoleType> assignedRoles = new ArrayList<>();

  public UserJson() {
    // for JSON deserialization
  }

  public UserJson(
      Integer id,
      String userName,
      String realName,
      String emailAddress,
      String title,
      boolean isActive,
      List<ApplicationType> allowedApplicationTypes,
      List<RoleType> assignedRoles) {
    this.id = id;
    this.userName = userName;
    this.realName = realName;
    this.emailAddress = emailAddress;
    this.title = title;
    this.isActive = isActive;
    this.allowedApplicationTypes = allowedApplicationTypes;
    this.assignedRoles = assignedRoles;
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

  public void setActive(boolean active) {
    isActive = active;
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
    this.allowedApplicationTypes = allowedApplicationTypes;
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
    this.assignedRoles = assignedRoles;
  }
}
