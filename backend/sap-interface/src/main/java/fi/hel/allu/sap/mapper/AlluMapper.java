package fi.hel.allu.sap.mapper;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.model.domain.InvoiceUnit;
import fi.hel.allu.sap.model.LineItem;
import fi.hel.allu.sap.model.OrderParty;
import fi.hel.allu.sap.model.SalesOrder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A mapper class for Allu model -> SAP class mapping
 */
public class AlluMapper {

  // Allu-specific constant values for various SAP fields:
  private static final String ALLU_ORDER_ITEM_NUMBER = "2831300000";
  private static final String ALLU_SENDER_ID = "ID292";
  private static final String ALLU_ORDER_TYPE = "ZYHD";
  private static final String ALLU_SALES_ORG = "2800";
  private static final String ALLU_DISTRIBUTION_CHANNEL = "10";
  private static final String ALLU_DIVISION = "10";
  private static final String ALLU_SALES_OFFICE = "2805";
  private static final String ALLU_PAYMENT_TERM = "N143";

  public static SalesOrder mapToSAP(Application application, List<InvoiceRow> invoiceRows) {
    SalesOrder salesOrder = new SalesOrder();
    salesOrder.setBillTextL1(application.getName());
    salesOrder.setDistributionChannel(ALLU_DISTRIBUTION_CHANNEL);
    salesOrder.setDivision(ALLU_DIVISION);
    salesOrder.setLineItems(invoiceRows.stream().map(row -> mapToSAP(row)).collect(Collectors.toList()));
    salesOrder.setOrderParty(null); // TODO
    salesOrder.setOrderType(ALLU_ORDER_TYPE);
    salesOrder.setPaymentTerm(ALLU_PAYMENT_TERM);
    salesOrder.setPoNumber(application.getApplicationId());
    salesOrder.setReferenceText(null); // TODO: probably not used
    salesOrder.setSalesOffice(ALLU_SALES_OFFICE);
    salesOrder.setSalesOrg(ALLU_SALES_ORG);
    salesOrder.setSenderId(ALLU_SENDER_ID);
    return salesOrder;
  }

  public static LineItem mapToSAP(InvoiceRow invoiceRow) {
    LineItem lineItem = new LineItem();
    lineItem.setLineText1(invoiceRow.getRowText());
    lineItem.setMaterial("27100000"); // TODO: from application type
    lineItem.setNetPrice(String.format("%.02f", (double) invoiceRow.getNetPrice() / 100));
    lineItem.setOrderItemNumber(ALLU_ORDER_ITEM_NUMBER);
    lineItem.setQuantity(String.format("%.02f", invoiceRow.getQuantity()));
    lineItem.setUnit(mapToSapUnit(invoiceRow.getUnit()));
    return lineItem;
  }

  public static OrderParty mapToSap(Customer customer) {
    OrderParty orderParty = new OrderParty();
    orderParty.setSapCustomerId(null); // TODO: Sap ID for customer!
    return orderParty;
  }

  private static String mapToSapUnit(InvoiceUnit unit) {
    switch (unit) {
    case DAY:
      return "PV";
    case HOUR:
      return "T";
    case MONTH:
      return "KK";
    case PIECE:
      return "KPL";
    case SQUARE_METER:
      return "M2";
    case WEEK:
      return "VK";
    case YEAR:
      return "V";
    default:
      return "?";
    }
  }
}
