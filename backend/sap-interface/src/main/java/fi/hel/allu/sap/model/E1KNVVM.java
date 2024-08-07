package fi.hel.allu.sap.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * SAP customer sales data.
 */
@XmlRootElement(name = "E1KNVVM")
public class E1KNVVM {

  private String eikto;

  /**
   * Allu ID (SAP customer account number)
   */
  @XmlElement(name = "EIKTO")
  public String getEikto() {
    return eikto;
  }

  public void setEikto(String eikto) {
    this.eikto = eikto;
  }





}
