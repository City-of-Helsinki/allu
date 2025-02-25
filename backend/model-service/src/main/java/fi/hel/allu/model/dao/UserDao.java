package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.QUser;
import fi.hel.allu.QUserApplicationType;
import fi.hel.allu.QUserRole;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.exception.NonUniqueException;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.postgres.ExceptionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.Projections.bean;
import static com.querydsl.sql.SQLExpressions.select;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QUser.user;
import static fi.hel.allu.QUserApplicationType.userApplicationType;
import static fi.hel.allu.QUserCityDistrict.userCityDistrict;
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
      mapUsersRolesTypes(
          Collections.singletonList(user),
          getRoles(Collections.singletonList(id)),
          getApplicationTypes(Collections.singletonList(id)),
          getCityDistricts(Collections.singletonList(id)));
    }
    return Optional.ofNullable(user);
  }

  @Transactional(readOnly = true)
  public List<User> findByIds(List<Integer> ids) {
    List<User> users = queryFactory.select(userBean).from(user).where(user.id.in(ids)).fetch();
    users.forEach(e -> mapDefaultUsersRolesTypes(e));
    return users;
  }

  @Transactional(readOnly = true)
  public Map<Integer, User> findByApplicationIds(List<Integer> ids) {
    List<Tuple> users = queryFactory.select(userBean, application.id)
            .from(user)
            .leftJoin(application).on(user.id.eq(application.owner))
            .where(application.id.in(ids)).fetch();
    Map<Integer, User> mapUser = new HashMap<>();
    for(Tuple tuple : users){
      User user = tuple.get(0, User.class);
      mapDefaultUsersRolesTypes(user);
      mapUser.put(tuple.get(1, Integer.class), user);
    }
    return mapUser;
  }

   private void mapDefaultUsersRolesTypes(User user){
    if (user != null) {
      mapUsersRolesTypes(
              Collections.singletonList(user),
              getRoles(Collections.singletonList(user.getId())),
              getApplicationTypes(Collections.singletonList(user.getId())),
              getCityDistricts(Collections.singletonList(user.getId())));
    }
   }

  @Transactional(readOnly = true)
  public Optional<User> findByUserName(String userName) {
    User user = queryFactory.select(userBean).from(QUser.user).where(QUser.user.userName.eq(userName)).fetchOne();
    if (user != null) {
      mapUsersRolesTypes(
          Collections.singletonList(user),
          getRoles(Collections.singletonList(user.getId())),
          getApplicationTypes(Collections.singletonList(user.getId())),
          getCityDistricts(Collections.singletonList(user.getId())));
    }
    return Optional.ofNullable(user);
  }

  @Transactional
  public User findAnonymizationUser() {
    return findByUserName("alluanon").get();
  }

  @Transactional(readOnly = true)
  public List<User> findAll() {
    List<User> users = queryFactory.select(userBean).from(QUser.user)
        .orderBy(QUser.user.realName.asc()).fetchResults().getResults();
    List<Integer> userIds = users.stream().map(user -> user.getId()).collect(Collectors.toList());
    mapUsersRolesTypes(users, getRoles(userIds), getApplicationTypes(userIds), getCityDistricts(userIds));
    return users;
  }

  /**
   * Find all users that match the given role, application type, and city
   * district id.
   *
   * @param roleType
   *          required role type
   * @param applicationType
   *          required application type
   * @param cityDistrictId
   *          required city district id
   * @return
   */
  @Transactional(readOnly = true)
  public List<User> findMatching(RoleType roleType, ApplicationType applicationType, Integer cityDistrictId) {
    List<User> users = queryFactory.select(userBean).from(QUser.user).innerJoin(userApplicationType)
        .on(user.id.eq(userApplicationType.userId)).innerJoin(userRole).on(user.id.eq(userRole.userId))
        .innerJoin(userCityDistrict).on(user.id.eq(userCityDistrict.userId))
        .where(userApplicationType.applicationType.eq(applicationType.name())
            .and(userRole.role.eq(roleType.name()).and(userCityDistrict.cityDistrictId.eq(cityDistrictId))))
        .fetchResults().getResults();
    List<Integer> userIds = users.stream().map(user -> user.getId()).collect(Collectors.toList());
    mapUsersRolesTypes(users, getRoles(userIds), getApplicationTypes(userIds), getCityDistricts(userIds));
    return users;
  }

  @Transactional(readOnly = true)
  public List<User> findMatching(RoleType hasRoleType, RoleType doesntHaveRoleType, ApplicationType applicationType, Integer cityDistrictId) {
    // Find users that have role 'hasRoleType' but don't have role 'doesntHaveRoleType' and that
    // have 'applicationType' allowed and that have 'cityDistrcitId' enabled.
    final List<User> users = queryFactory
        .select(userBean).from(
          select(userBean).from(QUser.user).innerJoin(userApplicationType)
            .on(user.id.eq(userApplicationType.userId)).innerJoin(userRole).on(user.id.eq(userRole.userId))
            .innerJoin(userCityDistrict).on(user.id.eq(userCityDistrict.userId))
            .where(userApplicationType.applicationType.eq(applicationType.name())
                .and(userRole.role.eq(hasRoleType.name())
                .and(userCityDistrict.cityDistrictId.eq(cityDistrictId)))).as("user"))
        .where(user.id.notIn(
          select(user.id).from(QUser.user).innerJoin(userApplicationType)
            .on(user.id.eq(userApplicationType.userId)).innerJoin(userRole).on(user.id.eq(userRole.userId))
            .innerJoin(userCityDistrict).on(user.id.eq(userCityDistrict.userId))
            .where(userApplicationType.applicationType.eq(applicationType.name())
                .and(userRole.role.eq(doesntHaveRoleType.name())
                .and(userCityDistrict.cityDistrictId.eq(cityDistrictId)))))
        ).fetchResults().getResults();
    final List<Integer> userIds = users.stream().map(user -> user.getId()).collect(Collectors.toList());
    mapUsersRolesTypes(users, getRoles(userIds), getApplicationTypes(userIds), getCityDistricts(userIds));
    return users;
  }

  @Transactional(readOnly = true)
  public List<User> findByRole(RoleType roleType) {
    List<User> users = queryFactory.select(userBean).from(QUser.user)
        .innerJoin(userRole).on(user.id.eq(userRole.userId))
        .where(userRole.role.eq(roleType.name()))
        .orderBy(QUser.user.realName.asc())
        .fetchResults().getResults();
    List<Integer> userIds = users.stream().map(user -> user.getId()).collect(Collectors.toList());
    mapUsersRolesTypes(users, getRoles(userIds), getApplicationTypes(userIds), getCityDistricts(userIds));
    return users;
  }

  @Transactional
  public User insert(User userData) {
    try {
      Integer id = queryFactory.insert(user).populate(userData).executeWithKey(user.id);
      if (id == null) {
        throw new QueryException("user.insert.failed");
      }
      insertRoles(id, userData);
      insertApplicationTypes(id, userData);
      insertCityDistricts(id, userData);
      return findById(id).get();
    } catch (DataIntegrityViolationException e) {
      if (ExceptionResolver.isUniqueConstraintViolation(e)) {
        throw new NonUniqueException("user.insert.nonunique");
      }
      throw e;
    }
  }

  @Transactional
  public void update(User userData) throws NoSuchEntityException {
    try {
      long changed = queryFactory.update(user).populate(userData).where(user.id.eq(userData.getId())).execute();
      if (changed == 0) {
        throw new NoSuchEntityException("user.update.failed", userData.getId());
      }
      queryFactory.delete(userRole).where(userRole.userId.eq(userData.getId())).execute();
      queryFactory.delete(userApplicationType).where(userApplicationType.userId.eq(userData.getId())).execute();
      queryFactory.delete(userCityDistrict).where(userCityDistrict.userId.eq(userData.getId())).execute();
      insertRoles(userData.getId(), userData);
      insertApplicationTypes(userData.getId(), userData);
      insertCityDistricts(userData.getId(), userData);
    } catch (DataIntegrityViolationException e) {
      if (ExceptionResolver.isUniqueConstraintViolation(e)) {
        throw new NonUniqueException("user.update.nonunique");
      }
      throw e;
    }
  }

  @Transactional
  public void setLastLogin(int id, ZonedDateTime loginTime) {
    queryFactory.update(user).set(user.lastLogin, loginTime).where(user.id.eq(id)).execute();
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

  private void insertCityDistricts(int id, User userData) {
    userData.getCityDistrictIds().stream().forEach(
        cityDistrictId -> queryFactory
            .insert(userCityDistrict)
            .set(userCityDistrict.userId, id)
            .set(userCityDistrict.cityDistrictId, cityDistrictId)
            .execute());
  }

  private Map<Integer, List<RoleType>> getRoles(List<Integer> userIds) {
    Map<Integer, List<String>> userIdToRoleName = queryFactory
        .from(QUser.user, QUserRole.userRole)
        .where(QUserRole.userRole.userId.eq(QUser.user.id).and(QUserRole.userRole.userId.in(userIds)))
        .transform(groupBy(QUserRole.userRole.userId).as(list(QUserRole.userRole.role)));
    Map<Integer, List<RoleType>> userIdToRoleType = userIdToRoleName.entrySet().stream().collect(
        Collectors.toMap(
            entry -> entry.getKey(),
            entry -> entry.getValue().stream().map(roleName -> RoleType.valueOf(roleName)).collect(Collectors.toList())));
    return userIdToRoleType;
  }

  private Map<Integer, List<ApplicationType>> getApplicationTypes(List<Integer> userIds) {
    Map<Integer, List<String>> userIdToTypeName = queryFactory
        .from(QUser.user, QUserApplicationType.userApplicationType)
        .where(QUserApplicationType.userApplicationType.userId.eq(QUser.user.id).and(QUserApplicationType.userApplicationType.userId.in(userIds)))
        .transform(groupBy(QUserApplicationType.userApplicationType.userId).as(list(QUserApplicationType.userApplicationType.applicationType)));
    Map<Integer, List<ApplicationType>> userIdToApplicationType = userIdToTypeName.entrySet().stream().collect(
        Collectors.toMap(
            entry -> entry.getKey(),
            entry -> entry.getValue().stream().map(typeName -> ApplicationType.valueOf(typeName))
                .collect(Collectors.toList())));
    return userIdToApplicationType;
  }

  private Map<Integer, List<Integer>> getCityDistricts(List<Integer> userIds) {
    Map<Integer, List<Integer>> userIdToCityDistrict = queryFactory
        .from(QUser.user, userCityDistrict)
        .where(userCityDistrict.userId.eq(QUser.user.id).and(userCityDistrict.userId.in(userIds)))
        .transform(groupBy(userCityDistrict.userId).as(list(userCityDistrict.cityDistrictId)));
    return userIdToCityDistrict;
  }

  private void mapUsersRolesTypes(
      List<User> users,
      Map<Integer, List<RoleType>> userIdToRoleType,
      Map<Integer, List<ApplicationType>> userIdToApplicationType,
      Map<Integer, List<Integer>> userIdToCityDistrict) {
    users.forEach(user ->
      {
        List<RoleType> roles = userIdToRoleType.get(user.getId()) == null ? Collections.emptyList() : userIdToRoleType.get(user.getId());
        List<ApplicationType> types =
            userIdToApplicationType.get(user.getId()) == null ? Collections.emptyList() : userIdToApplicationType.get(user.getId());
        List<Integer> cityDistricts = userIdToCityDistrict.get(user.getId()) == null ?
            Collections.emptyList() : userIdToCityDistrict.get(user.getId());
        user.setAssignedRoles(roles);
        user.setAllowedApplicationTypes(types);
        user.setCityDistrictIds(cityDistricts);
      });
  }
}
