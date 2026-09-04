package fi.hel.allu.external.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import fi.hel.allu.external.service.ExternalUserDetailService;
import fi.hel.allu.external.service.ServerTokenAuthenticationService;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.security.StatelessAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  @Autowired
  private ServerTokenAuthenticationService tokenAuthenticationService;
  @Autowired
  private ExternalUserDetailService externalUserDetailService;
  @Autowired
  private ApplicationProperties applicationProperties;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() // disable use of JSESSIONID
        .authorizeHttpRequests()
        // Allow anonymous logins for configured paths
        .requestMatchers(applicationProperties.getAnonymousAccessPaths().toArray(new String[0])).permitAll()
        .anyRequest()
        .authenticated()
        .and().addFilterBefore(
            new StatelessAuthenticationFilter(tokenAuthenticationService),
            UsernamePasswordAuthenticationFilter.class)
        .csrf().disable();
    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return externalUserDetailService;
  }

  /**
   * The real {@link ExternalUserDetailService} + {@link PasswordEncoder} combination is preserved here
   * (unlike allu-ui-service/supervision-api, this AuthenticationManager is actually exercised by
   * {@code AuthenticationController#login} for username/password credential exchange).
   */
  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder =
        http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder);
    return authenticationManagerBuilder.build();
  }

}
