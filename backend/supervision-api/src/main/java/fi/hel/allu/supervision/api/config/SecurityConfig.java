package fi.hel.allu.supervision.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.security.StatelessAuthenticationFilter;
import fi.hel.allu.supervision.api.security.TokenAuthenticationService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  private final TokenAuthenticationService tokenAuthenticationService;
  private final ApplicationProperties applicationProperties;

  public SecurityConfig(TokenAuthenticationService tokenAuthenticationService,
      ApplicationProperties applicationProperties) {
    this.tokenAuthenticationService = tokenAuthenticationService;
    this.applicationProperties = applicationProperties;
  }

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

  /**
   * Exposed only for consistency with the Spring Security component-based configuration style;
   * not used by the JWT-based {@link StatelessAuthenticationFilter} authentication flow, which
   * bypasses the {@link AuthenticationManager} entirely (same as before this class's WebSecurityConfigurerAdapter
   * removal, where the underlying UserDetailsService was a throw-away stub never expected to be called).
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}