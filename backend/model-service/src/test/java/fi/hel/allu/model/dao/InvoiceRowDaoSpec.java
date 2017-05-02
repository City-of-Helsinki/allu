package fi.hel.allu.model.dao;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.model.domain.InvoiceUnit;
import fi.hel.allu.model.testUtils.SpeccyTestBase;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Spectrum.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
public class InvoiceRowDaoSpec extends SpeccyTestBase {

  @Autowired
  private InvoiceRowDao invoiceRowDao;

  {
    describe("InvoiceDAO spec", () -> {
      beforeEach(() -> {
        testCommon.deleteAllData();
      });

      it("Should get empty rows for empty DB", () -> {
        List<InvoiceRow> rows = invoiceRowDao.getInvoiceRows(123);
        assertEquals(0, rows.size());
      });

      it("Setting rows for nonexistent application should throw", () -> {
        try {
          invoiceRowDao.setInvoiceRows(123, generateTestRows(12, "Fail"), true);
          fail("Exception not thrown!");
        } catch (RuntimeException e) {
        }
      });

      describe("When applications exist", () -> {
        final Supplier<Integer> appId1 = let(() -> testCommon.insertApplication("Hakemus", "Käsittelijä"));
        final Supplier<Integer> appId2 = let(() -> testCommon.insertApplication("Ansökning", "Handläggare"));

        it("Setting shouldn't throw",
            () -> invoiceRowDao.setInvoiceRows(appId1.get(), generateTestRows(15, "test"), true));

        it("Setting empty list should pass",
            () -> invoiceRowDao.setInvoiceRows(appId2.get(), Collections.emptyList(), false));

        describe("Set two rows for two separate applications", () -> {
          final int NUM_1ST_ROWS = 12;
          final int NUM_2ND_ROWS = 23;
          beforeEach(() -> {
            invoiceRowDao.setInvoiceRows(appId1.get(), generateTestRows(NUM_1ST_ROWS, "First rows"), true);
            invoiceRowDao.setInvoiceRows(appId2.get(), generateTestRows(NUM_2ND_ROWS, "Second rows"), true);
          });

          describe("Read rows back and check", () -> {

            final Supplier<List<InvoiceRow>> first = let(() -> invoiceRowDao.getInvoiceRows(appId1.get()));
            final Supplier<List<InvoiceRow>> second = let(() -> invoiceRowDao.getInvoiceRows(appId2.get()));

            it("Returns right number of rows", () -> {
              assertEquals(NUM_1ST_ROWS, first.get().size());
              assertEquals(NUM_2ND_ROWS, second.get().size());
            });

            it("Returns the right rows", () -> {
              checkTestRows(first.get(), "First rows");
              checkTestRows(second.get(), "Second rows");
            });
          });
        });

        describe("Set both manual and calculated rows", () -> {
          final int NUM_MANUAL_ROWS = 12;
          final int NUM_CALCULATED_ROWS = 23;
          final String MANUAL_ROW_PREFIX = "Manual rows";
          final String CALCULATED_ROW_PREFIX = "Calculated rows";

          beforeEach(() -> {
            invoiceRowDao.setInvoiceRows(appId1.get(), generateTestRows(NUM_MANUAL_ROWS, MANUAL_ROW_PREFIX), true);
            invoiceRowDao.setInvoiceRows(appId1.get(), generateTestRows(NUM_CALCULATED_ROWS, CALCULATED_ROW_PREFIX),
                false);
          });

          it("Should return both manual and calculated rows", () -> {
            final List<InvoiceRow> rows = invoiceRowDao.getInvoiceRows(appId1.get());
            assertEquals(NUM_MANUAL_ROWS+NUM_CALCULATED_ROWS, rows.size());
            checkTestRows(rows.stream().filter(r -> r.getManuallySet() == true).collect(Collectors.toList()),
                MANUAL_ROW_PREFIX);
            checkTestRows(rows.stream().filter(r -> r.getManuallySet() == false).collect(Collectors.toList()),
                CALCULATED_ROW_PREFIX);
          });

          it("Clearing manual rows should leave the calculated in place", () -> {
            invoiceRowDao.setInvoiceRows(appId1.get(), Collections.emptyList(), true);
            final List<InvoiceRow> rows = invoiceRowDao.getInvoiceRows(appId1.get());
            assertEquals(NUM_CALCULATED_ROWS, rows.size());
            checkTestRows(rows.stream().filter(r -> r.getManuallySet() == false).collect(Collectors.toList()),
                CALCULATED_ROW_PREFIX);
          });

          it("Clearing calculcated rows should leave the manual in place", () -> {
            invoiceRowDao.setInvoiceRows(appId1.get(), Collections.emptyList(), false);
            final List<InvoiceRow> rows = invoiceRowDao.getInvoiceRows(appId1.get());
            assertEquals(NUM_MANUAL_ROWS, rows.size());
            checkTestRows(rows.stream().filter(r -> r.getManuallySet() == false).collect(Collectors.toList()),
                MANUAL_ROW_PREFIX);
          });

          it("Should calculate the correct total price for the application", () -> {
            final List<InvoiceRow> rows = invoiceRowDao.getInvoiceRows(appId1.get());
            final int expectedPrice = rows.stream().mapToInt(r -> r.getNetPrice()).sum();
            assertEquals(expectedPrice, invoiceRowDao.getTotalPrice(appId1.get()));
          });
        });
      });
    });
  }

  private List<InvoiceRow> generateTestRows(int numRows, String text) {
    List<InvoiceRow> rows = new ArrayList<>();
    for (int r = 1; r <= numRows; ++r) {
      InvoiceRow row = new InvoiceRow();
      row.setRowText(String.format("%s (%d)", text, r));
      row.setUnitPrice(r * 100);
      row.setNetPrice(r * 200);
      row.setUnit(InvoiceUnit.SQUARE_METER);
      row.setQuantity(r * 20.0);
      rows.add(row);
    }
    return rows;
  }

  private void checkTestRows(final Collection<InvoiceRow> rows, String text) {
    int r = 1;
    for (InvoiceRow row : rows) {
      assertTrue(row.getRowText().startsWith(text));
      assertEquals(r * 100, row.getUnitPrice());
      assertEquals(r * 200, row.getNetPrice());
      assertEquals(InvoiceUnit.SQUARE_METER, row.getUnit());
      assertTrue(Math.abs(r * 20.0 - row.getQuantity()) < 0.00001);
      ++r;
    }
  }

}
