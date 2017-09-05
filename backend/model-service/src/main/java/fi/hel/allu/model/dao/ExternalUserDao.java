package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.ResultTransformer;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.exception.NonUniqueException;
import fi.hel.allu.model.domain.ExternalUser;
import fi.hel.allu.model.postgres.ExceptionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QExternalUser.externalUser;
import static fi.hel.allu.QExternalUserCustomer.externalUserCustomer;

@Repository
public class ExternalUserDao {
  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<ExternalUser> externalUserBean = bean(ExternalUser.class, externalUser.all());


  @Transactional(readOnly = true)
  public Optional<ExternalUser> findById(int id) {
    // the query returns only one results at most
    return getSelectJoin()
        .where(externalUser.id.eq(id))
        .transform(getGroupBy()).values().stream().findFirst();
  }

  @Transactional(readOnly = true)
  public Optional<ExternalUser> findByUsername(String username) {
    // the query returns only one results at most
    return getSelectJoin()
        .where(externalUser.username.eq(username))
        .transform(getGroupBy()).values().stream().findFirst();
  }

  @Transactional(readOnly = true)
  public List<ExternalUser> findAll() {
    return new ArrayList<>(getSelectJoin().transform(getGroupBy()).values());
  }

  @Transactional
  public ExternalUser insert(ExternalUser userData) {
    try {
      Integer id = queryFactory.insert(externalUser).populate(userData).executeWithKey(externalUser.id);
      insertConnectedCustomers(id, userData.getConnectedCustomers());
      if (id == null) {
        throw new QueryException("Failed to insert external user");
      }
      return findById(id).get();
    } catch (DataIntegrityViolationException e) {
      if (ExceptionResolver.isUniqueConstraintViolation(e)) {
        throw new NonUniqueException(
            "Inserting external user failed. Perhaps given username collided with another: " + userData.getUsername());
      }
      throw e;
    }
  }

  @Transactional
  public void update(ExternalUser userData) throws NoSuchEntityException {
    try {
      queryFactory.delete(externalUserCustomer).where(externalUserCustomer.externalUserId.eq(userData.getId()));
      long changed = queryFactory.update(externalUser).populate(userData).where(externalUser.id.eq(userData.getId())).execute();
      if (changed == 0) {
        throw new NoSuchEntityException("Failed to update user", Integer.toString(userData.getId()));
      }
      insertConnectedCustomers(userData.getId(), userData.getConnectedCustomers());
    } catch (DataIntegrityViolationException e) {
      if (ExceptionResolver.isUniqueConstraintViolation(e)) {
        throw new NonUniqueException(
            "Updating external user failed. Perhaps given username collided with another: " + userData.getUsername());
      }
      throw e;
    }
  }

  @Transactional
  public void setLastLogin(int id, ZonedDateTime loginTime) {
    queryFactory.update(externalUser).set(externalUser.lastLogin, loginTime).where(externalUser.id.eq(id)).execute();
  }


  private void insertConnectedCustomers(int externalUserId, List<Integer> connectedCustomers) {
    connectedCustomers.forEach(customerId -> {
      queryFactory.insert(
          externalUserCustomer).set(externalUserCustomer.externalUserId, externalUserId).set(externalUserCustomer.customerId, customerId).execute();
    });
  }

  private ResultTransformer<Map<Integer,ExternalUser>> getGroupBy() {
    return groupBy(externalUser.id).as(Projections.constructor(
        ExternalUser.class,
        externalUser.id,
        externalUser.username,
        externalUser.name,
        externalUser.emailAddress,
        externalUser.token,
        externalUser.active,
        externalUser.lastLogin,
        list(externalUserCustomer.customerId)));
  }

  private SQLQuery<Tuple> getSelectJoin() {
    return queryFactory
        .select(externalUserBean, externalUserCustomer.customerId)
        .from(externalUser)
        .leftJoin(externalUserCustomer)
        .on(externalUser.id.eq(externalUserCustomer.externalUserId));
  }
}
