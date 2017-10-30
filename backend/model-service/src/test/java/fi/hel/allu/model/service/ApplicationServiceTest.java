package fi.hel.allu.model.service;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.ChargeBasisDao;
import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ApplicationTag;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.Customer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;

public class ApplicationServiceTest {

  @Mock
  private ApplicationDao applicationDao;
  @Mock
  private PricingService pricingService;
  @Mock
  private ChargeBasisDao chargeBasisDao;
  @Mock
  private InvoiceService invoiceService;
  @Mock
  private CustomerDao customerDao;

  private ApplicationService applicationService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    applicationService = new ApplicationService(applicationDao, pricingService, chargeBasisDao, invoiceService,
        customerDao);
  }

  @Test
  public void testInsertSetsInvoiceLines() {
    Application inserted = new Application();
    inserted.setId(112);
    Mockito.when(applicationDao.insert(Mockito.any(Application.class))).thenReturn(inserted);

    Application newApp = new Application();
    newApp.setName("Foo");
    applicationService.insert(newApp);
    Mockito.verify(pricingService).updatePrice(Mockito.eq(newApp), Mockito.anyListOf(ChargeBasisEntry.class));
    Mockito.verify(applicationDao).insert(Mockito.eq(newApp));
    Mockito.verify(chargeBasisDao).setChargeBasis(Mockito.eq(112), Mockito.anyListOf(ChargeBasisEntry.class),
        Mockito.eq(false));
  }

  @Test
  public void testUpdateSetsInvoiceLines() {
    Application updated = new Application();
    updated.setId(112);
    Mockito.when(applicationDao.update(Mockito.anyInt(), Mockito.any(Application.class))).thenReturn(updated);

    Application application = new Application();
    application.setName("Foo");
    applicationService.update(123, application);
    Mockito.verify(pricingService).updatePrice(Mockito.eq(application), Mockito.anyListOf(ChargeBasisEntry.class));
    Mockito.verify(applicationDao).update(Mockito.eq(123), Mockito.eq(application));
    Mockito.verify(chargeBasisDao).setChargeBasis(Mockito.eq(123), Mockito.anyListOf(ChargeBasisEntry.class),
        Mockito.eq(false));
  }

  @Test
  public void testSetInvoiceLinesChangesPrice() {
    final int TOTAL_PRICE = 999;
    final int APP_ID = 123;
    Application application = Mockito.mock(Application.class);
    Mockito.when(applicationDao.findByIds(Mockito.anyListOf(Integer.class))).thenReturn(Arrays.asList(application));
    Mockito.when(pricingService.totalPrice(Mockito.anyListOf(ChargeBasisEntry.class))).thenReturn(TOTAL_PRICE);

    applicationService.setManualChargeBasis(APP_ID, Arrays.asList(new ChargeBasisEntry()));

    // Should have set new manually set entries
    Mockito.verify(chargeBasisDao).setChargeBasis(Mockito.eq(APP_ID), Mockito.anyListOf(ChargeBasisEntry.class),
        Mockito.eq(true));
    // Should have set new calculated price
    Mockito.verify(application).setCalculatedPrice(TOTAL_PRICE);
    // Should have updated the application
    Mockito.verify(applicationDao).update(APP_ID, application);
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
    applicationService.changeApplicationStatus(APP_ID, StatusType.DECISION, UID);
    Mockito.verify(invoiceService).createInvoices(APP_ID, false);
    Mockito.verify(applicationDao).updateDecision(APP_ID, StatusType.DECISION, UID);
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
    applicationService.changeApplicationStatus(APP_ID, StatusType.DECISION, UID);
    Mockito.verify(invoiceService).createInvoices(APP_ID, true);
    Mockito.verify(applicationDao).updateDecision(APP_ID, StatusType.DECISION, UID);
    ArgumentCaptor<ApplicationTag> tagCaptor = ArgumentCaptor.forClass(ApplicationTag.class);
    Mockito.verify(applicationDao, times(1)).addTag(Mockito.eq(APP_ID), tagCaptor.capture());
    assertEquals(ApplicationTagType.SAP_ID_MISSING, tagCaptor.getValue().getType());
  }

  @Test
  public void testChangeStatusToRejected() {
    final int APP_ID = 123;
    final int UID = 234;
    applicationService.changeApplicationStatus(APP_ID, StatusType.REJECTED, UID);
    Mockito.verify(invoiceService, times(0)).createInvoices(APP_ID, false);
    Mockito.verify(applicationDao).updateDecision(APP_ID, StatusType.REJECTED, UID);
  }

  @Test
  public void testChangeStatusToOther() {
    final int APP_ID = 123;
    final int UID = 234;
    applicationService.changeApplicationStatus(APP_ID, StatusType.FINISHED, UID);
    Mockito.verify(invoiceService, times(0)).createInvoices(APP_ID, false);
    Mockito.verify(applicationDao).updateStatus(APP_ID, StatusType.FINISHED);
  }

}
