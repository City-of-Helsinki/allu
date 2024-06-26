package fi.hel.allu.model.dao;

import java.util.*;
import java.util.stream.Collectors;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

import fi.hel.allu.QApplication;
import fi.hel.allu.QApplicationTag;
import fi.hel.allu.QPostalAddress;
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
import static fi.hel.allu.QApplicationCustomer.applicationCustomer;
import static fi.hel.allu.QApplicationCustomerContact.applicationCustomerContact;
import static fi.hel.allu.QContact.contact;
import static fi.hel.allu.QCustomer.customer;
import static fi.hel.allu.QPostalAddress.postalAddress;

@Repository
public class CustomerDao {
  @Autowired
  private SQLQueryFactory queryFactory;
  @Autowired
  PostalAddressDao postalAddressDao;

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

}
