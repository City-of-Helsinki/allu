package fi.hel.allu.ui.config;

import fi.hel.allu.ui.security.StatelessAuthenticationFilter;
import fi.hel.allu.ui.security.TokenAuthenticationService;
import fi.hel.allu.ui.security.TokenHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  public enum SECURITY_PATHS {
    LOGIN("/auth/login"), // TODO: remove or replace this with something once dummy login is removed
    OAUTH2("/oauth2/");

    private final String path;

    SECURITY_PATHS(String path) {
      this.path = path;
    }

    @Override
    public String toString() {
      return this.path;
    }
  }

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration.hours:12}")
  private String expirationHours;

  @Autowired
  private TokenAuthenticationService tokenAuthenticationService;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .authorizeRequests()
        // Allow anonymous logins
        .antMatchers(SECURITY_PATHS.LOGIN.toString()).permitAll() // TODO: remove or replace this with something once dummy login is removed
        .antMatchers(SECURITY_PATHS.OAUTH2.toString()).permitAll()
        .anyRequest()
        .authenticated()
        .and().addFilterBefore(
        new StatelessAuthenticationFilter(tokenAuthenticationService),
        UsernamePasswordAuthenticationFilter.class)
        .csrf().disable();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    UserDetailsService uds = (String username) ->
    { throw new UnsupportedOperationException("UserDetailsService.loadUserByUsername not expected to be called ever"); };
    auth.userDetailsService(uds);
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public TokenHandler tokenHandler() {
    return new TokenHandler(secret, Integer.parseInt(expirationHours));
  }
}
