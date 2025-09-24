package fi.hel.allu.sap.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * XML Mapping for a SAP line item. Field comments from specification, thus in
 * Finnish.
 */
@XmlRootElement(name = "LineItem")
@XmlType(propOrder = { "material", "quantity", "unit", "netPrice", "lineText1", "lineText2", "lineText3", "lineText4",
    "lineText5", "lineText6", "wbsElement" })
public class LineItem {

  private String material;

  private String quantity;

  private String unit;

  private String netPrice;

  private String lineText1;
  private String lineText2;
  private String lineText3;
  private String lineText4;
  private String lineText5;
  private String lineText6;

  private String wbsElement;

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
   * Riviteksti rivi 2/6, tulostuu laskulle. Sovittu että käytössä on 6 riviä
   * joiden pituus on 70 merkkiä.
   */
  @XmlElement(name = "LineTextL2")
  public String getLineText2() {
    return lineText2;
  }

  public void setLineText2(String lineText2) {
    this.lineText2 = lineText2;
  }

  /**
   * Riviteksti rivi 3/6, tulostuu laskulle. Sovittu että käytössä on 6 riviä
   * joiden pituus on 70 merkkiä.
   */
  @XmlElement(name = "LineTextL3")
  public String getLineText3() {
    return lineText3;
  }

  public void setLineText3(String lineText3) {
    this.lineText3 = lineText3;
  }

  /**
   * Riviteksti rivi 4/6, tulostuu laskulle. Sovittu että käytössä on 6 riviä
   * joiden pituus on 70 merkkiä.
   */
  @XmlElement(name = "LineTextL4")
  public String getLineText4() {
    return lineText4;
  }

  public void setLineText4(String lineText4) {
    this.lineText4 = lineText4;
  }

  /**
   * Riviteksti rivi 5/6, tulostuu laskulle. Sovittu että käytössä on 6 riviä
   * joiden pituus on 70 merkkiä.
   */
  @XmlElement(name = "LineTextL5")
  public String getLineText5() {
    return lineText5;
  }

  public void setLineText5(String lineText5) {
    this.lineText5 = lineText5;
  }

  /**
   * Riviteksti rivi 6/6, tulostuu laskulle. Sovittu että käytössä on 6 riviä
   * joiden pituus on 70 merkkiä.
   */
  @XmlElement(name = "LineTextL6")
  public String getLineText6() {
    return lineText6;
  }

  public void setLineText6(String lineText6) {
    this.lineText6 = lineText6;
  }

  /**
   * SAP projektinumero (14 merkkiä)
   */
  @XmlElement(name = "WBS_Element")
  public String getWbsElement() {
    return wbsElement;
  }

  public void setWbsElement(String wbsElement) {
    this.wbsElement = wbsElement;
  }
}
