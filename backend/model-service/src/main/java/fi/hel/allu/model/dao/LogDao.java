package fi.hel.allu.model.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.domain.MailSenderLog;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QMailSenderLog.mailSenderLog;

@Repository
public class LogDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<MailSenderLog> logBean = bean(MailSenderLog.class, mailSenderLog.all());

  @Transactional
  public Integer insert(MailSenderLog log) {
    log.setId(null);
    return queryFactory.insert(mailSenderLog).populate(log).executeWithKey(mailSenderLog.id);
  }
}
