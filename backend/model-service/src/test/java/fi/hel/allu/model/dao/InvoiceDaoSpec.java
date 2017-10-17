package fi.hel.allu.model.dao;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Variable;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Invoice;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.model.testUtils.SpeccyTestBase;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.*;

@RunWith(Spectrum.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
public class InvoiceDaoSpec extends SpeccyTestBase {

  final static Invoice TEST_INVOICE = testInvoice();

  @Autowired
  private InvoiceDao invoiceDao;
  {
    describe("InvoiceDao", () -> {
      beforeEach(() -> {
        testCommon.deleteAllData();
      });

      context("When DB is empty", () -> {
        it("find should return empty optional", () -> {
          Optional<Invoice> result = invoiceDao.find(99);
          assertFalse(result.isPresent());
        });

        it("insert should throw error", () -> {
          assertThrows(RuntimeException.class).when(() -> invoiceDao.insert(123, TEST_INVOICE));
        });

        it("delete should throw error", () -> {
          assertThrows(NoSuchEntityException.class).when(() -> invoiceDao.delete(99));
        });

        it("update should throw error", () -> {
          assertThrows(NoSuchEntityException.class).when(() -> invoiceDao.update(99, TEST_INVOICE));
        });

        it("findByApplication returns empty list", () -> {
          assertTrue(invoiceDao.findByApplication(123).isEmpty());
        });

        it("deleteByApplication doesn't throw", () -> {
          invoiceDao.deleteByApplication(123);
        });

        it("findPending returns empty list", () -> {
          assertTrue(invoiceDao.findPending().isEmpty());
        });

        it("markAsSent doesn't throw", () -> {
          invoiceDao.markSent(Arrays.asList(1, 2, 44, 9010));
        });
      });

      context("When application exists", () -> {
        final Variable<Integer> appId = new Variable<>();
        beforeEach(() -> appId.set(testCommon.insertApplication("Hakemus", "Käsittelijä")));

        it("should insert successfully", () -> {
          invoiceDao.insert(appId.get(), TEST_INVOICE);
        });

        context("When invoice has been inserted", () -> {
          final Variable<Integer> invoiceId = new Variable<>();
          beforeEach(() -> invoiceId.set(invoiceDao.insert(appId.get(), TEST_INVOICE)));

          it("can find the invoice", () -> {
            Optional<Invoice> invoiceOpt = invoiceDao.find(invoiceId.get());
            assertTrue(invoiceOpt.isPresent());
            compareInvoices(TEST_INVOICE, invoiceOpt.get());
          });

          it("can update the invoice", () -> {
            final Invoice newInvoice = otherInvoice();
            invoiceDao.update(invoiceId.get(), newInvoice);
            Invoice readBack = invoiceDao.find(invoiceId.get()).get();
            compareInvoices(newInvoice, readBack);
          });

          it("can delete the invoice", () -> {
            invoiceDao.delete(invoiceId.get());
            assertFalse(invoiceDao.find(invoiceId.get()).isPresent());
          });

          it("can find all inserted with findByApplication", () -> {
            int otherId = invoiceDao.insert(appId.get(), otherInvoice());
            List<Invoice> result = invoiceDao.findByApplication(appId.get());
            assertEquals(2, result.size());
            assertTrue(result.stream().anyMatch(i -> i.getId().equals(invoiceId.get())));
            assertTrue(result.stream().anyMatch(i -> i.getId().equals(otherId)));
          });

          it("can delete all inserted with deleteByApplication", () -> {
            int otherId = invoiceDao.insert(appId.get(), otherInvoice());
            invoiceDao.deleteByApplication(appId.get());
            assertFalse(invoiceDao.find(invoiceId.get()).isPresent());
            assertFalse(invoiceDao.find(otherId).isPresent());
          });

        });

        context("When two billable invoices and two non-billables exist", () -> {
          beforeEach(() -> {
            Invoice tmp = testInvoice();
            tmp.setInvoicableTime(ZonedDateTime.now().minusDays(2));
            tmp.setInvoiced(false);
            invoiceDao.insert(appId.get(), tmp);
            tmp.setInvoicableTime(ZonedDateTime.now().minusDays(1));
            invoiceDao.insert(appId.get(), tmp);
            tmp.setInvoiced(true);
            invoiceDao.insert(appId.get(), tmp);
            tmp.setInvoicableTime(ZonedDateTime.now().plusDays(2));
            tmp.setInvoiced(false);
            invoiceDao.insert(appId.get(), tmp);
          });

          it("findPending returns two invoices, both are invoicable", () -> {
            final List<Invoice> invoices = invoiceDao.findPending();
            assertEquals(2, invoices.size());
            invoices.forEach(
                i -> assertTrue(i.getInvoicableTime().isBefore(ZonedDateTime.now()) && i.isInvoiced() == false));
          });

          it("One of pending invoices can be marked as sent", () -> {
            final Integer invoiceId = invoiceDao.findPending().get(0).getId();
            invoiceDao.markSent(Collections.singletonList(invoiceId));
            final List<Invoice> invoices = invoiceDao.findPending();
            assertEquals(1, invoices.size());
            assertNotEquals(invoiceId, invoices.get(0).getId());
          });
        });
      });
    });
  }

  private void compareInvoices(Invoice expected, Invoice actual) {
    assertEquals(expected.getInvoicableTime().toInstant(), actual.getInvoicableTime().toInstant());
    assertEquals(expected.isInvoiced(), actual.isInvoiced());
    assertEquals(expected.getRows().size(), actual.getRows().size());
  }

  private static Invoice testInvoice() {
    Invoice invoice = new Invoice(null, null, ZonedDateTime.parse("2017-12-15T08:00:00+02:00[Europe/Helsinki]"), false,
        null);
    invoice.setRows(Arrays.asList(
        new InvoiceRow(ChargeBasisUnit.PIECE, 3.141, "One Pie", 12300, -99999),
        new InvoiceRow(ChargeBasisUnit.DAY, 14, "A Forthnight", 300, 4200)));
    return invoice;
  }

  private static Invoice otherInvoice() {
    Invoice invoice = new Invoice(null, null, ZonedDateTime.parse("2017-12-07T08:00:00+02:00[Europe/Helsinki]"), true,
        null);
    invoice.setRows(Arrays.asList(
        new InvoiceRow(ChargeBasisUnit.MONTH, 12, "A Whole year", 12000, 144000),
        new InvoiceRow(ChargeBasisUnit.WEEK, 2, "Two weeks", 1230, 2460),
        new InvoiceRow(ChargeBasisUnit.DAY, 14, "A Forthnight", 300, 4200)));
    return invoice;
  }
}
