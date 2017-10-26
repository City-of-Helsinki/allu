package fi.hel.allu.model.service;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.model.dao.ChargeBasisDao;
import fi.hel.allu.model.dao.InvoiceDao;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.Invoice;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.model.testUtils.SpeccyTestBase;

import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class InvoiceServiceSpec extends SpeccyTestBase {

  private ChargeBasisDao chargeBasisDao;
  private InvoiceDao invoiceDao;
  private PricingService pricingService;

  private InvoiceService invoiceService;

  {
    beforeEach(() -> {
      chargeBasisDao = Mockito.mock(ChargeBasisDao.class);
      invoiceDao = Mockito.mock(InvoiceDao.class);
      pricingService = Mockito.mock(PricingService.class);
      invoiceService = new InvoiceService(chargeBasisDao, invoiceDao, pricingService);
    });

    describe("InvoiceService", () -> {
      it("Creates invoices", () -> {
        final int APPLICATION_ID = 123;
        final List<ChargeBasisEntry> CB_ENTRIES = Collections.singletonList(new ChargeBasisEntry());
        final List<InvoiceRow> INVOICE_ROWS = Collections.singletonList(new InvoiceRow());
        Mockito.when(chargeBasisDao.getChargeBasis(Mockito.eq(APPLICATION_ID))).thenReturn(CB_ENTRIES);
        Mockito.when(pricingService.toSingleInvoice(Mockito.eq(CB_ENTRIES))).thenReturn(INVOICE_ROWS);
        invoiceService.createInvoices(APPLICATION_ID, false);
        Mockito.verify(invoiceDao).deleteByApplication(Mockito.eq(APPLICATION_ID));
        ArgumentCaptor<Invoice> invoiceCaptor = ArgumentCaptor.forClass(Invoice.class);
        Mockito.verify(invoiceDao).insert(Mockito.eq(APPLICATION_ID), invoiceCaptor.capture());
        assertEquals(invoiceCaptor.getValue().getRows(), INVOICE_ROWS);
      });
    });
  }
}
