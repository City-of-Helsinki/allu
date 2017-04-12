package fi.hel.allu.model.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.RoleType;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.UserDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
  public List<Location> insert(List<Location> locations) {
    List<Location> newLocations = new ArrayList<>();
    locations.forEach(l -> newLocations.add(locationDao.insert(l)));
    int applicationId = getApplicationId(newLocations);
    Application application = findApplication(applicationId);
    if (application.getHandler() == null) {
      // TODO: area rental with multiple locations will get more or less "random" location
      newLocations.forEach(insertedLocation -> tryToAssignHandler(application, insertedLocation));
      if (application.getHandler() != null) {
        // New handler was assigned, set it
        applicationService.updateHandler(application.getHandler(), Collections.singletonList(application.getId()));
      }
    }
    updateApplicationAndProject(application);
    return newLocations;
  }

  @Transactional
  public List<Location> updateApplicationLocations(int applicationId, List<Location> locations) {
    List<Location> newLocations = locationDao.updateApplicationLocations(applicationId, locations);
    updateApplicationAndProject(getApplicationId(newLocations));
    return newLocations;
  }

  @Transactional
  public void delete(List<Integer> locationIds) {
    int applicationId = locationDao.findApplicationId(locationIds);
    locationIds.forEach(id -> locationDao.deleteById(id));
    updateApplicationAndProject(applicationId);
  }

  private int getApplicationId(List<Location> locations) {
    List<Integer> ids = locations.stream().map(l -> l.getApplicationId()).distinct().collect(Collectors.toList());
    if (ids.size() != 1) {
      throw new IllegalArgumentException("Given locations are related to more than one application: " + ids);
    } else {
      return ids.get(0);
    }
  }

  private void updateApplicationAndProject(int applicationId) {
    Application application = findApplication(applicationId);
    updateApplicationAndProject(application);
  }

  private void updateApplicationAndProject(Application application) {
    updateProject(application.getId());
    // update application to get the price calculations done
    applicationService.update(application.getId(), application);
  }

  private Application findApplication(int applicationId) {
    List<Application> applications = applicationService.findByIds(Collections.singletonList(applicationId));
    if (applications.size() != 1) {
      throw new NoSuchEntityException("Location referenced to non-existent application", Integer.toString(applicationId));
    }
    return applications.get(0);
  }

  private void updateProject(int applicationId) {
    Application application = applicationService.findById(applicationId);
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
