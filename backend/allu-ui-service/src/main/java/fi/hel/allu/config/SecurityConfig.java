package fi.hel.allu.config;

import fi.hel.allu.service.UserService;
import fi.hel.allu.security.StatelessAuthenticationFilter;
import fi.hel.allu.security.TokenAuthenticationService;
import fi.hel.allu.security.TokenHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public enum SECURITY_PATHS {
        LOGIN("/auth/login");

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

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // Allow anonymous logins
                .antMatchers(SECURITY_PATHS.LOGIN.toString()).permitAll()
                .anyRequest()
                .authenticated()
                .and().addFilterBefore(
                new StatelessAuthenticationFilter(tokenAuthenticationService()),
                UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService()).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public UserService userService() {
        return new UserService();
    }

    @Bean
    public TokenHandler tokenHandler() {
        return new TokenHandler(secret, Integer.parseInt(expirationHours), userService());
    }

    @Bean
    public TokenAuthenticationService tokenAuthenticationService() {
        return new TokenAuthenticationService();
    }
}
