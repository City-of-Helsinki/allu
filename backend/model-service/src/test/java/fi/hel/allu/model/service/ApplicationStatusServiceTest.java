package fi.hel.allu.model.service;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.TerminationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.service.event.ApplicationStatusChangeEvent;
import fi.hel.allu.model.testUtils.SpeccyTestBase;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(Spectrum.class)
public class ApplicationStatusServiceTest extends SpeccyTestBase {

  private ApplicationEventPublisher statusChangeEventPublisher;
  private ApplicationStatusService applicationStatusService;
  private TerminationDao terminationDao;
  private ApplicationService applicationService;


  {
    beforeEach(() -> {
      applicationService = Mockito.mock(ApplicationService.class);
      statusChangeEventPublisher = Mockito.mock(ApplicationEventPublisher.class);
      terminationDao = Mockito.mock(TerminationDao.class);
      applicationStatusService = new ApplicationStatusService(applicationService,
          Mockito.mock(ApplicationDao.class), terminationDao, statusChangeEventPublisher);
    });

    describe("Status change operations", () -> {
      describe("Change status", () -> {
        final Application app = createMockApplication(1, StatusType.PENDING, ApplicationType.EVENT);

        beforeEach(() -> {
          when(applicationService.changeApplicationStatus(eq(app.getId()), any(StatusType.class), eq(app.getOwner())))
              .thenReturn(createMockApplication(app.getId(), StatusType.HANDLING, app.getType()));
        });

        it("Should change status using application service", () -> {
          applicationStatusService.changeApplicationStatus(app.getId(), StatusType.HANDLING, app.getOwner());
          Mockito.verify(applicationService, Mockito.times(1))
              .changeApplicationStatus(eq(app.getId()), eq(StatusType.HANDLING), eq(app.getOwner()));
        });
        it("Should publish status change event", () -> {
          applicationStatusService.changeApplicationStatus(app.getId(), StatusType.DECISIONMAKING, app.getOwner());
          ArgumentCaptor<ApplicationStatusChangeEvent> captor = ArgumentCaptor.forClass(ApplicationStatusChangeEvent.class);
          Mockito.verify(statusChangeEventPublisher, Mockito.times(1))
              .publishEvent(captor.capture());
          assertEquals(app.getId(), captor.getValue().getApplication().getId());
          assertEquals(StatusType.DECISIONMAKING, captor.getValue().getNewStatus());
          assertEquals(app.getOwner(), captor.getValue().getUserId());
        });

      });

      describe("Return to status", () -> {
        final Application app = createMockApplication(1, StatusType.DECISIONMAKING, ApplicationType.SHORT_TERM_RENTAL);

        it("Should remove termination info", () -> {
          applicationStatusService.returnToStatus(app.getId(), StatusType.DECISION);
          Mockito.verify(terminationDao, Mockito.times(1)).removeTerminationInfo(eq(app.getId()));
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