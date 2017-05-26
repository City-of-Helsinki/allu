package fi.hel.allu.sap.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML Mapping for a SAP line item. Field comments from specification, thus in
 * Finnish.
 */
@XmlRootElement(name = "LineItem")
public class LineItem {

  private String material;

  private String quantity;

  private String unit;

  private String netPrice;

  private String lineText1;

  private String orderItemNumber;

  /**
   * SAP nimike (8 numeroa), pakollinen
   */
  @XmlElement(name = "Material", required = true)
  public String getMaterial() {
    return material;
  }

  public void setMaterial(String material) {
    this.material = material;
  }

  /**
   * Lukumäärä, määrä, (13 numeroa, ei etumerkkiä, desimaalierotin voi olla
   * pilkku tai piste, tuhaterottimet ei sallittuja esim. 3456123,00 tai
   * 11232123.00), pakollinen
   */
  @XmlElement(name = "Quantity", required = true)
  public String getQuantity() {
    return quantity;
  }

  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }

  /**
   * Määräyksikkö (3 merkkiä), valinnainen
   */
  @XmlElement(name = "Unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  /**
   * Veroton hinta (11 numeroa ja kaksi desimaalia, desimaalierotin voi olla
   * pilkku tai piste, tuhaterottimet ei sallittuja esim. 3456123,00 tai
   * -11232123.00), pakollinen, hyvityksissä etumerkki -, laskun kokonaissumma
   * ei saa olla silti negativinen
   */
  @XmlElement(name = "NetPrice", required = true)
  public String getNetPrice() {
    return netPrice;
  }

  public void setNetPrice(String netPrice) {
    this.netPrice = netPrice;
  }

  /**
   * Riviteksti rivi 1/6, tulostuu laskulle. Sovittu että käytössä on 6 riviä
   * joiden pituus on 70 merkkiä.
   */
  @XmlElement(name = "LineTextL1")
  public String getLineText1() {
    return lineText1;
  }

  public void setLineText1(String lineText1) {
    this.lineText1 = lineText1;
  }

  /**
   * SAP sisäinen tilaus (10 numeroa)
   */
  @XmlElement(name = "OrderItemNumber")
  public String getOrderItemNumber() {
    return orderItemNumber;
  }

  public void setOrderItemNumber(String orderItemNumber) {
    this.orderItemNumber = orderItemNumber;
  }
}
