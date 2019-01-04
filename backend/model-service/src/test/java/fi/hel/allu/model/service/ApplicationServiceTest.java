package fi.hel.allu.model.service;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.dao.UserDao;
import fi.hel.allu.model.domain.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;

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


  private ApplicationService applicationService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    applicationService = new ApplicationService(applicationDao, pricingService, chargeBasisService, invoiceService,
        customerDao, locationService, defaultValueService, userDao, invoicingPeriodService);
  }

  @Test
  public void testInsertSetsInvoiceLines() {
    Application inserted = new Application();
    inserted.setId(112);
    Mockito.when(applicationDao.insert(Mockito.any(Application.class))).thenReturn(inserted);

    Application newApp = new Application();
    newApp.setLocations(Collections.singletonList(new Location()));
    newApp.setName("Foo");
    applicationService.insert(newApp, 1);
    Mockito.verify(pricingService).calculateChargeBasis(Mockito.eq(inserted));
    Mockito.verify(applicationDao).insert(Mockito.eq(newApp));
    Mockito.verify(chargeBasisService).setCalculatedChargeBasis(Mockito.eq(112), Mockito.anyListOf(ChargeBasisEntry.class));
  }

  @Test
  public void testUpdateSetsInvoiceLines() {
    Application updated = new Application();
    updated.setId(112);
    Mockito.when(applicationDao.update(Mockito.anyInt(), Mockito.any(Application.class))).thenReturn(updated);

    Application application = new Application();
    application.setName("Foo");
    applicationService.update(123, application, 1);
    Mockito.verify(pricingService).calculateChargeBasis(Mockito.eq(application));
    Mockito.verify(applicationDao).update(Mockito.eq(123), Mockito.eq(application));
    Mockito.verify(chargeBasisService).setCalculatedChargeBasis(Mockito.eq(123), Mockito.anyListOf(ChargeBasisEntry.class));
  }

  @Test
  public void testUpdateTags() {
    final int APP_ID = 123;
    final ApplicationTag tag = new ApplicationTag(1, ApplicationTagType.STATEMENT_REQUESTED, ZonedDateTime.now());
    final List<ApplicationTag> tagList = Arrays.asList(tag);
    Mockito.when(applicationDao.updateTags(APP_ID, tagList)).thenReturn(tagList);

    List<ApplicationTag> saved = applicationService.updateTags(APP_ID, tagList);
    Mockito.verify(applicationDao).updateTags(APP_ID, tagList);
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
    Mockito.when(applicationDao.findByIds(Mockito.anyListOf(Integer.class))).thenReturn(Arrays.asList(application));
    Mockito.when(application.getNotBillable()).thenReturn(false);
    Mockito.when(application.getInvoiceRecipientId()).thenReturn(IID);
    Mockito.when(customerDao.findById(IID)).thenReturn(Optional.of(customer));
    Mockito.when(applicationDao.findById(APP_ID)).thenReturn(application);
    applicationService.changeApplicationStatus(APP_ID, StatusType.DECISION, UID);
    Mockito.verify(invoiceService).createInvoices(APP_ID, false);
    Mockito.verify(applicationDao).updateDecision(APP_ID, StatusType.DECISION, UID, 0);
    Mockito.verify(applicationDao, times(0)).addTag(Mockito.anyInt(), Mockito.any());
  }

  @Test
  public void testChangeStatusToDecisionWithoutSapIdSetsTag() {
    final int APP_ID = 123;
    final int UID = 234;
    final Integer IID = 345;
    final Customer customer = new Customer();
    customer.setSapCustomerNumber("");
    Application application = Mockito.mock(Application.class);
    Mockito.when(applicationDao.findByIds(Mockito.anyListOf(Integer.class))).thenReturn(Arrays.asList(application));
    Mockito.when(application.getNotBillable()).thenReturn(false);
    Mockito.when(application.getInvoiceRecipientId()).thenReturn(IID);
    Mockito.when(customerDao.findById(IID)).thenReturn(Optional.of(customer));
    Mockito.when(applicationDao.findById(APP_ID)).thenReturn(application);
    applicationService.changeApplicationStatus(APP_ID, StatusType.DECISION, UID);
    Mockito.verify(invoiceService).createInvoices(APP_ID, true);
    Mockito.verify(applicationDao).updateDecision(APP_ID, StatusType.DECISION, UID, 0);
    ArgumentCaptor<ApplicationTag> tagCaptor = ArgumentCaptor.forClass(ApplicationTag.class);
    Mockito.verify(applicationDao, times(1)).addTag(Mockito.eq(APP_ID), tagCaptor.capture());
    assertEquals(ApplicationTagType.SAP_ID_MISSING, tagCaptor.getValue().getType());
  }

  @Test
  public void testChangeStatusToRejected() {
    final int APP_ID = 123;
    final int UID = 234;
    Application application = Mockito.mock(Application.class);
    Mockito.when(applicationDao.findById(APP_ID)).thenReturn(application);
    applicationService.changeApplicationStatus(APP_ID, StatusType.REJECTED, UID);
    Mockito.verify(invoiceService, times(0)).createInvoices(APP_ID, false);
    Mockito.verify(applicationDao).updateDecision(APP_ID, StatusType.REJECTED, UID, 0);
  }

  @Test
  public void testChangeStatusToOther() {
    final int APP_ID = 123;
    final int UID = 234;
    applicationService.changeApplicationStatus(APP_ID, StatusType.FINISHED, UID);
    Mockito.verify(invoiceService, times(0)).createInvoices(APP_ID, false);
    Mockito.verify(applicationDao).updateStatus(APP_ID, StatusType.FINISHED);
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
    Mockito.when(application.getHandler()).thenReturn(HANDLER_ID);
    Mockito.when(application.getInvoiceRecipientId()).thenReturn(IID);
    Mockito.when(customerDao.findById(IID)).thenReturn(Optional.of(customer));
    Mockito.when(applicationDao.findByIds(Mockito.anyListOf(Integer.class))).thenReturn(Arrays.asList(application));
    Mockito.when(applicationDao.findById(APP_ID)).thenReturn(application);
    applicationService.changeApplicationStatus(APP_ID, StatusType.DECISION, UID);
    Mockito.verify(applicationDao).updateDecision(APP_ID, StatusType.DECISION, UID, HANDLER_ID);
  }

  @Test
  public void rejectedSetsHandlerAsOwner() {
    final int APP_ID = 123;
    final int UID = 234;
    final int HANDLER_ID = 235;
    final Application application = new Application();
    application.setHandler(HANDLER_ID);
    Mockito.when(applicationDao.findById(APP_ID)).thenReturn(application);
    applicationService.changeApplicationStatus(APP_ID, StatusType.REJECTED, UID);
    Mockito.verify(applicationDao).updateDecision(APP_ID, StatusType.REJECTED, UID, HANDLER_ID);
  }

  @Test(expected = IllegalOperationException.class)
  public void shouldThrowWhenTryingToUpdateCancelledApplication() {
    final int APP_ID = 123;
    final Application application = new Application();
    application.setId(APP_ID);
    Mockito.when(applicationDao.getStatus(APP_ID)).thenReturn(StatusType.CANCELLED);
    applicationService.update(APP_ID, application, 1);
  }

  @Test(expected = IllegalOperationException.class)
  public void shouldThrowWhenTryingToUpdateCancelledApplicationsStatus() {
    final int APP_ID = 123;
    Mockito.when(applicationDao.getStatus(APP_ID)).thenReturn(StatusType.CANCELLED);
    applicationService.changeApplicationStatus(APP_ID, StatusType.HANDLING, 1);
  }
}
