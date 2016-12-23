package fi.hel.allu.model.dao;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.model.domain.InvoiceUnit;
import fi.hel.allu.model.testUtils.TestCommon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class InvoiceRowDaoTest {

  @Autowired
  private InvoiceRowDao invoiceRowDao;

  @Autowired
  TestCommon testCommon;

  @Before
  public void setup() throws Exception {
    testCommon.deleteAllData();
  }

  @Test
  public void testGetInvoiceRows() {
    // Empty db: should get empty rows for application
    List<InvoiceRow> rows = invoiceRowDao.getInvoiceRows(123);
    assertEquals(0, rows.size());
  }

  @Test
  public void testSetApplicationRows() {
    int appId1 = testCommon.insertApplication("Hakemus", "Käsittelijä");
    int appId2 = testCommon.insertApplication("Ansökning", "Handläggare");
    // Setting shouldn't throw
    invoiceRowDao.setInvoiceRows(appId1, generateTestRows(15, "test"));
    // Setting empty list should pass too:
    invoiceRowDao.setInvoiceRows(appId2, Collections.emptyList());
  }

  @Test
  public void testSetAndGet() {
    int appId1 = testCommon.insertApplication("Hakemus", "Käsittelijä");
    int appId2 = testCommon.insertApplication("Ansökning", "Handläggare");
    // Set rows for two separate applications and read them back
    invoiceRowDao.setInvoiceRows(appId1, generateTestRows(12, "First rows"));
    invoiceRowDao.setInvoiceRows(appId2, generateTestRows(23, "Second rows"));

    List<InvoiceRow> first = invoiceRowDao.getInvoiceRows(appId1);
    List<InvoiceRow> second = invoiceRowDao.getInvoiceRows(appId2);

    assertEquals(12, first.size());
    assertEquals(23, second.size());
    checkTestRows(first, "First rows");
    checkTestRows(second, "Second rows");
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

  private void checkTestRows(List<InvoiceRow> rows, String text) {
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
