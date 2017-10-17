package fi.hel.allu.sap.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * XML Mapping for a SAP Order party. Field comments from specification, thus in
 * Finnish.
 */
@XmlRootElement(name = "OrderParty")
@XmlType(propOrder = { "sapCustomerId", "infoCustomerId", "infoCustomerYid", "infoCustomerOvt", "infoName1",
    "infoAddress1", "infoPoCode", "infoCity" })
public class OrderParty {
  private String sapCustomerId;
  private String infoCustomerId;
  private String infoCustomerYid;
  private String infoCustomerOvt;
  private String infoName1;
  private String infoAddress1;
  private String infoPoCode;
  private String infoCity;

  /**
   * Asiakkaan SAP-asiakasnumero, (10 merkkiä)
   */
  @XmlElement(name = "SAPCustomerID")
  public String getSapCustomerId() {
    return sapCustomerId;
  }

  public void setSapCustomerId(String sapCustomerId) {
    this.sapCustomerId = sapCustomerId;
  }

  /**
   * Infotieto Henkilöasiakkaan HeTu, (11 merkkiä, muoto HeTu 121212-123K),
   * tarvitaan siltä varalta että kumppani joudutaan perustamaan käsin, ei
   * vaikuta muulla tavoin
   */
  @XmlElement(name = "InfoCustomerID")
  public String getInfoCustomerId() {
    return infoCustomerId;
  }

  public void setInfoCustomerId(String infoCustomerId) {
    this.infoCustomerId = infoCustomerId;
  }

  /**
   * Infotieto Yritysasiakkaan Y-tunnus (9 merkkiä, muoto YTunnus 1234567-8),
   * tarvitaan siltä varalta että kumppani joudutaan perustamaan käsin, ei
   * vaikuta muulla tavoin
   */
  @XmlElement(name = "InfoCustomerYID")
  public String getInfoCustomerYid() {
    return infoCustomerYid;
  }

  public void setInfoCustomerYid(String infoCustomerYid) {
    this.infoCustomerYid = infoCustomerYid;
  }

  /**
   * Infotieto Yritysasiakkaan OVT-tunnus (12-17 merkkiä, muoto OVT), tarvitaan
   * siltä varalta että kumppani joudutaan perustamaan käsin, ei vaikuta muulla
   * tavoin
   */
  @XmlElement(name = "InfoCustomerOVT")
  public String getInfoCustomerOvt() {
    return infoCustomerOvt;
  }

  public void setInfoCustomerOvt(String infoCustomerOvt) {
    this.infoCustomerOvt = infoCustomerOvt;
  }

  /**
   * Infotieto Nimi rivi1, tarvitaan siltä varalta että kumppani joudutaan
   * perustamaan käsin, ei vaikuta muulla tavoin
   */
  @XmlElement(name = "InfoName1")
  public String getInfoName1() {
    return infoName1;
  }

  public void setInfoName1(String infoName1) {
    this.infoName1 = infoName1;
  }

  /**
   * Infotieto Osoite rivi1, tarvitaan siltä varalta että kumppani joudutaan
   * perustamaan käsin, ei vaikuta muulla tavoin
   */
  @XmlElement(name = "InfoAddress1")
  public String getInfoAddress1() {
    return infoAddress1;
  }

  public void setInfoAddress1(String infoAddress1) {
    this.infoAddress1 = infoAddress1;
  }

  /**
   * Infotieto Postilokero, tarvitaan siltä varalta että kumppani joudutaan
   * perustamaan käsin, ei vaikuta muulla tavoin
   */
  @XmlElement(name = "InfoPOCode")
  public String getInfoPoCode() {
    return infoPoCode;
  }

  public void setInfoPoCode(String infoPoCode) {
    this.infoPoCode = infoPoCode;
  }

  /**
   * Infotieto Paikkakunta, tarvitaan siltä varalta että kumppani joudutaan
   * perustamaan käsin, ei vaikuta muulla tavoin
   */
  @XmlElement(name = "InfoCity")
  public String getInfoCity() {
    return infoCity;
  }

  public void setInfoCity(String infoCity) {
    this.infoCity = infoCity;
  }
}
