package fi.hel.allu.model.dao;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Variable;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.ChargeBasisUnit;
import fi.hel.allu.model.domain.Invoice;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.model.testUtils.SpeccyTestBase;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
