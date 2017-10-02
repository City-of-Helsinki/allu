package fi.hel.allu.sap.mapper;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.ChargeBasisUnit;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.sap.model.LineItem;
import fi.hel.allu.sap.model.OrderParty;
import fi.hel.allu.sap.model.SalesOrder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A mapper class for Allu model -> SAP class mapping
 */
public class AlluMapper {

  /*
   * Allu-specific constant values for various SAP fields:
   */
  private static final String ALLU_ORDER_ITEM_NUMBER = "2831300000";
  private static final String ALLU_SENDER_ID = "ID341";
  // Civil law orders ("Yksityisoikeudellinen tilauslaji")
  private static final String ALLU_ORDER_TYPE_CIVIL = "ZTY1";
  // Public law orders ("Julkisoikeudellinen tilauslaji")
  private static final String ALLU_ORDER_TYPE_PUBLIC = "ZTJ1";
  private static final String ALLU_SALES_ORG = "2800";
  private static final String ALLU_DISTRIBUTION_CHANNEL = "10";
  private static final String ALLU_DIVISION = "10";
  // Sales office for civil law matters
  private static final String ALLU_SALES_OFFICE_CIVIL = "2805";
  // Sales office code for public law matters
  private static final String ALLU_SALES_OFFICE_PUBLIC = "2808";
  private static final String ALLU_PAYMENT_TERM = "N143";

  /**
   * Map an application and its charge basis entries to SAP SalesOrder
   *
   * @param application
   * @param chargeBasisEntries
   * @return
   */
  public static SalesOrder mapToSalesOrder(Application application, List<ChargeBasisEntry> chargeBasisEntries) {
    SalesOrder salesOrder = new SalesOrder();
    salesOrder.setBillTextL1(application.getName());
    salesOrder.setDistributionChannel(ALLU_DISTRIBUTION_CHANNEL);
    salesOrder.setDivision(ALLU_DIVISION);
    salesOrder.setLineItems(
        chargeBasisEntries.stream().map(entry -> mapToLineItem(entry)).collect(Collectors.toList()));
    Customer orderer = Optional.ofNullable(application.getCustomersWithContacts())
        .orElseThrow(() -> new IllegalArgumentException("Application's customersWithContacts is null")).stream()
        .filter(cwc -> cwc.getRoleType() == CustomerRoleType.APPLICANT).map(cwc -> cwc.getCustomer()).findAny()
        .orElseThrow(() -> new IllegalArgumentException("Application doesn't have applicant"));
    salesOrder.setOrderParty(mapToOrderParty(orderer));
    salesOrder.setOrderType(mapToOrderType(application.getType()));
    salesOrder.setPaymentTerm(ALLU_PAYMENT_TERM);
    salesOrder.setPoNumber(application.getApplicationId());
    salesOrder.setReferenceText(application.getApplicationId());
    salesOrder.setSalesOffice(mapToSalesOffice(application.getType()));
    salesOrder.setSalesOrg(ALLU_SALES_ORG);
    salesOrder.setSenderId(ALLU_SENDER_ID);
    return salesOrder;
  }

  /**
   * Map Allu ChargeBasisEntry into a SAP LineItem
   *
   * @param chargeBasisEntry
   * @return
   */
  public static LineItem mapToLineItem(ChargeBasisEntry chargeBasisEntry) {
    LineItem lineItem = new LineItem();
    lineItem.setLineText1(chargeBasisEntry.getText());
    lineItem.setMaterial("27100000"); // TODO: from application type
    lineItem.setNetPrice(String.format("%.02f", (double) chargeBasisEntry.getNetPrice() / 100));
    lineItem.setOrderItemNumber(ALLU_ORDER_ITEM_NUMBER);
    lineItem.setQuantity(String.format("%.02f", chargeBasisEntry.getQuantity()));
    lineItem.setUnit(mapToSapUnit(chargeBasisEntry.getUnit()));
    return lineItem;
  }

  /**
   * Map an Allu Customer into a SAP OrderParty
   *
   * @param customer
   * @return
   */
  public static OrderParty mapToOrderParty(Customer customer) {
    OrderParty orderParty = new OrderParty();
    orderParty.setSapCustomerId("99999"); // TODO: Sap ID for customer!
    orderParty.setInfoName1(customer.getName());
    Optional.ofNullable(customer.getPostalAddress()).ifPresent(postalAddress -> {
      Optional.ofNullable(postalAddress.getStreetAddress()).ifPresent(sa -> orderParty.setInfoAddress1(sa));
      Optional.ofNullable(postalAddress.getPostalCode()).ifPresent(poc -> orderParty.setInfoPoCode(poc));
      Optional.ofNullable(postalAddress.getCity()).ifPresent(c -> orderParty.setInfoCity(c));
    });
    if (customer.getType() == CustomerType.PERSON) {
      orderParty.setInfoCustomerId(customer.getRegistryKey());
    } else {
      orderParty.setInfoCustomerYid(customer.getRegistryKey());
    }
    return orderParty;
  }

  /*
   * Map ApplicationType to proper sales offce
   */
  private static String mapToSalesOffice(ApplicationType type) {
    switch (type) {
      case AREA_RENTAL:
      case EXCAVATION_ANNOUNCEMENT:
        return ALLU_SALES_OFFICE_PUBLIC;
      default:
        return ALLU_SALES_OFFICE_CIVIL;
    }
  }

  /*
   * Map ApplicationType to proper order type
   */
  private static String mapToOrderType(ApplicationType type) {
    switch (type) {
      case AREA_RENTAL:
      case EXCAVATION_ANNOUNCEMENT:
        return ALLU_ORDER_TYPE_PUBLIC;
      default:
        return ALLU_ORDER_TYPE_CIVIL;
    }
  }

  private static String mapToSapUnit(ChargeBasisUnit unit) {
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
