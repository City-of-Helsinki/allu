package fi.hel.allu.sap.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * SAP Customer basic data
 */
@XmlRootElement(name = "E1KNA1M")
public class E1KNA1M {

  private String kunnr;
  private String stcd1;
  private String stcd2;
  private String stcd3;
  private String stcd4;
  private String ktokd;
  private String land1;
  private String name1;
  private String ort01;
  private String pstlz;
  private String stras;
  private String sperr;

  private List<E1KNVVM> e1knvvm;

  /**
   * Customer sales data.
   */
  @XmlElement(name = "E1KNVVM", required = true)
  public List<E1KNVVM> getE1knvvm() {
    return e1knvvm;
  }

  public void setE1knvvm(List<E1KNVVM> e1knvvm) {
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
   * OVT
   */
  @XmlElement(name = "STCD3")
  public String getStcd3() {
    return stcd3;
  }

  public void setStcd3(String stcd3) {
    this.stcd3 = stcd3;
  }

  /**
   * E-invoicing operator code
   */
  @XmlElement(name = "STCD4")
  public String getStcd4() {
    return stcd4;
  }

  public void setStcd4(String stcd4) {
    this.stcd4 = stcd4;
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
