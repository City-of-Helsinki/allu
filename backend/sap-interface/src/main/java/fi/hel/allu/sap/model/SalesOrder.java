package fi.hel.allu.sap.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import java.util.List;

/**
 * XML Mapping for a SAP sales order. Field comments from specification, thus in
 * Finnish.
 */
@XmlRootElement(name = "SBO_SalesOrder")
@XmlType(propOrder = { "senderId", "orderType", "salesOrg", "distributionChannel", "division", "salesOffice",
    "poNumber", "billTextL1", "referenceText", "paymentTerm", "orderParty", "lineItems" })
public class SalesOrder {

  private String senderId;

  private String orderType;

  private String salesOrg;

  private String distributionChannel;

  private String division;

  private String salesOffice;

  private String poNumber;

  private String billTextL1;

  private String referenceText;

  private String paymentTerm;

  private OrderParty orderParty;

  private List<LineItem> lineItems;

  /**
   * Lähettäjätunnus (muoto IDXXX), pakollinen
   */
  @XmlElement(name = "SenderId", required = true)
  public String getSenderId() {
    return senderId;
  }

  public void setSenderId(String senderId) {
    this.senderId = senderId;
  }

  /**
   * Tilauslaji (4 merkkiä), pakollinen
   */
  @XmlElement(name = "OrderType", required = true)
  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  /**
   * Myyntiorganisaatio, virastotunnus (4 merkkiä), pakollinen
   */
  @XmlElement(name = "SalesOrg", required = true)
  public String getSalesOrg() {
    return salesOrg;
  }

  public void setSalesOrg(String salesOrg) {
    this.salesOrg = salesOrg;
  }

  /**
   * Jakelutie, (2 numeroa), pakollinen
   */
  @XmlElement(name = "DistributionChannel", required = true)
  public String getDistributionChannel() {
    return distributionChannel;
  }

  public void setDistributionChannel(String distributionChannel) {
    this.distributionChannel = distributionChannel;
  }

  /**
   * Sektori (2 numeroa), pakollinen
   */
  @XmlElement(name = "Division", required = true)
  public String getDivision() {
    return division;
  }

  public void setDivision(String division) {
    this.division = division;
  }

  /**
   * Myyntitoimisto (4 merkkiä), pakollinen
   */
  @XmlElement(name = "SalesOffice", required = true)
  public String getSalesOffice() {
    return salesOffice;
  }

  public void setSalesOffice(String salesOffice) {
    this.salesOffice = salesOffice;
  }

  /**
   * Asiakkaan viite (35 merkkiä), valinnainen
   */
  @XmlElement(name = "PONumber")
  public String getPoNumber() {
    return poNumber;
  }

  public void setPoNumber(String poNumber) {
    this.poNumber = poNumber;
  }

  /**
   * Ulkoisen tekstilajin teksti rivi 1/6 (70 merkkiä), valinnainen
   * <p>
   * Ulkoinen otsikkoteksti rivi 1/6, tulostuu laskulle. Sovittu että käytössä
   * on 6 riviä joiden pituus on 70 merkkiä.
   */
  @XmlElement(name = "BillTextL1")
  public String getBillTextL1() {
    return billTextL1;
  }

  public void setBillTextL1(String billTextL1) {
    this.billTextL1 = billTextL1;
  }

  /**
   * Viitteemme teksti (20 merkkiä), valinnainen
   * <p>
   * Viitteemme otsikkotason teksti, tulostuu laskulle.
   */
  @XmlElement(name = "ReferenceText")
  public String getReferenceText() {
    return referenceText;
  }

  public void setReferenceText(String referenceText) {
    this.referenceText = referenceText;
  }

  /**
   * Maksuehto (4 merkkiä), valinnainen
   */
  @XmlElement(name = "PMNTTERM")
  public String getPaymentTerm() {
    return paymentTerm;
  }

  public void setPaymentTerm(String paymentTerm) {
    this.paymentTerm = paymentTerm;
  }

  /**
   * Tilausasiakas
   */
  @XmlElement(name = "OrderParty", required = true)
  public OrderParty getOrderParty() {
    return orderParty;
  }

  public void setOrderParty(OrderParty odredParty) {
    this.orderParty = odredParty;
  }

  /**
   * Rivitason tiedot
   */
  @XmlElement(name = "LineItem", required = true)
  public List<LineItem> getLineItems() {
    return lineItems;
  }

  public void setLineItems(List<LineItem> lineItems) {
    this.lineItems = lineItems;
  }

}
