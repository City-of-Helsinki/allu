package fi.hel.allu.model.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.QApplication;
import fi.hel.allu.QAttributeMeta;
import fi.hel.allu.QStructureMeta;
import fi.hel.allu.QUser;
import fi.hel.allu.common.domain.SupervisionTaskSearchCriteria;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.common.PathUtil;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.domain.SupervisionTaskLocation;
import fi.hel.allu.model.domain.SupervisionWorkItem;
import fi.hel.allu.model.querydsl.ExcludingMapper;
import org.geolatte.geom.Geometry;
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

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QLocation.location;
import static fi.hel.allu.QProject.project;
import static fi.hel.allu.QSupervisionTask.supervisionTask;
import static fi.hel.allu.QSupervisionTaskLocation.supervisionTaskLocation;
import static fi.hel.allu.QSupervisionTaskLocationGeometry.supervisionTaskLocationGeometry;
import static fi.hel.allu.QSupervisionTaskSupervisedLocation.supervisionTaskSupervisedLocation;
import static fi.hel.allu.QSupervisionTaskWithAddress.supervisionTaskWithAddress;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

@Repository
public class SupervisionTaskDao {

  /**
   * Fields that won't be updated in regular updates
   */
  public static final List<Path<?>> UPDATE_READ_ONLY_FIELDS = Arrays.asList(
    supervisionTask.id, supervisionTask.creationTime, supervisionTask.type);

  final static QStructureMeta typeStructure = new QStructureMeta("typeStructure");
  final static QAttributeMeta typeAttribute = new QAttributeMeta("typeAttribute");
  final static QStructureMeta applTypeStructure = new QStructureMeta("applTypeStructure");
  final static QAttributeMeta applTypeAttribute = new QAttributeMeta("applTypeAttribute");
  final static QStructureMeta applStatusStructure = new QStructureMeta("applStatusStructure");
  final static QAttributeMeta applStatusAttribute = new QAttributeMeta("applStatusAttribute");
  final static QUser creator = new QUser("creator");
  final static QUser owner = new QUser("owner");

  final static Map<String, Path<?>> COLUMNS = orderByColumns();

  final QBean<SupervisionTask> supervisionTaskBean = bean(SupervisionTask.class, supervisionTask.all());
  final QBean<SupervisionWorkItem> supervisionWorkItemBean = bean(SupervisionWorkItem.class, supervisionWorkItemFields());
  final QBean<SupervisionTaskLocation> supervisionTaskLocationBean = bean(SupervisionTaskLocation.class, supervisionTaskLocation.all());


  private final SQLQueryFactory queryFactory;

  public SupervisionTaskDao(SQLQueryFactory queryFactory) {
    this.queryFactory = queryFactory;
  }

  @Transactional(readOnly = true)
  public Optional<SupervisionTask> findById(int id) {
    SupervisionTask st = queryFactory
      .select(supervisionTaskBean).from(supervisionTask).where(supervisionTask.id.eq(id)).fetchOne();
    return Optional.ofNullable(setSupervisedLocations(st));
  }

  @Transactional(readOnly = true)
  public List<SupervisionTask> findByApplicationId(int applicationId) {
    return querySupervisionTasks(supervisionTask.applicationId.eq(applicationId));
  }

  @Transactional(readOnly = true)
  public List<SupervisionTask> findByLocationId(int locationId) {
    return querySupervisionTasks(supervisionTask.locationId.eq(locationId));
  }

  @Transactional(readOnly = true)
  public List<SupervisionTask> findByApplicationIdAndType(int applicationId, SupervisionTaskType type) {
    return querySupervisionTasks(supervisionTask.applicationId.eq(applicationId), supervisionTask.type.eq(type));
  }

  @Transactional(readOnly = true)
  public List<SupervisionTask> findByApplicationIdAndTypeAndLocation(int applicationId, SupervisionTaskType type, int locationId) {
    return querySupervisionTasks(supervisionTask.applicationId.eq(applicationId),
      supervisionTask.type.eq(type),
      supervisionTask.locationId.eq(locationId));
  }

  private List<SupervisionTask> querySupervisionTasks(Predicate... predicates) {
    List<SupervisionTask> tasks = queryFactory.select(supervisionTaskBean).from(supervisionTask).where(predicates).fetch();
    tasks.forEach(t -> setSupervisedLocations(t));
    return tasks;
  }

  private SupervisionTask setSupervisedLocations(SupervisionTask task) {
    if (task != null) {
      task.setSupervisedLocations(getSupervisedLocations(task.getId()));
    }
    return task;
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
  public int updateOwner(int owner, List<Integer> tasks) {
    return (int) queryFactory
      .update(supervisionTask)
      .set(supervisionTask.ownerId, owner)
      .where(supervisionTask.id.in(tasks))
      .execute();
  }

  @Transactional
  public int removeOwner(List<Integer> tasks) {
    return (int) queryFactory
      .update(supervisionTask)
      .setNull(supervisionTask.ownerId)
      .where(supervisionTask.id.in(tasks))
      .execute();
  }


  @Transactional(readOnly = true)
  public List<SupervisionTask> findFinalSupervisions(int applicationId) {
    return querySupervisionTasks(supervisionTask.applicationId.eq(applicationId),
      supervisionTask.type.eq(SupervisionTaskType.FINAL_SUPERVISION));
  }

  /**
   * Abstracts over column paths shared between the supervision_task base table
   * and the supervision_task_with_address view, so that condition-building logic
   * can be reused for both the data query (against the view) and the lightweight
   * count query (against the base table).
   */
  private interface TaskPaths {
    EnumPath<SupervisionTaskStatusType> status();
    EnumPath<SupervisionTaskType> type();
    DateTimePath<ZonedDateTime> plannedFinishingTime();
    NumberPath<Integer> ownerId();
    NumberPath<Integer> applicationId();
    NumberPath<Integer> locationId();
  }

  private static final TaskPaths VIEW_PATHS = new TaskPaths() {
    @Override public EnumPath<SupervisionTaskStatusType> status() { return supervisionTaskWithAddress.status; }
    @Override public EnumPath<SupervisionTaskType> type() { return supervisionTaskWithAddress.type; }
    @Override public DateTimePath<ZonedDateTime> plannedFinishingTime() { return supervisionTaskWithAddress.plannedFinishingTime; }
    @Override public NumberPath<Integer> ownerId() { return supervisionTaskWithAddress.ownerId; }
    @Override public NumberPath<Integer> applicationId() { return supervisionTaskWithAddress.applicationId; }
    @Override public NumberPath<Integer> locationId() { return supervisionTaskWithAddress.locationId; }
  };

  private static final TaskPaths BASE_TABLE_PATHS = new TaskPaths() {
    @Override public EnumPath<SupervisionTaskStatusType> status() { return supervisionTask.status; }
    @Override public EnumPath<SupervisionTaskType> type() { return supervisionTask.type; }
    @Override public DateTimePath<ZonedDateTime> plannedFinishingTime() { return supervisionTask.plannedFinishingTime; }
    @Override public NumberPath<Integer> ownerId() { return supervisionTask.ownerId; }
    @Override public NumberPath<Integer> applicationId() { return supervisionTask.applicationId; }
    @Override public NumberPath<Integer> locationId() { return supervisionTask.locationId; }
  };

  /**
   * Sort keys that require the structure_meta/attribute_meta joins for enum
   * column ordering (Finnish UI names instead of raw enum values).
   */
  private static final Set<String> ENUM_SORT_KEYS = Set.of(
    "type", "application.type", "application.status");

  /**
   * Returns true if the page request sorts by any enum column that requires
   * the structure_meta/attribute_meta joins.
   */
  private static boolean needsEnumSortJoins(Pageable pageRequest) {
    if (pageRequest == null) {
      return false;
    }
    for (Sort.Order order : pageRequest.getSort()) {
      if (ENUM_SORT_KEYS.contains(order.getProperty())) {
        return true;
      }
    }
    return false;
  }

  @Transactional
  public Page<SupervisionWorkItem> search(SupervisionTaskSearchCriteria searchCriteria, Pageable pageRequest) {
    BooleanExpression dataConditions = conditions(searchCriteria, VIEW_PATHS)
      .reduce((left, right) -> left.and(right))
      .orElse(Expressions.TRUE);

    SQLQuery<SupervisionWorkItem> q = queryFactory.select(supervisionWorkItemBean)
      .from(supervisionTaskWithAddress)
      .leftJoin(application).on(supervisionTaskWithAddress.applicationId.eq(application.id))
      .leftJoin(project).on(application.projectId.eq(project.id))
      .leftJoin(creator).on(supervisionTaskWithAddress.creatorId.eq(creator.id))
      .leftJoin(owner).on(supervisionTaskWithAddress.ownerId.eq(owner.id));

    if (needsEnumSortJoins(pageRequest)) {
      q = q
        .leftJoin(typeStructure).on(typeStructure.typeName.eq("SupervisionTaskType"))
        .leftJoin(typeAttribute).on(typeAttribute.structureMetaId.eq(typeStructure.id)
          .and(typeAttribute.name.eq(supervisionTaskWithAddress.type.stringValue())))
        .leftJoin(applTypeStructure).on(applTypeStructure.typeName.eq("ApplicationType"))
        .leftJoin(applTypeAttribute).on(applTypeAttribute.structureMetaId.eq(applTypeStructure.id)
          .and(applTypeAttribute.name.eq(application.type.stringValue())))
        .leftJoin(applStatusStructure).on(applStatusStructure.typeName.eq("StatusType"))
        .leftJoin(applStatusAttribute).on(applStatusAttribute.structureMetaId.eq(applStatusStructure.id)
          .and(applStatusAttribute.name.eq(application.status.stringValue())));
    }

    q = q.where(dataConditions);

    q = handlePageRequest(q, pageRequest);

    List<SupervisionWorkItem> results = q.fetch();
    long total = countSupervisionTasks(searchCriteria);
    return new PageImpl<>(results, pageRequest, total);
  }

  /**
   * Lightweight count query against the base supervision_task table.
   * Avoids the view, address resolution, project/user/metadata joins.
   * Only joins application when the search criteria reference application fields.
   */
  private long countSupervisionTasks(SupervisionTaskSearchCriteria searchCriteria) {
    BooleanExpression countConditions = conditions(searchCriteria, BASE_TABLE_PATHS)
      .reduce((left, right) -> left.and(right))
      .orElse(Expressions.TRUE);

    SQLQuery<Long> countQuery = queryFactory.select(supervisionTask.id.count())
      .from(supervisionTask);

    if (needsApplicationJoin(searchCriteria)) {
      countQuery = countQuery.leftJoin(application).on(supervisionTask.applicationId.eq(application.id));
    }

    return countQuery.where(countConditions).fetchOne();
  }

  /**
   * Returns true if the search criteria contain any conditions that reference
   * the application table (applicationId text prefix, applicationTypes, applicationStatus).
   */
  private static boolean needsApplicationJoin(SupervisionTaskSearchCriteria searchCriteria) {
    return searchCriteria.getApplicationId() != null
      || (searchCriteria.getApplicationTypes() != null && !searchCriteria.getApplicationTypes().isEmpty())
      || (searchCriteria.getApplicationStatus() != null && !searchCriteria.getApplicationStatus().isEmpty());
  }

  /*
   * Add sort and paging to query from the given pageRequest.
   */
  private SQLQuery<SupervisionWorkItem> handlePageRequest(SQLQuery<SupervisionWorkItem> query, Pageable pageRequest) {
    if (pageRequest == null) {
      return query;
    }
    query = query.orderBy(toOrder(pageRequest.getSort()));
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

  private Stream<BooleanExpression> conditions(SupervisionTaskSearchCriteria searchCriteria, TaskPaths paths) {
    List<SupervisionTaskStatusType> statuses = searchCriteria.getStatuses() != null && !searchCriteria.getStatuses().isEmpty() ?
      searchCriteria.getStatuses() : Collections.singletonList(SupervisionTaskStatusType.OPEN);


    return Stream.of(
        Optional.of(paths.status().in(statuses)),
        values(searchCriteria.getTaskTypes()).map(paths.type()::in),
        Optional.ofNullable(searchCriteria.getAfter()).map(paths.plannedFinishingTime()::goe),
        Optional.ofNullable(searchCriteria.getBefore()).map(paths.plannedFinishingTime()::lt),
        Optional.ofNullable(searchCriteria.getApplicationId()).map(String::toUpperCase).map(application.applicationId::startsWith),
        values(searchCriteria.getOwners()).map(paths.ownerId()::in),
        values(searchCriteria.getApplicationTypes()).map(application.type::in),
        values(searchCriteria.getApplicationStatus()).map(application.status::in),
        values(searchCriteria.getCityDistrictIds()).map(ids -> cityDistrictsIn(ids, paths)),
        // Include also empty application ID list in search conditions
        Optional.ofNullable(searchCriteria.getApplicationIds()).map(paths.applicationId()::in)
      ).filter(opt -> opt.isPresent())
      .map(opt -> opt.get());
  }

  private <T> Optional<List<T>> values(List<T> valueList) {
    return Optional.ofNullable(valueList)
      .filter(values -> !values.isEmpty());
  }

  private BooleanExpression cityDistrictsIn(List<Integer> ids, TaskPaths paths) {
    // Use city district override when it is defined
    BooleanExpression override = location.cityDistrictIdOverride.isNotNull().and(location.cityDistrictIdOverride.in(ids));
    BooleanExpression calculated = location.cityDistrictIdOverride.isNull().and(location.cityDistrictId.in(ids));
    BooleanExpression effective = override.or(calculated);

    // Use tasks location when available otherwise use supervision tasks application's locations
    return SQLExpressions.selectOne()
      .from(location)
      .where(
        paths.locationId().isNotNull()
          .and(paths.locationId().eq(location.id)
            .and(effective))
          .or(paths.locationId().isNull()
            .and(paths.applicationId().eq(location.applicationId)
              .and(effective))))
      .exists();
  }

  private static Map<String, Path<?>> orderByColumns() {
    Map<String, Path<?>> cols = supervisionTaskWithAddress.getColumns().stream()
      .collect(Collectors.toMap(c -> c.getMetadata().getName(), c -> c));

    // Override sorting for enum-column supervisionTask.type:
    cols.put(supervisionTask.type.getMetadata().getName(), typeAttribute.uiName);
    cols.put(PathUtil.pathNameWithParent(application.type), applTypeAttribute.uiName);
    cols.put(PathUtil.pathNameWithParent(application.status), applStatusAttribute.uiName);
    cols.put(PathUtil.pathNameWithParent(application.applicationId), application.applicationId);
    cols.put(PathUtil.pathNameWithParent(project.name), project.name);
    cols.put(PathUtil.pathNameWithParent(creator.realName), creator.realName);
    cols.put(PathUtil.pathNameWithParent(owner.realName), owner.realName);
    return cols;
  }

  private static Map<String, Path<?>> supervisionWorkItemFields() {
    Map<String, Path<?>> map = new HashMap<>();
    map.put("id", supervisionTaskWithAddress.id);
    map.put("type", supervisionTaskWithAddress.type);
    map.put("applicationId", application.id);
    map.put("applicationIdText", application.applicationId);
    map.put("applicationStatus", application.status);
    map.put("creatorId", supervisionTaskWithAddress.creatorId);
    map.put("plannedFinishingTime", supervisionTaskWithAddress.plannedFinishingTime);
    map.put("address", supervisionTaskWithAddress.address);
    map.put("projectName", project.name);
    map.put("ownerId", supervisionTaskWithAddress.ownerId);
    return map;
  }

  public void cancelOpenTasksOfApplication(Integer applicationId) {
    queryFactory
      .update(supervisionTask)
      .set(supervisionTask.status, SupervisionTaskStatusType.CANCELLED)
      .where(supervisionTask.applicationId.eq(applicationId), supervisionTask.status.eq(SupervisionTaskStatusType.OPEN))
      .execute();
  }

  public void copyApprovedSupervisionTasks(int fromApplicationId, Integer toApplicationId) {
    List<SupervisionTask> tasks = queryFactory.select(supervisionTaskBean).from(supervisionTask)
      .where(supervisionTask.applicationId.eq(fromApplicationId), supervisionTask.status.eq(SupervisionTaskStatusType.APPROVED))
      .fetch();
    tasks.forEach(t -> insertCopyForApplication(t, toApplicationId));
  }

  private void insertCopyForApplication(SupervisionTask task, Integer toApplicationId) {
    Integer originalTaskId = task.getId();
    task.setId(null);
    task.setApplicationId(toApplicationId);
    Integer taskId = queryFactory.insert(supervisionTask).populate(task).executeWithKey(supervisionTask.id);
    // Link approved locations to copied task
    queryFactory.select(supervisionTaskSupervisedLocation.supervisionTaskLocationId)
      .from(supervisionTaskSupervisedLocation)
      .where(supervisionTaskSupervisedLocation.supervisionTaskId.eq(originalTaskId))
      .fetch()
      .forEach(locationId -> addSupervisedLocation(taskId, locationId));
  }


  @Transactional(readOnly = true)
  public Map<Integer, List<SupervisionTask>> getSupervisionTaskHistoryForExternalOwner(Integer externalOwnerId,
                                                                                       ZonedDateTime eventsAfter, List<Integer> includedExternalApplicationIds) {
    QApplication application = QApplication.application;
    BooleanBuilder builder = new BooleanBuilder();
    builder.and(application.externalOwnerId.eq(externalOwnerId));
    builder.and(supervisionTask.status.in(SupervisionTaskStatusType.APPROVED, SupervisionTaskStatusType.REJECTED));
    if (eventsAfter != null) {
      builder.and(supervisionTask.actualFinishingTime.after(eventsAfter));
    }
    if (!includedExternalApplicationIds.isEmpty()) {
      builder.and(application.externalApplicationId.in(includedExternalApplicationIds));
    }
    Map<Integer, List<SupervisionTask>> result = queryFactory.select(supervisionTask.all())
      .from(supervisionTask)
      .join(application).on(application.id.eq(supervisionTask.applicationId))
      .where(builder)
      .transform(groupBy(application.externalApplicationId).as(list(supervisionTaskBean)));
    return result;

  }

  @Transactional(readOnly = true)
  public String[] findAddressById(int id) {
    return queryFactory.select(supervisionTaskWithAddress.address)
      .from(supervisionTaskWithAddress)
      .where(supervisionTaskWithAddress.id.eq(id))
      .fetchFirst();
  }

  @Transactional(readOnly = true)
  public List<SupervisionTaskLocation> getSupervisedLocations(Integer supervisionTaskId) {
    List<SupervisionTaskLocation> locations = queryFactory.select(supervisionTaskLocationBean)
      .from(supervisionTaskLocation)
      .innerJoin(supervisionTaskSupervisedLocation)
      .on(supervisionTaskLocation.id.eq(supervisionTaskSupervisedLocation.supervisionTaskLocationId))
      .where(supervisionTaskSupervisedLocation.supervisionTaskId.eq(supervisionTaskId))
      .fetch();
    locations.forEach(l -> setLocationGeometry(l));
    return locations;
  }

  private void setLocationGeometry(SupervisionTaskLocation location) {
    List<Geometry> geometries = queryFactory.select(supervisionTaskLocationGeometry.geometry)
      .from(supervisionTaskLocationGeometry)
      .where(supervisionTaskLocationGeometry.supervisionLocationId.eq(location.getId()))
      .fetch();
    location.setGeometry(GeometryUtil.toGeometryCollection(geometries));
  }

  @Transactional
  public void saveSupervisedLocation(Integer supervisionTaskId, SupervisionTaskLocation location) {
    Integer id = queryFactory.insert(supervisionTaskLocation).populate(location).executeWithKey(supervisionTaskLocation.id);
    addSupervisedLocation(supervisionTaskId, id);
    List<Geometry> geometries = new ArrayList<>();
    GeometryUtil.flatten(location.getGeometry(), geometries);
    geometries.forEach(geometry -> queryFactory.insert(supervisionTaskLocationGeometry)
      .columns(supervisionTaskLocationGeometry.supervisionLocationId, supervisionTaskLocationGeometry.geometry)
      .values(id, geometry)
      .execute());
  }

  private void addSupervisedLocation(Integer supervisionTaskId, Integer id) {
    queryFactory.insert(supervisionTaskSupervisedLocation)
      .columns(supervisionTaskSupervisedLocation.supervisionTaskId, supervisionTaskSupervisedLocation.supervisionTaskLocationId)
      .values(supervisionTaskId, id)
      .execute();
  }

  @Transactional
  public void deleteSupervisedLocations(Integer supervisionTaskId) {
    queryFactory.delete(supervisionTaskSupervisedLocation)
      .where(supervisionTaskSupervisedLocation.supervisionTaskId.eq(supervisionTaskId))
      .execute();
  }

  @Transactional
  public void anonymizeSupervisionTasks(List<Integer> applicationIds) {
    queryFactory
      .update(supervisionTask)
      .set(supervisionTask.creatorId, (Integer)null)
      .set(supervisionTask.ownerId, (Integer)null)
      .set(supervisionTask.description, (String)null)
      .set(supervisionTask.result, (String)null)
      .where(supervisionTask.applicationId.in(applicationIds))
      .execute();
  }
}
