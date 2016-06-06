package fi.hel.allu.model.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fi.hel.allu.model.dao.ApplicantDao;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.ContactDao;
import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.OrganizationDao;
import fi.hel.allu.model.dao.PersonDao;
import fi.hel.allu.model.dao.ProjectDao;

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
