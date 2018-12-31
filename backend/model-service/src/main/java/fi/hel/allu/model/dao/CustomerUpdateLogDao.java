package fi.hel.allu.model.dao;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.model.domain.CustomerUpdateLog;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QCustomerUpdateLog.customerUpdateLog;

@Repository
public class CustomerUpdateLogDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<CustomerUpdateLog> customerUpdateLogBean = bean(CustomerUpdateLog.class, customerUpdateLog.all());

  @Transactional(readOnly = true)
  public List<CustomerUpdateLog> getUnprocessedUpdates() {
    return queryFactory.select(customerUpdateLogBean).from(customerUpdateLog)
        .where(customerUpdateLog.processedTime.isNull()).fetch();
  }

  @Transactional
  public void insertUpdateLog(CustomerUpdateLog log) {
    queryFactory.insert(customerUpdateLog).populate(log).execute();
  }

  @Transactional
  public void setUpdateLogsProcessed(List<Integer> logIds) {
    queryFactory.update(customerUpdateLog).set(customerUpdateLog.processedTime, ZonedDateTime.now())
        .where(customerUpdateLog.id.in(logIds)).execute();
  }

}
