package fi.hel.allu.model.service;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.InvoiceRowDao;
import fi.hel.allu.model.domain.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * The service class for application operations
 */
@Service
public class ApplicationService {

  private ApplicationDao applicationDao;
  private PricingService pricingService;
  private InvoiceRowDao invoiceRowDao;

  @Autowired
  public ApplicationService(ApplicationDao applicationDao, PricingService pricingService,
      InvoiceRowDao invoiceRowDao) {
    this.applicationDao = applicationDao;
    this.pricingService = pricingService;
    this.invoiceRowDao = invoiceRowDao;
  }

  /**
   * Find application by application ID
   *
   * @param id
   * @return the application
   */
  @Transactional(readOnly = true)
  public Application findById(int id) {
    List<Application> applications = applicationDao.findByIds(Collections.singletonList(id));
    if (applications.size() != 1) {
      throw new NoSuchEntityException("Application not found", Integer.toString(id));
    }
    return applications.get(0);
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
    return applicationDao.findByLocation(lsc);
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
   * Update existing application
   *
   * @param id
   * @param application
   * @return the updated application
   */
  @Transactional
  public Application update(int id, Application application) {
    List<InvoiceRow> invoiceRows = new ArrayList<>();
    pricingService.updatePrice(application, invoiceRows);
    invoiceRowDao.setInvoiceRows(id, invoiceRows, false);
    application.setCalculatedPrice(pricingService.totalPrice(invoiceRowDao.getInvoiceRows(id)));
    Application result = applicationDao.update(id, application);
    return result;
  }

  /**
   * Updates handler of given applications.
   *
   * @param   handlerId     New handler set to the applications.
   * @param   applications  Applications whose handler is updated.
   */
  @Transactional
  public void updateHandler(int handlerId, List<Integer> applications) {
    applicationDao.updateHandler(handlerId, applications);
  }

  /**
   * Removes handler of given applications.
   *
   * @param   applications  Applications whose handler is removed.
   */
  @Transactional
  public void removeHandler(List<Integer> applications) {
    applicationDao.removeHandler(applications);
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
    List<InvoiceRow> invoiceRows = new ArrayList<>();
    pricingService.updatePrice(application, invoiceRows);
    Application result = applicationDao.insert(application);
    invoiceRowDao.setInvoiceRows(result.getId(), invoiceRows, false);
    return result;
  }

  /**
   * Delete note and its related data
   *
   * @param id application's database ID.
   */
  public void deleteNote(int id) {
    applicationDao.deleteNote(id);
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
    if (StatusType.DECISION.equals(statusType) || StatusType.REJECTED.equals(statusType)) {
      return applicationDao.updateDecision(applicationId, statusType, userId);
    } else {
      return applicationDao.updateStatus(applicationId, statusType);
    }
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
   * Set the manually set invoice rows for application.
   *
   * @param applicartionId  application's database id
   * @param invoiceRows     Invoice rows to set (only the ones marked as manually
   *                        set are used)
   * @return Application's invoice rows after operation
   */
  @Transactional
  public List<InvoiceRow> setManualInvoiceRows(int applicationId, List<InvoiceRow> invoiceRows) {
    invoiceRowDao.setInvoiceRows(applicationId,
        invoiceRows.stream().filter(r -> r.getManuallySet() == true).collect(Collectors.toList()), true);
    Application application = findById(applicationId);
    application.setCalculatedPrice(pricingService.totalPrice(invoiceRowDao.getInvoiceRows(applicationId)));
    applicationDao.update(applicationId, application);
    return invoiceRowDao.getInvoiceRows(applicationId);
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
}
