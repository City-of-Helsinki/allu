package fi.hel.allu.model.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.PersonDao;
import fi.hel.allu.model.dao.PersonDaoImpl;
import fi.hel.allu.model.dao.ProjectDao;
import fi.hel.allu.model.dao.ProjectDaoImpl;

@Configuration
@EnableAutoConfiguration
public class AppConfig {

    @Bean
    public PersonDao personDao() {
        return new PersonDaoImpl();
    }

    @Bean
    public ProjectDao projectDao() {
        return new ProjectDaoImpl();
    }

    @Bean
    public ApplicationDao applicationDao() {
        return new ApplicationDao();
    }
}
