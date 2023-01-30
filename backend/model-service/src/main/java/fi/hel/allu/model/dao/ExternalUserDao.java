package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.ResultTransformer;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;
import fi.hel.allu.common.domain.types.ExternalRoleType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.exception.NonUniqueException;
import fi.hel.allu.common.util.EmptyUtil;
import fi.hel.allu.model.domain.user.ExternalUser;
import fi.hel.allu.model.postgres.ExceptionResolver;
import fi.hel.allu.model.querydsl.ExcludingMapper;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QExternalUser.externalUser;
import static fi.hel.allu.QExternalUserCustomer.externalUserCustomer;
import static fi.hel.allu.QExternalUserRole.externalUserRole;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

@Repository
public class ExternalUserDao {


  protected static final List<Path<?>> UPDATE_READ_ONLY_FIELDS =
          Collections.singletonList(externalUser.password);

  private final SQLQueryFactory queryFactory;

  final QBean<ExternalUser> externalUserBean = bean(ExternalUser.class, externalUser.all());

  public ExternalUserDao(SQLQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Transactional(readOnly = true)
  public Optional<ExternalUser> findById(int id) {
    Optional<ExternalUser> externalUserOpt = getSelectJoin()
        .where(externalUser.id.eq(id))
        .transform(getGroupBy()).values().stream().findFirst();
    externalUserOpt.ifPresent(this::addRoles);
    return externalUserOpt;
  }

  @Transactional(readOnly = true)
  public Optional<ExternalUser> findByUsername(String username) {
    // the query returns only one results at most
    Optional<ExternalUser> externalUserOpt = getSelectJoin()
        .where(externalUser.username.eq(username))
        .transform(getGroupBy()).values().stream().findFirst();
    externalUserOpt.ifPresent(this::addRoles);
    return externalUserOpt;
  }

  @Transactional(readOnly = true)
  public List<ExternalUser> findAll() {
    Collection<ExternalUser> externalUsers = getSelectJoin().transform(getGroupBy()).values();
    addRolesToUsers(externalUsers);
    return new ArrayList<>(externalUsers);
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
      long changed = queryFactory.update(externalUser).populate(userData, new ExcludingMapper(WITH_NULL_BINDINGS,
          UPDATE_READ_ONLY_FIELDS)).where(externalUser.id.eq(userData.getId())).execute();
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
  public void setPassword(int id, String password) {
    queryFactory.update(externalUser).set(externalUser.password, password).where(externalUser.id.eq(id)).execute();
  }

  @Transactional
  public void setLastLogin(int id, ZonedDateTime loginTime) {
    queryFactory.update(externalUser).set(externalUser.lastLogin, loginTime).where(externalUser.id.eq(id)).execute();
  }

  private void replaceConnectedCustomers(int externalUserId, List<Integer> connectedCustomers) {
    queryFactory.delete(externalUserCustomer).where(externalUserCustomer.externalUserId.eq(externalUserId)).execute();
    if (EmptyUtil.isNotEmpty(connectedCustomers)) {
      SQLInsertClause insertClause = queryFactory.insert(externalUserCustomer);
      connectedCustomers.forEach(customerId -> insertClause.set(externalUserCustomer.externalUserId, externalUserId)
              .set(externalUserCustomer.customerId, customerId).addBatch());
      insertClause.execute();
    }
  }

  private void replaceAssignedRoles(int externalUserId, List<ExternalRoleType> roles) {
    queryFactory.delete(externalUserRole).where(externalUserRole.externalUserId.eq(externalUserId)).execute();
    if(EmptyUtil.isNotEmpty(roles)) {
      SQLInsertClause insertClause = queryFactory.insert(externalUserRole);
      roles.forEach(role -> insertClause.set(externalUserRole.externalUserId, externalUserId)
              .set(externalUserRole.role, role).addBatch());
      insertClause.execute();
    }
  }

  private ResultTransformer<Map<Integer,ExternalUser>> getGroupBy() {
    return groupBy(externalUser.id).as(Projections.constructor(
        ExternalUser.class,
        externalUser.id,
        externalUser.username,
        externalUser.name,
        externalUser.emailAddress,
        externalUser.password,
        externalUser.active,
        externalUser.expirationTime,
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

  private void addRoles(ExternalUser externalUser) {
    externalUser.setAssignedRoles(
        queryFactory
            .select(externalUserRole.role)
            .from(externalUserRole)
            .where(externalUserRole.externalUserId.eq(externalUser.getId()))
            .fetch());
  }

  private void addRolesToUsers(Collection<ExternalUser> externalUsers) {
    if(EmptyUtil.isNotEmpty(externalUsers)) {
      Map<Integer, List<ExternalRoleType>> mappedRoles = queryFactory
              .select(externalUserRole.role)
              .from(externalUserRole)
              .where(externalUserRole.externalUserId.in(
                      externalUsers.stream().map(ExternalUser::getId).collect(Collectors.toList())))
              .transform(groupBy(externalUserRole.externalUserId).as(list(externalUserRole.role)));
      externalUsers.forEach(user -> mapRolesToUser(user, mappedRoles));
    }
  }

  private void mapRolesToUser(ExternalUser user, Map<Integer, List<ExternalRoleType>> mappedRoles) {
    if (mappedRoles.containsKey(user.getId())) {
      user.setAssignedRoles(mappedRoles.get(user.getId()));
    }
  }
}