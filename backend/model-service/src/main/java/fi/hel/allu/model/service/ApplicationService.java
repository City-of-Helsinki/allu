package fi.hel.allu.model.service;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.domain.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

  @Autowired
  public ApplicationService(ApplicationDao applicationDao, PricingService pricingService,
    ChargeBasisService chargeBasisService, InvoiceService invoiceService, CustomerDao customerDao) {
    this.applicationDao = applicationDao;
    this.pricingService = pricingService;
    this.chargeBasisService = chargeBasisService;
    this.invoiceService = invoiceService;
    this.customerDao = customerDao;
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
      throw new NoSuchEntityException("Application not found", Integer.toString(id));
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
   * Find applications within an area
   *
   * @param   lsc the location search criteria
   * @return  All intersecting applications
   */
  @Transactional(readOnly = true)
  public List<Application> findByLocation(LocationSearchCriteria lsc) {
    return applicationDao.findActiveByLocation(lsc);
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
   * @return the updated application
   */
  @Transactional
  public Application update(int id, Application application) {
    verifyApplicationIsUpdatable(id);
    List<ChargeBasisEntry> chargeBasisEntries = pricingService.calculateChargeBasis(application);
    chargeBasisService.setCalculatedChargeBasis(id, chargeBasisEntries);
    application.setCalculatedPrice(pricingService.totalPrice(chargeBasisService.getChargeBasis(id)));
    Application result = applicationDao.update(id, application);
    return result;
  }

  /**
   * Updates owner of given applications.
   *
   * @param   ownerId     New owner set to the applications.
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

  /**
   * Create new application
   *
   * @param   application  The application data
   * @return  The created application
   */
  @Transactional
  public Application insert(Application application) {
    if (application.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    List<ChargeBasisEntry> chargeBasisEntries = pricingService.calculateChargeBasis(application);
    application.setCalculatedPrice(pricingService.totalPrice(chargeBasisEntries));
    Application result = applicationDao.insert(application);
    chargeBasisService.setCalculatedChargeBasis(result.getId(), chargeBasisEntries);
    return result;
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
        changeReplacedApplicationStatus(application);
        return applicationDao.updateDecision(applicationId, statusType, userId, application.getHandler());
      case DECISIONMAKING:
        return applicationDao.startDecisionMaking(applicationId, statusType, userId);
      default:
        return applicationDao.updateStatus(applicationId, statusType);
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
    List<Integer> candidates = applicationDao.findByEndTime(checkParams.getEndsAfter(), checkParams.getEndsBefore(),
        checkParams.getTypeSelector(), checkParams.getStatusSelector());
    candidates = applicationDao.excludeSentReminders(candidates);
    return findByIds(candidates);
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
    Application application = findById(applicationId);
    application.setCalculatedPrice(pricingService.totalPrice(chargeBasisService.getChargeBasis(applicationId)));
    applicationDao.update(applicationId, application);
  }

  /**
   * Finds finished applications having one of the given statuses.
   * @return
   */
  @Transactional(readOnly = true)
  public List<Integer> findFinishedApplications(List<StatusType> statuses) {
    return applicationDao.findByEndTime(null, ZonedDateTime.now(), null, statuses);
  }


  /*
   * Create invoice for the given application if it's needed
   */
  private void createInvoiceIfNeeded(int applicationId, int userId) {
    List<Application> applications = applicationDao.findByIds(Collections.singletonList(applicationId));
    if (applications.isEmpty()) {
      throw new NoSuchEntityException("No application found with ID " + applicationId);
    }
    Application application = applications.get(0);
    if (application.getNotBillable() == true) {
      return;
    }
    Customer invoicee = customerDao.findById(application.getInvoiceRecipientId())
        .orElseThrow(() -> new NoSuchEntityException("No customer exists"));
    final boolean sapIdPending = StringUtils.isEmpty(invoicee.getSapCustomerNumber());
    invoiceService.createInvoices(applicationId, sapIdPending);
    if (sapIdPending) {
      applicationDao.addTag(applicationId,
          new ApplicationTag(userId, ApplicationTagType.SAP_ID_MISSING, ZonedDateTime.now()));
    }
  }

  private void changeReplacedApplicationStatus(Application application) {
    final Integer replacedAppId = application.getReplacesApplicationId();
    if (replacedAppId != null) {
      applicationDao.updateStatus(replacedAppId, StatusType.REPLACED);
    }
  }

  private void verifyApplicationIsUpdatable(Integer id) throws IllegalOperationException {
    StatusType status = applicationDao.getStatus(id);
    if (StatusType.CANCELLED.equals(status)) {
      throw new IllegalOperationException("Tried to update cancelled application " + id);
    }
  }
}
