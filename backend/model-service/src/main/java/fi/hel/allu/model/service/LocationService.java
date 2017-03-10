package fi.hel.allu.model.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.RoleType;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.UserDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.User;

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
  private ProjectService projectService;
  private UserDao userDao;

  @Autowired
  public LocationService(
      LocationDao locationDao,
      ApplicationService applicationService,
      ProjectService projectService,
      UserDao userDao) {
    this.locationDao = locationDao;
    this.applicationService = applicationService;
    this.projectService = projectService;
    this.userDao = userDao;
  }


  @Transactional
  public Location insert(Location location) {
    Location insertedLocation = locationDao.insert(location);
    Application application = findApplication(location.getApplicationId());
    if (application.getHandler() == null) {
      tryToAssignHandler(application, insertedLocation);
    }
    updateProject(insertedLocation);
    // update application to get the price calculations done
    applicationService.update(application.getId(), application);
    return insertedLocation;
  }

  @Transactional
  public Location update(int locationId, Location location) {
    Application application = findApplication(location.getApplicationId());
    Location updatedLocation = locationDao.update(locationId, location);
    updateProject(updatedLocation);
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

  private void updateProject(Location location) {
    Application application = applicationService.findById(location.getApplicationId());
    if (application.getProjectId() != null) {
      projectService.updateProjectInformation(Collections.singletonList(application.getProjectId()));
    }
  }

  /*
   * Try to find a user that matches the given application and location and
   * assign the application to him/her.
   */
  private void tryToAssignHandler(Application application, Location location) {
    Integer cityDistrictId = location.getEffectiveCityDistrictId();
    ApplicationType applicationType = application.getType();
    if (cityDistrictId != null && applicationType != null) {
      List<User> users = userDao.findMatching(RoleType.ROLE_PROCESS_APPLICATION, applicationType, cityDistrictId);
      if (!users.isEmpty()) {
        application.setHandler(users.get(0).getId());
      }
    }
  }
}
