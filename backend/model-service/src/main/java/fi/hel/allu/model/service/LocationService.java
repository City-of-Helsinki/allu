package fi.hel.allu.model.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.UserDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for handling location changes.
 */
@Service
public class LocationService {

  private final LocationDao locationDao;
  private final ApplicationService applicationService;
  private final ProjectService projectService;
  private final UserDao userDao;

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
  public List<Location> insert(List<Location> locations, int userId) {
    List<Location> newLocations = new ArrayList<>();
    locations.forEach(l -> newLocations.add(locationDao.insert(l)));
    int applicationId = getApplicationId(newLocations);
    Application application = findApplication(applicationId);
    assignOwner(application, newLocations, userId);
    updateApplicationAndProject(application, userId);
    return newLocations;
  }

  private void assignOwner(Application application, List<Location> locations, int userId) {
    if (application.getOwner() == null) {
      Optional<Integer> owner = Optional.ofNullable(userId);

      if (!ApplicationType.NOTE.equals(application.getType())) {
        // TODO: area rental with multiple locations will get more or less "random" location
        owner = locations.stream()
            .findFirst()
            .flatMap(location -> ownerFromLocations(application.getType(), location));
      }

      owner.ifPresent(id -> {
        application.setOwner(id);
        applicationService.updateOwner(id, Arrays.asList(application.getId()));
      });
    }
  }

  @Transactional
  public List<Location> updateApplicationLocations(int applicationId, List<Location> locations, int userId) {
    List<Location> newLocations = locationDao.updateApplicationLocations(applicationId, locations);
    updateApplicationAndProject(getApplicationId(newLocations), userId);
    return newLocations;
  }

  @Transactional
  public void delete(List<Integer> locationIds, int userId) {
    int applicationId = locationDao.findApplicationId(locationIds);
    locationIds.forEach(id -> locationDao.deleteById(id));
    updateApplicationAndProject(applicationId, userId);
  }

  @Transactional(readOnly = true)
  public List<Location> findByApplicationId(Integer applicationId) {
    return this.locationDao.findByApplication(applicationId);
  }

  /**
   * Tries to find single location for application.
   * If application contains none or more than one locations exception is thrown
   */
  @Transactional(readOnly = true)
  public Location findSingleByApplicationId(Integer applicationId) {
    List<Location> locations = findByApplicationId(applicationId);
    if (locations.size() == 1) {
      return locations.get(0);
    } else {
      throw new IllegalStateException("Application contains " + locations.size()
          + " locations where single location was expected");
    }
  }

  private int getApplicationId(List<Location> locations) {
    List<Integer> ids = locations.stream().map(l -> l.getApplicationId()).distinct().collect(Collectors.toList());
    if (ids.size() != 1) {
      throw new IllegalArgumentException("Given locations are related to more than one application: " + ids);
    } else {
      return ids.get(0);
    }
  }

  private void updateApplicationAndProject(int applicationId, int userId) {
    Application application = findApplication(applicationId);
    updateApplicationAndProject(application, userId);
  }

  private void updateApplicationAndProject(Application application, int userId) {
    updateProject(application.getId(), userId);
    // update application to get the price calculations done
    applicationService.update(application.getId(), application);
  }

  private Application findApplication(int applicationId) {
    List<Application> applications = applicationService.findByIds(Collections.singletonList(applicationId), false);
    if (applications.size() != 1) {
      throw new NoSuchEntityException("Location referenced to non-existent application", Integer.toString(applicationId));
    }
    return applications.get(0);
  }

  private void updateProject(int applicationId, int userId) {
    Application application = applicationService.findById(applicationId);
    if (application.getProjectId() != null) {
      projectService.updateProjectInformation(Collections.singletonList(application.getProjectId()), userId);
    }
  }

  /*
   * Try to find a user that matches the given application and location
   */
  private Optional<Integer> ownerFromLocations(ApplicationType type, Location location) {
    Integer cityDistrictId = location.getEffectiveCityDistrictId();
    return Optional.ofNullable(cityDistrictId)
        .flatMap(cityDistrict -> findOwner(type, cityDistrictId))
        .map(user -> user.getId());
  }

  private Optional<User> findOwner(ApplicationType type, Integer cityDistrictId) {
    return userDao.findMatching(RoleType.ROLE_PROCESS_APPLICATION, type, cityDistrictId).stream()
        .findFirst();
  }

  /**
   * Copy application locations from application to another application
   */
  @Transactional
  public void copyApplicationLocations(Integer copyFromApplicationId, Integer copyToApplicationId, int userId) {
    List<Location> locations = findByApplicationId(copyFromApplicationId);
    locations.forEach(l -> l.setApplicationId(copyToApplicationId));
    insert(locations, userId);
  }
}
