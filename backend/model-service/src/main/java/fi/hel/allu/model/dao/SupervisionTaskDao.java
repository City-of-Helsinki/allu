package fi.hel.allu.model.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.QApplication;
import fi.hel.allu.QAttributeMeta;
import fi.hel.allu.QStructureMeta;
import fi.hel.allu.QUser;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.querydsl.ExcludingMapper;
import fi.hel.allu.model.util.SupervisionLocationMap;
import org.geolatte.geom.Geometry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
  protected static final List<Path<?>> UPDATE_READ_ONLY_FIELDS = Arrays.asList(
    supervisionTask.id, supervisionTask.creationTime, supervisionTask.type);

  private static final QStructureMeta typeStructure = new QStructureMeta("typeStructure");
  private static final QAttributeMeta typeAttribute = new QAttributeMeta("typeAttribute");
  private static final QStructureMeta applTypeStructure = new QStructureMeta("applTypeStructure");
  private static final QAttributeMeta applTypeAttribute = new QAttributeMeta("applTypeAttribute");
  private static final QStructureMeta applStatusStructure = new QStructureMeta("applStatusStructure");
  private static final QAttributeMeta applStatusAttribute = new QAttributeMeta("applStatusAttribute");
  private static final QUser creator = new QUser("creator");
  private static final QUser owner = new QUser("owner");

  final QBean<SupervisionTask> supervisionTaskBean = bean(SupervisionTask.class, supervisionTask.all());
  final QBean<SupervisionWorkItem> supervisionWorkItemBean = bean(SupervisionWorkItem.class, supervisionWorkItemFields());
  final QBean<SupervisionTaskLocation> supervisionTaskLocationBean = bean(SupervisionTaskLocation.class, supervisionTaskLocation.all());
  private static final QBean<User> ownerBean = bean(User.class, owner.all());
  private static final QBean<User> creatorBean = bean(User.class, creator.all());
  final QBean<SupervisionTaskLocationGeometry> supervisionTaskLocationGeometryBean = bean(SupervisionTaskLocationGeometry.class, supervisionTaskLocationGeometry.all());

  private final LocationDao locationDao;

  private final SQLQueryFactory queryFactory;

  public SupervisionTaskDao(SQLQueryFactory queryFactory,
                            LocationDao locationDao) {
    this.queryFactory = queryFactory;
    this.locationDao = locationDao;
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
    List<Tuple> tasks = queryFactory.select(supervisionTaskBean, supervisionTaskLocationBean, supervisionTaskLocationGeometryBean).from(supervisionTask)
            .leftJoin(supervisionTaskSupervisedLocation).on(supervisionTask.id.eq(supervisionTaskSupervisedLocation.supervisionTaskId))
            .leftJoin(supervisionTaskLocation).on(supervisionTaskLocation.id.eq(supervisionTaskSupervisedLocation.supervisionTaskLocationId))
            .leftJoin(supervisionTaskLocationGeometry).on(supervisionTaskLocation.id.eq(supervisionTaskLocationGeometry.supervisionLocationId))
            .where(predicates).fetch();

    return populateSupervisionTasks(tasks);
  }

  private List<SupervisionTask> populateSupervisionTasks(List<Tuple> tuples) {
    Map<Integer, Map<Integer, SupervisionLocationMap>> resultMap = new HashMap<>();
    List<SupervisionTask> result = new ArrayList<>();
    for (Tuple tuple : tuples) {
      SupervisionTask supervisionTask = tuple.get(0, SupervisionTask.class);
      if (supervisionTask != null) {
        resultMap.putIfAbsent(supervisionTask.getId(), initSupervisionTask(result, supervisionTask));
        Map<Integer, SupervisionLocationMap> locationMap = resultMap.get(supervisionTask.getId());
        SupervisionTaskLocation supervisionTaskLocation = tuple.get(1, SupervisionTaskLocation.class);
        if (supervisionTaskLocation != null && supervisionTaskLocation.getId() != null) {
          mapTaskLocation(supervisionTaskLocation, locationMap);
          mapGeometries(tuple, locationMap);
        }
      }
    }
    result.forEach(t -> populateTaskLocations(resultMap.get(t.getId()), t));
    return result;
  }

  private Map<Integer, SupervisionLocationMap> initSupervisionTask(List<SupervisionTask> result,
                                                                   SupervisionTask supervisionTask) {
    if(result.stream().noneMatch(task -> Objects.equals(task.getId(), supervisionTask.getId()))) {
      result.add(supervisionTask);
    }
    return new HashMap<>();
  }

  private void mapTaskLocation(SupervisionTaskLocation supervisionTaskLocation,
                               Map<Integer, SupervisionLocationMap> locationMap) {
    if (supervisionTaskLocation != null && supervisionTaskLocation.getId() != null) {
      locationMap.putIfAbsent(supervisionTaskLocation.getId(), new SupervisionLocationMap(supervisionTaskLocation, new ArrayList<>()));
    }
  }

  private void mapGeometries(Tuple tuple, Map<Integer, SupervisionLocationMap> locationMap) {
    SupervisionTaskLocationGeometry locationGeometry = tuple.get(2, SupervisionTaskLocationGeometry.class);
    if (locationGeometry != null && locationMap.containsKey(locationGeometry.getSupervisionLocationId())) {
        locationMap.get(locationGeometry.getSupervisionLocationId()).getGeometries()
                .add(locationGeometry.getGeometry());
    }
  }

  private void populateTaskLocations(Map<Integer, SupervisionLocationMap> locationMap, SupervisionTask task) {
    List<SupervisionTaskLocation> locations = locationMap.values().stream().map(this::populateGeometry)
            .collect(Collectors.toList());
    task.setSupervisedLocations(locations);
  }

  private SupervisionTaskLocation populateGeometry(SupervisionLocationMap locationMap) {
    SupervisionTaskLocation supervisionTaskLocation = locationMap.getSupervisionTaskLocation();
    supervisionTaskLocation.setGeometry(GeometryUtil.toGeometryCollection(locationMap.getGeometries()));
    return supervisionTaskLocation;
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
  public Page<SupervisionWorkItem> findAll(Pageable pageRequest) {
    long offset = (pageRequest == null) ? 0 : pageRequest.getOffset();
    int count = (pageRequest == null) ? 100 : pageRequest.getPageSize();
    SQLQuery<Tuple> query = getSupervisionWorkItemQuery();
    QueryResults<Tuple> results = query.orderBy(application.id.asc()).offset(offset).limit(count)
            .fetchResults();
    List<SupervisionWorkItem> mappedResult = results.getResults().stream().map(this::mapSupervisionWorkItemTuple).collect(Collectors.toList());
    return new PageImpl<>(mappedResult, pageRequest, results.getTotal());
  }

  /**
   * Use SupervisionTask to find correct values but values are populated to class named SupervisionWorkItem
   * @param id SupervisionTask id
   * @return returns SupervisionWorkItem object
   */
  @Transactional(readOnly = true)
  public SupervisionWorkItem findSupervisionWorkItem(Integer id) {
    SQLQuery<Tuple> query = getSupervisionWorkItemQuery();
    Tuple tuple = query.where(supervisionTaskWithAddress.id.eq(id)).fetchOne();
     return mapSupervisionWorkItemTuple(tuple);
  }

  private SQLQuery<Tuple> getSupervisionWorkItemQuery(){
    return queryFactory.select(supervisionWorkItemBean, location.cityDistrictId, location.cityDistrictIdOverride,
                               ownerBean, creatorBean, supervisionTaskWithAddress.type, application.type)
            .from(supervisionTaskWithAddress)
            .leftJoin(application).on(supervisionTaskWithAddress.applicationId.eq(application.id))
            .leftJoin(project).on(application.projectId.eq(project.id))
            .leftJoin(creator).on(supervisionTaskWithAddress.creatorId.eq(creator.id))
            .leftJoin(owner).on(supervisionTaskWithAddress.ownerId.eq(owner.id))
            .leftJoin(typeStructure).on(typeStructure.typeName.eq("SupervisionTaskType"))
            .leftJoin(typeAttribute).on(typeAttribute.structureMetaId.eq(typeStructure.id)
                                                .and(typeAttribute.name.eq(supervisionTaskWithAddress.type.stringValue())))
            .leftJoin(applTypeStructure).on(applTypeStructure.typeName.eq("ApplicationType"))
            .leftJoin(applTypeAttribute).on(applTypeAttribute.structureMetaId.eq(applTypeStructure.id)
                                                    .and(applTypeAttribute.name.eq(application.type.stringValue())))
            .leftJoin(applStatusStructure).on(applStatusStructure.typeName.eq("StatusType"))
            .leftJoin(applStatusAttribute).on(applStatusAttribute.structureMetaId.eq(applStatusStructure.id)
                                                      .and(applStatusAttribute.name.eq(application.status.stringValue())))
            .leftJoin(location).on(supervisionTaskWithAddress.locationId.eq(location.id));
  }

  @Transactional(readOnly = true)
  public List<Integer> getCountOfSupervisionTask(Integer applicationId){
    return queryFactory.select(supervisionTask.id)
            .from(supervisionTask)
            .where(supervisionTask.applicationId.eq(applicationId)).fetch();

  }

  private SupervisionWorkItem mapSupervisionWorkItemTuple(Tuple tuple) {
    SupervisionWorkItem result = tuple.get(0, SupervisionWorkItem.class);
    if (result != null) {
      Integer cityDistrictId = tuple.get(1, Integer.class);
      Integer cityDistrictIdOverride = tuple.get(2, Integer.class);
      result.setCityDistrictId(cityDistrictIdOverride != null ? cityDistrictIdOverride : cityDistrictId);
      result.setOwner(tuple.get(3, User.class));
      result.setCreator(tuple.get(4, User.class));
      result.setType(new SupervisionTypeES(tuple.get(5, SupervisionTaskType.class)));
      result.setApplicationType(tuple.get(6, ApplicationType.class));
      if (result.getCityDistrictId() == null) {
        List<Location> locations = locationDao.findByApplicationId(result.getApplicationId());
        Optional<Location> firstLocation = locations.stream().findFirst();
        firstLocation.ifPresent(e -> updateCityDictrict(e, result));
      }
    }
    return result;
  }

  private void updateCityDictrict(Location location, SupervisionWorkItem result) {
    if (location.getCityDistrictIdOverride() == null) {
      result.setCityDistrictId(location.getCityDistrictId());
    } else {
      result.setCityDistrictId(location.getCityDistrictIdOverride());
    }
  }

  private static Map<String, Path<?>> supervisionWorkItemFields() {
    Map<String, Path<?>> map = new HashMap<>();
    map.put("id", supervisionTaskWithAddress.id);
    map.put("applicationId", application.id);
    map.put("applicationIdText", application.applicationId);
    map.put("applicationStatus", application.status);
    map.put("plannedFinishingTime", supervisionTaskWithAddress.plannedFinishingTime);
    map.put("address", supervisionTaskWithAddress.address);
    map.put("projectName", project.name);
    map.put("cityDistrictId", location.cityDistrictId);
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
    return queryFactory.select(supervisionTask.all())
      .from(supervisionTask)
      .join(application).on(application.id.eq(supervisionTask.applicationId))
      .where(builder)
      .transform(groupBy(application.externalApplicationId).as(list(supervisionTaskBean)));
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
    locations.forEach(this::setLocationGeometry);
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
}