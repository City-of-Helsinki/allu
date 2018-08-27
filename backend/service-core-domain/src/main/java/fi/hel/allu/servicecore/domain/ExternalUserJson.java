package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import fi.hel.allu.common.domain.types.ExternalRoleType;
import fi.hel.allu.common.util.TimeUtil;

/**
 * User of the external-service. Conceptually different than <code>UserJson</code>, because external user is only used in the external interface
 * and not as Allu user available for user interface.
 */
public class ExternalUserJson {
  public static final int PASSWORD_MIN_LENGTH = 20;
  public interface Create {
  }

  private Integer id;
  @NotBlank(message = "{externaluser.username}", groups = {Create.class, Default.class})
  private String username;
  @NotBlank(message = "{externaluser.name}", groups = {Create.class, Default.class})
  private String name;
  private String emailAddress;
  @NotEmpty(message = "{externaluser.password.required}", groups = Create.class)
  @Size(message = "{externaluser.password.length}", min = PASSWORD_MIN_LENGTH, groups = {Create.class, Default.class})
  private String password;
  private boolean active;
  @NotNull(message = "{externaluser.expirationTime}", groups = {Create.class, Default.class})
  private ZonedDateTime expirationTime = TimeUtil.millisToZonedDateTime(0);
  private ZonedDateTime lastLogin;
  private List<ExternalRoleType> assignedRoles = Collections.emptyList();
  private List<Integer> connectedCustomers = Collections.emptyList();

  public ExternalUserJson() {
    // for JSON deserialization
  }

  public ExternalUserJson(
      Integer id,
      String username,
      String name,
      String emailAddress,
      boolean active,
      ZonedDateTime expirationTime,
      ZonedDateTime lastLogin,
      List<ExternalRoleType> assignedRoles,
      List<Integer> connectedCustomers) {
    this.id = id;
    this.username = username;
    this.name = name;
    this.emailAddress = emailAddress;
    this.active = active;
    setExpirationTime(expirationTime);
    this.lastLogin = lastLogin;
    this.assignedRoles = assignedRoles;
    this.connectedCustomers = connectedCustomers;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Returns the username of external user. Always prefixed by <code>external_</code> to avoid confusion with Allu's other users.
   *
   * @return  the username of external user.
   */
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Returns the external user's human readable name (such as Testiyritys Oy) of the user.
   *
   * @return   the real name of the user.
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
   * Returns true if this user is active in the system. If user is removed or passivated, user is not active.
   *
   * @return  true if this user is active in the system. If user is removed or passivated, user is not active.
   */
  public boolean getActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  /**
   * Returns the expiration time of the user i.e. the time user is not allowed to access the system anymore.
   *
   * @return  the expiration time of the user i.e. the time user is not allowed to access the system anymore.
   */
  public ZonedDateTime getExpirationTime() {
    return expirationTime;
  }

  public void setExpirationTime(ZonedDateTime expirationTime) {
    if (expirationTime == null) {
      // expiration in the past
      this.expirationTime = TimeUtil.millisToZonedDateTime(0);
    } else {
      this.expirationTime = expirationTime;
    }
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
   * Returns the role types user has.
   *
   * @return  the role types user has.
   */
  public List<ExternalRoleType> getAssignedRoles() {
    return assignedRoles;
  }

  public void setAssignedRoles(List<ExternalRoleType> assignedRoles) {
    this.assignedRoles = assignedRoles;
  }

  /**
   * Allu customers connected to the external user. User is allowed to act on behalf of these customers.
   *
   * @return  Allu customers connected to the external user.
   */
  public List<Integer> getConnectedCustomers() {
    return connectedCustomers;
  }

  public void setConnectedCustomers(List<Integer> connectedCustomers) {
    this.connectedCustomers = connectedCustomers;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
