package fi.hel.allu.model.service;

import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.InvoiceRowDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.InvoiceRow;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

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
    Mockito.verify(invoiceRowDao).setInvoiceRows(Mockito.eq(112), Mockito.anyListOf(InvoiceRow.class));
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
    Mockito.verify(invoiceRowDao).setInvoiceRows(Mockito.eq(112), Mockito.anyListOf(InvoiceRow.class));
  }
}
