package fi.hel.allu.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Allu user object with additional email field
 */
public class AlluUser extends User {

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
