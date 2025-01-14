package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;
import fi.hel.allu.QAnonymizableApplication;
import fi.hel.allu.QApplication;
import fi.hel.allu.QChangeHistory;
import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.ApplicationStatusInfo;
import fi.hel.allu.common.domain.RequiredTasks;
import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.exception.OptimisticLockException;
import fi.hel.allu.common.util.SupervisionDates;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.querydsl.ExcludingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.Projections.bean;
import static com.querydsl.sql.SQLExpressions.select;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QApplicationCustomer.applicationCustomer;
import static fi.hel.allu.QApplicationCustomerContact.applicationCustomerContact;
import static fi.hel.allu.QApplicationKind.applicationKind;
import static fi.hel.allu.QApplicationReminder.applicationReminder;
import static fi.hel.allu.QApplicationTag.applicationTag;
import static fi.hel.allu.QContact.contact;
import static fi.hel.allu.QKindSpecifier.kindSpecifier;
import static fi.hel.allu.QPostalAddress.postalAddress;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

@Repository
public class ApplicationDao {

  /** Fields that won't be updated in regular updates */
  public static final List<Path<?>> UPDATE_READ_ONLY_FIELDS =
      Arrays.asList(application.status, application.decisionMaker, application.decisionTime, application.creationTime,
          application.metadataVersion, application.owner, application.replacedByApplicationId, application.replacesApplicationId,
          application.invoiced, application.clientApplicationData, application.applicationId,
          application.externalOwnerId, application.invoicingChanged, application.targetState,
          application.externalApplicationId, application.invoicingPeriodLength, application.ownerNotification);

  private static final BooleanExpression APPLICATION_NOT_REPLACED = application.status.ne(StatusType.REPLACED);

  private static final List<StatusType> INACTIVE_EXCAVATION_ANNOUNCEMENT_STATUSES =
      List.of(StatusType.ARCHIVED, StatusType.REPLACED, StatusType.CANCELLED, StatusType.FINISHED);

  private final SQLQueryFactory queryFactory;
  private final ApplicationSequenceDao applicationSequenceDao;
  private final StructureMetaDao structureMetaDao;
  private final DistributionEntryDao distributionEntryDao;
  private final CustomerDao customerDao;
  private final AttachmentDao attachmentDao;
  private final LocationDao locationDao;

  final QBean<Application> applicationBean = bean(Application.class, application.all());
  final QBean<ApplicationTag> applicationTagBean = bean(ApplicationTag.class, applicationTag.all());

  final QBean<ApplicationIdentifier> applicationIdentifierBean = bean(ApplicationIdentifier.class,
      application.id, application.applicationId, application.identificationNumber);

  @Autowired
  public ApplicationDao(
      SQLQueryFactory queryFactory,
      ApplicationSequenceDao applicationSequenceDao,
      DistributionEntryDao distributionEntryDao,
      StructureMetaDao structureMetaDao,
      CustomerDao customerDao,
      AttachmentDao attachmentDao,
      LocationDao locationDao) {
    this.queryFactory = queryFactory;
    this.applicationSequenceDao = applicationSequenceDao;
    this.distributionEntryDao = distributionEntryDao;
    this.structureMetaDao = structureMetaDao;
    this.customerDao = customerDao;
    this.attachmentDao = attachmentDao;
    this.locationDao = locationDao;
  }

  /**
   * Returns also replaced application.
   */
  @Transactional
  public Application findById(int id) {
    Application app = queryFactory.select(applicationBean).from(application).where(application.id.eq(id)).fetchOne();
    Optional.ofNullable(app).ifPresent(a -> populateDependencies(Collections.singletonList(a)));
    return app;
  }

  @Transactional(readOnly = true)
  public List<Application> findByIds(List<Integer> ids) {
    List<Application> appl = queryFactory.select(applicationBean).from(application)
        .where(application.id.in(ids).and(APPLICATION_NOT_REPLACED)).fetch();
    return populateDependencies(appl);
  }

  @Transactional(readOnly = true)
  public List<Application> findByProject(int projectId) {
    List<Application> applications =
        queryFactory.select(applicationBean).from(application).where(application.projectId.eq(projectId)).fetch();
    return populateDependencies(applications);
  }

  /**
   * Find all applications, with paging
   *
   * @param pageRequest page request
   * @return a page of applications
   */
  @Transactional(readOnly = true)
  public Page<Application> findAll(Pageable pageRequest) {
    long offset = (pageRequest == null) ? 0 : pageRequest.getOffset();
    int count = (pageRequest == null) ? 100 : pageRequest.getPageSize();
    QueryResults<Application> queryResults = queryFactory.select(applicationBean).from(application)
        .orderBy(application.id.asc()).offset(offset).limit(count).fetchResults();
    return new PageImpl<>(populateDependencies(queryResults.getResults()), pageRequest, queryResults.getTotal());
  }

  /**
   * Finds applications related to the given customer with the role customer has within the application.
   *
   * @param id    Id of the customer whose applications are fetched.
   * @return List of related application ids and roles of the customer within the application. Never <code>null</code>.
   */
  @Transactional(readOnly = true)
  public Map<Integer, List<CustomerRoleType>> findByCustomer(int id) {
    return queryFactory
        .select(applicationCustomer.applicationId, applicationCustomer.customerRoleType)
        .from(applicationCustomer)
        .where(applicationCustomer.customerId.eq(id))
        .transform(groupBy(applicationCustomer.applicationId).as(list(applicationCustomer.customerRoleType)));
  }


  /**
   * Find applications that are about to end
   * <p>
   * This method returns IDs of all the applications that are about to end
   * within the given number of days. It can also filter these applications and
   * only return applications that have some of the given specifiers set.
   *
   * @param endsAfter         Applications must end after this time
   * @param endsBefore        Applications must end before this time
   * @param applicationTypes  Selector for application types. If this list is given
   *                          and not empty, the applications must also be of a type
   *                          that's included in the given specifiers.
   * @param statusTypes       Selector for application status. If this list is given
   *                          and not empty, the results are also filtered by application
   *                          status: applications must be in a status that's included in
   *                          the list.
   * @return List of application identifiers.
   */
  @Transactional(readOnly = true)
  public List<Integer> findByEndTime(ZonedDateTime endsAfter, ZonedDateTime endsBefore,
      List<ApplicationType> applicationTypes, List<StatusType> statusTypes) {
    BooleanExpression whereCondition = application.endTime.between(endsAfter, endsBefore);
    if (applicationTypes != null && ! applicationTypes.isEmpty()) {
      whereCondition = whereCondition.and(application.type.in(applicationTypes));
    }
    if (statusTypes != null && ! statusTypes.isEmpty()) {
      whereCondition = whereCondition.and(application.status.in(statusTypes));
    }
    List<Integer> applications = queryFactory.select(application.id).from(application).where(whereCondition.and(APPLICATION_NOT_REPLACED)).fetch();
    return applications;
  }

  @Transactional(readOnly = true)
  public List<Application> findActiveExcavationAnnouncements() {
    BooleanExpression whereCondition = application.type.eq(ApplicationType.EXCAVATION_ANNOUNCEMENT);
    whereCondition = whereCondition.and(application.status.notIn(INACTIVE_EXCAVATION_ANNOUNCEMENT_STATUSES));
    return queryFactory.select(applicationBean).from(application).where(whereCondition).fetch();
  }

  @Transactional(readOnly = true)
  public List<Integer> findFinishedNotes() {
    ZonedDateTime now = ZonedDateTime.now();
    BooleanExpression recurringCondition = application.recurringEndTime.isNull()
        .or(application.recurringEndTime.before(now));
    return queryFactory.select(application.id).from(application).where(application.type.eq(ApplicationType.NOTE),
        application.status.eq(StatusType.NOTE), application.endTime.before(now), recurringCondition).fetch();
  }

  /**
   * Given a list of application ids, filter out those for which a reminder has
   * already been sent
   *
   * @param  applications   list of application IDs to check
   * @return those application IDs that don't have a reminder sent
   */
  @Transactional(readOnly = true)
  public List<Integer> excludeSentReminders(Collection<Integer> applications) {
    final Set<Integer> sentReminders = new HashSet<>(
        queryFactory.select(applicationReminder.applicationId).from(applicationReminder).join(application)
            .on(applicationReminder.applicationId.eq(application.id)).where(applicationReminder.applicationId
                .in(applications).and(applicationReminder.reminderTrigger.eq(application.endTime)))
            .fetch());
    return applications.stream().filter(i -> !sentReminders.contains(i)).collect(Collectors.toList());
  }

  /**
   * Mark the given applications to have a reminder set. For every given
   * application ID, an entry with application's ID and current end date is
   * added to applicationReminder table. Existing reminder entries for these
   * applications are removed.
   *
   * @param  applications   list of application IDs
   * @return number of inserted applicationReminder entries
   */
  @Transactional
  public long markReminderSent(List<Integer> applications) {
    queryFactory.delete(applicationReminder).where(applicationReminder.applicationId.in(applications)).execute();
    long inserted = queryFactory.insert(applicationReminder)
        .columns(applicationReminder.applicationId, applicationReminder.reminderTrigger)
        .select(SQLExpressions
            .select(application.id, application.endTime).from(application).where(application.id.in(applications)))
        .execute();
    return inserted;
  }

  /**
   * Find all contacts of applications having given contact.
   *
   * @param ids Contact ids to be searched.
   * @return  all contacts of applications having given contacts.
   */
  @Transactional(readOnly = true)
  public List<ApplicationWithContacts> findRelatedApplicationsWithContacts(List<Integer> ids) {
    // find all application_customer rows, which have link to search contacts through application_customer_contact
    SubQueryExpression<Integer> sq =
        select(applicationCustomer.id)
            .from(applicationCustomer)
            .join(applicationCustomerContact).on(applicationCustomer.id.eq(applicationCustomerContact.applicationCustomerId))
            .where(applicationCustomerContact.contactId.in(ids));

    List<Expression<?>> mappedExpressions = new ArrayList<>(Arrays.asList(contact.all()));
    mappedExpressions.add(bean(PostalAddress.class, postalAddress.all()).as("postalAddress"));
    // find all application_customer rows with all their contacts (there's normally more distinct contacts in this set than what were
    // searched originally in the sub query's in condition)
    Map<List<?>, List<Contact>> appIdRoleToContacts = queryFactory
        .select(applicationCustomer.applicationId, applicationCustomer.customerRoleType, bean(Contact.class, contact.all()))
        .from(applicationCustomerContact)
        .join(applicationCustomer).on(applicationCustomerContact.applicationCustomerId.eq(applicationCustomer.id))
        .join(contact).on(applicationCustomerContact.contactId.eq(contact.id))
        .leftJoin(postalAddress).on(contact.postalAddressId.eq(postalAddress.id))
        .where(applicationCustomer.id.in(sq))
        .transform(groupBy(applicationCustomer.applicationId, applicationCustomer.customerRoleType)
            .as(list(bean(Contact.class, mappedExpressions.toArray(new Expression[0])))));

    return appIdRoleToContacts.entrySet().stream()
        .map(entry -> new ApplicationWithContacts((Integer) entry.getKey().get(0), (CustomerRoleType) entry.getKey().get(1), entry.getValue()))
        .collect(Collectors.toList());
  }


  @Transactional
  public Application insert(Application appl) {
    // Use application id from application if present, otherwise generate id.
    appl.setApplicationId(Optional.ofNullable(appl.getApplicationId()).orElseGet(() -> createApplicationId(appl.getType())));
    appl.setCreationTime(ZonedDateTime.now());
    if (appl.getReceivedTime() == null) {
      appl.setReceivedTime(appl.getCreationTime());
    }
    // Do not overwrite given status
    if (appl.getStatus() == null) {
      appl.setStatus(StatusType.PENDING);
    }
    appl.setMetadataVersion(structureMetaDao.getLatestMetadataVersion());
    Integer id = queryFactory.insert(application).populate(appl).executeWithKey(application.id);
    if (id == null) {
      throw new QueryException("Failed to insert record");
    }
    setExternalApplicationId(appl, id);
    replaceCustomersWithContacts(id, appl.getCustomersWithContacts());
    replaceKindsWithSpecifiers(id, appl.getKindsWithSpecifiers());
    Application application = findByIds(Collections.singletonList(id)).get(0);
    replaceApplicationTags(application.getId(), appl.getApplicationTags());
    return populateTags(application);
  }

  protected void setExternalApplicationId(Application appl, Integer id) {
    if (appl.getExternalOwnerId() != null && appl.getExternalApplicationId() == null) {
      queryFactory.update(application).set(application.externalApplicationId, id).where(application.id.eq(id)).execute();
    }
  }

  /**
   * Delete note-type application and its related data
   *
   * @param id application's database ID.
   */
  @Transactional
  public void deleteNote(int id) {
    ApplicationType applicationType = queryFactory.select(application.type).from(application)
        .where(application.id.eq(id)).fetchOne();
    if (ApplicationType.NOTE != applicationType) {
      throw new IllegalArgumentException("application.delete.nonnote");
    }
    deleteApplication(id);
  }

  /**
   * Delete draft application and its related data
   *
   * @param id application's database ID.
   */
  @Transactional
  public void deleteDraft(int id) {
    StatusType status = queryFactory.select(application.status).from(application)
        .where(application.id.eq(id)).fetchOne();
    if (StatusType.PRE_RESERVED != status) {
      throw new IllegalArgumentException("application.delete.nondraft");
    }
    deleteApplication(id);
  }

  private void deleteApplication(int id) {
    queryFactory.delete(application).where(application.id.eq(id)).execute();
    /*
     * After removing the application, some attachments might be unreferenced,
     * so we need a cleanup action for them (attachment doesn't reference
     * application directly, so ON DELETE CASCADE can't be used to automatically
     * clean it up):
     */
    attachmentDao.deleteUnreferencedAttachments();
  }


  /**
   * Updates owner of given applications.
   *
   * @param owner New owner set to the applications.
   * @param applications Applications whose owner is updated.
   * @return Number of updated applications.
   */
  @Transactional
  public int updateOwner(int owner, List<Integer> applications) {
    int updated = (int) queryFactory
        .update(application)
        .set(application.owner, owner)
        .where(application.id.in(applications))
        .execute();
    return updated;
  }

  @Transactional
  public int removeOwner(List<Integer> applications) {
    int updated = (int) queryFactory
        .update(application)
        .set(application.owner, Expressions.nullExpression())
        .where(application.id.in(applications))
        .execute();
    return updated;
  }

  @Transactional
  public int updateHandler(Integer applicationId, Integer handlerId) {
    int updated = (int) queryFactory
        .update(application)
        .set(application.handler, handlerId)
        .where(application.id.eq(applicationId))
        .execute();
    return updated;
  }

  @Transactional
  public Application startDecisionMaking(int applicationId, StatusType status) {
    int updated = (int) queryFactory
        .update(application)
        .set(application.status, status)
        .where(application.id.eq(applicationId))
        .execute();
    if (updated != 1) {
      throw new NoSuchEntityException("application.update.notFound", applicationId);
    }
    return findByIds(Collections.singletonList(applicationId)).get(0);
  }

  /**
   * Update application's decision.
   *
   * @param applicationId   Id of the application to be updated.
   * @param status          New status.
   * @return  Updated application.
   */
  @Transactional
  public Application updateDecision(int applicationId, StatusType status, int decisionMakerId, Integer ownerId) {
    int updated = (int) queryFactory
        .update(application)
        .set(application.owner, ownerId)
        .set(application.decisionMaker, decisionMakerId)
        .set(application.status, status)
        .set(application.decisionTime, ZonedDateTime.now())
        .where(application.id.eq(applicationId))
        .execute();
    if (updated != 1) {
      throw new NoSuchEntityException("application.update.notFound", applicationId);
    }
    return findByIds(Collections.singletonList(applicationId)).get(0);
  }

  /**
   * Update application status.
   *
   * @param applicationId   Application to be updated
   * @param status          New status
   * @return  Updated application.
   */
  @Transactional
  public Application updateStatus(int applicationId, StatusType status) {
    int updated = (int) queryFactory
        .update(application)
        .set(application.status, status)
        .where(application.id.eq(applicationId))
        .execute();
    if (updated != 1) {
      throw new NoSuchEntityException("application.update.notFound", applicationId);
    }
    return findById(applicationId);
  }

  /**
   * Updates project of the given applications.
   *
   * @param   projectId     New project set to the applications. May be <code>null</code>.
   * @param   applications  Applications whose project is updated.
   *
   * @return  Number of updated applications.
   */
  @Transactional
  public int updateProject(Integer projectId, List<Integer> applications) {
    int updated = (int) queryFactory
        .update(application)
        .set(application.projectId, projectId)
        .where(application.id.in(applications))
        .execute();
    return updated;
  }

  /**
   * Update application. All other fields are taken from appl, but {@link #UPDATE_READ_ONLY_FIELDS}
   */
  @Transactional
  public Application update(int id, Application appl) {
    appl.setId(id);
    Integer rowVersion = appl.getVersion();
    appl.setVersion(rowVersion + 1);
    long changed = queryFactory.update(application)
        .populate(appl,
            new ExcludingMapper(WITH_NULL_BINDINGS,
                UPDATE_READ_ONLY_FIELDS))
        .where(application.id.eq(id), application.version.eq(rowVersion)).execute();
    if (changed == 0) {
      throw new OptimisticLockException("application.stale");
    }
    replaceCustomersWithContacts(id, appl.getCustomersWithContacts());
    replaceKindsWithSpecifiers(id, appl.getKindsWithSpecifiers());
    Application application = findByIds(Collections.singletonList(id)).get(0);
    return populateTags(application);
  }

  /**
   * Replaces applications tags with given new tags.
   * Returns updated tags.
   */
  @Transactional
  public List<ApplicationTag> updateTags(int id, List<ApplicationTag> tags) {
    replaceApplicationTags(id, tags);
    return findTagsByApplicationId(id);
  }

  /**
   * Add single tag to application
   *
   * @param applicationId Application's database ID
   * @param tag Tag to add
   * @return existing tag if there is one, otherwise added tag
   */
  @Transactional
  public ApplicationTag addTag(int applicationId, ApplicationTag tag) {
    ApplicationTag existing = queryFactory.select(applicationTagBean)
        .from(applicationTag)
        .where(applicationTag.applicationId.eq(applicationId).and(applicationTag.type.eq(tag.getType())))
        .fetchFirst();

    return Optional.ofNullable(existing)
        .orElseGet(() -> insertSingleTag(applicationId, tag));
  }

  /**
   * Fetches tags for specified application
   */
  @Transactional(readOnly = true)
  public List<ApplicationTag> findTagsByApplicationId(Integer applicationId) {
    return queryFactory.select(applicationTagBean)
        .from(applicationTag)
        .where(applicationTag.applicationId.eq(applicationId))
        .fetch();
  }

  @Transactional(readOnly = true)
  public Map<Integer, List<ApplicationTag>> findTagsForMultipleApplicationId(Integer[] applicationIds) {
    return queryFactory.select(applicationTagBean)
            .from(applicationTag)
            .where(applicationTag.applicationId.in(applicationIds)).
            transform(groupBy(applicationTag.applicationId).as(list(applicationTagBean)));
  }

  /**
   * Removes all tags of given type from application
   * @param applicationId Application's database ID
   * @param tagType type of tag(s) to remove
   */
  @Transactional
  public void removeTagByType(int applicationId, ApplicationTagType tagType) {
    queryFactory.delete(applicationTag)
        .where(applicationTag.applicationId.eq(applicationId).and(applicationTag.type.eq(tagType)))
        .execute();
  }

  /**
   * Removes all tags of from application
   * @param applicationId Application's database ID*
   */
  @Transactional
  public void removeTags(int applicationId) {
    queryFactory.delete(applicationTag)
      .where(applicationTag.applicationId.eq(applicationId))
      .execute();
  }

  String createApplicationId(ApplicationType applicationType) {
    long seqValue = applicationSequenceDao
        .getNextValue(ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.of(applicationType));
    return ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.of(applicationType).name() + seqValue;
  }

  private List<Application> populateDependencies(List<Application> applications) {
    Integer[] ids = applications.stream().map(Application::getId).toArray(Integer[]::new);
    mapDecisionDistributionsToApplications(applications, ids);
    mapTagsToApplications(applications, ids);
    mapCustomerWithContactsToApplication(applications, ids);
    mapKindWithSpecificiersToApplication(applications, ids);
    mapLocationsToApplications(applications, ids);
    return applications;
  }

  private void mapDecisionDistributionsToApplications(List<Application> applicationList, Integer[] ids){
    List<DistributionEntry> unMappedDistributionEntries = distributionEntryDao.findByApplicationIds(ids);
    if (!unMappedDistributionEntries.isEmpty()){
      Map<Integer, List<DistributionEntry>> mappedDistributionEntries =
              unMappedDistributionEntries.stream().collect(Collectors.groupingBy(DistributionEntry::getApplicationId));
      for (Application application : applicationList){
        if(mappedDistributionEntries.containsKey(application.getId())){
          application.setDecisionDistributionList(mappedDistributionEntries.get(application.getId()));
        }else{
          application.setDecisionDistributionList(new ArrayList<>());
        }
      }
    }
  }

  private void mapTagsToApplications(List<Application> applicationList, Integer[] ids){
    Map<Integer, List<ApplicationTag>> mappedApplicationTags = findTagsForMultipleApplicationId(ids);
    for (Application application : applicationList){
      if(mappedApplicationTags.containsKey(application.getId())){
        application.setApplicationTags(mappedApplicationTags.get(application.getId()));
      }else{
        application.setApplicationTags(new ArrayList<>());
      }
    }
  }

  private Application populateTags(Application application) {
    application.setApplicationTags(findTagsByApplicationId(application.getId()));
    return application;
  }

  private void mapCustomerWithContactsToApplication(List<Application> applications, Integer[] ids) {
    Map<Integer, List<CustomerWithContacts>> mappedCustomerContacts = customerDao.findByApplicationsWithContacts(ids);
    for (Application application : applications) {
      if (mappedCustomerContacts.containsKey(application.getId())) {
        application.setCustomersWithContacts(mappedCustomerContacts.get(application.getId()));
      } else {
        application.setCustomersWithContacts(new ArrayList<>());
      }
    }
  }

  private void mapKindWithSpecificiersToApplication(List<Application> applications, Integer[] ids){
    Map<Integer, Map<ApplicationKind, List<ApplicationSpecifier>>> mappedToApplication =  findKindsAndSpecifiers(ids);
    for (Application application : applications) {
      Integer id = application.getId();
      if (mappedToApplication.containsKey(id) && mappedToApplication.get(id) != null) {
        application.setKindsWithSpecifiers(mappedToApplication.get(id));
      } else {
        application.setKindsWithSpecifiers(new LinkedHashMap<>());
      }
    }
  }

  private Map<Integer, Map<ApplicationKind, List<ApplicationSpecifier>>> findKindsAndSpecifiers(Integer... applicationIds) {
    List<Tuple> tuples = queryFactory
            .select(applicationKind.kind, kindSpecifier.specifier, applicationKind.applicationId).from(applicationKind)
            .leftJoin(kindSpecifier)
            .on(applicationKind.id.eq(kindSpecifier.kindId)).where(applicationKind.applicationId.in(applicationIds))
            .fetch();
    Map<Integer, Map<ApplicationKind, List<ApplicationSpecifier>>> mappedResult = new LinkedHashMap<>();
    for (Tuple tuple : tuples) {
      ApplicationKind applicationKind = tuple.get(0, ApplicationKind.class);
      ApplicationSpecifier applicationSpecifier = tuple.get(1, ApplicationSpecifier.class);
      Integer applicationId = tuple.get(2, Integer.class);
      if (!mappedResult.containsKey(applicationId)) {
        mappedResult.put(applicationId, new LinkedHashMap<>());
      }
      if (!mappedResult.get(applicationId).containsKey(applicationKind)) {
        mappedResult.get(applicationId).put(applicationKind, new ArrayList<>());
      }
      if (applicationSpecifier != null) {
        mappedResult.get(applicationId).get(applicationKind).add(applicationSpecifier);
      }
    }

    return mappedResult;
  }

  private void mapLocationsToApplications(List<Application> applicationsList, Integer[] ids) {
    List<Location> unmappedLocations = locationDao.findByApplicationIds(Arrays.asList(ids));
    if (!unmappedLocations.isEmpty()) {
      Map<Integer, List<Location>> mappedLocations = unmappedLocations.stream()
              .collect(Collectors.groupingBy(Location::getApplicationId));
      applicationsList.forEach(e -> e.setLocations(sortLocationByLocationKey(mappedLocations.get(e.getId()))));
    }

  }

  public List<Location> sortLocationByLocationKey(List<Location> locations) {
    if (locations == null || locations.isEmpty()) {
      return locations;
    } else {
      return locations.stream().sorted(Comparator.comparing(Location::getLocationKey)).collect(Collectors.toList());
    }
  }

  private void replaceApplicationTags(Integer applicationId, List<ApplicationTag> tags) {
    queryFactory.delete(applicationTag).where(applicationTag.applicationId.eq(applicationId)).execute();
    if (tags != null && !tags.isEmpty()) {
      tags.forEach(tag -> insertSingleTag(applicationId, tag));
    }
  }

  private ApplicationTag insertSingleTag(int applicationId, ApplicationTag tag) {
    // Let the database assign ID
    tag.setId(null);
    tag.setApplicationId(applicationId);
    Integer id = queryFactory.insert(applicationTag).populate(tag).executeWithKey(applicationTag.id);
    return queryFactory.select(applicationTagBean).from(applicationTag).where(applicationTag.id.eq(id)).fetchOne();
  }

  /* Set new kinds and specifiers for the given application id */
  private void replaceKindsWithSpecifiers(Integer applicationId,
      Map<ApplicationKind, List<ApplicationSpecifier>> kindsWithSpecifiers) {
    queryFactory.delete(applicationKind).where(applicationKind.applicationId.eq(applicationId)).execute();
    kindsWithSpecifiers.forEach((kind, specifiers) -> {
      final int kindId = queryFactory.insert(applicationKind).set(applicationKind.applicationId, applicationId)
          .set(applicationKind.kind, kind).executeWithKey(applicationKind.id);
      specifiers.forEach(specifier -> queryFactory.insert(kindSpecifier).set(kindSpecifier.kindId, kindId)
          .set(kindSpecifier.specifier, specifier).execute());
    });
  }

  private List<CustomerWithContacts> replaceCustomersWithContacts(Integer applicationId, List<CustomerWithContacts> customerWithContacts) {
    List<Integer> applicationCustomers =
        queryFactory.select(applicationCustomer.id).from(applicationCustomer).where(applicationCustomer.applicationId.eq(applicationId)).fetch();
    queryFactory.delete(applicationCustomerContact).where(applicationCustomerContact.applicationCustomerId.in(applicationCustomers)).execute();
    queryFactory.delete(applicationCustomer).where(applicationCustomer.id.in(applicationCustomers)).execute();
    customerWithContacts.stream().forEach(cwc -> insertCustomerWithContact(applicationId, cwc));
    return customerDao.findByApplicationWithContacts(applicationId);
  }

  @Transactional
  public CustomerWithContacts replaceCustomerWithContacts(Integer applicationId, CustomerWithContacts customerWithContacts) {
    CustomerRoleType customerRoleType = customerWithContacts.getRoleType();
    removeCustomerByRoleType(applicationId, customerRoleType);
    insertCustomerWithContact(applicationId, customerWithContacts);
    return customerDao.findByApplicationAndCustomerTypeWithContacts(applicationId, customerRoleType);
  }

  @Transactional
  public void removeCustomerByRoleType(Integer applicationId, CustomerRoleType customerRoleType) {
    BooleanExpression idCondition = applicationCustomer.applicationId.eq(applicationId);
    BooleanExpression typeCondition = applicationCustomer.customerRoleType.eq(customerRoleType);

    List<Integer> applicationCustomers = queryFactory.select(applicationCustomer.id)
        .from(applicationCustomer)
        .where(idCondition.and(typeCondition)).fetch();
    queryFactory.delete(applicationCustomerContact).where(applicationCustomerContact.applicationCustomerId.in(applicationCustomers)).execute();
    queryFactory.delete(applicationCustomer).where(applicationCustomer.id.in(applicationCustomers)).execute();
  }

  private void insertCustomerWithContact(int applicationId, CustomerWithContacts cwc) {
    Integer applicationCustomerId = queryFactory.insert(applicationCustomer)
        .set(applicationCustomer.applicationId, applicationId)
        .set(applicationCustomer.customerId, cwc.getCustomer().getId())
        .set(applicationCustomer.customerRoleType, cwc.getRoleType())
        .executeWithKey(applicationCustomer.id);

    SQLInsertClause inserts = queryFactory.insert(applicationCustomerContact);
    if (!cwc.getContacts().isEmpty()) {
      cwc.getContacts().forEach(
          c -> inserts.populate(bean(applicationCustomerContact, applicationCustomerContact.all()))
              .set(applicationCustomerContact.contactId, c.getId())
              .set(applicationCustomerContact.applicationCustomerId, applicationCustomerId)
              .addBatch());
      inserts.executeWithKeys(contact.id);
    }
  }

  private boolean hasValidRecurringEndTime(Application application) {
    return application.getEndTime() != null &&
        application.getRecurringEndTime() != null &&
        application.getEndTime().isBefore(application.getRecurringEndTime());
  }

  public List<Integer> findByInvoiceRecipient(int invoiceRecipientId) {
    QApplication application = QApplication.application;
    List<Integer> applicationIds = queryFactory
        .select(application.id)
        .from(application)
        .where(application.invoiceRecipientId.eq(invoiceRecipientId)).
        fetch();
    return applicationIds;
  }

  public List<ApplicationIdentifier> findByApplicationIdStartingWith(String idStart) {
    return queryFactory
        .select(applicationIdentifierBean)
        .from(application)
        .where(application.applicationId.startsWith(idStart))
        .orderBy(application.creationTime.desc())
        .fetch();
  }

  public void copyApplicationAttachments(Integer copyFromApplicationId, Integer copyToApplicationId) {
    attachmentDao.copyApplicationAttachments(copyFromApplicationId, copyToApplicationId);
  }

  public void setApplicationReplaced(int replacedApplicationId, Integer replacingApplicationId) {
    queryFactory
        .update(application)
        .set(application.replacedByApplicationId, replacingApplicationId)
        .where(application.id.eq(replacedApplicationId))
        .execute();
  }

  /**
   * Get invoicing date for application with given application ID
   * @param applicationId
   * @return
   */
  public ZonedDateTime getInvoicingDate(int applicationId) {
    return queryFactory
      .select(application.invoicingDate)
      .from(application)
      .where(application.id.eq(applicationId))
      .fetchOne();
  }

  /**
   * Mark applications (completely) invoiced
   */
  @Transactional
  public void markInvoiced(List<Integer> applicationIds) {
    queryFactory.update(application).set(application.invoiced, true).where(application.id.in(applicationIds)).execute();
  }

  /**
   * Fetches status of specified application
   */
  @Transactional(readOnly = true)
  public StatusType getStatus(int applicationId) {
    StatusType status = queryFactory
        .select(application.status)
        .from(application)
        .where(application.id.eq(applicationId))
        .fetchOne();
    if (status == null) {
      throw new NoSuchEntityException("No application status found for ID {}", applicationId);
    }
    return status;
  }

  @Transactional(readOnly = true)
  public ApplicationStatusInfo getStatusWithIdentifier(int applicationId) {
    ApplicationStatusInfo statusWithIdentifier = queryFactory
        .select(Projections.bean(ApplicationStatusInfo.class, application.status, application.applicationId, application.type))
        .from(application)
        .where(application.id.eq(applicationId))
        .fetchOne();
    if (statusWithIdentifier == null) {
      throw new NoSuchEntityException("No application found for ID {}", applicationId);
    }
    return statusWithIdentifier;
  }


  @Transactional(readOnly = true)
  public Integer getApplicationExternalOwner(Integer applicationId) {
    return queryFactory
        .select(application.externalOwnerId)
        .from(application)
        .where(application.id.eq(applicationId))
        .fetchOne();
  }

  public void updateCalculatedPrice(Integer id, Integer calculatedPrice) {
    queryFactory.update(application).set(application.calculatedPrice, calculatedPrice).where(application.id.eq(id)).execute();
  }

  @Transactional
  public void updateClientApplicationData(int id, ClientApplicationData clientApplicationData) {
    // Client application data is updated only if application is still pending
    StatusType status = getStatus(id);
    if (clientApplicationData != null && (status == StatusType.PENDING_CLIENT || status == StatusType.PENDING)) {
      queryFactory.update(application)
      .set(application.clientApplicationData, clientApplicationData)
      .where(application.id.eq(id))
      .execute();
    }
  }

  public Application setCustomerOperationalConditionDates(Integer id, ApplicationDateReport dateReport) {
    final ApplicationExtension extension = findExtension(id);
    if (extension instanceof OperationalConditionDates) {
      ((OperationalConditionDates) extension).setOperationalConditionReported(dateReport.getReportingDate());
      ((OperationalConditionDates) extension).setCustomerWinterTimeOperation(dateReport.getReportedDate());
      updateExtension(id, extension);
      return findById(id);
    } else {
      throw new IllegalArgumentException("Setting customer operational condition date not allowed for application found for ID " + id);
    }
  }

  public Application setCustomerWorkFinishedDates(Integer id, ApplicationDateReport dateReport) {
    final ApplicationExtension extension = findExtension(id);
    if (extension instanceof WorkFinishedDates) {
      ((WorkFinishedDates) extension).setWorkFinishedReported(dateReport.getReportingDate());
      ((WorkFinishedDates) extension).setCustomerWorkFinished(dateReport.getReportedDate());
      updateExtension(id, extension);
      return findById(id);
    } else {
      throw new IllegalArgumentException("Setting customer work finished date not allowed for application found for ID " + id);
    }
  }

  public Application setCustomerValidityDates(Integer id, ApplicationDateReport dateReport) {
    final ApplicationExtension extension = findExtension(id);
    if (extension instanceof ValidityDates) {
      ((ValidityDates) extension).setValidityReported(dateReport.getReportingDate());
      ((ValidityDates) extension).setCustomerStartTime(dateReport.getReportedDate());
      ((ValidityDates) extension).setCustomerEndTime(dateReport.getReportedEndDate());
      updateExtension(id, extension);
      return findById(id);
    } else {
      throw new IllegalArgumentException("Setting validity date not allowed for application found for ID " + id);
    }
  }

  public Application setOperationalConditionDate(Integer id, ZonedDateTime operationalConditionDate) {
    final ApplicationExtension extension = findExtension(id);
    if (extension instanceof OperationalConditionDates) {
      ((OperationalConditionDates) extension).setWinterTimeOperation(operationalConditionDate);
      updateExtension(id, extension);
      return findById(id);
    } else {
      throw new IllegalArgumentException("Setting operational condition date not allowed for application found for ID " + id);
    }
  }

  public Application setWorkFinishedDate(Integer id, ZonedDateTime workFinishedDate) {
    final ApplicationExtension extension = findExtension(id);
    if (extension instanceof WorkFinishedDates) {
      ((WorkFinishedDates) extension).setWorkFinished(workFinishedDate);
      if (extension instanceof GuaranteeEndTime) {
        ((GuaranteeEndTime) extension).setGuaranteeEndTime(SupervisionDates.guaranteeEndDate(workFinishedDate));
      }
      updateExtension(id, extension);
      return findById(id);
    } else {
      throw new IllegalArgumentException("Setting work finished date not allowed for application found for ID " + id);
    }
  }

  public void setRequiredTasks(Integer id, RequiredTasks tasks) {
    ExcavationAnnouncement excavationAnnouncement = findExtension(id, ApplicationType.EXCAVATION_ANNOUNCEMENT);
    excavationAnnouncement.setCompactionAndBearingCapacityMeasurement(tasks.getCompactionAndBearingCapacityMeasurement());
    excavationAnnouncement.setQualityAssuranceTest(tasks.getQualityAssuranceTest());
    updateExtension(id, excavationAnnouncement);
  }

  private void updateExtension(Integer id, ApplicationExtension extension) {
    queryFactory.update(application)
    .set(application.extension, extension)
    .where(application.id.eq(id))
    .execute();
  }

  @SuppressWarnings("unchecked")
  private <T extends ApplicationExtension> T findExtension(Integer id, ApplicationType type) {
    ApplicationExtension extension =  queryFactory
        .select(application.extension)
        .from(application)
        .where(application.id.eq(id), application.type.eq(type))
        .fetchOne();
    if (extension == null) {
      throw new NoSuchEntityException("No application with type " + type + " found for ID " + id);
    }
    return (T)extension;
  }

  private ApplicationExtension findExtension(Integer id) {
    ApplicationExtension extension =  queryFactory
        .select(application.extension)
        .from(application)
        .where(application.id.eq(id))
        .fetchOne();
    if (extension == null) {
      throw new NoSuchEntityException("No application found for ID " + id);
    }
    return extension;
  }

  /**
   * Sets invoicing date for application if not already set
   */
  @Transactional
  public void setInvoicingDate(int applicationId, ZonedDateTime invoicableTime) {
    queryFactory.update(application).set(application.invoicingDate, invoicableTime)
        .where(application.id.eq(applicationId), application.invoicingDate.isNull()).execute();
  }

  @Transactional
  public void setInvoicingChanged(int applicationId, boolean changed) {
    queryFactory.update(application)
    .set(application.invoicingChanged, changed)
    .where(application.id.eq(applicationId))
    .execute();
  }

  @Transactional
  public Application setTargetState(int applicationId, StatusType targetState) {
    queryFactory.update(application)
    .set(application.targetState, targetState)
    .where(application.id.eq(applicationId))
    .execute();
    return findById(applicationId);
  }

  public ApplicationType getType(Integer applicationId) {
    ApplicationType type = queryFactory
        .select(application.type)
        .from(application)
        .where(application.id.eq(applicationId))
        .fetchOne();
    if (type == null) {
      throw new NoSuchEntityException("No application type found for ID {}", applicationId);
    }
    return type;
  }

  public Integer getReplacingApplicationId(int id) {
    return queryFactory
      .select(application.replacedByApplicationId)
      .from(application)
      .where(application.id.eq(id))
      .fetchOne();
  }

  @Transactional(readOnly = true)
  public Integer getApplicationHandlerId(Integer applicationId) {
    return queryFactory
        .select(application.handler)
        .from(application)
        .where(application.id.eq(applicationId))
        .fetchOne();
  }

  public Integer getApplicationDecisionMakerId(Integer applicationId) {
    return queryFactory
        .select(application.decisionMaker)
        .from(application)
        .where(application.id.eq(applicationId))
        .fetchOne();
  }

  @Transactional(readOnly = true)
  public Integer getApplicationIdForExternalId(Integer externalId) {
    // Return latest application ID for external ID i.e. the one that is not replaced
    BooleanExpression notReplaced = application.replacedByApplicationId.isNull().and(application.status.ne(StatusType.REPLACED));
    BooleanExpression replacingNotCancelled = application.replacesApplicationId.isNull().or(application.status.ne(StatusType.CANCELLED));
    return queryFactory
        .select(application.id)
        .from(application)
        .where(application.externalApplicationId.eq(externalId), notReplaced, replacingNotCancelled)
        .fetchOne();
  }

  @Transactional
  public void removeClientApplicationData(Integer id) {
    queryFactory.update(application).setNull(application.clientApplicationData).where(application.id.eq(id)).execute();
  }

  @Transactional
  public void setInvoicingPeriodLength(Integer applicationId, Integer periodLength) {
    queryFactory.update(application).set(application.invoicingPeriodLength, periodLength).where(application.id.eq(applicationId)).execute();
  }

  @Transactional(readOnly = true)
  public Integer getInvoiceRecipientId(int id) {
    return queryFactory
        .select(application.invoiceRecipientId)
        .from(application)
        .where(application.id.eq(id))
        .fetchOne();
  }

  @Transactional(readOnly = true)
  public List<Integer> findExcavationAnnouncementByOperationalDate(ZonedDateTime conditionAfter, ZonedDateTime conditionBefore,
      List<StatusType> statusTypes) {
    BooleanExpression whereCondition = Expressions.booleanTemplate("(extension::json->>'winterTimeOperation') is not null");
    whereCondition = whereCondition.and(Expressions.booleanTemplate("to_timestamp((extension::json->>'winterTimeOperation')::float)  BETWEEN {0} AND {1}", conditionAfter, conditionBefore));
    whereCondition = whereCondition.and(application.type.eq(ApplicationType.EXCAVATION_ANNOUNCEMENT));
    if (!CollectionUtils.isEmpty(statusTypes)) {
      whereCondition = whereCondition.and(application.status.in(statusTypes));
    }
    List<Integer> applications = queryFactory.select(application.id).from(application).where(whereCondition.and(APPLICATION_NOT_REPLACED)).fetch();
    return applications;
  }

  @Transactional
  public void setInvoiceRecipient(int id, Integer invoiceRecipientId) {
    queryFactory.update(application)
        .set(application.invoiceRecipientId, invoiceRecipientId)
        .where(application.id.eq(id)).execute();
  }

  @Transactional(readOnly = true)
  public Integer getVersion(int id) {
    return queryFactory.select(application.version).from(application).where(application.id.eq(id)).fetchFirst();
  }

  public void addOwnerNotification(Integer id) {
    setOwnerNotification(id, true);
  }

  public void removeOwnerNotification(Integer id) {
    setOwnerNotification(id, false);
  }

  private void setOwnerNotification(Integer id, boolean hasNotification) {
    queryFactory.update(application)
        .set(application.ownerNotification, hasNotification)
        .where(application.id.eq(id))
        .execute();
  }

  public Integer getApplicationOwner(Integer applicationId) {
    return queryFactory.select(application.owner)
        .from(application)
        .where(application.id.eq(applicationId))
        .fetchFirst();
  }

  /**
   * Find anonymizable/"deletable" application data (ID, application ID, application type, start time, end time, change type and (latest) change time)
   * by application id found from anonymizable_application-table from database.
   * @return list of anonymizable/"deletable" applications
   */
  public List<AnonymizableApplication> findAnonymizableApplications() {
    QAnonymizableApplication aa = QAnonymizableApplication.anonymizableApplication;
    QApplication a = QApplication.application;
    QChangeHistory ch = QChangeHistory.changeHistory;

    SubQueryExpression<ZonedDateTime> latestChangeTime = select(ch.changeTime.max())
      .from(ch)
      .where(ch.applicationId.eq(aa.applicationId));

    List<AnonymizableApplication> applications = queryFactory
      .select(Projections.constructor(AnonymizableApplication.class,
        aa.applicationId.as("id"),
        a.applicationId.as("applicationId"),
        a.type,
        a.startTime,
        a.endTime,
        ch.changeType,
        ch.changeTime
      ))
      .from(aa)
      .join(a).on(aa.applicationId.eq(a.id))
      .join(ch).on(aa.applicationId.eq(ch.applicationId)
        .and(ch.changeTime.eq(latestChangeTime)))
      .fetch();

    // Use Set to follow IDs which are already processed and remove them from the list
    // There can be duplicate change_time-values in change_history table which means that query will find more than one row for the same application's ID
    // (This is a rare case of anonymisation, but possible)
    // It was decided that it is not fatal, even if the "wrong" change_type occurs
    Set<Integer> seenIds = new HashSet<>();
    applications.removeIf(application -> !seenIds.add(application.getId()));

    return applications;
  }
}
