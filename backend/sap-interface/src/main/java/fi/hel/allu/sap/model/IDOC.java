package fi.hel.allu.sap.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * SAP intermediate document
 *
 */
@XmlRootElement(name = "IDOC")
public class IDOC {

  private E1KNA1M e1kna1m;

  /**
   * Customer basic data
   */
  @XmlElement(name = "E1KNA1M", required = true)
  public E1KNA1M getE1kna1m() {
    return e1kna1m;
  }

  public void setE1kna1m(E1KNA1M e1kna1m) {
    this.e1kna1m = e1kna1m;
  }
}
