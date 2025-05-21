package fi.hel.allu.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.ChangeType;
import fi.hel.allu.model.dao.*;
import fi.hel.allu.model.service.chargeBasis.ChargeBasisService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.model.domain.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ApplicationServiceTest {

  @Mock
  private ApplicationDao applicationDao;
  @Mock
  private PricingService pricingService;
  @Mock
  private ChargeBasisService chargeBasisService;
  @Mock
  private InvoiceService invoiceService;
  @Mock
  private CustomerDao customerDao;
  @Mock
  private LocationService locationService;
  @Mock
  private ApplicationDefaultValueService defaultValueService;
  @Mock
  private UserDao userDao;
  @Mock
  private InvoicingPeriodService invoicingPeriodService;
  @Mock
  private ApplicationService applicationService;
  @Mock
  private InvoiceRecipientDao invoiceRecipientDao;
  @Mock
  private DistributionEntryDao distributionEntryDao;
  @Mock
  private LocationDao locationDao;
  @Mock
  private DecisionDao decisionDao;
  @Mock
  private AttachmentDao attachmentDao;
  @Mock
  private SupervisionTaskDao supervisionTaskDao;
  @Mock
  private CommentDao commentDao;
  @Mock
  private HistoryDao historyDao;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    applicationService = new ApplicationService(applicationDao, pricingService, chargeBasisService, invoiceService,
        customerDao, locationService, defaultValueService, userDao, invoicingPeriodService, invoiceRecipientDao,
        distributionEntryDao, locationDao, decisionDao, attachmentDao, supervisionTaskDao, commentDao, historyDao);
  }

  @Test
  public void testInsertSetsInvoiceLines() {
    Application inserted = new Application();
    inserted.setId(112);
    when(applicationDao.insert(Mockito.any(Application.class))).thenReturn(inserted);

    Application newApp = new Application();
    newApp.setLocations(Collections.singletonList(new Location()));
    newApp.setName("Foo");
    applicationService.insert(newApp, 1);
    verify(pricingService).calculateChargeBasis(Mockito.eq(inserted));
    verify(applicationDao).insert(Mockito.eq(newApp));
    verify(chargeBasisService).setCalculatedChargeBasis(Mockito.eq(112), Mockito.anyList());
  }

  @Test
  public void testUpdateSetsInvoiceLines() {
    Application updated = new Application();
    updated.setId(112);
    when(applicationDao.update(Mockito.anyInt(), Mockito.any(Application.class))).thenReturn(updated);

    Application application = new Application();
    application.setName("Foo");
    applicationService.update(123, application, 1);
    verify(applicationDao).update(Mockito.eq(123), Mockito.eq(application));
    verify(chargeBasisService).setCalculatedChargeBasis(Mockito.eq(123), Mockito.anyList());
    verify(pricingService).calculateChargeBasis(Mockito.any(Application.class));
  }

  @Test
  public void testUpdateTags() {
    final int APP_ID = 123;
    final ApplicationTag tag = new ApplicationTag(1, ApplicationTagType.STATEMENT_REQUESTED, ZonedDateTime.now());
    final List<ApplicationTag> tagList = Arrays.asList(tag);
    when(applicationDao.updateTags(APP_ID, tagList)).thenReturn(tagList);

    List<ApplicationTag> saved = applicationService.updateTags(APP_ID, tagList);
    verify(applicationDao).updateTags(APP_ID, tagList);
    assertEquals(tag, saved.get(0));
  }

  @Test
  public void testChangeStatusToDecisionWithSapIdWontSetTag() {
    final int APP_ID = 123;
    final int UID = 234;
    final Integer IID = 345;
    final Customer customer = new Customer();
    customer.setSapCustomerNumber("SAP_123");
    Application application = Mockito.mock(Application.class);
    when(applicationDao.findByIds(Mockito.anyList())).thenReturn(Arrays.asList(application));
    when(application.getNotBillable()).thenReturn(false);
    when(application.getInvoiceRecipientId()).thenReturn(IID);
    when(customerDao.findById(IID)).thenReturn(Optional.of(customer));
    when(applicationDao.findById(APP_ID)).thenReturn(application);
    applicationService.changeApplicationStatus(APP_ID, StatusType.DECISION, UID);
    verify(invoiceService).createInvoices(APP_ID, false);
    verify(applicationDao).updateDecision(APP_ID, StatusType.DECISION, UID, 0);
    verify(applicationDao, times(0)).addTag(Mockito.anyInt(), Mockito.any());
  }

  @Test
  public void testChangeStatusToDecisionWithoutSapIdSetsTag() {
    final int APP_ID = 123;
    final int UID = 234;
    final Integer IID = 345;
    final Customer customer = new Customer();
    customer.setSapCustomerNumber("");
    Application application = Mockito.mock(Application.class);
    when(applicationDao.findByIds(Mockito.anyList())).thenReturn(Arrays.asList(application));
    when(application.getNotBillable()).thenReturn(false);
    when(application.getInvoiceRecipientId()).thenReturn(IID);
    when(customerDao.findById(IID)).thenReturn(Optional.of(customer));
    when(applicationDao.findById(APP_ID)).thenReturn(application);
    applicationService.changeApplicationStatus(APP_ID, StatusType.DECISION, UID);
    verify(invoiceService).createInvoices(APP_ID, true);
    verify(applicationDao).updateDecision(APP_ID, StatusType.DECISION, UID, 0);
    ArgumentCaptor<ApplicationTag> tagCaptor = ArgumentCaptor.forClass(ApplicationTag.class);
    verify(applicationDao, times(1)).addTag(Mockito.eq(APP_ID), tagCaptor.capture());
    assertEquals(ApplicationTagType.SAP_ID_MISSING, tagCaptor.getValue().getType());
  }

  @Test
  public void testChangeStatusToRejected() {
    final int APP_ID = 123;
    final int UID = 234;
    Application application = Mockito.mock(Application.class);
    when(applicationDao.findById(APP_ID)).thenReturn(application);
    applicationService.changeApplicationStatus(APP_ID, StatusType.REJECTED, UID);
    verify(invoiceService, times(0)).createInvoices(APP_ID, false);
    verify(applicationDao).updateDecision(APP_ID, StatusType.REJECTED, UID, 0);
  }

  @Test
  public void testChangeStatusToOther() {
    final int APP_ID = 123;
    final int UID = 234;
    applicationService.changeApplicationStatus(APP_ID, StatusType.FINISHED, UID);
    verify(invoiceService, times(0)).createInvoices(APP_ID, false);
    verify(applicationDao).updateStatus(APP_ID, StatusType.FINISHED);
  }

  @Test
  public void decisionSetsHandlerAsOwner() {
    final int APP_ID = 123;
    final int UID = 234;
    final int HANDLER_ID = 235;
    final Integer IID = 345;
    final Customer customer = new Customer();
    customer.setSapCustomerNumber("");
    Application application = Mockito.mock(Application.class);
    when(application.getHandler()).thenReturn(HANDLER_ID);
    when(application.getInvoiceRecipientId()).thenReturn(IID);
    when(customerDao.findById(IID)).thenReturn(Optional.of(customer));
    when(applicationDao.findByIds(Mockito.anyList())).thenReturn(Arrays.asList(application));
    when(applicationDao.findById(APP_ID)).thenReturn(application);
    applicationService.changeApplicationStatus(APP_ID, StatusType.DECISION, UID);
    verify(applicationDao).updateDecision(APP_ID, StatusType.DECISION, UID, HANDLER_ID);
  }

  @Test
  public void rejectedSetsHandlerAsOwner() {
    final int APP_ID = 123;
    final int UID = 234;
    final int HANDLER_ID = 235;
    final Application application = new Application();
    application.setHandler(HANDLER_ID);
    when(applicationDao.findById(APP_ID)).thenReturn(application);
    applicationService.changeApplicationStatus(APP_ID, StatusType.REJECTED, UID);
    verify(applicationDao).updateDecision(APP_ID, StatusType.REJECTED, UID, HANDLER_ID);
  }

  @Test(expected = IllegalOperationException.class)
  public void shouldThrowWhenTryingToUpdateCancelledApplication() {
    final int APP_ID = 123;
    final Application application = new Application();
    application.setId(APP_ID);
    when(applicationDao.getStatus(APP_ID)).thenReturn(StatusType.CANCELLED);
    applicationService.update(APP_ID, application, 1);
  }

  @Test(expected = IllegalOperationException.class)
  public void shouldThrowWhenTryingToUpdateCancelledApplicationsStatus() {
    final int APP_ID = 123;
    when(applicationDao.getStatus(APP_ID)).thenReturn(StatusType.CANCELLED);
    applicationService.changeApplicationStatus(APP_ID, StatusType.HANDLING, 1);
  }

  @Test
  public void testGetAnonymizableApplications_returnsCorrectData() {
    ZonedDateTime startTime = ZonedDateTime.parse("2024-12-01T10:00:00+02:00");
    ZonedDateTime endTime = ZonedDateTime.parse("2024-12-31T18:00:00+02:00");
    ZonedDateTime changeTime = ZonedDateTime.parse("2024-11-27T12:31:01+02:00");

    List<AnonymizableApplication> mockApplications = List.of(
      new AnonymizableApplication(1, "APP001", ApplicationType.EXCAVATION_ANNOUNCEMENT, startTime, endTime, ChangeType.CONTENTS_CHANGED, "foo", changeTime),
      new AnonymizableApplication(2, "APP002", ApplicationType.EXCAVATION_ANNOUNCEMENT, startTime, endTime, ChangeType.STATUS_CHANGED, null, changeTime)
    );

    ApplicationType type = ApplicationType.EXCAVATION_ANNOUNCEMENT;
    Pageable pageable = PageRequest.of(0, 10);
    when(applicationDao.findAnonymizableApplications(pageable, type)).thenReturn(new PageImpl<>(mockApplications, pageable, 2));

    Page<AnonymizableApplication> results = applicationService.getAnonymizableApplications(pageable, type);

    assertEquals(2, results.getContent().size());
    for (int i = 0; i < results.getContent().size(); i++) {
      AnonymizableApplication aa = results.getContent().get(i);
      assertEquals(i + 1, aa.getId().intValue());
      assertEquals("APP00" + (i + 1), aa.getApplicationId());
      assertEquals(ApplicationType.EXCAVATION_ANNOUNCEMENT, aa.getApplicationType());
      assertEquals(startTime, aa.getStartTime());
      assertEquals(endTime, aa.getEndTime());
      assertEquals(i == 0 ? ChangeType.CONTENTS_CHANGED : ChangeType.STATUS_CHANGED, aa.getChangeType());
      assertEquals(changeTime, aa.getChangeTime());
    }

    verify(applicationDao, times(1)).findAnonymizableApplications(pageable, type);
  }

  @Test
  public void testFindApplicationsReplacing() {
    when (applicationDao.findAnonymizableApplicationsReplacing(any())).thenReturn(List.of(1, 2 ,3)).thenReturn(List.of(4,5)).thenReturn(List.of());

    List<Integer> result = applicationService.findApplicationsReplacing(List.of(23, 42));

    assertEquals(5, result.size());
    assertTrue(result.containsAll(List.of(1, 2, 3, 4, 5)));
  }
}
