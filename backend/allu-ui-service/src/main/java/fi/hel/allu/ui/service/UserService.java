package fi.hel.allu.ui.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  private final Map<String, User> userMap = new HashMap<String, User>();

  @Override
  public final User loadUserByUsername(String username) throws UsernameNotFoundException {
    final User user = userMap.get(username);
    if (user == null) {
      throw new UsernameNotFoundException("user not found");
    }
    return user;
  }

  public void addUser(User user) {
    if (user != null && !userMap.containsKey(user.getUsername())) {
      userMap.put(user.getUsername(), user);
    }
  }
}