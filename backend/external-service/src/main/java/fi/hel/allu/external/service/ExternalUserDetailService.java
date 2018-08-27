package fi.hel.allu.external.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.model.domain.user.ExternalUser;
import fi.hel.allu.servicecore.config.ApplicationProperties;

@Service
public class ExternalUserDetailService implements UserDetailsService {

  private final RestTemplate restTemplate;
  private final ApplicationProperties applicationProperties;

  @Autowired
  public ExternalUserDetailService(RestTemplate restTemplate, ApplicationProperties applicationProperties) {
    this.restTemplate = restTemplate;
    this.applicationProperties = applicationProperties;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    ExternalUser externalUser = restTemplate
        .getForEntity(applicationProperties.getExternalUserByUserNameUrl(), ExternalUser.class, username).getBody();
    boolean isActive = externalUser.getActive();
    boolean notExpired = !externalUser.getExpirationTime().isBefore(ZonedDateTime.now());
    return new User(externalUser.getUsername(), externalUser.getPassword(), isActive, notExpired, notExpired, isActive,
        getRoles(externalUser));
  }

  private List<GrantedAuthority> getRoles(ExternalUser externalUser) {
    return externalUser.getAssignedRoles().stream().map(r -> new SimpleGrantedAuthority(r.name())).collect(Collectors.toList());
  }

}
