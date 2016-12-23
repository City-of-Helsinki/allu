package fi.hel.allu.model.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.InvoiceRowDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.model.domain.LocationSearchCriteria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
  public List<Application> findByIds(List<Integer> ids) {
    return applicationDao.findByIds(ids);
  }

  /**
   * Find applications within an area
   *
   * @param   lsc the location search criteria
   * @return  All intersecting applications
   */
  public List<Application> findByLocation(LocationSearchCriteria lsc) {
    return applicationDao.findByLocation(lsc);
  }

  /**
   * Update existing application
   *
   * @param id
   * @param application
   * @return the updated application
   */
  public Application update(int id, Application application) {
    List<InvoiceRow> invoiceRows = new ArrayList<>();
    pricingService.updatePrice(application, invoiceRows);
    Application result = applicationDao.update(id, application);
    invoiceRowDao.setInvoiceRows(result.getId(), invoiceRows);
    return result;
  }

  /**
   * Updates handler of given applications.
   *
   * @param   handlerId     New handler set to the applications.
   * @param   applications  Applications whose handler is updated.
   */
  public void updateHandler(int handlerId, List<Integer> applications) {
    applicationDao.updateHandler(handlerId, applications);
  }

  /**
   * Removes handler of given applications.
   *
   * @param   applications  Applications whose handler is removed.
   */
  public void removeHandler(List<Integer> applications) {
    applicationDao.removeHandler(applications);
  }

  /**
   * Create new application
   *
   * @param   application  The application data
   * @return  The created application
   */
  public Application insert(Application application) {
    if (application.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    List<InvoiceRow> invoiceRows = new ArrayList<>();
    pricingService.updatePrice(application, invoiceRows);
    Application result = applicationDao.insert(application);
    invoiceRowDao.setInvoiceRows(result.getId(), invoiceRows);
    return result;
  }

}
