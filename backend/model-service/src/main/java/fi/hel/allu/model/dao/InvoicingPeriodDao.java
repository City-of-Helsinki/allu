package fi.hel.allu.model.dao;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.QInvoice;
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
   public void deletePeriod(Integer periodId) {
     // Deletes also invoices and charge basis entries
     queryFactory.delete(invoicingPeriod).where(invoicingPeriod.id.eq(periodId)).execute();
   }

   public void closeInvoicingPeriod(Integer invoicingPeriodId) {
    queryFactory.update(invoicingPeriod).set(invoicingPeriod.closed, true)
        .where(invoicingPeriod.id.eq(invoicingPeriodId)).execute();
   }

  @Transactional
  public void removeEntriesFromPeriod(Integer periodId) {
    queryFactory.update(chargeBasis)
       .setNull(chargeBasis.invoicingPeriodId)
       .where(chargeBasis.invoicingPeriodId.eq(periodId))
       .execute();
  }

  @Transactional
  public void updateEndTime(Integer periodId, ZonedDateTime endTime) {
    queryFactory.update(invoicingPeriod)
      .set(invoicingPeriod.endTime, endTime)
      .where(invoicingPeriod.id.eq(periodId))
      .execute();
  }
}
