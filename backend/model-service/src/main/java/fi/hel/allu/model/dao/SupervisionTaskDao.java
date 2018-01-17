package fi.hel.allu.model.dao;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.QAttributeMeta;
import fi.hel.allu.QStructureMeta;
import fi.hel.allu.common.domain.SupervisionTaskSearchCriteria;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.common.PathUtil;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.querydsl.ExcludingMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QLocation.location;
import static fi.hel.allu.QPostalAddress.postalAddress;
import static fi.hel.allu.QProject.project;
import static fi.hel.allu.QSupervisionTask.supervisionTask;
import static fi.hel.allu.QUser.user;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

@Repository
public class SupervisionTaskDao {

  /** Fields that won't be updated in regular updates */
  public static final List<Path<?>> UPDATE_READ_ONLY_FIELDS = Arrays.asList(supervisionTask.id, supervisionTask.creationTime);

  final static QStructureMeta typeStructure = new QStructureMeta("typeStructure");
  final static QAttributeMeta typeAttribute = new QAttributeMeta("typeAttribute");
  final static QStructureMeta applTypeStructure = new QStructureMeta("applTypeStructure");
  final static QAttributeMeta applTypeAttribute = new QAttributeMeta("applTypeAttribute");
  final static QStructureMeta applStatusStructure = new QStructureMeta("applStatusStructure");
  final static QAttributeMeta applStatusAttribute = new QAttributeMeta("applStatusAttribute");

  final static Map<String, Path<?>> COLUMNS = orderByColumns();

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<SupervisionTask> supervisionTaskBean = bean(SupervisionTask.class, supervisionTask.all());

  @Transactional(readOnly = true)
  public Optional<SupervisionTask> findById(int id) {
    SupervisionTask st = queryFactory
        .select(supervisionTaskBean).from(supervisionTask).where(supervisionTask.id.eq(id)).fetchOne();
    return Optional.ofNullable(st);
  }

  @Transactional(readOnly = true)
  public List<SupervisionTask> findByApplicationId(int applicationId) {
    return queryFactory.select(supervisionTaskBean).from(supervisionTask).where(supervisionTask.applicationId.eq(applicationId)).fetch();
  }

  @Transactional
  public SupervisionTask insert(SupervisionTask st) {
    st.setCreationTime(ZonedDateTime.now());
    st.setStatus(SupervisionTaskStatusType.OPEN);
    Integer id = queryFactory.insert(supervisionTask).populate(st).executeWithKey(supervisionTask.id);
    return findById(id).get();
  }

  @Transactional
  public SupervisionTask update(SupervisionTask st) {
    long changed = queryFactory.update(supervisionTask)
        .populate(st, new ExcludingMapper(WITH_NULL_BINDINGS, UPDATE_READ_ONLY_FIELDS))
        .where(supervisionTask.id.eq(st.getId())).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update supervision task", st.getId());
    }
    return findById(st.getId()).get();
  }

  @Transactional
  public void delete(int id) {
    SupervisionTask st = findById(id).orElseThrow(() -> new NoSuchEntityException("Attempted to delete non-existent supervision task", id));
    if (st.getStatus().equals(SupervisionTaskStatusType.OPEN)) {
      queryFactory.delete(supervisionTask).where(supervisionTask.id.eq(id)).execute();
    } else {
      throw new IllegalStateException("Attempted to delete processed supervision task with current state " + st.getStatus());
    }
  }

  @Transactional
  public int updateHandler(int handler, List<Integer> tasks) {
    return (int) queryFactory
        .update(supervisionTask)
        .set(supervisionTask.handlerId, handler)
        .where(supervisionTask.id.in(tasks))
        .execute();
  }

  @Transactional
  public int removeHandler(List<Integer> tasks) {
    return (int) queryFactory
        .update(supervisionTask)
        .setNull(supervisionTask.handlerId)
        .where(supervisionTask.id.in(tasks))
        .execute();
  }

  @Transactional
  public List<SupervisionTask> search(SupervisionTaskSearchCriteria searchCriteria) {
    return search(searchCriteria, null).getContent();
  }

  @Transactional
  public Page<SupervisionTask> search(SupervisionTaskSearchCriteria searchCriteria, Pageable pageRequest) {
    BooleanExpression conditions = conditions(searchCriteria)
        .reduce((left, right) -> left.and(right))
        .orElse(Expressions.TRUE);

    SQLQuery<SupervisionTask> q = queryFactory.select(supervisionTaskBean)
        .from(supervisionTask)
        .leftJoin(application).on(supervisionTask.applicationId.eq(application.id))
        .leftJoin(location).on(location.applicationId.eq(application.id))
        .leftJoin(postalAddress).on(location.postalAddressId.eq(postalAddress.id))
        .leftJoin(project).on(application.projectId.eq(project.id))
        .leftJoin(user).on(supervisionTask.creatorId.eq(user.id))
        .leftJoin(typeStructure).on(typeStructure.typeName.eq("SupervisionTaskType"))
        .leftJoin(typeAttribute).on(typeAttribute.structureMetaId.eq(typeStructure.id)
            .and(typeAttribute.name.eq(supervisionTask.type.stringValue())))
        .leftJoin(applTypeStructure).on(applTypeStructure.typeName.eq("ApplicationType"))
        .leftJoin(applTypeAttribute).on(applTypeAttribute.structureMetaId.eq(applTypeStructure.id)
            .and(applTypeAttribute.name.eq(application.type.stringValue())))
        .leftJoin(applStatusStructure).on(applStatusStructure.typeName.eq("StatusType"))
        .leftJoin(applStatusAttribute).on(applStatusAttribute.structureMetaId.eq(applStatusStructure.id)
            .and(applStatusAttribute.name.eq(application.status.stringValue())))
        .where(conditions);

    q = handlePageRequest(q, pageRequest);

    QueryResults<SupervisionTask> results = q.fetchResults();
    return new PageImpl<>(results.getResults(), pageRequest, results.getTotal());
  }

  /*
   * Add sort and paging to query from the given pageRequest.
   */
  private SQLQuery<SupervisionTask> handlePageRequest(SQLQuery<SupervisionTask> query, Pageable pageRequest) {
    if (pageRequest == null) {
      return query;
    }
    if (pageRequest.getSort() != null) {
      query = query.orderBy(toOrder(pageRequest.getSort()));
    }
    if (pageRequest.getOffset() != 0) {
      query = query.offset(pageRequest.getOffset());
    }
    if (pageRequest.getPageSize() != 0) {
      query = query.limit(pageRequest.getPageSize());
    }
    return query;
  }

  /**
   * Convert given sort criteria to OrderSpecifiers
   *
   * @param sort
   * @return
   */
  public static OrderSpecifier<?>[] toOrder(Sort sort) {
    List<OrderSpecifier<?>> order = new ArrayList<>();
    sort.forEach(o -> {
      ComparableExpressionBase<?> path = (ComparableExpressionBase<?>) Optional.ofNullable(COLUMNS.get(o.getProperty()))
          .orElseThrow(() -> new NoSuchEntityException("Bad sort key: " + o.getProperty()));
      order.add(o.isDescending() ? path.desc() : path.asc());
    });
    return order.toArray(new OrderSpecifier<?>[order.size()]);
  }

  private Stream<BooleanExpression> conditions(SupervisionTaskSearchCriteria searchCriteria) {
    return Stream.of(
        Optional.of(supervisionTask.status.eq(SupervisionTaskStatusType.OPEN)),
        values(searchCriteria.getTaskTypes()).map(supervisionTask.type::in),
        Optional.ofNullable(searchCriteria.getHandlerId()).map(supervisionTask.handlerId::eq),
        Optional.ofNullable(searchCriteria.getAfter()).map(supervisionTask.plannedFinishingTime::goe),
        Optional.ofNullable(searchCriteria.getBefore()).map(supervisionTask.plannedFinishingTime::loe),
        Optional.ofNullable(searchCriteria.getApplicationId()).map(application.applicationId::startsWith),
        values(searchCriteria.getApplicationTypes()).map(application.type::in),
        values(searchCriteria.getApplicationStatus()).map(application.status::in)
    ).filter(opt -> opt.isPresent())
     .map(opt -> opt.get());
  }


  private <T> Optional<List<T>> values(List<T> valueList) {
    return Optional.ofNullable(valueList)
        .filter(values -> !values.isEmpty());
  }

  private static Map<String, Path<?>> orderByColumns() {
    Map<String, Path<?>> cols = supervisionTask.getColumns().stream()
        .collect(Collectors.toMap(c -> c.getMetadata().getName(), c -> c));

    // Override sorting for enum-column supervisionTask.type:
    cols.put(supervisionTask.type.getMetadata().getName(), typeAttribute.uiName);
    cols.put(PathUtil.pathNameWithParent(application.type), applTypeAttribute.uiName);
    cols.put(PathUtil.pathNameWithParent(application.status), applStatusAttribute.uiName);
    cols.put(PathUtil.pathNameWithParent(application.applicationId), application.applicationId);
    cols.put(PathUtil.pathNameWithParent(project.name), project.name);
    cols.put(PathUtil.pathNameWithParent(user.realName), user.realName);
    cols.put(PathUtil.pathName(postalAddress.streetAddress), postalAddress.streetAddress);
    return cols;
  }

  @Transactional
  public void copySupervisionTasks(Integer copyFromApplicationId, Integer copyToApplicationId) {
    List<SupervisionTask> tasks = findByApplicationId(copyFromApplicationId);
    tasks.forEach(t -> copyForApplication(t, copyToApplicationId));
  }

  private void copyForApplication(SupervisionTask task, Integer applicationId) {
    task.setId(null);
    task.setApplicationId(applicationId);
    insert(task);
  }
}
