package fi.hel.allu.ui.config;

import fi.hel.allu.ui.security.StatelessAuthenticationFilter;
import fi.hel.allu.ui.security.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  public enum SECURITY_PATHS {
    LOGIN("/auth/login"), // TODO: remove or replace this with something once dummy login is removed
    OAUTH2("/oauth2/"),
    UICONFIG("/uiconfig");

    private final String path;

    SECURITY_PATHS(String path) {
      this.path = path;
    }

    @Override
    public String toString() {
      return this.path;
    }
  }

  @Autowired
  private TokenAuthenticationService tokenAuthenticationService;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() // disable use of JSESSIONID
        .authorizeRequests()
        // Allow anonymous logins
        .antMatchers(SECURITY_PATHS.LOGIN.toString()).permitAll() // TODO: remove or replace this with something once dummy login is removed
        .antMatchers(SECURITY_PATHS.OAUTH2.toString()).permitAll()
        .antMatchers(SECURITY_PATHS.UICONFIG.toString()).permitAll()
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
}
