package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.ResultTransformer;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.common.domain.types.ExternalRoleType;
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
import static fi.hel.allu.QExternalUserRole.externalUserRole;

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
      replaceConnectedCustomers(id, userData.getConnectedCustomers());
      replaceAssignedRoles(id, userData.getAssignedRoles());
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
      long changed = queryFactory.update(externalUser).populate(userData).where(externalUser.id.eq(userData.getId())).execute();
      if (changed == 0) {
        throw new NoSuchEntityException("Failed to update user", Integer.toString(userData.getId()));
      }
      replaceConnectedCustomers(userData.getId(), userData.getConnectedCustomers());
      replaceAssignedRoles(userData.getId(), userData.getAssignedRoles());
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


  private void replaceConnectedCustomers(int externalUserId, List<Integer> connectedCustomers) {
    queryFactory.delete(externalUserCustomer).where(externalUserCustomer.externalUserId.eq(externalUserId)).execute();
    connectedCustomers.forEach(customerId -> {
      queryFactory.insert(
          externalUserCustomer).set(externalUserCustomer.externalUserId, externalUserId).set(externalUserCustomer.customerId, customerId).execute();
    });
  }

  private void replaceAssignedRoles(int externalUserId, List<ExternalRoleType> roles) {
    queryFactory.delete(externalUserRole).where(externalUserRole.externalUserId.eq(externalUserId)).execute();
    roles.forEach(role -> {
      queryFactory.insert(
          externalUserRole).set(externalUserRole.externalUserId, externalUserId).set(externalUserRole.role, role).execute();
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
        externalUser.expirationTime,
        externalUser.lastLogin,
        list(externalUserRole.role),
        list(externalUserCustomer.customerId)));
  }

  private SQLQuery<Tuple> getSelectJoin() {
    return queryFactory
        .select(externalUserBean, externalUserCustomer.customerId)
        .from(externalUser)
        .leftJoin(externalUserCustomer)
        .on(externalUser.id.eq(externalUserCustomer.externalUserId))
        .leftJoin(externalUserRole)
        .on(externalUser.id.eq(externalUserRole.externalUserId));
  }
}
