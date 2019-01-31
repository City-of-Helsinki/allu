package fi.hel.allu.model.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hel.allu.common.domain.user.Constants;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.ChangeType;
import fi.hel.allu.common.util.ObjectComparer;
import fi.hel.allu.model.dao.ContactDao;
import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.dao.UserDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.service.event.CustomerUpdateEvent;

/**
 * Customer related operations
 */
@Service
public class CustomerService {

  private ObjectComparer objectComparer;
  private CustomerDao customerDao;
  private ContactDao contactDao;
  private HistoryDao historyDao;
  private final UserDao userDao;
  private ApplicationEventPublisher customerUpdateEventPublisher;

  @Autowired
  public CustomerService(CustomerDao customerDao, ContactDao contactDao, HistoryDao historyDao, UserDao userDao, ApplicationEventPublisher customerUpdateEventPublisher) {
    this.customerDao = customerDao;
    this.contactDao = contactDao;
    this.historyDao = historyDao;
    this.userDao = userDao;
    this.customerUpdateEventPublisher = customerUpdateEventPublisher;
    objectComparer = new ObjectComparer();
  }

  /**
   * Find customer by ID
   *
   * @param id customer's database ID
   * @return customer's data
   * @throws NoSuchEntityException customer not found
   */
  @Transactional(readOnly = true)
  public Customer findById(int id) throws NoSuchEntityException {
    return customerDao.findById(id)
        .orElseThrow(() -> new NoSuchEntityException("Customer not found", Integer.toString(id)));
  }

  /**
   * Find all customers with matching IDs
   *
   * @param ids list of customer IDs to search for
   * @return all found customers. Can be less than was searched for.
   */
  @Transactional(readOnly = true)
  public List<Customer> findByIds(List<Integer> ids) {
    return customerDao.findByIds(ids);
  }

  /**
   * Find customers by their business ids. Several customers may have the same business id.
   *
   * @param businessId  Business id to be searched.
   * @return list of found customers
   */
  @Transactional(readOnly = true)
  public List<Customer> findByBusinessId(String businessId) {
    return customerDao.findByBusinessId(businessId);
  }

  /**
   * Find all customers, with paging
   *
   * @param pageRequest the paging request
   * @return a page of customers
   */
  @Transactional(readOnly = true)
  public Page<Customer> findAll(Pageable pageRequest) {
    return customerDao.findAll(pageRequest);
  }

  /**
   * Update existing customer
   *
   * @param id Customer's ID
   * @param customer Customer's data
   * @param userId ID of the user doing the change
   * @return Updated customer's data
   * @throws NoSuchEntityException Customer not found
   */
  @Transactional()
  public Customer update(int id, Customer customer, int userId) {
    Customer oldCustomer = customerDao.findById(id).orElseThrow(() -> new NoSuchEntityException("Customer not found", Integer.toString(id)));
    Customer newCustomer = customerDao.update(id, customer);
    addChangeItem(id, userId, oldCustomer, newCustomer, "", false);
    if (!isExternalUser(userId)) {
      customerUpdateEventPublisher.publishEvent(new CustomerUpdateEvent(this, oldCustomer, newCustomer));
    }
    return newCustomer;
  }

  /**
   * Add new customer
   *
   * @param customer Customer's data
   * @param userId ID of the user who made the change
   * @return Created customer's data
   */
  @Transactional()
  public Customer insert(Customer customer, int userId) {
    Customer newCustomer = customerDao.insert(customer);
    addChangeItem(newCustomer.getId(), userId, null, newCustomer, "", true);
    return newCustomer;
  }

  /**
   * Get customer's change history
   *
   * @param id the customer's database ID
   * @return list of changes for the customer
   */
  @Transactional(readOnly = true)
  public List<ChangeHistoryItem> getCustomerChanges(int id) {
    return historyDao.getCustomerHistory(id);
  }

  /**
   * Find contact item by id
   *
   * @param id The id of the contact item
   * @return The contents of requested contact item
   * @throws NoSuchEntityException Contact not found.
   */
  @Transactional(readOnly = true)
  public Contact findContact(int id) {
    return contactDao.findById(id)
        .orElseThrow(() -> new NoSuchEntityException("Contact not found", Integer.toString(id)));
  }

  /**
   * Find a list of contacts
   *
   * @param ids list of contact ids to search for
   * @return list of found contacts (can be empty)
   */
  @Transactional(readOnly = true)
  public List<Contact> findContacts(List<Integer> ids) {
    return contactDao.findByIds(ids);
  }

  /**
   * Find all contacts, with paging
   *
   * @param pageRequest the paging request
   * @return a page of contacts
   */
  @Transactional(readOnly = true)
  public Page<Contact> findAllContacts(Pageable pageRequest) {
    return contactDao.findAll(pageRequest);
  }

  /**
   * Get all contacts for a customer.
   *
   * @param customerId The ID of the customer.
   * @return All contact items for the given customer.
   */
  @Transactional(readOnly = true)
  public List<Contact> findContactsByCustomer(int customerId) {
    return contactDao.findByCustomer(customerId);
  }

  /**
   * Insert contact items.
   *
   * @param contacts The contacts to be inserted.
   * @param userId ID of the user who made the change
   * @return The inserted contacts.
   */
  @Transactional
  public List<Contact> insertContacts(List<Contact> contacts, int userId) {
    List<Contact> inserted = contactDao.insert(contacts);
    inserted.forEach(c -> addChangeItem(c.getCustomerId(), userId, null, c, "/contacts/" + c.getId(), false));
    return inserted;
  }

  /**
   * Update contact items
   *
   * @param contacts The new contents of the contact items
   * @param userId ID of the user who made the change
   * @return The contact items after update
   */
  @Transactional
  public List<Contact> updateContacts(List<Contact> contacts, int userId) {
    Map<Integer, Contact> oldContactsById = contactDao
        .findByIds(contacts.stream().map(Contact::getId).collect(Collectors.toList())).stream()
        .collect(Collectors.toMap(Contact::getId, Function.identity()));
    List<Contact> newContacts = contactDao.update(contacts);
    newContacts
        .forEach(
            c -> addChangeItem(c.getCustomerId(), userId, oldContactsById.get(c.getId()), c, "/contacts/" + c.getId(),
                false));
    return newContacts;
  }

  public List<InvoiceRecipientCustomer> findInvoiceRecipientsWithoutSapNumber() {
    return customerDao.findInvoiceRecipientsWithoutSapNumber();
  }

  public Integer getNumberInvoiceRecipientsWithoutSapNumber() {
    return customerDao.getNumberOfInvoiceRecipientsWithoutSapNumber();
  }

  /*
   * Add a change item to given user's change history. Compare oldData with
   * newData and log all differences between them. Prefix the key names with
   * given pathPrefix. If isCreate is true, make a CREATED-type change,
   * otherwise make it CONTENTS_CHANGED.
   */
  private void addChangeItem(int customerId, int userId, Object oldData, Object newData, String pathPrefix,
      boolean isCreate) {
    List<FieldChange> fieldChanges = objectComparer.compare(oldData, newData).stream()
        .map(d -> new FieldChange(pathPrefix + d.keyName, d.oldValue, d.newValue)).collect(Collectors.toList());
    if (!fieldChanges.isEmpty()) {
      ChangeHistoryItem change = new ChangeHistoryItem(userId, null,
          isCreate ? ChangeType.CREATED : ChangeType.CONTENTS_CHANGED, null, ZonedDateTime.now(), fieldChanges);
      historyDao.addCustomerChange(customerId, change);
    }

  }

  private boolean isExternalUser(Integer userId) {
    return userDao.findById(userId).map(u -> u.getUserName().equals(Constants.EXTERNAL_USER_USERNAME)).orElse(false);
  }

}
