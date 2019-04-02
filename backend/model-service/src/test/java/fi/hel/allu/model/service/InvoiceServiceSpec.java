package fi.hel.allu.model.service;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.Invoice;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.model.testUtils.SpeccyTestBase;

import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.model.dao.*;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.InvoiceRecipient;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Spectrum.class)
public class InvoiceServiceSpec extends SpeccyTestBase {

  private ChargeBasisService chargeBasisService;
  private InvoiceDao invoiceDao;
  private PricingService pricingService;
  private ApplicationDao applicationDao;
  private InvoiceRecipientDao invoiceRecipientDao;
  private CustomerDao customerDao;
  private HistoryDao historyDao;
  private InvoicingPeriodService invoicingPeriodService;
  private InvoicingDateService invoicingDateService;

  private InvoiceService invoiceService;

  {
    beforeEach(() -> {
      chargeBasisService = Mockito.mock(ChargeBasisService.class);
      invoiceDao = Mockito.mock(InvoiceDao.class);
      pricingService = Mockito.mock(PricingService.class);
      applicationDao = Mockito.mock(ApplicationDao.class);
      invoiceRecipientDao = Mockito.mock(InvoiceRecipientDao.class);
      customerDao = Mockito.mock(CustomerDao.class);
      historyDao = Mockito.mock(HistoryDao.class);
      invoicingPeriodService = Mockito.mock(InvoicingPeriodService.class);
      invoicingDateService = Mockito.mock(InvoicingDateService.class);

      invoiceService = new InvoiceService(chargeBasisService, invoiceDao, pricingService, applicationDao,
          invoiceRecipientDao, customerDao, historyDao, invoicingPeriodService, invoicingDateService);
    });

    describe("InvoiceService", () -> {
      it("Creates invoices", () -> {
        final int APPLICATION_ID = 123;
        final List<ChargeBasisEntry> CB_ENTRIES = Collections.singletonList(new ChargeBasisEntry());
        final List<InvoiceRow> INVOICE_ROWS = Collections.singletonList(new InvoiceRow());
        final int INVOICE_RECIPIENT_ID = 33;
        final Customer customer = new Customer();
        customer.setType(CustomerType.COMPANY);
        customer.setName("The Company");
        Application application = new Application();
        application.setInvoiceRecipientId(INVOICE_RECIPIENT_ID);
        Mockito.when(chargeBasisService.getChargeBasis(Mockito.eq(APPLICATION_ID))).thenReturn(CB_ENTRIES);
        Mockito.when(pricingService.toSingleInvoice(Mockito.eq(CB_ENTRIES))).thenReturn(INVOICE_ROWS);
        Mockito.when(customerDao.findById(INVOICE_RECIPIENT_ID)).thenReturn(Optional.of(customer));
        Mockito.when(invoiceRecipientDao.insert(Mockito.any())).thenReturn(INVOICE_RECIPIENT_ID);
        Mockito.when(applicationDao.findById(APPLICATION_ID)).thenReturn(application);
        Mockito.when(invoicingDateService.getInvoicingDate(Mockito.any(Application.class))).thenReturn(null);
        invoiceService.createInvoices(APPLICATION_ID, false);
        Mockito.verify(invoiceDao).deleteOpenInvoicesByApplication(Mockito.eq(APPLICATION_ID));
        ArgumentCaptor<Invoice> invoiceCaptor = ArgumentCaptor.forClass(Invoice.class);
        Mockito.verify(invoiceDao).insert(Mockito.eq(APPLICATION_ID), invoiceCaptor.capture());
        assertEquals(invoiceCaptor.getValue().getRows(), INVOICE_ROWS);
        assertEquals(INVOICE_RECIPIENT_ID, (long)invoiceCaptor.getValue().getRecipientId());
      });

      it("Find pending invoices", () -> {
        final Customer customer = new Customer();
        customer.setType(CustomerType.COMPANY);
        customer.setName("The Company");
        final int INVOICE_RECIPIENT_ID = 33;
        final InvoiceRecipient invoiceRecipient = new InvoiceRecipient(customer);
        InvoiceRow row = new InvoiceRow();
        row.setNetPrice(10);
        final List<InvoiceRow> INVOICE_ROWS = Collections.singletonList(row);
        Invoice invoice = new Invoice(1, 2, ZonedDateTime.now(), false, false, INVOICE_ROWS, INVOICE_RECIPIENT_ID, null);
        List<Invoice> pendingInvoices = Collections.singletonList(invoice);
        Mockito.when(invoiceDao.findPending()).thenReturn(pendingInvoices);
        Mockito.when(invoiceRecipientDao.findById(INVOICE_RECIPIENT_ID)).thenReturn(Optional.of(invoiceRecipient));

        List<Invoice> foundPendingInvoices = invoiceService.findPending();
        assertEquals(pendingInvoices.size(), foundPendingInvoices.size());
        Invoice foundInvoice = foundPendingInvoices.get(0);
        assertEquals(invoiceRecipient.getName(), foundInvoice.getInvoiceRecipient().getName());
        assertEquals(invoiceRecipient.getType(), foundInvoice.getInvoiceRecipient().getType());
      });
      it("Filters invoices if sum is zero", () -> {
        final Customer customer = new Customer();
        customer.setType(CustomerType.COMPANY);
        customer.setName("The Company");
        final int INVOICE_RECIPIENT_ID = 33;
        final InvoiceRecipient invoiceRecipient = new InvoiceRecipient(customer);
        InvoiceRow row = new InvoiceRow();
        row.setNetPrice(0);
        final List<InvoiceRow> INVOICE_ROWS = Collections.singletonList(row);
        Invoice invoice = new Invoice(1, 2, ZonedDateTime.now(), false, false, INVOICE_ROWS, INVOICE_RECIPIENT_ID, null);
        List<Invoice> pendingInvoices = Collections.singletonList(invoice);
        Mockito.when(invoiceDao.findPending()).thenReturn(pendingInvoices);
        Mockito.when(invoiceRecipientDao.findById(INVOICE_RECIPIENT_ID)).thenReturn(Optional.of(invoiceRecipient));
        List<Invoice> foundPendingInvoices = invoiceService.findPending();
        assertTrue(foundPendingInvoices.isEmpty());
      });

    });
  }
}
