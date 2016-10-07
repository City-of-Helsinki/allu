package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.QUser;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.exception.NonUniqueException;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.RoleType;
import fi.hel.allu.model.domain.User;
import fi.hel.allu.model.postgres.ExceptionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QUser.user;
import static fi.hel.allu.QUserApplicationType.userApplicationType;
import static fi.hel.allu.QUserRole.userRole;

/**
 * User related database access.
 */
@Repository
public class UserDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<User> userBean = bean(User.class, user.all());

  @Transactional(readOnly = true)
  public Optional<User> findById(int id) {
    User user = queryFactory.select(userBean).from(QUser.user).where(QUser.user.id.eq(id)).fetchOne();
    if (user != null) {
      user.setAssignedRoles(getRoles(id));
      user.setAllowedApplicationTypes(getApplicationTypes(id));
    }
    return Optional.ofNullable(user);
  }

  @Transactional(readOnly = true)
  public Optional<User> findByUserName(String userName) {
    User user = queryFactory.select(userBean).from(QUser.user).where(QUser.user.userName.eq(userName)).fetchOne();
    if (user != null) {
      user.setAssignedRoles(getRoles(user.getId()));
      user.setAllowedApplicationTypes(getApplicationTypes(user.getId()));
    }
    return Optional.ofNullable(user);
  }

  @Transactional(readOnly = true)
  public List<User> findAll() {
    return queryFactory.select(userBean).from(QUser.user).fetchResults().getResults();
  }

  @Transactional
  public User insert(User userData) {
    try {
      Integer id = queryFactory.insert(user).populate(userData).executeWithKey(user.id);
      if (id == null) {
        throw new QueryException("Failed to insert record");
      }
      insertRoles(id, userData);
      insertApplicationTypes(id, userData);

      return findById(id).get();
    } catch (DataIntegrityViolationException e) {
      if (ExceptionResolver.isUniqueConstraintViolation(e)) {
        throw new NonUniqueException("Inserting user failed.");
      }
      throw e;
    }
  }

  @Transactional
  public void update(User userData) throws NoSuchEntityException {
    long changed = queryFactory.update(user).populate(userData).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update user", Integer.toString(userData.getId()));
    }
    queryFactory.delete(userRole).where(userRole.userId.eq(userData.getId())).execute();
    queryFactory.delete(userApplicationType).where(userApplicationType.userId.eq(userData.getId())).execute();
    insertRoles(userData.getId(), userData);
    insertApplicationTypes(userData.getId(), userData);
  }

  private List<RoleType> getRoles(Integer userId) {
    SQLQuery<String> roleQuery =
        queryFactory.select(userRole.role).from(userRole).where(userRole.userId.eq(userId));
    List<String> roles = roleQuery.fetch();
    return roles.stream().map(r -> RoleType.valueOf(r)).collect(Collectors.toList());
  }

  private List<ApplicationType> getApplicationTypes(Integer userId) {
    SQLQuery<String> appTypeQuery =
        queryFactory.select(userApplicationType.applicationType).from(userApplicationType).where(userApplicationType.userId.eq(userId));
    List<String> userApplicationTypes = appTypeQuery.fetch();
    return userApplicationTypes.stream().map(a -> ApplicationType.valueOf(a)).collect(Collectors.toList());
  }

  private void insertRoles(int id, User userData) {
    userData.getAssignedRoles().stream().forEach(
        role -> queryFactory.insert(userRole).set(userRole.userId, id).set(userRole.role, role.name()).execute());
  }

  private void insertApplicationTypes(int id, User userData) {
    userData.getAllowedApplicationTypes().stream().forEach(
        appType -> queryFactory.insert(userApplicationType)
            .set(userApplicationType.userId, id)
            .set(userApplicationType.applicationType, appType.name()).execute());
  }
}
