package fi.hel.allu.model.service;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.testUtils.SpeccyTestBase;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.time.ZonedDateTime;
import java.util.Collections;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(Spectrum.class)
public class ApplicationStatusServiceTest extends SpeccyTestBase {

  private ApplicationStatusService applicationStatusService;
  private ApplicationService applicationService;
  private LocationService locationService;

  {
    beforeEach(() -> {
      applicationService = Mockito.mock(ApplicationService.class);
      locationService = Mockito.mock(LocationService.class);
      applicationStatusService = new ApplicationStatusService(applicationService, locationService);
    });

    describe("Status change operations", () -> {
      describe("Change status", () -> {
        final Application app = createMockApplication(1, StatusType.PENDING, ApplicationType.EVENT);

        beforeEach(() -> {
          when(applicationService.changeApplicationStatus(app.getId(), StatusType.HANDLING, app.getOwner()))
              .thenReturn(createMockApplication(app.getId(), StatusType.HANDLING, app.getType()));
        });

        it("Should change status using application service", () -> {
          applicationStatusService.changeApplicationStatus(app.getId(), StatusType.HANDLING, app.getOwner());
          Mockito.verify(applicationService, Mockito.times(1))
              .changeApplicationStatus(eq(app.getId()), eq(StatusType.HANDLING), eq(app.getOwner()));
        });
      });

      describe("Change status to DECISION", () -> {
        final Application app = createMockApplication(1, StatusType.HANDLING, ApplicationType.EVENT);
        final Location location = new Location();

        Application mockApp = createMockApplication(app.getId(), StatusType.DECISION, app.getType());
        mockApp.setDecisionTime(ZonedDateTime.now());

        beforeEach(() -> {
          when(applicationService.changeApplicationStatus(app.getId(), StatusType.DECISION, app.getOwner()))
              .thenReturn(mockApp);
          when(locationService.findSingleByApplicationId(app.getId()))
              .thenReturn(location);
        });

        it("should change status but not update location", () -> {
          applicationStatusService.changeApplicationStatus(app.getId(), StatusType.DECISION, app.getOwner());

          Mockito.verify(applicationService, Mockito.times(1))
              .changeApplicationStatus(eq(app.getId()), eq(StatusType.DECISION), eq(app.getOwner()));

          Mockito.verify(locationService, Mockito.never())
              .updateApplicationLocations(eq(app.getId()), eq(Collections.singletonList(location)));
        });

        it("Should change status and update placement contracts locations end date to decision date + 3 years", () -> {
          mockApp.setType(ApplicationType.PLACEMENT_CONTRACT);
          applicationStatusService.changeApplicationStatus(app.getId(), StatusType.DECISION, app.getOwner());

          Mockito.verify(applicationService, Mockito.times(1))
              .changeApplicationStatus(eq(app.getId()), eq(StatusType.DECISION), eq(app.getOwner()));

          Mockito.verify(locationService, Mockito.times(1))
              .updateApplicationLocations(eq(app.getId()), eq(Collections.singletonList(location)));

          assertEquals(location.getEndTime(), mockApp.getDecisionTime().plusYears(3));
        });

      });
    });
  }

  private Application createMockApplication(Integer id, StatusType status, ApplicationType applicationType) {
    Application app = new Application();
    app.setId(id);
    app.setStatus(status);
    app.setType(applicationType);
    app.setOwner(1);
    return app;
  }
}
