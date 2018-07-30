package fi.hel.allu.model.service;

import java.util.Collections;

import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.UserDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.user.User;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(Spectrum.class)
public class LocationServiceSpec {

  private LocationDao locationDao;
  private ApplicationDao applicationDao;
  private ProjectService projectService;
  private UserDao userDao;
  private LocationService locationService;

  private Location insertedLocation;
  private Application testApplication;

  private static final int TEST_DISTRICT_ID = 42;
  private static final int TEST_APPLICATION_ID = 69;
  private static final int TEST_USER_ID = 420;
  private static final int TEST_PROJECT_ID = 313;

  {
    describe("Location service", () -> {

      beforeEach(() -> {
        locationDao = mock(LocationDao.class);
        applicationDao = mock(ApplicationDao.class);
        projectService = mock(ProjectService.class);
        userDao = mock(UserDao.class);
        locationService = new LocationService(locationDao, applicationDao, projectService, userDao);
      });

      describe("Insert", () -> {

        beforeEach(() -> {
          insertedLocation = dummyLocation();
          testApplication = dummyApplication();
          Mockito.when(applicationDao.findByIds(Mockito.any()))
              .thenReturn(Collections.singletonList(testApplication));
          Mockito.when(applicationDao.findById(Mockito.anyInt())).thenReturn(testApplication);
          Mockito.when(locationDao.insert(Mockito.any())).thenReturn(insertedLocation);
          Mockito
              .when(userDao.findMatching(Mockito.eq(RoleType.ROLE_PROCESS_APPLICATION), Mockito.any(), Mockito.any(), Mockito.any()))
              .thenReturn(Collections.singletonList(dummyUser()));
          locationService.insert(Collections.singletonList(dummyLocation()), TEST_USER_ID);
        });

        it("should set application owner", () -> {
          assertEquals(TEST_USER_ID, testApplication.getOwner().intValue());
        });
        it("should update application's project", () -> {
          Mockito.verify(projectService).updateProjectInformation(Collections.singletonList(TEST_PROJECT_ID), TEST_USER_ID);
        });
      });

    });
  }

  private Location dummyLocation() {
    Location l = new Location();
    l.setCityDistrictId(TEST_DISTRICT_ID);
    l.setApplicationId(TEST_APPLICATION_ID);
    return l;
  }

  private Application dummyApplication() {
    Application a = new Application();
    a.setId(TEST_APPLICATION_ID);
    a.setType(ApplicationType.EXCAVATION_ANNOUNCEMENT);
    a.setProjectId(TEST_PROJECT_ID);
    return a;
  }

  private User dummyUser() {
    User u = new User();
    u.setRealName("Dummy User");
    u.setId(TEST_USER_ID);
    return u;
  }

}
