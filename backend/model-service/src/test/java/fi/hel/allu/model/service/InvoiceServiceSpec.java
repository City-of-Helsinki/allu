package fi.hel.allu.model.service;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.Invoice;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.model.service.chargeBasis.ChargeBasisService;
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
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.dao.*;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.InvoiceRecipient;
import fi.hel.allu.model.domain.InvoicingPeriod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

  private Invoice pendingInvoice(int applicationId, int invoiceRecipientId, int chargeBasisId) {
    InvoiceRow row = new InvoiceRow();
    row.setChargeBasisId(chargeBasisId);
    return new Invoice(1, applicationId, ZonedDateTime.now(), false, false,
        Collections.singletonList(row), invoiceRecipientId, null);
  }

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

      it("Leaves the final period of a phase without an invoicing date", () -> {
        final int APPLICATION_ID = 123;
        final int INVOICE_RECIPIENT_ID = 33;
        final Customer customer = new Customer();
        customer.setType(CustomerType.COMPANY);
        customer.setName("The Company");
        Application application = new Application();
        application.setId(APPLICATION_ID);
        application.setInvoiceRecipientId(INVOICE_RECIPIENT_ID);
        InvoicingPeriod finalPeriod = new InvoicingPeriod(APPLICATION_ID,
            ZonedDateTime.parse("2026-04-01T00:00:00+03:00"), ZonedDateTime.parse("2026-04-30T00:00:00+03:00"));
        finalPeriod.setId(7);
        finalPeriod.setInvoicableStatus(StatusType.FINISHED);
        ChargeBasisEntry entry = new ChargeBasisEntry();
        entry.setId(99);
        entry.setInvoicingPeriodId(7);
        entry.setInvoicable(true);
        List<ChargeBasisEntry> entries = Collections.singletonList(entry);
        List<InvoiceRow> rows = Collections.singletonList(new InvoiceRow());

        Mockito.when(invoicingPeriodService.findOpenPeriodsForApplicationId(APPLICATION_ID))
            .thenReturn(Collections.singletonList(finalPeriod));
        Mockito.when(invoicingPeriodService.isFinalPeriod(Mockito.any(Application.class),
            Mockito.any(InvoicingPeriod.class))).thenReturn(true);
        Mockito.when(chargeBasisService.getChargeBasis(Mockito.eq(APPLICATION_ID))).thenReturn(entries);
        Mockito.when(pricingService.toSingleInvoice(Mockito.anyList())).thenReturn(rows);
        Mockito.when(customerDao.findById(INVOICE_RECIPIENT_ID)).thenReturn(Optional.of(customer));
        Mockito.when(invoiceRecipientDao.insert(Mockito.any())).thenReturn(INVOICE_RECIPIENT_ID);
        Mockito.when(applicationDao.findById(APPLICATION_ID)).thenReturn(application);

        invoiceService.createInvoices(APPLICATION_ID, false);

        ArgumentCaptor<Invoice> invoiceCaptor = ArgumentCaptor.forClass(Invoice.class);
        Mockito.verify(invoiceDao).insert(Mockito.eq(APPLICATION_ID), invoiceCaptor.capture());
        // No invoicing date means the invoice is not sent before the phase decision
        assertEquals(null, invoiceCaptor.getValue().getInvoicableTime());
      });

      it("Invoices a non-final period on the date given by the invoicing date service", () -> {
        final int APPLICATION_ID = 123;
        final int INVOICE_RECIPIENT_ID = 33;
        final ZonedDateTime INVOICING_DATE = ZonedDateTime.parse("2026-05-01T00:00:00+03:00");
        final Customer customer = new Customer();
        customer.setType(CustomerType.COMPANY);
        customer.setName("The Company");
        Application application = new Application();
        application.setId(APPLICATION_ID);
        application.setInvoiceRecipientId(INVOICE_RECIPIENT_ID);
        InvoicingPeriod period = new InvoicingPeriod(APPLICATION_ID,
            ZonedDateTime.parse("2026-04-01T00:00:00+03:00"), ZonedDateTime.parse("2026-04-30T00:00:00+03:00"));
        period.setId(8);
        period.setInvoicableStatus(StatusType.FINISHED);
        ChargeBasisEntry entry = new ChargeBasisEntry();
        entry.setId(100);
        entry.setInvoicingPeriodId(8);
        entry.setInvoicable(true);

        Mockito.when(invoicingPeriodService.findOpenPeriodsForApplicationId(APPLICATION_ID))
            .thenReturn(Collections.singletonList(period));
        Mockito.when(invoicingPeriodService.isFinalPeriod(Mockito.any(Application.class),
            Mockito.any(InvoicingPeriod.class))).thenReturn(false);
        Mockito.when(chargeBasisService.getChargeBasis(Mockito.eq(APPLICATION_ID)))
            .thenReturn(Collections.singletonList(entry));
        Mockito.when(pricingService.toSingleInvoice(Mockito.anyList()))
            .thenReturn(Collections.singletonList(new InvoiceRow()));
        Mockito.when(customerDao.findById(INVOICE_RECIPIENT_ID)).thenReturn(Optional.of(customer));
        Mockito.when(invoiceRecipientDao.insert(Mockito.any())).thenReturn(INVOICE_RECIPIENT_ID);
        Mockito.when(applicationDao.findById(APPLICATION_ID)).thenReturn(application);
        Mockito.when(invoicingDateService.getInvoicingDateForPeriod(Mockito.any(Application.class),
            Mockito.any(InvoicingPeriod.class))).thenReturn(INVOICING_DATE);

        invoiceService.createInvoices(APPLICATION_ID, false);

        ArgumentCaptor<Invoice> invoiceCaptor = ArgumentCaptor.forClass(Invoice.class);
        Mockito.verify(invoiceDao).insert(Mockito.eq(APPLICATION_ID), invoiceCaptor.capture());
        assertEquals(INVOICING_DATE, invoiceCaptor.getValue().getInvoicableTime());
      });

      describe("Find pending invoices", () -> {
        beforeEach(() -> {
          Mockito.when(invoiceDao.findLastInvoicedSentTimeByApplication(Mockito.anyCollection()))
              .thenReturn(Collections.emptyMap());
          Mockito.when(chargeBasisService.findModificationTimesByIds(Mockito.anyCollection()))
              .thenReturn(Collections.emptyMap());
        });

        it("Finds pending invoices", () -> {
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
        it("Returns invoices even if sum is zero", () -> {
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
          assertEquals(pendingInvoices.size(), foundPendingInvoices.size());
          Invoice foundInvoice = foundPendingInvoices.get(0);
          assertEquals(invoiceRecipient.getName(), foundInvoice.getInvoiceRecipient().getName());
          assertEquals(invoiceRecipient.getType(), foundInvoice.getInvoiceRecipient().getType());
        });
      });

      describe("Invoice change detection", () -> {
        final int APPLICATION_ID = 2;
        final int INVOICE_RECIPIENT_ID = 33;
        final int CHARGE_BASIS_ID = 1;

        beforeEach(() -> {
          final InvoiceRecipient invoiceRecipient = new InvoiceRecipient(new Customer());
          Mockito.when(invoiceRecipientDao.findById(INVOICE_RECIPIENT_ID)).thenReturn(Optional.of(invoiceRecipient));
        });

        it("Marks invoice as changed when application has not been invoiced before", () -> {
          Mockito.when(invoiceDao.findPending())
              .thenReturn(Collections.singletonList(pendingInvoice(APPLICATION_ID, INVOICE_RECIPIENT_ID, CHARGE_BASIS_ID)));
          Mockito.when(invoiceDao.findLastInvoicedSentTimeByApplication(Mockito.anyCollection()))
              .thenReturn(Collections.emptyMap());
          Mockito.when(chargeBasisService.findModificationTimesByIds(Mockito.anyCollection()))
              .thenReturn(Collections.emptyMap());
          Invoice found = invoiceService.findPending().get(0);
          assertTrue(found.isInvoiceChanged());
        });

        it("Marks invoice as unchanged when re-invoiced without change", () -> {
          ZonedDateTime sentTime = ZonedDateTime.now().minusDays(10);
          Mockito.when(invoiceDao.findPending())
              .thenReturn(Collections.singletonList(pendingInvoice(APPLICATION_ID, INVOICE_RECIPIENT_ID, CHARGE_BASIS_ID)));
          Mockito.when(invoiceDao.findLastInvoicedSentTimeByApplication(Mockito.anyCollection()))
              .thenReturn(Collections.singletonMap(APPLICATION_ID, sentTime));
          Mockito.when(chargeBasisService.findModificationTimesByIds(Mockito.anyCollection()))
              .thenReturn(Collections.singletonMap(CHARGE_BASIS_ID, sentTime.minusDays(1)));
          Invoice found = invoiceService.findPending().get(0);
          assertFalse(found.isInvoiceChanged());
        });

        it("Marks invoice as changed when charge basis entry modified after last invoicing", () -> {
          ZonedDateTime sentTime = ZonedDateTime.now().minusDays(10);
          Mockito.when(invoiceDao.findPending())
              .thenReturn(Collections.singletonList(pendingInvoice(APPLICATION_ID, INVOICE_RECIPIENT_ID, CHARGE_BASIS_ID)));
          Mockito.when(invoiceDao.findLastInvoicedSentTimeByApplication(Mockito.anyCollection()))
              .thenReturn(Collections.singletonMap(APPLICATION_ID, sentTime));
          Mockito.when(chargeBasisService.findModificationTimesByIds(Mockito.anyCollection()))
              .thenReturn(Collections.singletonMap(CHARGE_BASIS_ID, sentTime.plusDays(1)));
          Invoice found = invoiceService.findPending().get(0);
          assertTrue(found.isInvoiceChanged());
        });

        it("Marks invoice as unchanged when no charge basis entry modification time is available", () -> {
          ZonedDateTime sentTime = ZonedDateTime.now().minusDays(10);
          Mockito.when(invoiceDao.findPending())
              .thenReturn(Collections.singletonList(pendingInvoice(APPLICATION_ID, INVOICE_RECIPIENT_ID, CHARGE_BASIS_ID)));
          Mockito.when(invoiceDao.findLastInvoicedSentTimeByApplication(Mockito.anyCollection()))
              .thenReturn(Collections.singletonMap(APPLICATION_ID, sentTime));
          Mockito.when(chargeBasisService.findModificationTimesByIds(Mockito.anyCollection()))
              .thenReturn(Collections.emptyMap());
          Invoice found = invoiceService.findPending().get(0);
          assertFalse(found.isInvoiceChanged());
        });
      });

    });
  }
}
