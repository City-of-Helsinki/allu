package fi.hel.allu.model.service;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.UserDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.user.User;

import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Collections;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(Spectrum.class)
public class LocationServiceSpec {

  private LocationDao locationDao;
  private ApplicationService applicationService;
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
        applicationService = mock(ApplicationService.class);
        projectService = mock(ProjectService.class);
        userDao = mock(UserDao.class);
        locationService = new LocationService(locationDao, applicationService, projectService, userDao);
      });

      describe("Insert", () -> {

        beforeEach(() -> {
          insertedLocation = dummyLocation();
          testApplication = dummyApplication();
          Mockito.when(applicationService.findByIds(Mockito.any(), Mockito.eq(false)))
              .thenReturn(Collections.singletonList(testApplication));
          Mockito.when(applicationService.findById(Mockito.anyInt())).thenReturn(testApplication);
          Mockito.when(locationDao.insert(Mockito.any())).thenReturn(insertedLocation);
          Mockito
              .when(userDao.findMatching(Mockito.eq(RoleType.ROLE_PROCESS_APPLICATION), Mockito.any(), Mockito.any()))
              .thenReturn(Collections.singletonList(dummyUser()));
          locationService.insert(Collections.singletonList(dummyLocation()));
        });

        it("should set application owner", () -> {
          assertEquals(TEST_USER_ID, testApplication.getOwner().intValue());
        });

        it("should update the application in database", () -> {
          Mockito.verify(applicationService).update(TEST_APPLICATION_ID, testApplication);
        });

        it("should update application's project", () -> {
          Mockito.verify(projectService).updateProjectInformation(Collections.singletonList(TEST_PROJECT_ID));
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
