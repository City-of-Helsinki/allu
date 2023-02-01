package fi.hel.allu.model.dao;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Variable;
import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.testUtils.SpeccyTestBase;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.*;

@RunWith(Spectrum.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
public class InvoiceDaoSpec extends SpeccyTestBase {

  private Invoice TEST_INVOICE;

  @Autowired
  private ApplicationDao applicationDao;

  @Autowired
  private InvoiceDao invoiceDao;

  @Autowired
  private InvoiceRecipientDao invoiceRecipientDao;

  {
    describe("InvoiceDao", () -> {
      beforeEach(() -> {
        testCommon.deleteAllData();
        TEST_INVOICE = testInvoice();
      });

      context("When DB is empty", () -> {
        it("find should return empty optional", () -> {
          List<Invoice> result = invoiceDao.findInvoices(Collections.singletonList(99));
          assertTrue(result.isEmpty());
        });

        it("insert should throw error", () -> {
          assertThrows(RuntimeException.class).when(() -> invoiceDao.insert(123, TEST_INVOICE));
        });

        it("delete should throw error", () -> {
          assertThrows(NoSuchEntityException.class).when(() -> invoiceDao.deleteSingleInvoice(99));
        });

        it("update should throw error", () -> {
          assertThrows(NoSuchEntityException.class).when(() -> invoiceDao.update(99, TEST_INVOICE));
        });

        it("findByApplication returns empty list", () -> {
          assertTrue(invoiceDao.findByApplication(123).isEmpty());
        });

        it("deleteByApplication doesn't throw", () -> {
          invoiceDao.deleteOpenInvoicesByApplication(123);
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
            List<Invoice> invoiceOpt = invoiceDao.findInvoices(Collections.singletonList(invoiceId.get()));
            assertFalse(invoiceOpt.isEmpty());
            compareInvoices(TEST_INVOICE, invoiceOpt.get(0));
          });

          it("can update the invoice", () -> {
            final Invoice newInvoice = otherInvoice();
            invoiceDao.update(invoiceId.get(), newInvoice);
            Invoice readBack = invoiceDao.findInvoices(Collections.singletonList(invoiceId.get())).get(0);
            compareInvoices(newInvoice, readBack);
          });

          it("can delete the invoice", () -> {
            invoiceDao.deleteSingleInvoice(invoiceId.get());
            assertTrue( invoiceDao.findInvoices(Collections.singletonList(invoiceId.get())).isEmpty());
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
            invoiceDao.deleteOpenInvoicesByApplication(appId.get());
            assertTrue(invoiceDao.findInvoices(Collections.singletonList(invoiceId.get())).isEmpty());
            assertTrue(invoiceDao.findInvoices(Collections.singletonList(otherId)).isEmpty());
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
        context("When application is replaced find pending invoices", () -> {
          beforeEach(() -> {
            Invoice tmp = testInvoice();
            tmp.setInvoicableTime(ZonedDateTime.now().minusDays(2));
            tmp.setInvoiced(false);
            invoiceDao.insert(appId.get(), tmp);
          });
          it("should not return invoice of replaced application if replacing application not cancelled", () -> {
            Application app = testCommon.dummyBridgeBannerApplication("replacing_app", "replacing_app");
            app.setReplacesApplicationId(appId.get());
            app.setStatus(StatusType.DECISION);
            applicationDao.insert(app);
            final List<Invoice> invoices = invoiceDao.findPending();
            assertTrue(invoices.isEmpty());
          });
          it("should return invoice of replaced application if replacing application cancelled", () -> {
            Application app = testCommon.dummyBridgeBannerApplication("replacing_app", "replacing_app");
            app.setReplacesApplicationId(appId.get());
            app.setStatus(StatusType.CANCELLED);
            applicationDao.insert(app);
            final List<Invoice> invoices = invoiceDao.findPending();
            assertEquals(1, invoices.size());
          });
          it("should return not invoice of application if status is replaced", () -> {
            applicationDao.updateStatus(appId.get(), StatusType.REPLACED);
            Application app = testCommon.dummyBridgeBannerApplication("replacing_app", "replacing_app");
            app.setReplacesApplicationId(appId.get());
            app.setStatus(StatusType.CANCELLED);
            applicationDao.insert(app);
            final List<Invoice> invoices = invoiceDao.findPending();
            assertEquals(0, invoices.size());
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

  private Invoice testInvoice() {
    int recipientId = invoiceRecipientDao.insert(invoiceRecipient());
    final Invoice invoice = new Invoice(null, null, ZonedDateTime.parse("2017-12-15T08:00:00+02:00[Europe/Helsinki]"), false,
        false, null, recipientId, null);
    invoice.setRows(Arrays.asList(
        new InvoiceRow(null, ChargeBasisUnit.PIECE, 3.141, "One Pie", new String[] { "A pie", "With Apples" }, 12300, -99999),
        new InvoiceRow(null, ChargeBasisUnit.DAY, 14, "A Forthnight", new String[] { "Two weeks", "Fourteen nights" }, 300,
            4200)));
    return invoice;
  }

  private Invoice otherInvoice() {
    int recipientId = invoiceRecipientDao.insert(invoiceRecipient());
    final Invoice invoice = new Invoice(null, null, ZonedDateTime.parse("2017-12-07T08:00:00+02:00[Europe/Helsinki]"), false,
        false, null, recipientId, null);
    invoice.setRows(Arrays.asList(
        new InvoiceRow(null, ChargeBasisUnit.MONTH, 12, "A Whole year", new String[] { "A calendar year", "About 365 days" },
            12000, 144000),
        new InvoiceRow(null, ChargeBasisUnit.WEEK, 2, "Two weeks", new String[] { "Forthnight", "Plenty of hours" }, 1230,
            2460),
        new InvoiceRow(null, ChargeBasisUnit.DAY, 14, "A Forthnight", new String[] { "Unit of time", "14 days" }, 300,
            4200)));
    return invoice;
  }

  private InvoiceRecipient invoiceRecipient() {
    final Customer customer = new Customer();
    customer.setName("The Company");
    customer.setType(CustomerType.COMPANY);
    final InvoiceRecipient recipient = new InvoiceRecipient(customer);
    return recipient;
  }
}