package fi.hel.allu.model.dao;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import fi.hel.allu.QApplication;
import fi.hel.allu.QApplicationTag;
import fi.hel.allu.QPostalAddress;
import fi.hel.allu.common.types.ChangeType;
import fi.hel.allu.model.controller.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryException;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.common.PostalAddressUtil;
import fi.hel.allu.model.domain.*;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QApplicationCustomer.applicationCustomer;
import static fi.hel.allu.QApplicationCustomerContact.applicationCustomerContact;
import static fi.hel.allu.QChangeHistory.changeHistory;
import static fi.hel.allu.QContact.contact;
import static fi.hel.allu.QCustomer.customer;
import static fi.hel.allu.QCustomerArchive.customerArchive;
import static fi.hel.allu.QPostalAddress.postalAddress;
import static fi.hel.allu.QProject.project;
import static fi.hel.allu.QCustomerUpdateLog.customerUpdateLog;
import static fi.hel.allu.QPersonAuditLog.personAuditLog;

@Repository
public class CustomerDao {

  private final SQLQueryFactory queryFactory;
  private final PostalAddressDao postalAddressDao;

  @Autowired
  public CustomerDao(
    SQLQueryFactory queryFactory,
    PostalAddressDao postalAddressDao
  ) {
    this.queryFactory = queryFactory;
    this.postalAddressDao = postalAddressDao;
  }

  final QBean<Customer> customerBean = bean(Customer.class, customer.all());
  final QBean<Contact> contactBean = bean(Contact.class, contact.all());
  final QBean<PostalAddress> postalAddressBean = bean(PostalAddress.class, postalAddress.all());

  @Transactional(readOnly = true)
  public Optional<Customer> findById(int id) {
    Tuple customerPostalAddress = queryFactory
        .select(customerBean, postalAddressBean)
        .from(customer)
        .leftJoin(postalAddress).on(customer.postalAddressId.eq(postalAddress.id))
        .where(customer.id.eq(id)).fetchOne();
    Customer appl = null;
    if (customerPostalAddress != null) {
      appl = PostalAddressUtil.mapPostalAddress(customerPostalAddress).get(0, Customer.class);
    }
    return Optional.ofNullable(appl);
  }

  @Transactional(readOnly = true)
  public List<Customer> findByBusinessId(String businessId) {
    List<Expression<?>> mappedExpressions = new ArrayList<>(Arrays.asList(customer.all()));
    mappedExpressions.add(bean(PostalAddress.class, postalAddress.all()).as("postalAddress"));
    List<Customer> customers = queryFactory
        .select(Projections.bean(Customer.class, mappedExpressions.toArray(new Expression[0])))
        .from(customer)
        .leftJoin(postalAddress).on(customer.postalAddressId.eq(postalAddress.id))
        .where(customer.type.ne(CustomerType.PERSON).and(customer.registryKey.eq(businessId)))
        .fetch();
    return customers;

  }

  @Transactional(readOnly = true)
  public List<Customer> findByIds(Collection<Integer> ids) {
    List<Tuple> customerPostalAddress = queryFactory
        .select(customerBean, postalAddressBean)
        .from(customer)
        .leftJoin(postalAddress).on(customer.postalAddressId.eq(postalAddress.id))
        .where(customer.id.in(ids)).fetch();

    return customerPostalAddress.stream()
        .map(apa -> PostalAddressUtil.mapPostalAddress(apa).get(0, Customer.class))
        .collect(Collectors.toList());
  }

  /**
   * Find all customers, with paging
   *
   * @param pageRequest page request
   * @return a page of customers
   */
  @Transactional(readOnly = true)
  public Page<Customer> findAll(Pageable pageRequest) {
    long offset = (pageRequest == null) ? 0 : pageRequest.getOffset();
    int count = (pageRequest == null) ? 100 : pageRequest.getPageSize();
    QueryResults<Tuple> customerPostalAddress = queryFactory
        .select(customerBean, postalAddressBean)
        .from(customer)
        .leftJoin(postalAddress).on(customer.postalAddressId.eq(postalAddress.id))
        .orderBy(customer.id.asc()).offset(offset).limit(count).fetchResults();

    List<Customer> customers = customerPostalAddress.getResults().stream()
        .map(apa -> PostalAddressUtil.mapPostalAddress(apa).get(0, Customer.class))
        .collect(Collectors.toList());
    return new PageImpl<>(customers, pageRequest, customerPostalAddress.getTotal());
  }

  private List<Tuple> getCustomersWithContactsTuples(BooleanExpression whereCondition) {
    QPostalAddress customerPostalAddress = new QPostalAddress("customerPostalAddress");
    QPostalAddress contactPostalAddress = new QPostalAddress("contactPostalAddress");

    return queryFactory
        .select(applicationCustomer.customerRoleType, customerBean,
            bean(PostalAddress.class, customerPostalAddress.all()), contactBean,
            bean(PostalAddress.class, contactPostalAddress.all()), applicationCustomer.id, applicationCustomer.applicationId)
        .from(applicationCustomer)
        .join(customer).on(applicationCustomer.customerId.eq(customer.id))
        .leftJoin(customerPostalAddress).on(customer.postalAddressId.eq(customerPostalAddress.id))
        .leftJoin(applicationCustomerContact).on(applicationCustomer.id.eq(applicationCustomerContact.applicationCustomerId))
        .leftJoin(contact).on(applicationCustomerContact.contactId.eq(contact.id))
        .leftJoin(contactPostalAddress).on(contact.postalAddressId.eq(contactPostalAddress.id))
      .where(whereCondition).fetch();
  }

  @Transactional(readOnly = true)
  public List<CustomerWithContacts> findByApplicationWithContacts(int applicationId) {
    List<Tuple> tuples = getCustomersWithContactsTuples(applicationCustomer.applicationId.eq(applicationId));

    Map<Integer, CustomerWithContacts> customerIdToCwc = new HashMap<>();
    tuples.forEach(t -> mapCustomerWithContactTuple(customerIdToCwc, t));
    return new ArrayList<>(customerIdToCwc.values());
  }

  @Transactional(readOnly = true)
  public Map<Integer, List<CustomerWithContacts>> findByApplicationsWithContacts(Integer... applicationIds) {
    List<Tuple> tuples = getCustomersWithContactsTuples(applicationCustomer.applicationId.in(applicationIds));

    /*
    A more memory-heavy approach was needed, as adding applicationId to the client_application_data table
    would have caused saving of redundant information. Hence, we have this algorithm.
     */
    Map<Integer, Map<Integer, CustomerWithContacts>> applicationIdToCustomerIdToCwcList = new HashMap<>();
    tuples.forEach(t -> mapByApplicationCustomerWithContactTuple(applicationIdToCustomerIdToCwcList, t));
    Map<Integer, List<CustomerWithContacts>> applicationIdToCwcList = new HashMap<>();
    for (Integer applicationId : applicationIdToCustomerIdToCwcList.keySet()) {
      applicationIdToCwcList.put(applicationId, new ArrayList<>(applicationIdToCustomerIdToCwcList.get(applicationId).values()));
    }
    return applicationIdToCwcList;
  }

  @Transactional(readOnly = true)
  public CustomerWithContacts findByApplicationAndCustomerTypeWithContacts(int applicationId, CustomerRoleType customerRoleType) {
    BooleanExpression idCondition = applicationCustomer.applicationId.eq(applicationId);
    BooleanExpression whereCondition = idCondition.and(applicationCustomer.customerRoleType.eq(customerRoleType));

    List<Tuple> tuples = getCustomersWithContactsTuples(whereCondition);

    Map<Integer, CustomerWithContacts> customerIdToCwc = new HashMap<>();
    tuples.forEach(t -> mapCustomerWithContactTuple(customerIdToCwc, t));

    // exactly one customer of a type should exist for an application
    return customerIdToCwc.values().stream().findFirst().orElseThrow(() -> new NoSuchEntityException("customer.update.failed"));
  }

  private void mapByApplicationCustomerWithContactTuple(Map<Integer, Map<Integer, CustomerWithContacts>> applicationIdCustomerIdToCwcList, Tuple tuple) {
    Integer applicationId = tuple.get(6, Integer.class);
    Map<Integer, CustomerWithContacts> customerIdToCwc = applicationIdCustomerIdToCwcList.get(applicationId);
    if (customerIdToCwc == null) {
      customerIdToCwc = new HashMap<>();
      applicationIdCustomerIdToCwcList.put(applicationId, customerIdToCwc);
    }
    mapCustomerWithContactTuple(customerIdToCwc, tuple);
  }

  private void mapCustomerWithContactTuple(Map<Integer, CustomerWithContacts> customerIdToCwc, Tuple tuple) {
    Customer customer = tuple.get(1, Customer.class);
    Integer applicationCustomerId = tuple.get(5, Integer.class);
    CustomerWithContacts cwc = customerIdToCwc.get(applicationCustomerId);
    if (cwc == null) {
      PostalAddress customerPostalAddress = tuple.get(2, PostalAddress.class);
      if (customerPostalAddress.getId() != null) {
        customer.setPostalAddress(customerPostalAddress);
      }
      cwc = new CustomerWithContacts(tuple.get(0, CustomerRoleType.class), customer, new ArrayList<>());
      customerIdToCwc.put(applicationCustomerId, cwc);
    }
    Contact contact = tuple.get(3, Contact.class);
    if (contact != null && contact.getId() != null) {
      PostalAddress contactPostalAddress = tuple.get(4, PostalAddress.class);
      if (contactPostalAddress.getId() != null) {
        contact.setPostalAddress(contactPostalAddress);
      }
      cwc.getContacts().add(contact);
    }
  }

  @Transactional
  public Customer insert(Customer customerData) {
    customerData.setId(null);
    Integer id = queryFactory
        .insert(customer)
        .populate(customerData).set(customer.postalAddressId, postalAddressDao.insertIfNotNull(customerData))
        .executeWithKey(customer.id);
    if (id == null) {
      throw new QueryException("customer.insert.failed");
    }
    return findById(id).get();
  }

  @Transactional
  public Customer update(int id, Customer customerData) {
    customerData.setId(id);

    Optional<Customer> currentCustomerOpt = findById(id);
    if (!currentCustomerOpt.isPresent()) {
      throw new NoSuchEntityException("Attempted to update non-existent customer", Integer.toString(id));
    }
    Customer currentCustomer = currentCustomerOpt.get();

    Integer deletedPostalAddressId = postalAddressDao.mapAndUpdatePostalAddress(currentCustomer, customerData);
    Integer postalAddressId = Optional.ofNullable(currentCustomer.getPostalAddress()).map(pAddress -> pAddress.getId()).orElse(null);

    long changed = queryFactory
        .update(customer)
        .populate(customerData, DefaultMapper.WITH_NULL_BINDINGS)
        .set(customer.postalAddressId, postalAddressId)
        .where(customer.id.eq(id)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("customer.update.failed", Integer.toString(id));
    }

    if (deletedPostalAddressId != null) {
      postalAddressDao.delete(Collections.singletonList(deletedPostalAddressId));
    }

    return findById(id).get();
  }

  @Transactional(readOnly = true)
  public List<InvoiceRecipientCustomer> findInvoiceRecipientsWithoutSapNumber() {
    Map<Integer, List<String>> recipientIdToApplicationIds = findInvoiceRecipientIdsWithoutSapNumber();
    List<Customer> customers = findByIds(recipientIdToApplicationIds.keySet());
    return customers.stream()
        .map(c -> new InvoiceRecipientCustomer(c, recipientIdToApplicationIds.get(c.getId())))
        .collect(Collectors.toList());
  }

  private Map<Integer, List<String>> findInvoiceRecipientIdsWithoutSapNumber() {
    QApplicationTag tag = QApplicationTag.applicationTag;
    QApplication application = QApplication.application;
    QApplication replacingApplication = new QApplication("replacingApplication");
    return queryFactory
        .select(application.invoiceRecipientId)
        .from(application)
        .join(tag).on(tag.applicationId.eq(application.id))
        .where(application.status.notIn(StatusType.REPLACED, StatusType.CANCELLED)
        .and(tag.type.eq(ApplicationTagType.SAP_ID_MISSING))
        .and(
              SQLExpressions.select(replacingApplication.id)
              .from(replacingApplication)
              .where(replacingApplication.id.eq(application.replacedByApplicationId)
                  .and(replacingApplication.status.ne(StatusType.CANCELLED))).notExists())
        )
        .transform(groupBy(application.invoiceRecipientId).as(list(application.applicationId)));
  }

  @Transactional(readOnly = true)
  public Integer getNumberOfInvoiceRecipientsWithoutSapNumber() {
    return findInvoiceRecipientIdsWithoutSapNumber().size();
  }

  /**
   * Retrieves a paginated list of customers eligible for deletion. A customer is considered eligible for
   * deletion if it meets specific criteria, such as not being associated with any dependent entities
   * like applications, projects, or invoice recipients. This method performs a database query to fetch
   * and construct the list of deletable customers from the deletable_customer table.
   *
   * @param pageable the pagination information, including page number, page size, and sorting parameters.
   *                 If null, default pagination parameters are used.
   * @return a {@code Page} containing {@code DeletableCustomer} objects that meet the criteria for deletion.
   *         If no customers meet the criteria, an empty page is returned.
   */
  public Page<DeletableCustomer> getDeletableCustomers(Pageable pageable) {
    if (pageable == null) {
      pageable = PageRequest.of(Constants.DEFAULT_PAGE_NUMBER, Constants.DEFAULT_PAGE_SIZE);
    }

    // Used to search for customers which are a day older from creation date
    Instant cutoff = Instant.now().minus(1, ChronoUnit.DAYS);

    BooleanExpression deletable = isDeletableCustomer(cutoff);

    long totalCount = queryFactory
      .select(customer.id.count())
      .from(customer)
      .where(deletable)
      .fetchOne();

    List<DeletableCustomer> deletables =
      queryFactory
        .select(Projections.constructor(
          DeletableCustomer.class,
          customer.id,
          customer.sapCustomerNumber,
          customer.name,
          customer.type
        ))
        .from(customer)
        .where(deletable)
        .orderBy(toCustomerOrderSpecifier(pageable.getSort()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    return new PageImpl<>(deletables, pageable, totalCount);
  }

  private BooleanExpression isDeletableCustomer(Instant cutoff) {
    BooleanExpression notLinkedToApplication = SQLExpressions.selectOne()
      .from(applicationCustomer)
      .where(applicationCustomer.customerId.eq(customer.id))
      .notExists();

    BooleanExpression notLinkedToProject = SQLExpressions.selectOne()
      .from(project)
      .where(project.customerId.eq(customer.id))
      .notExists();

    BooleanExpression notInvoiceRecipient = SQLExpressions.selectOne()
      .from(application)
      .where(application.invoiceRecipientId.eq(customer.id))
      .notExists();

    BooleanExpression notRecentlyCreated = SQLExpressions.selectOne()
      .from(changeHistory)
      .where(
        changeHistory.customerId.eq(customer.id),
        changeHistory.changeType.eq(ChangeType.CREATED),
        changeHistory.changeTime.gt(cutoff.atZone(ZoneOffset.UTC))
      )
      .notExists();

    return customer.isActive.isTrue()
      .and(notLinkedToApplication)
      .and(notLinkedToProject)
      .and(notInvoiceRecipient)
      .and(notRecentlyCreated);
  }

  private OrderSpecifier<?> toCustomerOrderSpecifier(Sort sort) {
    if (sort == null || sort.isUnsorted()) {
      return customer.id.asc();
    }

    Sort.Order order = sort.iterator().next();
    boolean asc = order.getDirection().isAscending();

    return switch (order.getProperty()) {
      case "id" -> asc ? customer.id.asc() : customer.id.desc();
      case "sapCustomerNumber" -> asc ? customer.sapCustomerNumber.asc() : customer.sapCustomerNumber.desc();
      case "name" -> asc ? customer.name.asc() : customer.name.desc();
      default -> customer.id.asc();
    };
  }

  /**
   * Finds customer IDs that are eligible for permanent (hard) deletion.
   * A customer is purgeable when ALL the following conditions are met:
   * - Is_active = false (already soft-deleted; FK checks were done at soft-delete time
   *   and a deactivated customer cannot be re-linked to any application or project)
   * - The data retention period of 5 years has elapsed since the most recent entry across
   *   ALL history and audit log tables:
   *     * change_history (change_time)
   *     * customer_update_log (update_time)
   *     * person_audit_log (creation_time) — checked for both the customer and its contacts
   *   A customer is only purgeable when every one of these tables either has no rows for the
   *   customer or its latest row is older than the retention cutoff.
   *
   * @param pageSize  number of IDs to return
   * @param offset    pagination offset
   * @return list of customer IDs eligible for permanent deletion
   */
  @Transactional(readOnly = true)
  public List<Integer> findPurgeableCustomerIds(int pageSize, long offset) {
    ZonedDateTime retentionCutoff = ZonedDateTime.now().minusYears(5);

    // Subquery expressions for MAX() timestamps across the three log tables.
    // When a table has no rows for a given customer, MAX() returns NULL.
    // In SQL, NULL < value evaluates to NULL (not TRUE), so the condition would never
    // pass for customers with no rows in a given table.
    // We therefore treat NULL (= no rows) as satisfying the retention condition:
    //   "no rows at all" means there is nothing newer than the cutoff.
    var maxChangeTime = SQLExpressions.select(changeHistory.changeTime.max())
      .from(changeHistory)
      .where(changeHistory.customerId.eq(customer.id));

    var maxUpdateTime = SQLExpressions.select(customerUpdateLog.updateTime.max())
      .from(customerUpdateLog)
      .where(customerUpdateLog.customerId.eq(customer.id));

    var maxAuditTime = SQLExpressions.select(personAuditLog.creationTime.max())
      .from(personAuditLog)
      .where(
        personAuditLog.customerId.eq(customer.id)
          .or(
            personAuditLog.contactId.in(
              SQLExpressions.select(contact.id)
                .from(contact)
                .where(contact.customerId.eq(customer.id))
            )
          )
      );

    return queryFactory
      .select(customer.id)
      .from(customer)
      .where(
        customer.isActive.isFalse()
          // change_history: either no rows for this customer, or latest entry older than cutoff
          .and(maxChangeTime.isNull().or(maxChangeTime.lt(retentionCutoff)))
          // customer_update_log: either no rows for this customer, or latest entry older than cutoff
          .and(maxUpdateTime.isNull().or(maxUpdateTime.lt(retentionCutoff)))
          // person_audit_log: either no rows for this customer/contacts, or latest entry older than cutoff
          .and(maxAuditTime.isNull().or(maxAuditTime.lt(retentionCutoff)))
      )
      .orderBy(customer.id.asc())
      .limit(pageSize)
      .offset(offset)
      .fetch();
  }

  /**
   * Retrieves a list of customer IDs from the provided list that is considered non-deletable.
   * A customer is determined to be non-deletable if it is associated with an application,
   * project, or designated as an invoice recipient.
   *
   * @param ids a list of customer IDs to evaluate for non-deletable status.
   *            The list must not be null and may contain zero or more IDs.
   * @return a list of non-deletable customer IDs. If no customers in the input list are
   *         deemed non-deletable, an empty list is returned.
   */
  public List<Integer> findNonDeletableCustomerIds(List<Integer> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }
    
    BooleanExpression linkedToApplication = SQLExpressions.selectOne()
      .from(applicationCustomer)
      .where(applicationCustomer.customerId.eq(customer.id))
      .exists();

    BooleanExpression linkedToProject = SQLExpressions.selectOne()
      .from(project)
      .where(project.customerId.eq(customer.id))
      .exists();

    BooleanExpression isInvoiceRecipient = SQLExpressions.selectOne()
      .from(application)
      .where(application.invoiceRecipientId.eq(customer.id))
      .exists();

    return queryFactory
      .select(customer.id)
      .from(customer)
      .where(
        customer.id.in(ids),
        linkedToApplication.or(linkedToProject).or(isInvoiceRecipient)
      )
      .fetch();
  }

  /**
   * Archives customers by moving their information into a customer archive table.
   * The method retrieves the customer data identified by the provided set of IDs
   * and inserts the relevant fields into the archive.
   *
   * @param ids a set of customer IDs to be archived. The set must not be null and
   *            may contain zero or more IDs. Each ID corresponds to a customer
   *            whose information will be archived.
   */
  public void archiveCustomers(Set<Integer> ids) {
    queryFactory
      .insert(customerArchive)
      .columns(
        customerArchive.customerId,
        customerArchive.sapCustomerNumber,
        customerArchive.deletedAt
      )
      .select(
        queryFactory
          .select(
            customer.id,
            customer.sapCustomerNumber,
            Expressions.constant(ZonedDateTime.now())
          )
          .from(customer)
          .where(customer.id.in(ids))
      )
      .execute();
  }

  /**
   * Retrieves the total of archived customers from the customer archive table.
   * @return the total number of archived customers.
   */
  public long getArchivedCustomerCount() {
    return queryFactory
      .select(customerArchive.customerId.countDistinct())
      .from(customerArchive)
      .fetchOne();
  }

  /**
   * Performs a soft delete operation on customers by setting their "isActive" attribute to false.
   * This method updates the status of the customers with the specified IDs in the database.
   *
   * @param ids a list of contact IDs to be soft deleted; must not be null or empty
   * @return the number of customers that were successfully updated as part of the soft delete operation
   */
  public long softDeleteCustomers(Set<Integer> ids) {
    if (ids == null || ids.isEmpty()) {
      return 0;
    }

    return queryFactory
      .update(customer)
      .set(customer.isActive, false)
      .where(customer.id.in(ids))
      .execute();
  }

  /**
   * Deletes customers from the customer table based on the provided set of IDs.
   *
   * @param ids a set of customer IDs to be deleted.
   * @return the number of customers deleted.
   */
  public long deleteCustomers(Set<Integer> ids) {
    if (ids == null || ids.isEmpty()) {
      return 0;
    }

    return queryFactory
      .delete(customer)
      .where(customer.id.in(ids))
      .execute();
  }

  /**
   * Find 'removed' inactive SAP customers which have not been marked as notified (no email notification sent).
   *
   * @return list of inactive SAP customers which have not been marked as notified.
   */
  @Transactional(readOnly = true)
  public List<CustomerSapInfo> findUnnotifiedSapCustomers() {
    return
      queryFactory
        .select(Projections.constructor(
          CustomerSapInfo.class,
          customer.id,
          customer.sapCustomerNumber,
          customer.notificationSentAt
        ))
        .from(customer)
        .where(
          customer.sapCustomerNumber.isNotNull(),
          customer.notificationSentAt.isNull(),
          customer.isActive.eq(false)
        )
        .orderBy(customer.id.asc())
        .fetch();

  }

  /**
   * Marks 'removed' inactive SAP customers as notified by updating their notification timestamp
   * in the database to the current time.
   *
   * @param customerIds a list of 'removed' inactive SAP customers IDs to be marked as notified.
   */
  @Transactional
  public void markSapCustomersNotified(List<Integer> customerIds) {
    queryFactory
      .update(customer)
      .set(customer.notificationSentAt, ZonedDateTime.now())
      .where(customer.id.in(customerIds), customer.notificationSentAt.isNull(), customer.isActive.eq(false))
      .execute();
  }
}
