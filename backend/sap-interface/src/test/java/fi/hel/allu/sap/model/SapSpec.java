package fi.hel.allu.sap.model;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.model.domain.InvoiceUnit;
import fi.hel.allu.sap.mapper.AlluMapper;

import org.junit.runner.RunWith;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Spectrum.class)
public class SapSpec {

  {
    describe("SAP model", () -> {

      describe("Marshalling test", () -> {
        final JAXBContext jc = JAXBContext.newInstance("fi.hel.allu.sap.model");
        final Marshaller m = jc.createMarshaller();
        beforeEach(() -> {
          m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        });

        final SalesOrderContainer container = new SalesOrderContainer();

        beforeEach(() -> {
          final SalesOrder order = new SalesOrder();
          order.setBillTextL1("Bill of Money-fest");
          order.setSalesOffice("SeilsOffis");
          order.setLineItems(new ArrayList<>());
          order.getLineItems().add(dummyLineItem("LainTekst", "Mäteriö", "1234,00", "junit"));
          order.getLineItems().add(dummyLineItem("Änatö LainTekst", "Änatö Mäteriö", "2345,00", "piis"));
          order.setOrderParty(new OrderParty());
          order.getOrderParty().setSapCustomerId("KastomöAiDii");
          container.setSalesOrders(Collections.singletonList(order));
        });

        it("marshalling doesn't throw", () -> {
          m.marshal(container, System.out);
          assertTrue(true);
        });
      });

      describe("Mapper test", () -> {

        describe("Single InvoiceRow", () -> {
          final Supplier<InvoiceRow> invoiceRow = let(
              () -> dummyInvoiceRow(1234, 12.34, "Row text", InvoiceUnit.MONTH));

          describe("Mapped to SAP LineItem", () -> {

            final Supplier<LineItem> lineItem = let(() -> AlluMapper.mapToSAP(invoiceRow.get()));

            it("Has the proper net price", () -> {
              final int netPrice = (int) (Double.parseDouble(lineItem.get().getNetPrice()) * 100);
              assertEquals(invoiceRow.get().getNetPrice(), netPrice);
            });

            it("Has the proper line text", () -> {
              assertEquals(invoiceRow.get().getRowText(), lineItem.get().getLineText1());
            });

            it("Has the right quantity", () -> {
              final double quantity = Double.parseDouble(lineItem.get().getQuantity());
              final double diff = quantity - invoiceRow.get().getQuantity();
              assertTrue(Math.abs(diff) < 0.000001);
            });

            it("Has the pre-set order item number", () -> {
              assertEquals("2831300000", lineItem.get().getOrderItemNumber());
            });
          });
        });

        describe("A single bill", () -> {
          final Supplier<List<InvoiceRow>> invoiceRows = let(() -> dummyInvoiceRows());
          final Supplier<Application> application = let(() -> dummyApplication());

          describe("Mapped to SAP SalesOrder", () -> {
            final Supplier<SalesOrder> salesOrder = let(
                () -> AlluMapper.mapToSAP(application.get(), invoiceRows.get()));

            it("All lines are in", () -> {
              assertEquals(salesOrder.get().getLineItems().size(), invoiceRows.get().size());
            });

            it("The name matches", () -> {
              assertEquals(salesOrder.get().getBillTextL1(), application.get().getName());
            });
          });
        });

      });
    });
  }

  private LineItem dummyLineItem(String lineText, String material, String netPrice, String unit) {
    final LineItem lineItem = new LineItem();
    lineItem.setLineText1(lineText);
    lineItem.setMaterial(material);
    lineItem.setNetPrice(netPrice);
    lineItem.setUnit(unit);
    return lineItem;
  }

  private List<InvoiceRow> dummyInvoiceRows() {
    return Arrays.asList(dummyInvoiceRow(1234, 12.34, "Row text", InvoiceUnit.MONTH),
        dummyInvoiceRow(2345, 1, "Other row text", InvoiceUnit.SQUARE_METER),
        dummyInvoiceRow(54321, 13.12, "Yet another row text", InvoiceUnit.HOUR));
  }

  private InvoiceRow dummyInvoiceRow(int netPrice, double quantity, String rowText, InvoiceUnit invoiceUnit) {
    final InvoiceRow invoiceRow = new InvoiceRow();
    invoiceRow.setNetPrice(netPrice);
    invoiceRow.setQuantity(quantity);
    invoiceRow.setRowText(rowText);
    invoiceRow.setUnit(invoiceUnit);
    return invoiceRow;
  }

  private Application dummyApplication() {
    final Application application = new Application();
    application.setName("Dummy Application");
    return application;
  }

}