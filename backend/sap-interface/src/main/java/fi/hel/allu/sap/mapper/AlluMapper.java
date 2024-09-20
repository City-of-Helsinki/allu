package fi.hel.allu.sap.mapper;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Event;
import fi.hel.allu.model.domain.InvoiceRecipient;
import fi.hel.allu.model.domain.InvoiceRow;
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

  /**
   * Bill text lines for area rentals and excavation announcements
   */
  private static final String[] BILL_TEXT_LINES = {
      "LASKUTUSPERUSTE: Laki kadun ja eräiden yleisten alueiden kunnossa- ja",
      "puhtaanapidosta."
   };

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
   * Map an application and its invoice rows to SAP SalesOrder
   *
   * @param application
   * @param invoiceRows
   * @return
   */
  public static SalesOrder mapToSalesOrder(Application application, InvoiceRecipient invoiceRecipient,
      String sapCustomerNumber, List<InvoiceRow> invoiceRows) {
    SalesOrder salesOrder = new SalesOrder();
    setBillTextLines(salesOrder, application);
    salesOrder.setDistributionChannel(ALLU_DISTRIBUTION_CHANNEL);
    salesOrder.setDivision(ALLU_DIVISION);
    final String sapMaterial = mapToSapMaterial(application);
    salesOrder.setLineItems(
        invoiceRows.stream().map(entry -> mapToLineItem(entry, sapMaterial)).collect(Collectors.toList()));
    salesOrder.setOrderParty(mapToOrderParty(invoiceRecipient, sapCustomerNumber));
    salesOrder.setOrderType(mapToOrderType(application.getType()));
    salesOrder.setPaymentTerm(ALLU_PAYMENT_TERM);
    salesOrder.setPoNumber(application.getCustomerReference());
    salesOrder.setReferenceText(application.getApplicationId());
    salesOrder.setSalesOffice(mapToSalesOffice(application.getType()));
    salesOrder.setSalesOrg(ALLU_SALES_ORG);
    salesOrder.setSenderId(ALLU_SENDER_ID);
    return salesOrder;
  }

  private static void setBillTextLines(SalesOrder salesOrder, Application application) {
    if (application.getType() == ApplicationType.AREA_RENTAL || application.getType() == ApplicationType.EXCAVATION_ANNOUNCEMENT) {
      salesOrder.setBillTextL1(BILL_TEXT_LINES[0]);
      salesOrder.setBillTextL2(BILL_TEXT_LINES[1]);
    } else {
      salesOrder.setBillTextL1(application.getName());
    }
  }

  /**
   * Map Allu InvoiceRow into a SAP LineItem
   *
   * @param invoiceRow
   * @return
   */
  public static LineItem mapToLineItem(InvoiceRow invoiceRow, String sapMaterial) {
    LineItem lineItem = new LineItem();
    lineItem.setLineText1(invoiceRow.getText());
    storeExplanation(lineItem, invoiceRow.getExplanation());
    // Note: SAP's "Net price" is actually unit price
    lineItem.setNetPrice(String.format("%.02f", (double) invoiceRow.getUnitPrice() / 100));
    lineItem.setOrderItemNumber(ALLU_ORDER_ITEM_NUMBER);
    lineItem.setQuantity(String.format("%.02f", invoiceRow.getQuantity()));
    lineItem.setUnit(mapToSapUnit(invoiceRow.getUnit()));
    lineItem.setMaterial(sapMaterial);
    return lineItem;
  }

  /*
   * Store the explanation texts (max 5) to line item's line texts 2...6:
   */
  private static void storeExplanation(LineItem lineItem, final String[] explanation) {
    switch (explanation.length) {
      case 5:
      default:
        lineItem.setLineText6("- " + explanation[4]);
      case 4:
        lineItem.setLineText5("- " + explanation[3]);
      case 3:
        lineItem.setLineText4("- " + explanation[2]);
      case 2:
        lineItem.setLineText3("- " + explanation[1]);
      case 1:
        lineItem.setLineText2("- " + explanation[0]);
      case 0:
        break;
    }
  }

  /**
   * Map an Allu Customer into a SAP OrderParty
   *
   * @param invoiceRecipient
   * @return
   */
  public static OrderParty mapToOrderParty(InvoiceRecipient invoiceRecipient, String sapCustomerNumber) {
    OrderParty orderParty = new OrderParty();
    orderParty.setSapCustomerId(sapCustomerNumber);
    orderParty.setInfoName1(invoiceRecipient.getName());
    orderParty.setInfoAddress1(invoiceRecipient.getStreetAddress());
    orderParty.setInfoPoCode(invoiceRecipient.getPostalCode());
    orderParty.setInfoCity(invoiceRecipient.getCity());
    if (invoiceRecipient.getType() == CustomerType.PERSON) {
      orderParty.setInfoCustomerId(invoiceRecipient.getRegistryKey());
    } else {
      orderParty.setInfoCustomerYid(invoiceRecipient.getRegistryKey());
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
      return "PVÄ";
    case HOUR:
      return "T";
    case MONTH:
      return "KK";
    case PIECE:
      return "KPL";
    case METER:
      return "M";
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

  // Material definitions for SAP (from 2017-10-16_Pelkat_Allun_nimikkeet.xlsx)

  private static String SAP_MATERIAL_AREA_RENTAL = "10002502";
  private static String SAP_MATERIAL_EXCAVATION_ANNOUNCEMENT = "10002501";
  private static String SAP_MATERIAL_FREE_EVENT = "27100024";
  private static String SAP_MATERIAL_NONFREE_EVENT = "27100025";
  private static String SAP_MATERIAL_CLOSED_EVENT = "27100026";
  private static String SAP_MATERIAL_PROMOTION = "27100020";
  private static String SAP_MATERIAL_BRIDGE_BANNER = "27100002";
  private static String SAP_MATERIAL_PROMOTION_OR_SALES = "27100018";
  private static String SAP_MATERIAL_URBAN_FARMING = "27100028";
  private static String SAP_MATERIAL_KESKUSKATU_SALES = "27100006";
  private static String SAP_MATERIAL_SUMMER_THEATER = "27100005";
  private static String SAP_MATERIAL_DOG_TRAINING_EVENT = "27100011";
  private static String SAP_MATERIAL_DOG_TRAINING_FIELD = "27100040";
  private static String SAP_MATERIAL_SEASON_SALE = "27100021";
  private static String SAP_MATERIAL_CIRCUS = "27100022";
  private static String SAP_MATERIAL_OTHER_RENTAL = "27100017";
  private static String SAP_MATERIAL_PLACEMENT_CONTRACT = "27100032";
  private static String SAP_MATERIAL_TERRACE = "27100052";
  private static String SAP_MATERIAL_PARKLET = "27100129";

  private static String mapToSapMaterial(Application application) {
    if (application.getType() == null) {
      return null;
    }
    switch (application.getType()) {
      case AREA_RENTAL:
        return SAP_MATERIAL_AREA_RENTAL;
      case EXCAVATION_ANNOUNCEMENT:
        return SAP_MATERIAL_EXCAVATION_ANNOUNCEMENT;
      case PLACEMENT_CONTRACT:
        return SAP_MATERIAL_PLACEMENT_CONTRACT;
      default:
        // Don't do anything, will be decided in next switch
    }
    switch (Optional.ofNullable(application.getKind()).orElse(ApplicationKind.OTHER)) {
      case OUTDOOREVENT:
        return mapOutdoorEventToSapMaterial((Event)application.getExtension());
      case BIG_EVENT:
        return SAP_MATERIAL_NONFREE_EVENT;
      case BRIDGE_BANNER:
        return SAP_MATERIAL_BRIDGE_BANNER;
      case PROMOTION_OR_SALES:
        return SAP_MATERIAL_PROMOTION_OR_SALES;
      case URBAN_FARMING:
        return SAP_MATERIAL_URBAN_FARMING;
      case KESKUSKATU_SALES:
        return SAP_MATERIAL_KESKUSKATU_SALES;
      case SUMMER_THEATER:
        return SAP_MATERIAL_SUMMER_THEATER;
      case DOG_TRAINING_EVENT:
        return SAP_MATERIAL_DOG_TRAINING_EVENT;
      case DOG_TRAINING_FIELD:
        return SAP_MATERIAL_DOG_TRAINING_FIELD;
      case PROMOTION:
        return SAP_MATERIAL_PROMOTION;
      case SEASON_SALE:
        return SAP_MATERIAL_SEASON_SALE;
      case CIRCUS:
        return SAP_MATERIAL_CIRCUS;
      case SUMMER_TERRACE:
      case WINTER_TERRACE:
        return SAP_MATERIAL_TERRACE;
      case PARKLET:
        return SAP_MATERIAL_PARKLET;
      default:
        return SAP_MATERIAL_OTHER_RENTAL;
    }
  }

  private static String mapOutdoorEventToSapMaterial(Event event) {
    switch (event.getNature()) {
      case PUBLIC_FREE:
        return SAP_MATERIAL_FREE_EVENT;
      case PUBLIC_NONFREE:
        return SAP_MATERIAL_NONFREE_EVENT;
      case CLOSED:
        return SAP_MATERIAL_CLOSED_EVENT;
      case PROMOTION:
        return SAP_MATERIAL_PROMOTION;
      default:
        return SAP_MATERIAL_OTHER_RENTAL;
    }
  }
}
