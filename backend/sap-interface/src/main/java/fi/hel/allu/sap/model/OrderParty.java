package fi.hel.allu.sap.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML Mapping for a SAP Order party. Field comments from specification, thus in
 * Finnish.
 */
@XmlRootElement(name = "OrderParty")
public class OrderParty {
  private String sapCustomerId;

  /**
   * Asiakkaan SAP-asiakasnumero, (10 merkkiä)
   */
  @XmlElement(name = "SAPCustomerId")
  public String getSapCustomerId() {
    return sapCustomerId;
  }

  public void setSapCustomerId(String sapCustomerId) {
    this.sapCustomerId = sapCustomerId;
  }
}
