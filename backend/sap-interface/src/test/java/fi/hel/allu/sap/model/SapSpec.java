package fi.hel.allu.sap.model;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.sap.mapper.AlluMapper;
import fi.hel.allu.sap.marshaller.AlluMarshaller;

import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.greghaskins.spectrum.Spectrum.beforeEach;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.InvoiceRecipient;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Spectrum.class)
public class SapSpec {

  {
    describe("SAP model", () -> {

      describe("Marshalling test", () -> {
        final AlluMarshaller alluMarshaller = new AlluMarshaller();
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
          alluMarshaller.marshal(container, System.out);
          assertTrue(true);
        });
      });

      describe("Mapper test", () -> {

        describe("Single InvoiceRow", () -> {
          final InvoiceRow invoiceRow = dummyInvoiceRow(1234, 12.34, "Entry text",
              ChargeBasisUnit.MONTH);

          describe("Mapped to SAP LineItem", () -> {

            final String MATERIAL = "DUMMY_MATERIAL";
            final LineItem lineItem = AlluMapper.mapToLineItem(invoiceRow, MATERIAL);

            it("Has the proper unit price", () -> {
              final int unitPrice = (int) (Double.parseDouble(lineItem.getNetPrice()) * 100);
              assertEquals(invoiceRow.getUnitPrice(), unitPrice);
            });

            it("Has the proper line text", () -> {
              assertEquals(invoiceRow.getText(), lineItem.getLineText1());
            });

            it("Has the right quantity", () -> {
              final double quantity = Double.parseDouble(lineItem.getQuantity());
              assertEquals(invoiceRow.getQuantity(), quantity, 0.000001);
            });

            it("Has the pre-set project number", () -> {
              assertEquals("2830K002831300", lineItem.getWbsElement());
            });

            it("Has the requested material code", () -> {
              assertEquals(MATERIAL, lineItem.getMaterial());
            });
          });
        });

        describe("A single bill", () -> {
          final List<InvoiceRow> invoiceRows = dummyInvoiceRows();
          final Application application = dummyApplication();

          describe("Mapped to SAP SalesOrder", () -> {
            final SalesOrder salesOrder = AlluMapper.mapToSalesOrder(application, dummyInvoiceRecipient(), "sap123", invoiceRows);

            it("All lines are in", () -> {
              assertEquals(invoiceRows.size(), salesOrder.getLineItems().size());
            });

            it("The name matches", () -> {
              assertEquals(application.getName(), salesOrder.getBillTextL1());
            });

            describe("Customer was mapped properly", () -> {
              final InvoiceRecipient applicationCustomer = dummyInvoiceRecipient();

              it("Customer name matches", () -> {
                assertEquals(applicationCustomer.getName(), salesOrder.getOrderParty().getInfoName1());
              });

              it("Customer street matches", () -> {
                assertEquals(applicationCustomer.getStreetAddress(),
                    salesOrder.getOrderParty().getInfoAddress1());
              });

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
    return Arrays.asList(dummyInvoiceRow(1234, 12.34, "Entry text", ChargeBasisUnit.MONTH),
        dummyInvoiceRow(2345, 1, "Other entry text", ChargeBasisUnit.SQUARE_METER),
        dummyInvoiceRow(54321, 13.12, "Yet another entry text", ChargeBasisUnit.HOUR));
  }

  private InvoiceRow dummyInvoiceRow(int netPrice, double quantity, String text,
      ChargeBasisUnit chargeBasisUnit) {
    final InvoiceRow invoiceRow = new InvoiceRow();
    invoiceRow.setNetPrice(netPrice);
    invoiceRow.setQuantity(quantity);
    invoiceRow.setText(text);
    invoiceRow.setUnit(chargeBasisUnit);
    return invoiceRow;
  }

  private Application dummyApplication() {
    final Application application = new Application();
    application.setName("Dummy Application");
    application.setType(ApplicationType.EVENT);
    return application;
  }

  private InvoiceRecipient dummyInvoiceRecipient() {
    final Customer customer = new Customer();
    customer.setName("Dummy Company");
    customer.setType(CustomerType.COMPANY);
    InvoiceRecipient invoiceRecipient = new InvoiceRecipient(customer);
    invoiceRecipient.setStreetAddress("DummyStreet 12 A");
    invoiceRecipient.setPostalCode("01230");
    invoiceRecipient.setCity("Dumville");
    return invoiceRecipient;
  }
}
