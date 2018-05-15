package fi.hel.allu.model.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.domain.ExternalApplication;
import fi.hel.allu.model.domain.Deposit;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QDeposit.deposit;
import static fi.hel.allu.QExternalApplication.externalApplication;

@Repository
public class ExternalApplicationDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  private final QBean<ExternalApplication> externalApplicationBean = bean(ExternalApplication.class, externalApplication.all());

  @Transactional
  public void save(ExternalApplication externalApp) {
    removeExisting(externalApp.getApplicationId(), externalApp.getInformationRequestId());
    queryFactory.insert(externalApplication).populate(externalApp).executeWithKey(externalApplication);
  }

  @Transactional(readOnly = true)
  public ExternalApplication findByApplicationId(Integer applicationId) {
    return queryFactory.select(externalApplicationBean).where(externalApplication.applicationId.eq(applicationId)).fetchOne();
  }

  private void removeExisting(Integer applicationId, Integer informationRequestId) {
    BooleanExpression informationRequestExpression = informationRequestId != null
        ? externalApplication.informationRequestId.eq(informationRequestId)
        : externalApplication.informationRequestId.isNull();
    queryFactory.delete(externalApplication).where(externalApplication.applicationId.eq(applicationId), informationRequestExpression).execute();
  }




}
