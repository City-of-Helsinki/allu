package fi.hel.allu.model.service;

import fi.hel.allu.common.types.ApplicationTagType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.InvoiceRowDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ApplicationTag;
import fi.hel.allu.model.domain.InvoiceRow;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ApplicationServiceTest {

  @Mock
  private ApplicationDao applicationDao;
  @Mock
  private PricingService pricingService;
  @Mock
  private InvoiceRowDao invoiceRowDao;

  private ApplicationService applicationService;

  private List<InvoiceRow> storedRows;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    applicationService = new ApplicationService(applicationDao, pricingService, invoiceRowDao);
  }

  @Test
  public void testInsertSetsInvoiceLines() {
    Application inserted = new Application();
    inserted.setId(112);
    Mockito.when(applicationDao.insert(Mockito.any(Application.class))).thenReturn(inserted);

    Application newApp = new Application();
    newApp.setName("Foo");
    applicationService.insert(newApp);
    Mockito.verify(pricingService).updatePrice(Mockito.eq(newApp), Mockito.anyListOf(InvoiceRow.class));
    Mockito.verify(applicationDao).insert(Mockito.eq(newApp));
    Mockito.verify(invoiceRowDao).setInvoiceRows(Mockito.eq(112), Mockito.anyListOf(InvoiceRow.class),
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
    Mockito.verify(pricingService).updatePrice(Mockito.eq(application), Mockito.anyListOf(InvoiceRow.class));
    Mockito.verify(applicationDao).update(Mockito.eq(123), Mockito.eq(application));
    Mockito.verify(invoiceRowDao).setInvoiceRows(Mockito.eq(123), Mockito.anyListOf(InvoiceRow.class),
        Mockito.eq(false));
  }

  @Test
  public void testSetInvoiceLinesChangesPrice() {
    final int TOTAL_PRICE = 999;
    final int APP_ID = 123;
    Application application = Mockito.mock(Application.class);
    Mockito.when(applicationDao.findByIds(Mockito.anyListOf(Integer.class))).thenReturn(Arrays.asList(application));
    Mockito.when(invoiceRowDao.getTotalPrice(APP_ID)).thenReturn(TOTAL_PRICE);

    applicationService.setManualInvoiceRows(APP_ID, Arrays.asList(new InvoiceRow()));

    // Should have set new manually set rows
    Mockito.verify(invoiceRowDao).setInvoiceRows(Mockito.eq(APP_ID), Mockito.anyListOf(InvoiceRow.class),
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

}
