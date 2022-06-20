package fi.hel.allu.model.service.event.handler;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.dao.InformationRequestDao;
import fi.hel.allu.model.dao.TerminationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.CableReport;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.InvoiceService;
import fi.hel.allu.model.service.LocationService;
import fi.hel.allu.model.service.SupervisionTaskService;
import fi.hel.allu.model.service.chargeBasis.ChargeBasisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CableReportStatusChangeHandlerTest {

  private static final Integer USER_ID = Integer.valueOf(99);

  private CableReportStatusChangeHandler statusChangeHandler;
  private Application application;

  @Mock
  private LocationService locationService;
  @Mock
  private SupervisionTaskService supervisionTaskService;
  @Mock
  private ApplicationService applicationService;
  @Mock
  private ApplicationDao applicationDao;
  @Mock
  private ChargeBasisService chargeBasisService;
  @Mock
  private HistoryDao historyDao;
  @Mock
  private InformationRequestDao informationRequestDao;
  @Mock
  private InvoiceService invoiceService;
  @Mock
  private TerminationDao terminationDao;

  @Captor
  ArgumentCaptor<Application> applicationCaptor;

  @BeforeEach
  public void setup() {
    statusChangeHandler = new CableReportStatusChangeHandler(applicationService, supervisionTaskService,
        locationService, applicationDao, chargeBasisService, historyDao, informationRequestDao,
        invoiceService, terminationDao);
    createApplication();
  }

  @Test
  public void onDecisionShouldUpdateValidityTime() {
    application.setType(ApplicationType.CABLE_REPORT);
    application.setExtension(new CableReport());
    statusChangeHandler.handleDecisionStatus(application, USER_ID);
    verify(applicationService, times(1)).update(eq(application.getId()), applicationCaptor.capture(), eq(USER_ID));
    CableReport cableReport = (CableReport)applicationCaptor.getValue().getExtension();
    LocalDate date = ZonedDateTime.now().plusMonths(1).toLocalDate();
    assertEquals(date, cableReport.getValidityTime().toLocalDate());
  }

  @Test
  public void onDecisionShouldSetUserAsOwner() {
    application.setType(ApplicationType.CABLE_REPORT);
    application.setExtension(new CableReport());
    statusChangeHandler.handleDecisionStatus(application, USER_ID);
    verify(applicationDao, times(1)).updateOwner(eq(USER_ID), eq(Collections.singletonList(application.getId())));
  }

  @Test
  public void onDecisionShouldSetUserAsHandler() {
    application.setType(ApplicationType.CABLE_REPORT);
    application.setExtension(new CableReport());
    statusChangeHandler.handleDecisionStatus(application, USER_ID);
    verify(applicationDao, times(1)).updateHandler(eq(application.getId()), eq(USER_ID));
  }

  private void createApplication() {
    this.application = new Application();
    application.setId(2);

  }
}