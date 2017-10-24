package fi.hel.allu.sap.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "E1KNA1M")
public class E1KNA1M {

  private String kunnr;
  private String stcd1;
  private String stcd2;
  private String ktokd;
  private String land1;
  private String name1;
  private String ort01;
  private String pstlz;
  private String stras;
  private String sperr;

  private E1KNVVM e1knvvm;

  @XmlElement(name = "E1KNVVM", required = true)
  public E1KNVVM getE1knvvm() {
    return e1knvvm;
  }

  public void setE1knvvm(E1KNVVM e1knvvm) {
    this.e1knvvm = e1knvvm;
  }

  /**
   * SAP customer number
   */
  @XmlElement(name = "KUNNR", required = true)
  public String getKunnr() {
    return kunnr;
  }

  public void setKunnr(String kunnr) {
    this.kunnr = kunnr;
  }

  /**
   * Business ID
   */
  @XmlElement(name = "STCD1")
  public String getStcd1() {
    return stcd1;
  }

  public void setStcd1(String stcd1) {
    this.stcd1 = stcd1;
  }

  /**
   * Personal identification number
   */
  @XmlElement(name = "STCD2")
  public String getStcd2() {
    return stcd2;
  }

  public void setStcd2(String stcd2) {
    this.stcd2 = stcd2;
  }

  /**
   * Customer account group
   */
  @XmlElement(name = "KTOKD")
  public String getKtokd() {
    return ktokd;
  }

  public void setKtokd(String ktokd) {
    this.ktokd = ktokd;
  }

  /**
   * Country key
   */
  @XmlElement(name = "LAND1")
  public String getLand1() {
    return land1;
  }

  public void setLand1(String land1) {
    this.land1 = land1;
  }

  /**
   * Name 1
   */
  @XmlElement(name = "NAME1")
  public String getName1() {
    return name1;
  }

  public void setName1(String name1) {
    this.name1 = name1;
  }

  /**
   * City
   */
  @XmlElement(name = "ORT01")
  public String getOrt01() {
    return ort01;
  }

  public void setOrt01(String ort01) {
    this.ort01 = ort01;
  }

  /**
   * Postal code
   */
  @XmlElement(name = "PSTLZ")
  public String getPstlz() {
    return pstlz;
  }

  public void setPstlz(String pstlz) {
    this.pstlz = pstlz;
  }

  /**
   * Street address
   */
  @XmlElement(name = "STRAS")
  public String getStras() {
    return stras;
  }

  public void setStras(String stras) {
    this.stras = stras;
  }

  /**
   * Invoicing forbidden ("X" = true)
   * @return
   */
  @XmlElement(name = "SPERR")
  public String getSperr() {
    return sperr;
  }

  public void setSperr(String sperr) {
    this.sperr = sperr;
  }
}
