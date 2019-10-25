package fi.hel.allu.model.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.model.domain.NotificationConfiguration;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QNotificationConfiguration.notificationConfiguration;

@Repository
public class NotificationConfigurationDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<NotificationConfiguration> notificationConfigurationBean = bean(NotificationConfiguration.class, notificationConfiguration.all());

  @Transactional(readOnly = true)
  public List<NotificationConfiguration> findAll() {
    return queryFactory.select(notificationConfigurationBean).from(notificationConfiguration).fetch();
  }
}
