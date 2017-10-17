package fi.hel.allu.sap.model;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.model.domain.*;
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

            final LineItem lineItem = AlluMapper.mapToLineItem(invoiceRow);

            it("Has the proper net price", () -> {
              final int netPrice = (int) (Double.parseDouble(lineItem.getNetPrice()) * 100);
              assertEquals(invoiceRow.getNetPrice(), netPrice);
            });

            it("Has the proper line text", () -> {
              assertEquals(invoiceRow.getText(), lineItem.getLineText1());
            });

            it("Has the right quantity", () -> {
              final double quantity = Double.parseDouble(lineItem.getQuantity());
              assertEquals(invoiceRow.getQuantity(), quantity, 0.000001);
            });

            it("Has the pre-set order item number", () -> {
              assertEquals("2831300000", lineItem.getOrderItemNumber());
            });
          });
        });

        describe("A single bill", () -> {
          final List<InvoiceRow> invoiceRows = dummyInvoiceRows();
          final Application application = dummyApplication();

          describe("Mapped to SAP SalesOrder", () -> {
            final SalesOrder salesOrder = AlluMapper.mapToSalesOrder(application, invoiceRows);

            it("All lines are in", () -> {
              assertEquals(invoiceRows.size(), salesOrder.getLineItems().size());
            });

            it("The name matches", () -> {
              assertEquals(application.getName(), salesOrder.getBillTextL1());
            });

            describe("Customer was mapped properly", () -> {
              final Customer applicationCustomer = application.getCustomersWithContacts().stream()
                  .filter(cwc -> cwc.getRoleType() == CustomerRoleType.APPLICANT).map(cwc -> cwc.getCustomer())
                  .findFirst().orElseThrow(() -> new AssertionError("Application didn't have customer"));

              it("Customer name matches", () -> {
                assertEquals(applicationCustomer.getName(), salesOrder.getOrderParty().getInfoName1());
              });

              it("Customer street matches", () -> {
                assertEquals(applicationCustomer.getPostalAddress().getStreetAddress(),
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
    application.setCustomersWithContacts(
        Collections.singletonList(new CustomerWithContacts(CustomerRoleType.APPLICANT, dummyCustomer(), null)));
    return application;
  }

  private Customer dummyCustomer() {
    final Customer customer = new Customer();
    customer.setName("Dummy C. Ustomer");
    customer.setPostalAddress(new PostalAddress("DummyStreet 12 A", "01230", "Dumville"));
    return customer;
  }
}
