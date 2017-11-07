package fi.hel.allu.model.config;

import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.spatial.PostGISTemplates;
import com.querydsl.sql.spring.SpringConnectionProvider;
import com.querydsl.sql.spring.SpringExceptionTranslator;

import fi.hel.allu.model.querydsl.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.inject.Provider;
import javax.sql.DataSource;

import java.sql.Connection;

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
    configuration.register(new StringToCustomerRoleType());
    configuration.register(new StringToApplicationType());
    configuration.register(new StringToApplicationKind());
    configuration.register(new StringToApplicationExtension());
    configuration.register(new StringToApplicationSpecifier());
    configuration.register(new StringToAttributeDataType());
    configuration.register(new StringToStatusType());
    configuration.register(new StringToDefaultTextType());
    configuration.register(new StringToChargeBasisUnit());
    configuration.register(new StringToAttachmentType());
    configuration.register(new StringToApplicationTagType());
    configuration.register(new StringToCommentType());
    configuration.register(new StringToChangeType());
    configuration.register(new StringToDistributionType());
    configuration.register(new StringToPublicityType());
    configuration.register(new StringToExternalRoleType());
    configuration.register(new StringToSupervisionTaskType());
    configuration.register(new StringToSupervisionTaskStatusType());
    configuration.register(new StringToChargeBasisType());
    return configuration;
  }

  @Bean
  public SQLQueryFactory queryFactory() {
    Provider<Connection> provider = new SpringConnectionProvider(dataSource());
    return new SQLQueryFactory(querydslConfiguration(), provider);
  }

}
