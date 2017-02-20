package fi.hel.allu.model.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.InvoiceRowDao;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.model.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service for handling location changes.
 */
@Service
public class LocationService {

  private LocationDao locationDao;
  private ApplicationService applicationService;

  @Autowired
  public LocationService(
      LocationDao locationDao,
      ApplicationService applicationService,
      PricingService pricingService,
      InvoiceRowDao invoiceRowDao) {
    this.locationDao = locationDao;
    this.applicationService = applicationService;
  }


  @Transactional
  public Location insert(Location location) {
    Application application = findApplication(location.getApplicationId());
    List<InvoiceRow> invoiceRows = new ArrayList<>();
    Location insertedLocation = locationDao.insert(location);
    // update application to get the price calculations done
    applicationService.update(application.getId(), application);
    return insertedLocation;
  }

  @Transactional
  public Location update(int locationId, Location location) {
    Application application = findApplication(location.getApplicationId());
    Location updatedLocation = locationDao.update(locationId, location);
    List<InvoiceRow> invoiceRows = new ArrayList<>();
    // update application to get the price calculations done
    applicationService.update(application.getId(), application);
    return updatedLocation;
  }

  private Application findApplication(int applicationId) {
    List<Application> applications = applicationService.findByIds(Collections.singletonList(applicationId));
    if (applications.size() != 1) {
      throw new NoSuchEntityException("Location referenced to non-existent application", Integer.toString(applicationId));
    }
    return applications.get(0);
  }
}
