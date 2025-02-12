package fi.hel.allu.model.service;

import java.time.ZonedDateTime;
import java.util.*;

import fi.hel.allu.common.util.OptionalUtil;
import fi.hel.allu.model.dao.InvoiceRecipientDao;
import fi.hel.allu.model.service.chargeBasis.ChargeBasisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.RequiredTasks;
import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.user.Constants;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.dao.UserDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.user.User;

/**
 *
 * The service class for application operations
 */
@Service
public class ApplicationService {

  private final ApplicationDao applicationDao;
  private final PricingService pricingService;
  private final ChargeBasisService chargeBasisService;
  private final InvoiceService invoiceService;
  private final CustomerDao customerDao;
  private final LocationService locationService;
  private final ApplicationDefaultValueService defaultValueService;
  private final UserDao userDao;
  private final InvoicingPeriodService invoicingPeriodService;
  private final InvoiceRecipientDao invoiceRecipientDao;

  @Autowired
  public ApplicationService(ApplicationDao applicationDao, PricingService pricingService,
    ChargeBasisService chargeBasisService, InvoiceService invoiceService, CustomerDao customerDao,
    LocationService locationService, ApplicationDefaultValueService defaultValueService, UserDao userDao,
    InvoicingPeriodService invoicingPeriodService, InvoiceRecipientDao invoiceRecipientDao) {
    this.applicationDao = applicationDao;
    this.pricingService = pricingService;
    this.chargeBasisService = chargeBasisService;
    this.invoiceService = invoiceService;
    this.customerDao = customerDao;
    this.locationService = locationService;
    this.defaultValueService = defaultValueService;
    this.userDao = userDao;
    this.invoicingPeriodService = invoicingPeriodService;
    this.invoiceRecipientDao = invoiceRecipientDao;
  }

  /**
   * Find application by application ID. Returns also replaced applications.
   *
   * @param id
   * @return the application
   */
  @Transactional(readOnly = true)
  public Application findById(int id) {
    Application application = applicationDao.findById(id);
    if (application == null) {
      throw new NoSuchEntityException("application.notFound", id);
    }
    return application;
  }

  /**
   * Find applications by application IDs
   *
   * @param   ids to be searched.
   * @return  found applications
   */
  @Transactional(readOnly = true)
  public List<Application> findByIds(List<Integer> ids) {
    return applicationDao.findByIds(ids);
  }

  /**
   * Find all applications, with paging
   *
   * @param pageRequest the paging request
   * @return a page of applications
   */
  @Transactional(readOnly = true)
  public Page<Application> findAll(Pageable pageRequest) {
    return applicationDao.findAll(pageRequest);
  }

  /**
   * Returns application ids of the applications having given customer.
   *
   * @param id id of the customer whose related applications are returned.
   * @return List of application ids. Never <code>null</code>.
   */
  @Transactional(readOnly = true)
  public Map<Integer, List<CustomerRoleType>> findByCustomer(int id) {
    return applicationDao.findByCustomer(id);
  }

  /**
   * Returns application ids of the applications having given invoice recipient.
   *
   * @param id id of the invoice recipient.
   * @return List of application ids. Never <code>null</code>.
   */
  @Transactional(readOnly = true)
  public List<Integer> findByInvoiceRecipient(int id) {
    return applicationDao.findByInvoiceRecipient(id);
  }

  /**
   * Fetches all applications whose applicationId's start with given string
   *
   * @param idStart start of the applicationId to be fetched
   * @return list of applications which replaced / were replaced matching give applicationId
   */
  @Transactional(readOnly = true)
  public List<ApplicationIdentifier> findByApplicationIdStartingWith(String idStart) {
    List<ApplicationIdentifier> identifiers = applicationDao.findByApplicationIdStartingWith(idStart);
    return identifiers;
  }

  /**
   * Update existing application
   *
   * @param id
   * @param application
   * @param userId
   * @return the updated application
   */
  @Transactional
  public Application update(int id, Application application, int userId) {
    verifyApplicationIsUpdatable(id);
    defaultValueService.setByType(application);
    List<Location> locations = locationService.updateApplicationLocations(id, application.getLocations(), userId);
    applicationDao.updateClientApplicationData(id, application.getClientApplicationData());
    Application result = applicationDao.update(id, application);
    result.setLocations(locations);
    if (result.getInvoicingPeriodLength() != null) {
      // Updates also charge basis entries
      invoicingPeriodService.updateInvoicingPeriods(id, result.getInvoicingPeriodLength());
    } else {
      updateChargeBasis(id, result);
    }
    return result;
  }

  private void updateChargeBasis(int id, Application application) {
    List<ChargeBasisEntry> chargeBasisEntries = pricingService.calculateChargeBasis(application);
    chargeBasisService.setCalculatedChargeBasis(id, chargeBasisEntries);
    application.setCalculatedPrice(pricingService.totalPrice(chargeBasisService.getChargeBasis(id)));
    applicationDao.updateCalculatedPrice(application.getId(), application.getCalculatedPrice());
  }

  @Transactional
  public void updateChargeBasis(int id) {
    updateChargeBasis(id, findById(id));
  }

  /**
   * Updates owner of given applications.
   *
   * @param   ownerId     New owner set to the applications.
   * @param   userId      Current user
   * @param   applications  Applications whose owner is updated.
   */
  @Transactional
  public void updateOwner(int ownerId, List<Integer> applications) {
    applicationDao.updateOwner(ownerId, applications);
  }

  /**
   * Removes owner of given applications.
   *
   * @param   applications  Applications whose owner is removed.
   */
  @Transactional
  public void removeOwner(List<Integer> applications) {
    applicationDao.removeOwner(applications);
  }

  @Transactional
  public void updateHandler(Integer applicationId, Integer handlerId) {
    // Never sets external user as handler
    applicationDao.updateHandler(applicationId, getHandlerId(applicationId, handlerId));
  }

  /**
   * Create new application
   *
   * @param   application  The application data
   * @param userId
   * @return  The created application
   */
  @Transactional
  public Application insert(Application application, int userId) {
    if (application.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    defaultValueService.setByType(application);

    Application result = applicationDao.insert(application);
    application.getLocations().forEach(l -> l.setApplicationId(result.getId()));
    List<Location> locations = locationService.insert(application.getLocations(), userId);
    result.setLocations(locations);
    // Calculate application price. This must be done after locations have been inserted.
    calculateApplicationPrice(result);
    // Fetch from DB because location insert updates start / end time
    return applicationDao.findById(result.getId());
  }

  @Transactional
  public CustomerWithContacts replaceCustomerWithContacts(Integer applicationId,
                                                          CustomerWithContacts customerWithContacts) {
    verifyApplicationIsUpdatable(applicationId);
    return applicationDao.replaceCustomerWithContacts(applicationId, customerWithContacts);
  }

  @Transactional
  public void removeCustomerWithContacts(Integer applicationId,
                                         CustomerRoleType roleType) {
    verifyApplicationIsUpdatable(applicationId);
    applicationDao.removeCustomerByRoleType(applicationId, roleType);
  }

  private void calculateApplicationPrice(Application application) {
    List<ChargeBasisEntry> chargeBasisEntries = pricingService.calculateChargeBasis(application);
    application.setCalculatedPrice(pricingService.totalPrice(chargeBasisEntries));
    chargeBasisService.setCalculatedChargeBasis(application.getId(), chargeBasisEntries);
    applicationDao.updateCalculatedPrice(application.getId(), application.getCalculatedPrice());
  }

  /**
   * Delete note and its related data
   *
   * @param id application's database ID.
   */
  @Transactional
  public void deleteNote(int id) {
    applicationDao.deleteNote(id);
  }


  /**
   * Delete draft and its related data
   *
   * @param id application's database ID.
   */
  @Transactional
  public void deleteDraft(int id) {
    applicationDao.deleteDraft(id);
  }


  /**
   * Change application status.
   *
   * @param applicationId   Id of the application to be changed.
   * @param statusType      New status
   * @param userId          User making the status change. May be <code>null</code>, but required for decision making.
   * @return  Updated application.
   */
  @Transactional
  public Application changeApplicationStatus(int applicationId, StatusType statusType, Integer userId) {
    verifyApplicationIsUpdatable(applicationId);
    switch (statusType) {
      case DECISION:
        createInvoiceIfNeeded(applicationId, userId);
        // the fall-through is intentional here
      case REJECTED:
        final Application application = findById(applicationId);
        return applicationDao.updateDecision(applicationId, statusType, userId, application.getHandler());
      case DECISIONMAKING:
        return applicationDao.startDecisionMaking(applicationId, statusType);
      case CANCELLED:
        addCompensationClarificationForInvoiced(applicationId, userId);
        return applicationDao.updateStatus(applicationId, statusType);
      default:
        return applicationDao.updateStatus(applicationId, statusType);
    }
  }

  // Returns application's current handler if given user ID is
  // external user's ID. Otherwise returns given user ID.
  private Integer getHandlerId(int applicationId, Integer userId) {
    Application application = applicationDao.findById(applicationId);
    return Optional.ofNullable(userId)
        .flatMap(i -> userDao.findById(i))
        .map(u -> {
          if (u.getUserName().equals(Constants.EXTERNAL_USER_USERNAME)) {
            return application.getHandler();
          } else {
            return u.getId();
          }
        }).orElse(userId);
  }

  private void addCompensationClarificationForInvoiced(int id, Integer userId) {
    if (invoiceService.applicationHasInvoiced(id)) {
      addTag(id, new ApplicationTag(userId, ApplicationTagType.COMPENSATION_CLARIFICATION));
    }
  }

  /**
   * Add single tag to application
   *
   * @param applicationId Application's database ID
   * @param tag Tag to add
   * @return added tag
   */
  @Transactional
  public ApplicationTag addTag(int applicationId, ApplicationTag tag) {
    return applicationDao.addTag(applicationId, tag);
  }

  @Transactional
  public void removeTag(int applicationId, ApplicationTagType type) {
    applicationDao.removeTagByType(applicationId, type);
  }

  @Transactional
  public void removeTags(int applicationId) {
    applicationDao.removeTags(applicationId);
  }

  /**
   * Update (replace) applications tags with new ones
   * @param applicationId Id of the application to be changed.
   * @param tags New tags
   * @return New stored tags
   */
  @Transactional
  public List<ApplicationTag> updateTags(int applicationId, List<ApplicationTag> tags) {
    return applicationDao.updateTags(applicationId, tags);
  }

  /**
   * Fetches tags for specified application
   *
   * @param applicationId id of application which tags are fetched for
   * @return List of tags for specified application
   */
  @Transactional(readOnly = true)
  public List<ApplicationTag> findTagsByApplicationId(Integer applicationId) {
    return applicationDao.findTagsByApplicationId(applicationId);
  }

  /**
   * Find applications that are ending in the given time range and don't already
   * have a notification sent
   *
   * @param checkParams  search parameters: end time range and application
   *                     specifiers
   * @return list of matching applications
   */
  @Transactional(readOnly = true)
  public List<Application> deadLineCheck(DeadlineCheckParams checkParams) {
    Set<Integer> candidates = new HashSet<>();
    candidates.addAll(applicationDao.findByEndTime(checkParams.getEndsAfter(), checkParams.getEndsBefore(),
        checkParams.getTypeSelector(), checkParams.getStatusSelector()));
    // Add excavation announcements having operational condition date in given period
    candidates.addAll(applicationDao.findExcavationAnnouncementByOperationalDate(checkParams.getEndsAfter(),
        checkParams.getEndsBefore(), checkParams.getStatusSelector()));
    return findByIds(applicationDao.excludeSentReminders(candidates));
  }

  /**
   * Mark the given applications to have a reminder set. For every given
   * application ID, an entry with application's ID and current end date is
   * added to applicationReminder table. Existing reminder entries for these
   * applications are removed.
   *
   * @param applications list of application IDs
   * @return number of inserted applicationReminder entries
   */
  @Transactional
  public long markReminderSent(List<Integer> applications) {
    return applicationDao.markReminderSent(applications);
  }

  /**
   * Find all contacts of applications having given contact.
   *
   * @param contactIds Ids of the contacts whose related applications with
   *          contacts are fetched.
   * @return all contacts of applications having given contact. It's worth
   *         noticing that the same application may appear more than once in the
   *         result list. This happens, if contact appears in application under
   *         several customer roles.
   */
  @Transactional(readOnly = true)
  public List<ApplicationWithContacts> findRelatedApplicationsWithContacts(List<Integer> contactIds) {
    return applicationDao.findRelatedApplicationsWithContacts(contactIds);
  }

  /**
   * Updates applications pricing for specified application
   * @param applicationId id of application to update
   */
  @Transactional
  public void updateApplicationPricing(int applicationId) {
    applicationDao.updateCalculatedPrice(applicationId, pricingService.totalPrice(chargeBasisService.getChargeBasis(applicationId)));
  }

  /**
   * Finds finished applications having one of the given statuses.
   * @return
   */
  @Transactional(readOnly = true)
  public List<Integer> findFinishedApplications(DeadlineCheckParams params) {
    return applicationDao.findByEndTime(null, params.getEndsBefore(), params.getTypeSelector(), params.getStatusSelector());
  }

  public List<Application> findActiveExcavationAnnouncements() {
    return applicationDao.findActiveExcavationAnnouncements();
  }

  public List<Application> findPotentiallyAnonymizableApplications() {
    return applicationDao.fetchPotentiallyAnonymizableApplications();
  }

  public void addToAnonymizableApplications(List<Integer> applicationsIds) {
    applicationDao.insertToAnonymizableApplication(applicationsIds);
  }

  public List<Integer> checkAnonymizability(List<Integer> applicationIds) {
    return applicationDao.findNonanonymizableOf(applicationIds);
  }

  public void anonymizeApplications(List<Integer> applicationIds) {
    for (Integer id : applicationIds) {
      removeTags(id);
    }
    applicationDao.updateStatuses(applicationIds, StatusType.ANONYMIZED);
  }

  @Transactional(readOnly = true)
  public List<Integer> findFinishedNotes() {
    return applicationDao.findFinishedNotes();
  }

  /*
   * Create invoice for the given application if it's needed
   */
  private void createInvoiceIfNeeded(int applicationId, int userId) {
    List<Application> applications = applicationDao.findByIds(Collections.singletonList(applicationId));
    if (applications.isEmpty()) {
      throw new NoSuchEntityException("application.notFound", applicationId);
    }
    Application application = applications.get(0);
    if (application.getNotBillable() == true) {
      return;
    }
    createInvoice(applicationId, userId, application);
  }

  public void createInvoice(int applicationId, int userId, Application application) {
    final boolean sapIdPending = isSapIdPending(application);
    invoiceService.createInvoices(applicationId, sapIdPending);
    if (sapIdPending) {
      applicationDao.addTag(applicationId,
          new ApplicationTag(userId, ApplicationTagType.SAP_ID_MISSING, ZonedDateTime.now()));
    }
  }

  public boolean isSapIdPending(Integer applicationId) {
    return isSapIdPending(findById(applicationId));
  }

  public boolean isSapIdPending(Application application) {
    Customer invoicee = customerDao.findById(application.getInvoiceRecipientId())
        .orElseThrow(() -> new NoSuchEntityException("application.customer.notFound"));
    final boolean sapIdPending = StringUtils.isEmpty(invoicee.getSapCustomerNumber());
    return sapIdPending;
  }

  private void verifyApplicationIsUpdatable(Integer id) throws IllegalOperationException {
    StatusType status = applicationDao.getStatus(id);
    if (StatusType.CANCELLED.equals(status)) {
      throw new IllegalOperationException("application.cancelled.updated");
    }
  }

  public Integer getApplicationExternalOwner(Integer id) {
    return applicationDao.getApplicationExternalOwner(id);
  }

  @Transactional
  public void setInvoiceRecipient(int id, Integer invoiceRecipientId, Integer userId) {
    changeInvoiceRecipient(id, invoiceRecipientId, userId);
  }

  @Transactional(readOnly = true)
  public Customer getInvoiceRecipient(int applicationId) {
    Integer invoiceRecipientId = applicationDao.getInvoiceRecipientId(applicationId);
    return OptionalUtil.or(
      getInvoiceRecipientCustomer(applicationId, invoiceRecipientId),
      () -> customerDao.findById(invoiceRecipientId)
    ).orElse(null);
  }

  public void changeInvoiceRecipient(int id, Integer invoiceRecipientId, Integer userId) {
    applicationDao.setInvoiceRecipient(id, invoiceRecipientId);
    if (invoiceService.hasInvoices(id)) {
      Application application = findById(id);
      boolean sapIdPending = isSapIdPending(application);
      if (!sapIdPending) {
        applicationDao.removeTagByType(id, ApplicationTagType.SAP_ID_MISSING);
      } else {
        applicationDao.addTag(id, new ApplicationTag(userId, ApplicationTagType.SAP_ID_MISSING, ZonedDateTime.now()));
      }
      invoiceService.updateInvoiceRecipient(id, invoiceRecipientId, sapIdPending);
    }
  }

  /**
   * Sets excavation announcement operational condition date reported by customer
   */
  @Transactional
  public Application setCustomerOperationalConditionDates(Integer id, ApplicationDateReport dateReport) {
    return applicationDao.setCustomerOperationalConditionDates(id, dateReport);
  }

  /**
   * Sets excavation announcement work finished date reported by customer
   */
  @Transactional
  public Application setCustomerWorkFinishedDates(Integer id, ApplicationDateReport dateReport) {
    return applicationDao.setCustomerWorkFinishedDates(id, dateReport);
  }

  /**
   * Sets excavation announcement validity dates reported by customer
   */
  @Transactional
  public Application setCustomerValidityDates(Integer id, ApplicationDateReport dateReport) {
    return applicationDao.setCustomerValidityDates(id, dateReport);
  }

  /**
   * Sets excavation announcement operational condition date and updates pricing of application
   */
  @Transactional
  public void setOperationalConditionDate(Integer id, ZonedDateTime operationalConditionDate) {
    Application application = applicationDao.setOperationalConditionDate(id, operationalConditionDate);
    updateChargeBasis(id, application);
    updateApplicationPricing(id);
  }

  /**
   * Sets excavation announcement work finished date and updates pricing of application
   */
  @Transactional
  public void setWorkFinishedDate(Integer id, ZonedDateTime workFinishedDate) {
    Application application = applicationDao.setWorkFinishedDate(id, workFinishedDate);
    updateChargeBasis(id, application);
    updateApplicationPricing(id);
  }

  @Transactional
  public void setRequiredTasks(Integer id, RequiredTasks requiredTasks) {
    applicationDao.setRequiredTasks(id, requiredTasks);
  }

  @Transactional
  public Application setTargetState(Integer id, StatusType targetState) {
    return applicationDao.setTargetState(id, targetState);
  }

  @Transactional(readOnly = true)
  public Integer getReplacingApplicationId(int id) {
    return applicationDao.getReplacingApplicationId(id);
  }

  @Transactional(readOnly = true)
  public User getApplicationHandler(Integer applicationId) {
    return getUser(applicationDao.getApplicationHandlerId(applicationId));
  }

  @Transactional(readOnly = true)
  public User getApplicationDecisionMaker(Integer applicationId) {
    return getUser(applicationDao.getApplicationDecisionMakerId(applicationId));
  }

  private User getUser(Integer userId) {
    return Optional.ofNullable(userId).flatMap(id -> userDao.findById(id)).orElse(null);
  }

  @Transactional(readOnly = true)
  public Integer getApplicationIdForExternalId(Integer externalId) {
    return applicationDao.getApplicationIdForExternalId(externalId);
  }

  @Transactional
  public void removeClientApplicationData(Integer id) {
    applicationDao.removeClientApplicationData(id);
  }

  @Transactional(readOnly = true)
  public List<CustomerWithContacts> getApplicationCustomers(Integer id) {
    return customerDao.findByApplicationWithContacts(id);
  }

  @Transactional(readOnly = true)
  public Integer getVersion(int id) {
    return applicationDao.getVersion(id);
  }

  @Transactional
  public void addOwnerNotification(Integer id) {
    applicationDao.addOwnerNotification(id);
  }

  @Transactional
  public void removeOwnerNotification(Integer id) {
    applicationDao.removeOwnerNotification(id);
  }

  @Transactional(readOnly = true)
  public Integer getApplicationOwner(Integer applicationId) {
    return applicationDao.getApplicationOwner(applicationId);
  }

  @Transactional(readOnly = true)
  public ApplicationType getApplicationType(Integer applicationId) {
    return applicationDao.getType(applicationId);
  }

  public void clearExcavationAnnouncementOperationalConditionDate(Integer applicationId) {
    applicationDao.setOperationalConditionDate(applicationId, null);
    invoicingPeriodService.setExcavationAnnouncementPeriods(applicationId);
  }

  private Optional<Customer> getInvoiceRecipientCustomer(Integer applicationId, Integer invoiceRecipientId) {
    return Optional.ofNullable(applicationId)
      .flatMap(i -> invoiceRecipientDao.findByApplicationId(i))
      .map(InvoiceRecipient::asCustomer)
      .map(customer -> setFieldsFromCustomer(invoiceRecipientId, customer));
  }

  private Customer setFieldsFromCustomer(Integer from, Customer target) {
    customerDao.findById(from).ifPresent(customer -> {
      target.setId(from);
      target.setSapCustomerNumber(customer.getSapCustomerNumber());
    });
    return target;
  }

  /**
   * Find anonymizable/"deletable" applications
   * @return list of anonymizable/"deletable" applications
   */
  @Transactional(readOnly = true)
  public List<AnonymizableApplication> getAnonymizableApplications() {
    return applicationDao.findAnonymizableApplications();
  }
}
