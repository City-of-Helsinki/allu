package fi.hel.allu.supervision.api.config;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.security.StatelessAuthenticationFilter;
import fi.hel.allu.supervision.api.security.TokenAuthenticationService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private TokenAuthenticationService tokenAuthenticationService;
  @Autowired
  private ApplicationProperties applicationProperties;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() // disable use of JSESSIONID
        .authorizeRequests()
        // Allow anonymous logins for configured paths
        .antMatchers(applicationProperties.getAnonymousAccessPaths().toArray(new String[0])).permitAll()
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
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}