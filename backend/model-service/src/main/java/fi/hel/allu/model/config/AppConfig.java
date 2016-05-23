package fi.hel.allu.model.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;

import fi.hel.allu.model.dao.PersonDao;
import fi.hel.allu.model.dao.PersonDaoImpl;
import fi.hel.allu.model.dao.ProjectDao;
import fi.hel.allu.model.dao.ProjectDaoImpl;

@Configuration
@EnableAutoConfiguration
public class AppConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    public SQLQueryFactory queryFactory() {
        SQLTemplates templates = new PostgreSQLTemplates();
        com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(templates);
        SQLQueryFactory queryFactory = new SQLQueryFactory(configuration, jdbcTemplate.getDataSource());
        return queryFactory;
    }

    @Bean
    public PersonDao personDao() {
        return new PersonDaoImpl();
    }

    @Bean
    public ProjectDao projectDao() {
        return new ProjectDaoImpl();
    }
}
