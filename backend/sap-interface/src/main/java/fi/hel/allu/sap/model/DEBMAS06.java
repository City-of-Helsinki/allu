package fi.hel.allu.sap.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * SAP customer master data
 *
 */
@XmlRootElement(name = "DEBMAS06")
public class DEBMAS06 {

  private IDOC iDoc;

  /**
   * Customer intermediate document
   */
  @XmlElement(name = "IDOC")
  public IDOC getiDoc() {
    return iDoc;
  }

  public void setiDoc(IDOC iDoc) {
    this.iDoc = iDoc;
  }

}
