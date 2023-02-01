package fi.hel.allu.model.dao;

import java.time.ZonedDateTime;
import java.util.List;

import fi.hel.allu.common.util.EmptyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.model.domain.InvoicingPeriod;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QInvoicingPeriod.invoicingPeriod;
import static fi.hel.allu.QChargeBasis.chargeBasis;

@Repository
public class InvoicingPeriodDao {

  private final SQLQueryFactory queryFactory;

  private final QBean<InvoicingPeriod> invoicingPeriodBean = bean(InvoicingPeriod.class, invoicingPeriod.all());

  @Autowired
  public InvoicingPeriodDao(SQLQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Transactional
  public List<InvoicingPeriod> insertPeriods(List<InvoicingPeriod> periods) {
     periods.forEach(p -> insertInvoicingPeriod(p));
     return periods;
  }

  @Transactional
  public InvoicingPeriod insertInvoicingPeriod(InvoicingPeriod p) {
    Integer id = queryFactory.insert(invoicingPeriod).populate(p).executeWithKey(invoicingPeriod.id);
    p.setId(id);
    return p;
  }

  @Transactional
  public InvoicingPeriod findInvoicingPeriod(Integer id){
    return queryFactory.select(invoicingPeriodBean).from(invoicingPeriod)
      .where(invoicingPeriod.id.eq(id)).fetchOne();
  }

  @Transactional(readOnly = true)
  public List<InvoicingPeriod> findForApplicationId(Integer applicationId) {
    return queryFactory.select(invoicingPeriodBean).from(invoicingPeriod)
        .where(invoicingPeriod.applicationId.eq(applicationId)).fetch();
  }

  @Transactional
  public void deletePeriods(Integer applicationId) {
    queryFactory.update(chargeBasis).setNull(chargeBasis.invoicingPeriodId).where(chargeBasis.applicationId.eq(applicationId)).execute();
    queryFactory.delete(invoicingPeriod).where(invoicingPeriod.applicationId.eq(applicationId)).execute();
 }

  @Transactional(readOnly = true)
  public List<InvoicingPeriod> findOpenPeriodsForApplicationId(Integer applicationId) {
    return queryFactory.select(invoicingPeriodBean).from(invoicingPeriod)
        .where(invoicingPeriod.applicationId.eq(applicationId), invoicingPeriod.closed.isFalse()).fetch();
  }

   @Transactional
   public void closeInvoicingPeriods(List<Integer> invoicingPeriodIds) {
    queryFactory.update(invoicingPeriod).set(invoicingPeriod.closed, true)
        .where(invoicingPeriod.id.in(invoicingPeriodIds)).execute();
   }

   @Transactional
   public void deleteByPeriodIds(List<Integer> periodIds) {
       // Deletes also invoices and charge basis entries
       if (EmptyUtil.isNotEmpty(periodIds)) {
           queryFactory.delete(invoicingPeriod).where(invoicingPeriod.id.in(periodIds)).execute();
       }
   }

   public void closeInvoicingPeriod(Integer invoicingPeriodId) {
    queryFactory.update(invoicingPeriod).set(invoicingPeriod.closed, true)
        .where(invoicingPeriod.id.eq(invoicingPeriodId)).execute();
   }

  @Transactional
  public void removeEntriesFromPeriods(List<Integer> periodIds) {
      if (EmptyUtil.isNotEmpty(periodIds)) {
          queryFactory.update(chargeBasis)
             .setNull(chargeBasis.invoicingPeriodId)
             .where(chargeBasis.invoicingPeriodId.in(periodIds))
             .execute();
      }
  }

  @Transactional
  public void updateEndTimes(List<Integer> periodId, ZonedDateTime endTime) {
      if (EmptyUtil.isNotEmpty(periodId)) {
          queryFactory.update(invoicingPeriod).set(invoicingPeriod.endTime, endTime)
                  .where(invoicingPeriod.id.in(periodId)).execute();
      }
  }
}