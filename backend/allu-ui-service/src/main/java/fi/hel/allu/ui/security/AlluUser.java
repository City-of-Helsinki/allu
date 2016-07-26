package fi.hel.allu.ui.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * Allu user object with additional email field
 */
public class AlluUser extends User {

  /**
   * Version UID for serialization
   */
  private static final long serialVersionUID = 1L;

  private final String emailAddress;

  public AlluUser(String username,
                  String password,
                  Collection<? extends GrantedAuthority> authorities,
                  String emailAddress) {
    super(username, password, authorities);
    this.emailAddress = emailAddress;
  }

  public String getEmailAddress() {
    return emailAddress;
  }
}
