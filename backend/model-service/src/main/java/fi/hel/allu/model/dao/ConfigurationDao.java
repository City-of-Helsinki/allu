package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import static fi.hel.allu.QConfiguration.configuration;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.model.domain.ConfigurationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ConfigurationDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<Configuration> configurationBean = bean(Configuration.class, configuration.all());

  @Transactional(readOnly = true)
  public Optional<Configuration> findById(int configurationId) {
    Configuration config = queryFactory.select(configurationBean).from(configuration)
        .where(configuration.id.eq(configurationId)).fetchOne();
    return Optional.ofNullable(config);
  }

  @Transactional(readOnly = true)
  public List<Configuration> findByKey(ConfigurationKey key) {
    return queryFactory.select(configurationBean).from(configuration)
        .where(configuration.key.eq(key)).fetch();
  }

  @Transactional
  public Configuration insert(Configuration config) {
    final int id = queryFactory.insert(configuration).populate(config).executeWithKey(configuration.id);
    return findById(id).get();
  }

  @Transactional
  public Configuration update(int configurationId, Configuration config) {
    final long changed = queryFactory.update(configuration)
        .set(configuration.value, config.getValue())
        .where(configuration.id.eq(configurationId)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update the configuration", Integer.toString(configurationId));
    }
    return findById(configurationId).get();
  }

  @Transactional
  public void delete(int configurationId) {
    long count = queryFactory.delete(configuration).where(configuration.id.eq(configurationId)).execute();
    if (count == 0) {
      throw new NoSuchEntityException("Deleting configuration failed", Integer.toString(configurationId));
    }
  }
}
