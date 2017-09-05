package fi.hel.allu.model.domain;

import fi.hel.allu.common.util.TimeUtil;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

/**
 * User of the external-service. Conceptually different than <code>User</code>, because external user is only used in the external interface
 * and not as Allu user available for user interface.
 */
public class ExternalUser {
  private Integer id;
  private String username;
  private String name;
  private String emailAddress;
  private String token;
  private boolean active;
  private ZonedDateTime lastLogin;
  private List<Integer> connectedCustomers = Collections.emptyList();

  public ExternalUser() {
    // for JSON deserialization
  }

  public ExternalUser(
      Integer id,
      String username,
      String name,
      String emailAddress,
      String token,
      boolean active,
      ZonedDateTime lastLogin,
      List<Integer> connectedCustomers) {
    this.id = id;
    setUsername(username);
    this.name = name;
    this.emailAddress = emailAddress;
    this.token = token;
    this.active = active;
    this.lastLogin = lastLogin;
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
    if (!username.startsWith("external_")) {
      throw new IllegalArgumentException("External service user name has to begin with external_");
    }
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
   * Security token of the external user.
   *
   * @return  Security token of the external user.
   */
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
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
   * Returns last time the user logged in
   */
  public ZonedDateTime getLastLogin() {
    return TimeUtil.homeTime(lastLogin);
  }

  public void setLastLogin(ZonedDateTime lastLogin) {
    this.lastLogin = lastLogin;
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
}
