package fi.hel.allu.model.config;

import fi.hel.allu.model.dao.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class AppConfig {

  @Bean
  public PersonDao personDao() {
    return new PersonDao();
  }

  @Bean
  public ProjectDao projectDao() {
    return new ProjectDao();
  }

  @Bean
  public ApplicationDao applicationDao() {
    return new ApplicationDao();
  }

  @Bean
  public ApplicantDao applicantDao() {
    return new ApplicantDao();
  }

  @Bean
  public ContactDao contactDao() {
    return new ContactDao();
  }

  @Bean
  public CustomerDao customerDao() {
    return new CustomerDao();
  }

  @Bean
  public OrganizationDao organizationDao() {
    return new OrganizationDao();
  }

  @Bean
  public LocationDao locationDao() {
    return new LocationDao();
  }
}
