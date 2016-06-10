package fi.hel.allu.model.config;

import java.sql.Connection;

import javax.inject.Provider;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.spatial.PostGISTemplates;
import com.querydsl.sql.spring.SpringConnectionProvider;
import com.querydsl.sql.spring.SpringExceptionTranslator;

import fi.hel.allu.model.querydsl.StringToCustomerType;

@Configuration
@EnableTransactionManagement
public class JdbcConfiguration {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private DataSource dataSource() {
    return jdbcTemplate.getDataSource();
  }

  @Bean
  public PlatformTransactionManager transactionManager() {
    return new DataSourceTransactionManager(dataSource());
  }

  @Bean
  public com.querydsl.sql.Configuration querydslConfiguration() {
    SQLTemplates templates = PostGISTemplates.builder().printSchema().build();
    com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(templates);
    configuration.setExceptionTranslator(new SpringExceptionTranslator());
    configuration.register(new StringToCustomerType());
    return configuration;
  }

  @Bean
  public SQLQueryFactory queryFactory() {
    Provider<Connection> provider = new SpringConnectionProvider(dataSource());
    return new SQLQueryFactory(querydslConfiguration(), provider);
  }

}